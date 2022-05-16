package root.service.implement;

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

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.domain.exceptions.PropertyNotLoadedException;
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
	 * @throws PropertyNotFoundException 
	 */
	@Override
	public List<String> getConnectionInfoList() throws PropertyNotFoundException {
		return new ArrayList<>(Arrays.asList(propRepo.getConnectionInfoFileNames()));
	}

	/**
	 * 최근 사용된 접속정보 설정파일 경로를 반환한다.
	 */
	@Override
	public String getLastUseConnectionInfoFilePath() {
		String filePath = propRepo.getLastUseConnInfoFilePath();
		return propRepo.isFileExist(filePath) ? filePath : null;
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
	public Map<String, String> getMonitoringPresetMap() throws PropertyNotLoadedException {
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
	public List<String> getMonitoringPresetFilePathList() throws PropertyNotLoadedException {
		return new ArrayList<>(getMonitoringPresetMap().values());
	}

	@Override
	public List<String> getMonitoringPresetNameList() throws PropertyNotLoadedException {
		return new ArrayList<>(getMonitoringPresetMap().keySet());
	}

	@Override
	public String getMonitoringPresetFilePath(String presetName) throws PropertyNotLoadedException {
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

	/**
	 * 서버의 접속정보를 가져온다.
	 */
	@Override
	public JschConnectionInfo getJschConnInfo(String serverName) {
		return propRepo.getJschConnectionInfo(serverName);
	}

	/**
	 * 서버들의 접속정보를 가져온다.
	 */
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

	/**
	 * 공통 설정정보를 저장한다.
	 * @throws PropertyNotLoadedException 
	 */
	@Override
	public void saveCommonConfig(String key, String value) throws PropertyNotLoadedException {
		propRepo.saveCommonConfig(key, value);
	}

	/**
	 * 최근 사용한 접속정보 설정정보를 저장한다.
	 * @throws PropertyNotLoadedException 
	 */
	@Override
	public void saveLastUseConnectionInfoSetting(String filePath) throws PropertyNotLoadedException {
		PropertiesConfiguration rememberConfig = propRepo.getConfiguration("rememberConfig");
		rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
		propRepo.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);
	}

	/**
	 * 접속정보 설정을 추가한다.
	 * @throws PropertyNotLoadedException 
	 */
	@Override
	public String addConnectionInfoSetting(String fileName) throws PropertyNotLoadedException {
		String filePath = "./config/connectioninfo/" + fileName + ".properties";
		propRepo.createNewPropertiesFile(filePath, "ConnectionInfo");
		propRepo.loadConnectionInfoConfig(filePath);
		addMonitoringPreset(filePath, "default");
		return filePath;
	}

	/**
	 * 모니터링여부 Preset 설정을 추가한다.
	 * @throws PropertyNotLoadedException 
	 */
	@Override
	public void addMonitoringPreset(String connInfoSetting, String presetName) throws PropertyNotLoadedException {
		String connInfoFileName = connInfoSetting.substring(connInfoSetting.lastIndexOf("/") + 1,
				connInfoSetting.indexOf(".properties"));
		String filePath = "./config/monitoring/" + connInfoFileName + "/" + presetName + ".properties";

		propRepo.createNewPropertiesFile(filePath, "Monitoring");

		PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");
		config.addProperty("monitoring.setting.preset." + presetName + ".filepath", filePath);
		propRepo.save(connInfoSetting, config);
	}

	/**
	 * 모니터링여부 Preset 설정을 저장한다.
	 */
	@Override
	public void saveMonitoringPresetSetting(String presetName,
			Map<MonitoringType, Map<String, Boolean>> settingedMonitoringYN) {
		PropertiesConfiguration config = new PropertiesConfiguration();
		String monitoringFilePath = propRepo.getMonitoringPresetMap().get(presetName);

		if (!monitoringFilePath.isEmpty()) {
			for (MonitoringType type : settingedMonitoringYN.keySet()) {
				Map<String, Boolean> aliasMap = settingedMonitoringYN.get(type);
				for (String alias : aliasMap.keySet()) {
					String key = StringUtils.join(type.getName().replace(" ", "_"), ".", alias);
					config.setProperty(key, aliasMap.get(alias) ? "Y" : "N");
				}
			}
			propRepo.save(monitoringFilePath, config);
			propRepo.loadMonitoringInfoConfig(monitoringFilePath);
		}
	}

	@Override
	public AlertLogCommand getAlertLogCommand(String connInfoSetting, String serverName) {
		loadConnectionInfoConfig(connInfoSetting);
		return propRepo.getAlertLogCommand(serverName);
	}
}
