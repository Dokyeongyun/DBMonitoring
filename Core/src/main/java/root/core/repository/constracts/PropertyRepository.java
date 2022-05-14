package root.core.repository.constracts;

import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;

public interface PropertyRepository {

	boolean isFileExist(String filePath);

	PropertiesConfiguration getConfiguration(String config);

	void save(String filePath, PropertiesConfiguration config);

	void saveDBConnectionInfo(String filePath, Map<String, JdbcConnectionInfo> config);

	void saveServerConnectionInfo(String filePath, Map<String, JschConnectionInfo> config);

	void saveCommonConfig(Map<String, Object> values);
	
	void saveCommonConfig(String key, String value);

	void loadCombinedConfiguration();

	void loadConnectionInfoConfig(String filePath);

	void loadMonitoringInfoConfig(String filePath);

	String[] getConnectionInfoFileNames();

	String getCommonResource(String key);

	int getIntegerCommonResource(String key);

	String[] getCommonResources(String key);

	String[] getDBMonitoringContents();

	String[] getServerMonitoringContents();

	String[] getOracleDrivers();

	String getLastUseConnInfoFilePath();

	List<String> getMonitoringPresetNameList();

	Map<String, String> getMonitoringPresetMap();

	String getLastUseMonitoringPresetName();

	String getLastUseMonitoringPresetName(String filePath);

	String[] getMonitoringDBNames();

	String[] getMonitoringServerNames();

	boolean isMonitoringContent(String toggleId);

	void createNewPropertiesFile(String filePath, String type);

	JdbcConnectionInfo getJdbcConnectionInfo(String dbName);

	JschConnectionInfo getJschConnectionInfo(String serverName);
	
	AlertLogCommand getAlertLogCommand(String serverName);
	
	String getMonitoringConfigResource(String key);
}
