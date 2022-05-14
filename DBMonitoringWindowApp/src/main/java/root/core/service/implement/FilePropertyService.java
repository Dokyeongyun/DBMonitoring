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

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
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
	 * �������� ���������� Load �Ѵ�.
	 */
	@Override
	public void loadConnectionInfoConfig(String filePath) {
		propRepo.loadConnectionInfoConfig(filePath);
	}

	/**
	 * ./config/connectioninfo/ ���͸� ������ �ִ� �������� �������� ����Ʈ�� ��ȯ�Ѵ�.
	 */
	@Override
	public List<String> getConnectionInfoList() {
		return new ArrayList<>(Arrays.asList(propRepo.getConnectionInfoFileNames()));
	}

	/**
	 * �ֱ� ���� �������� �������� ��θ� ��ȯ�Ѵ�.
	 */
	@Override
	public String getLastUseConnectionInfoFilePath() {
		String filePath = propRepo.getLastUseConnInfoFilePath();
		return propRepo.isFileExist(filePath) ? filePath : null;
	}

	/**
	 * ����͸� ���� ���������� Load �Ѵ�.
	 */
	@Override
	public void loadMonitoringInfoConfig(String filePath) {
		propRepo.loadMonitoringInfoConfig(filePath);
	}

	/**
	 * �ֱ� ���� ����͸� ���� Preset �������ϸ��� ��ȯ�Ѵ�.
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

	/**
	 * ������ ���������� �����´�.
	 */
	@Override
	public JschConnectionInfo getJschConnInfo(String serverName) {
		return propRepo.getJschConnectionInfo(serverName);
	}

	/**
	 * �������� ���������� �����´�.
	 */
	@Override
	public List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames) {
		return serverNames.stream().sorted().collect(
				Collectors.mapping(serverName -> propRepo.getJschConnectionInfo(serverName), Collectors.toList()));
	}

	/**
	 * �⺻������ ������ FileSize ������ ��ȯ�Ѵ�.
	 */
	@Override
	public FileSize getDefaultFileSizeUnit() {
		return FileSize.find(propRepo.getCommonResource("unit.filesize"));
	}

	/**
	 * �⺻������ ������ �ݿø� �ڸ����� ��ȯ�Ѵ�.
	 */
	@Override
	public RoundingDigits getDefaultRoundingDigits() {
		return RoundingDigits.find(propRepo.getCommonResource("unit.rounding"));
	}

	/**
	 * �⺻������ ������ ��뷮 �÷� UI Ÿ���� ��ȯ�Ѵ�.
	 */
	@Override
	public UsageUIType getDefaultUsageUIType() {
		return UsageUIType.find(propRepo.getCommonResource("usage-ui-type"));
	}

	/**
	 * ���� ���������� �����Ѵ�.
	 */
	@Override
	public void saveCommonConfig(String key, String value) {
		propRepo.saveCommonConfig(key, value);
	}

	/**
	 * �ֱ� ����� �������� ���������� �����Ѵ�.
	 */
	@Override
	public void saveLastUseConnectionInfoSetting(String filePath) {
		PropertiesConfiguration rememberConfig = propRepo.getConfiguration("rememberConfig");
		rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
		propRepo.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);
	}

	/**
	 * �������� ������ �߰��Ѵ�.
	 */
	@Override
	public String addConnectionInfoSetting(String fileName) {
		String filePath = "./config/connectioninfo/" + fileName + ".properties";
		propRepo.createNewPropertiesFile(filePath, "ConnectionInfo");
		addMonitoringPreset(filePath, "default");
		return filePath;
	}

	/**
	 * ����͸����� Preset ������ �߰��Ѵ�.
	 */
	@Override
	public void addMonitoringPreset(String connInfoSetting, String presetName) {
		String connInfoFileName = connInfoSetting.substring(0, connInfoSetting.indexOf(".properties"));
		String filePath = "./config/monitoring/" + connInfoFileName + "/" + presetName + ".properties";

		propRepo.createNewPropertiesFile(filePath, "Monitoring");

		PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");
		config.addProperty("monitoring.setting.preset." + presetName + ".filepath", filePath);
		propRepo.save(connInfoSetting, config);
	}

	/**
	 * ����͸����� Preset ������ �����Ѵ�.
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
