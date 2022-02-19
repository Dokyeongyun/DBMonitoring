package root.core.service.contracts;

import java.util.List;
import java.util.Map;

public interface PropertyService {

	Map<String, String> getMonitoringPresetMap();

	List<String> getMonitoringPresetFilePathList();

	List<String> getMonitoringPresetNameList();

	String getMonitoringPresetFilePath(String presetName);
}
