package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.ex.ConfigurationException;

import root.core.domain.exceptions.PropertyNotLoadedException;

public interface PropertyService {

	void loadAppConfiguration(String filePath) throws ConfigurationException;

	boolean isLoaded(String configName);

	Map<String, String> getMonitoringPresetMap() throws PropertyNotLoadedException;

	List<String> getMonitoringPresetFilePathList() throws PropertyNotLoadedException;

	List<String> getMonitoringPresetNameList() throws PropertyNotLoadedException;

	String getMonitoringPresetFilePath(String presetName) throws PropertyNotLoadedException;
}
