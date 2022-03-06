package root.core.service.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.core.repository.constracts.PropertyRepository;
import root.core.service.contracts.PropertyService;
import root.utils.UnitUtils.FileSize;

public class FilePropertyService implements PropertyService {

	private static final String MONITORING_PRESET_KEY = "monitoring.setting.preset.(.*).filepath";
	private static final Pattern MONITORING_PRESET_KEY_PATTERN = Pattern.compile(MONITORING_PRESET_KEY);

	private PropertyRepository propRepo;

	public FilePropertyService(PropertyRepository propRepo) {
		this.propRepo = propRepo;
	}

	/**
	 * 접속정보 설정파일을 Load 한다.
	 */
	@Override
	public void loadConnectionInfoConfig(String filePath) {
		propRepo.loadConnectionInfoConfig(filePath);
	}

	/**
	 * ./config/connectioninfo/ 디렉터리 하위에 있는 접속정보 설정파일 리스트를 반환한다.
	 */
	@Override
	public List<String> getConnectionInfoList() {
		return new ArrayList<>(Arrays.asList(propRepo.getConnectionInfoFileNames()));
	}

	/**
	 * 최근 사용된 접속정보 설정파일 경로를 반환한다.
	 */
	@Override
	public String getLastUseConnectionInfoFilePath() {
		return propRepo.getLastUseConnInfoFilePath();
	}

	/**
	 * 모니터링 여부 설정파일을 Load 한다.
	 */
	@Override
	public void loadMonitoringInfoConfig(String filePath) {
		propRepo.loadMonitoringInfoConfig(filePath);
	}

	/**
	 * 최근 사용된 모니터링 여부 Preset 설정파일명을 반환한다.
	 */
	@Override
	public String getLastUsePresetFileName(String filePath) {
		return propRepo.getLastUseMonitoringPresetName(filePath);
	}

	@Override
	public List<MonitoringYN> getDBMonitoringYnList(String presetConfigFileName) {
		String presetConfigFilePath = propRepo.getMonitoringPresetMap().get(presetConfigFileName);

		// Load
		loadMonitoringInfoConfig(presetConfigFilePath);

		List<String> dbAliasList = Arrays.asList(propRepo.getMonitoringDBNames());
		List<MonitoringType> monitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(t -> t.getCategory().equals("DB")).collect(Collectors.toList());

		return getMonitoringYNList(dbAliasList, monitoringTypeList);
	}

	@Override
	public List<MonitoringYN> getServerMonitoringYnList(String presetConfigFileName) {
		String presetConfigFilePath = propRepo.getMonitoringPresetMap().get(presetConfigFileName);

		// Load
		loadMonitoringInfoConfig(presetConfigFilePath);

		List<String> serverAliasList = Arrays.asList(propRepo.getMonitoringServerNames());
		List<MonitoringType> monitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(t -> t.getCategory().equals("SERVER")).collect(Collectors.toList());

		return getMonitoringYNList(serverAliasList, monitoringTypeList);
	}

	private List<MonitoringYN> getMonitoringYNList(List<String> aliasList, List<MonitoringType> monitoringTypeList) {

		List<MonitoringYN> result = new ArrayList<>();
		for (String serverAlias : aliasList) {
			MonitoringYN monitoringYn = new MonitoringYN(serverAlias);
			List<MonitoringTypeAndYN> list = new ArrayList<>();
			for (MonitoringType monitoringType : monitoringTypeList) {
				String yn = propRepo
						.getMonitoringConfigResource(monitoringType.getName().replace(" ", "_") + "." + serverAlias);
				if (!StringUtils.isEmpty(yn)) {
					list.add(new MonitoringTypeAndYN(monitoringType, yn.equals("Y") ? true : false));
				} else {
					list.add(new MonitoringTypeAndYN(monitoringType, false));
				}
			}
			monitoringYn.setMonitoringTypeList(list);
			result.add(monitoringYn);
		}

		return result;
	}

	@Override
	public Map<String, String> getMonitoringPresetMap() {
		Map<String, String> result = new HashMap<>();

		PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");

		config.getKeys().forEachRemaining(key -> {
			if (key.matches(MONITORING_PRESET_KEY)) {
				Matcher m = MONITORING_PRESET_KEY_PATTERN.matcher(key);
				if (m.matches()) {
					String presetName = m.group(1);
					result.put(presetName, config.getString(key));
				}
			}
		});
		return result;
	}

	@Override
	public List<String> getMonitoringPresetFilePathList() {
		return new ArrayList<>(getMonitoringPresetMap().values());
	}

	@Override
	public List<String> getMonitoringPresetNameList() {
		return new ArrayList<>(getMonitoringPresetMap().keySet());
	}

	@Override
	public String getMonitoringPresetFilePath(String presetName) {
		return getMonitoringPresetMap().get(presetName);
	}

	@Override
	public List<String> getMonitoringDBNameList() {
		return Arrays.asList(propRepo.getMonitoringDBNames());
	}

	@Override
	public List<String> getMonitoringServerNameList() {
		return Arrays.asList(propRepo.getMonitoringServerNames());
	}

	@Override
	public List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames) {
		return dbNames.stream().sorted()
				.collect(Collectors.mapping(dbName -> propRepo.getJdbcConnectionInfo(dbName), Collectors.toList()));
	}

	@Override
	public List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames) {
		return serverNames.stream().sorted().collect(
				Collectors.mapping(serverName -> propRepo.getJschConnectionInfo(serverName), Collectors.toList()));
	}

	/**
	 * 기본값으로 설정된 FileSize 단위를 반환한다.
	 */
	@Override
	public FileSize getDefaultFileSizeUnit() {
		return FileSize.find(propRepo.getCommonResource("unit.filesize"));
	}

	/**
	 * 기본값으로 설정된 반올림 자릿수를 반환한다.
	 */
	@Override
	public RoundingDigits getDefaultRoundingDigits() {
		return RoundingDigits.find(propRepo.getCommonResource("unit.rounding"));
	}

	/**
	 * 기본값으로 설정된 사용량 컬럼 UI 타입을 반환한다.
	 */
	@Override
	public UsageUIType getDefaultUsageUIType() {
		return UsageUIType.find(propRepo.getCommonResource("usage-ui-type"));
	}
}
