package root.core.service.implement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import root.core.domain.exceptions.PropertyNotLoadedException;
import root.core.service.contracts.PropertyService;

public class FilePropertyService implements PropertyService {

	private static final String MONITORING_PRESET_KEY = "monitoring.setting.preset.(.*).filepath";
	private static final Pattern MONITORING_PRESET_KEY_PATTERN = Pattern.compile(MONITORING_PRESET_KEY);

	private PropertiesConfiguration connInfoConfig = null; // DB, Server 접속정보 Configuration

	public FilePropertyService(String filePath) throws ConfigurationException {
		loadAppConfiguration(filePath);
	}

	/**
	 * 매개변수로 주어진 경로에 저장된 설정파일을 읽어 [propConfig] PropertiesConfiguration 객체를 초기화한다.
	 * 
	 * @param filePath
	 * @throws ConfigurationException
	 */
	@Override
	public void loadAppConfiguration(String filePath) throws ConfigurationException {
		File file = new File(filePath);
		ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(',');

		PropertiesBuilderParameters propertyParameters = new Parameters().properties();
		propertyParameters.setFile(file);
		propertyParameters.setThrowExceptionOnMissing(true);
		propertyParameters.setListDelimiterHandler(delimiter);

		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
				PropertiesConfiguration.class);
		builder.configure(propertyParameters);

		connInfoConfig = builder.getConfiguration();
	}

	@Override
	public boolean isLoaded(String configName) {
		boolean result = false;

		switch (configName) {
		case "connInfoConfig":
			if (connInfoConfig != null) {
				result = true;
			}
			break;
		}

		return result;
	}
	
	@Override
	public Map<String, String> getMonitoringPresetMap() throws PropertyNotLoadedException {
		if (!isLoaded("connInfoConfig")) {
			throw new PropertyNotLoadedException("connInfoConfig");
		}

		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(connInfoConfig.getKeys(), Spliterator.ORDERED), false)
				.filter(key -> key.matches(MONITORING_PRESET_KEY))
				.collect(Collectors.toUnmodifiableMap(key -> {
					Matcher m = MONITORING_PRESET_KEY_PATTERN.matcher(key);
					return m.matches() ? m.group(1) : null;
				}, key -> key));
	}

	@Override
	public List<String> getMonitoringPresetFilePathList() throws PropertyNotLoadedException {
		if (!isLoaded("connInfoConfig")) {
			throw new PropertyNotLoadedException("connInfoConfig");
		}

		return new ArrayList<>(getMonitoringPresetMap().values());
	}

	@Override
	public List<String> getMonitoringPresetNameList() throws PropertyNotLoadedException {
		if (!isLoaded("connInfoConfig")) {
			throw new PropertyNotLoadedException("connInfoConfig");
		}
		return new ArrayList<>(getMonitoringPresetMap().keySet());
	}

	@Override
	public String getMonitoringPresetFilePath(String presetName) throws PropertyNotLoadedException {
		if (!isLoaded("connInfoConfig")) {
			throw new PropertyNotLoadedException("connInfoConfig");
		}
		return getMonitoringPresetMap().get(presetName);
	}
}
