package root.core.service.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import root.core.domain.AlertLogCommand;
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

	@Override
	public Map<String, String> getMonitoringPresetMap() {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(propRepo.getConfiguration("connInfoConfig").getKeys(),
						Spliterator.ORDERED), false)
				.filter(key -> key.matches(MONITORING_PRESET_KEY))
				.collect(Collectors.toUnmodifiableMap(key -> {
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
	public List<String> getDBNameList() {
		return Arrays.asList(propRepo.getMonitoringDBNames());
	}

	@Override
	public List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames) {
		List<JdbcConnectionInfo> jdbcList = new ArrayList<>();
		for (String dbName : dbNames) {
			jdbcList.add(propRepo.getJdbcConnectionInfo(dbName));
		}
		Collections.sort(jdbcList, (o1, o2) -> o1.getJdbcDBName().compareTo(o2.getJdbcDBName()) < 0 ? -1 : 1);
		
		return jdbcList;
	}

	@Override
	public List<JschConnectionInfo> getJschConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, AlertLogCommand> getAlertLogCommandMap() {
		// TODO Auto-generated method stub
		return null;
	}
}
