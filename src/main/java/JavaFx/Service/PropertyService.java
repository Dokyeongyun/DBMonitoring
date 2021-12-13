package JavaFx.Service;

import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;

public interface PropertyService {
	
	PropertiesConfiguration getConfiguration(String config);
	
	void save(String filePath, PropertiesConfiguration config);
	
	boolean loadConnectionInfoConfig(String filePath);

	boolean loadMonitoringInfoConfig(String filePath);

	String[] getCommonResources(String key);
	
	String[] getDBMonitoringContents();
	
	String[] getServerMonitoringContents();
	
	String[] getOracleDrivers();
	
	String getLastUseConnInfoFileName();

	List<String> getMonitoringPresetNameList();
	
	Map<String, String> getMonitoringPresetMap();
	
	String getLastUseMonitoringPresetName();
	
	String[] getMonitoringDBNames();
	
	String[] getMonitoringServerNames();
	
	boolean isMonitoringContent(String toggleId);
}
