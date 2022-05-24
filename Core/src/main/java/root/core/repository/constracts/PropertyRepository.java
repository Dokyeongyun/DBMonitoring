package root.core.repository.constracts;

import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.domain.exceptions.PropertyNotLoadedException;

public interface PropertyRepository {

	PropertiesConfiguration getConfiguration(String config) throws PropertyNotLoadedException;

	void save(String filePath, PropertiesConfiguration config);

	void saveDBConnectionInfo(String filePath, Map<String, JdbcConnectionInfo> config);

	void saveServerConnectionInfo(String filePath, Map<String, JschConnectionInfo> config);

	void saveCommonConfig(Map<String, Object> values) throws PropertyNotLoadedException;
	
	void saveCommonConfig(String key, String value) throws PropertyNotLoadedException;

	void loadCombinedConfiguration() throws PropertyNotFoundException;

	void loadConnectionInfoConfig(String filePath);

	void loadMonitoringInfoConfig(String filePath);

	String[] getConnectionInfoFileNames() throws PropertyNotFoundException;

	String getCommonResource(String key);

	String[] getCommonResources(String key);

	String[] getDBMonitoringContents();

	String[] getServerMonitoringContents();

	String[] getOracleDrivers();

	String getLastUseConnInfoFilePath();

	List<String> getMonitoringPresetNameList();

	Map<String, String> getMonitoringPresetMap();

	String getLastUseMonitoringPresetName(String filePath);

	String[] getMonitoringDBNames();

	String[] getMonitoringServerNames();

	void createNewPropertiesFile(String filePath, String type);

	JdbcConnectionInfo getJdbcConnectionInfo(String dbName);

	JschConnectionInfo getJschConnectionInfo(String serverName);
	
	AlertLogCommand getAlertLogCommand(String serverName);
	
	String getMonitoringConfigResource(String key);
}
