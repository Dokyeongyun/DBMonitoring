package root.core.service.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.service.contracts.PropertyService;

public class FilePropertyService implements PropertyService {

	private static final String MONITORING_PRESET_KEY = "monitoring.setting.preset.(.*).filepath";
	private static final Pattern MONITORING_PRESET_KEY_PATTERN = Pattern.compile(MONITORING_PRESET_KEY);

	private PropertyRepository propRepo;

	public FilePropertyService(PropertyRepository propRepo) {
		this.propRepo = propRepo;
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
	public String getLastUseConnectionInfo() {
		return propRepo.getLastUseConnInfoFilePath();
	}

	/**
	 * �������� ���������� Load �Ѵ�.
	 */
	@Override
	public void loadConnectionInfoConfig(String filePath) {
		propRepo.loadConnectionInfoConfig(filePath);
	}

	@Override
	public Map<String, String> getMonitoringPresetMap() {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(propRepo.getConfiguration("connInfoConfig").getKeys(),
						Spliterator.ORDERED), false)
				.filter(key -> key.matches(MONITORING_PRESET_KEY)).collect(Collectors.toUnmodifiableMap(key -> {
					Matcher m = MONITORING_PRESET_KEY_PATTERN.matcher(key);
					return m.matches() ? m.group(1) : null;
				}, key -> key));
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

}
