package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.core.domain.AlertLogCommand;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;

public interface PropertyService {

	Map<String, String> getMonitoringPresetMap();

	List<String> getMonitoringPresetFilePathList();

	List<String> getMonitoringPresetNameList();

	String getMonitoringPresetFilePath(String presetName);
	
	List<String> getDBNameList();
	
	List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames);

	List<JschConnectionInfo> getJschConnections();

	Map<String, AlertLogCommand> getAlertLogCommandMap();
}
