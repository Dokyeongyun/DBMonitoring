package JavaFx.Service;

import java.util.List;

public interface PropertyService {
	
	boolean loadConnectionInfoConfig(String filePath);
	
	String getLastUseConnInfoFileName();

	List<String> getMonitoringPresetList();
	
	String getLastUseMonitoringPresetName();
	
	String[] getMonitoringDBNames();
	
	String[] getMonitoringServerNames();
}
