package root.core.repository.implement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration.PropertiesWriter;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.builder.CopyObjectDefaultHandler;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.XMLBuilderProperties;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.CombinedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.ServerOS;
import root.core.repository.constracts.PropertyRepository;

@Slf4j
public class PropertyRepositoryImpl implements PropertyRepository {

	// Private 필드로 선언 후 Singletone으로 관리
	private static PropertyRepository propRepo = new PropertyRepositoryImpl();

	// 생성자를 Private으로 선언함으로써 해당 객체를 생성할 수 있는 방법을 업애버림 => 안정적인 Singletone 관리방법
	private PropertyRepositoryImpl() {
		loadCombinedConfiguration();
	}

	// propertyService Field에 접근할 수 있는 유일한 방법 (Static Factory Pattern)
	public static PropertyRepository getInstance() {
		return propRepo;
	}

	private PropertiesConfiguration connInfoConfig; // 접속정보 설정 Configuration
	private PropertiesConfiguration monitoringConfig; // 모니터링여부 Configuration
	private CombinedConfiguration combinedConfig; // 공통 Configuration

	private static Pattern dbPropPattern = Pattern.compile("(.*).jdbc.(.*)");
	private static Pattern serverPropPattern = Pattern.compile("(.*).server.(.*)");

	/****************************************************************************/

	@Override
	public boolean isFileExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * Configuration 객체를 반환한다. TODO 굳이 메서드를 Wrapping 해서 호출할 필요가 있을까..? Controller와
	 * 의존성 제거목적으로 일단 이렇게 함..
	 */
	@Override
	public PropertiesConfiguration getConfiguration(String name) {
		if (name.equals("connInfoConfig")) {
			return connInfoConfig;
		} else if (name.equals("monitoringConfig")) {
			return monitoringConfig;
		}
		return (PropertiesConfiguration) combinedConfig.getConfiguration(name);
	}

	/**
	 * 주어진 경로에 PropertyConfiguration에 설정된 Key-Value를 저장한다.
	 */
	@Override
	public void save(String filePath, PropertiesConfiguration config) {
		PropertiesConfigurationLayout layout = config.getLayout();
		try {
			final PropertiesConfiguration.PropertiesWriter writer = config.getIOFactory()
					.createPropertiesWriter(new FileWriter(filePath, false), config.getListDelimiterHandler());

			// Write Header Comment;
			writer.writeln(layout.getHeaderComment());

			for (final String key : layout.getKeys()) {
				// Output blank lines before property
				for (int i = 0; i < layout.getBlancLinesBefore(key); i++) {
					writer.writeln(null);
				}

				// Output the comment
				if (layout.getComment(key) != null) {
					writer.writeln(layout.getComment(key));
				}

				// Output the property and its value
				final boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
				writer.setCurrentSeparator(layout.getSeparator(key));
				writer.writeProperty(key, config.getProperty(key), singleLine);
			}

			writer.writeln(layout.getCanonicalFooterCooment(true));
			writer.flush();

			log.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("[" + filePath + "] 파일 저장에 실패했습니다.");
		}
	}

	@Override
	public void saveDBConnectionInfo(String filePath, Map<String, JdbcConnectionInfo> dbConfig) {
		PropertiesConfiguration config = connInfoConfig;

		// TODO dbnames property..
		String dbNames = "";
		for (String dbName : dbConfig.keySet()) {
			dbNames += dbName + ",";

			JdbcConnectionInfo jdbc = dbConfig.get(dbName);
			config.setProperty(dbName + ".jdbc.alias", jdbc.getJdbcDBName());
			config.setProperty(dbName + ".jdbc.id", jdbc.getJdbcId());
			config.setProperty(dbName + ".jdbc.pw", jdbc.getJdbcPw());
			config.setProperty(dbName + ".jdbc.url", jdbc.getJdbcUrl());
			config.setProperty(dbName + ".jdbc.driver", jdbc.getJdbcDriver());
			config.setProperty(dbName + ".jdbc.validation", jdbc.getJdbcValidation());
			config.setProperty(dbName + ".jdbc.connections", jdbc.getJdbcConnections());
		}

		config.setProperty("dbnames", dbNames.substring(0, dbNames.length() - 1));

		PropertiesConfigurationLayout layout = config.getLayout();
		try {
			PropertiesWriter writer = config.getIOFactory().createPropertiesWriter(new FileWriter(filePath, false),
					config.getListDelimiterHandler());

			// Write Header Comment
			writer.writeln(layout.getHeaderComment());

			for (final String key : layout.getKeys()) {
				Matcher m = dbPropPattern.matcher(key);
				if (m.matches()) {
					String dbName = m.group(1);
					if (!dbConfig.containsKey(dbName)) {
						continue;
					}
				}

				// Output blank lines before property
				for (int i = 0; i < layout.getBlancLinesBefore(key); i++) {
					writer.writeln(null);
				}

				// Output the property and its value
				boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
				writer.setCurrentSeparator(layout.getSeparator(key));
				writer.writeProperty(key, config.getProperty(key), singleLine);
			}

			writer.writeln(layout.getCanonicalFooterCooment(true));
			writer.flush();

			log.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("[" + filePath + "] 파일 저장에 실패했습니다.");
			log.error(e.getMessage());
		}
	}

	@Override
	public void saveServerConnectionInfo(String filePath, Map<String, JschConnectionInfo> serverConfig) {
		PropertiesConfiguration config = connInfoConfig;

		// TODO servernames property..
		String serverNames = "";
		for (String serverName : serverConfig.keySet()) {
			serverNames += serverName + ",";

			JschConnectionInfo jsch = serverConfig.get(serverName);
			config.setProperty(serverName + ".server.name", jsch.getServerName());
			config.setProperty(serverName + ".server.os", jsch.getServerOS().name());
			config.setProperty(serverName + ".server.host", jsch.getHost());
			config.setProperty(serverName + ".server.port", jsch.getPort());
			config.setProperty(serverName + ".server.username", jsch.getUserName());
			config.setProperty(serverName + ".server.password", jsch.getPassword());
			config.setProperty(serverName + ".server.alertlog.filepath", jsch.getAlc().getReadFilePath());
			config.setProperty(serverName + ".server.alertlog.readline", 500);
		}
		config.setProperty("servernames", serverNames.substring(0, serverNames.length() - 1));

		PropertiesConfigurationLayout layout = config.getLayout();
		try {
			PropertiesWriter writer = config.getIOFactory().createPropertiesWriter(new FileWriter(filePath, false),
					config.getListDelimiterHandler());

			// Write Header Comment
			writer.writeln(layout.getHeaderComment());

			for (final String key : layout.getKeys()) {
				Matcher m = serverPropPattern.matcher(key);
				if (m.matches()) {
					String serverName = m.group(1);
					if (!serverConfig.containsKey(serverName)) {
						continue;
					}
				}

				// Output blank lines before property
				for (int i = 0; i < layout.getBlancLinesBefore(key); i++) {
					writer.writeln(null);
				}

				// Output the property and its value
				boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
				writer.setCurrentSeparator(layout.getSeparator(key));
				writer.writeProperty(key, config.getProperty(key), singleLine);
			}

			writer.writeln(layout.getCanonicalFooterCooment(true));
			writer.flush();

			log.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("[" + filePath + "] 파일 저장에 실패했습니다.");
			log.error(e.getMessage());
		}
	}

	@Override
	public void saveCommonConfig(Map<String, Object> values) {
		PropertiesConfiguration config = getConfiguration("commonConfig");
		for (String key : values.keySet()) {
			config.setProperty(key, values.get(key));
		}
		save("./config/common.properties", config);
	}
	
	@Override
	public void saveCommonConfig(String key, String value) {
		PropertiesConfiguration config = getConfiguration("commonConfig");
		config.setProperty(key, value);
		save("./config/common.properties", config);
	}

	private static PropertiesConfiguration load(String filePath) {
		Parameters param = new Parameters();
		PropertiesBuilderParameters propertyParameters = param.properties()
				.setListDelimiterHandler(new DefaultListDelimiterHandler(',')).setThrowExceptionOnMissing(false)
				.setFile(new File(filePath));

		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
				PropertiesConfiguration.class);
		builder.configure(propertyParameters);

		try {
			return builder.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * [/config/config_definition.xml] 파일을 읽어 CombinedConfiguration 객체를 초기화한다.
	 * 
	 * @param path
	 * @throws Exception
	 */
	@Override
	public void loadCombinedConfiguration() {
		Parameters params = new Parameters();

		CombinedConfigurationBuilder builder = new CombinedConfigurationBuilder();
		XMLBuilderParameters xmlParams = params.xml().setListDelimiterHandler(new DefaultListDelimiterHandler(','));
		XMLBuilderParameters definitionParams = params.xml().setFile(new File("./config/config_definition.xml"));
		CombinedBuilderParameters combinedParameters = params.combined()
				.setDefinitionBuilderParameters(definitionParams).setThrowExceptionOnMissing(false)
				.setListDelimiterHandler(new DefaultListDelimiterHandler(','))
				.registerChildDefaultsHandler(XMLBuilderProperties.class, new CopyObjectDefaultHandler(xmlParams));
		builder.configure(combinedParameters);
		try {
			combinedConfig = builder.getConfiguration();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 접속정보 프로퍼티 파일을 Load한다.
	 * 
	 * @throws ConfigurationException
	 */
	@Override
	public void loadConnectionInfoConfig(String filePath) {
		connInfoConfig = load(filePath);
	}

	/**
	 * 모니터링여부 프로퍼티 파일을 Load한다.
	 */
	@Override
	public void loadMonitoringInfoConfig(String filePath) {
		monitoringConfig = load(filePath);
	}

	@Override
	public String[] getConnectionInfoFileNames() {
		String connInfoDirPath = "./config/connectioninfo";
		String[] connInfoFileList = new File(connInfoDirPath).list();
		for (int i = 0; i < connInfoFileList.length; i++) {
			connInfoFileList[i] = connInfoDirPath + "/" + connInfoFileList[i];
		}
		return connInfoFileList;
	}

	/**
	 * commons.properties에서 값을 읽어 반환한다.
	 */
	@Override
	public String getCommonResource(String key) {
		return combinedConfig.getString(key);
	}

	/**
	 * commons.properties에서 값을 읽어 반환한다.
	 */
	@Override
	public int getIntegerCommonResource(String key) {
		return combinedConfig.getInt(key);
	}

	/**
	 * commons.properties에서 값을 읽어 반환한다.
	 */
	@Override
	public String[] getCommonResources(String key) {
		return combinedConfig.getStringArray(key);
	}

	/**
	 * DB에 연결하여 모니터링할 내용을 반환한다.
	 */
	@Override
	public String[] getDBMonitoringContents() {
		return combinedConfig.getStringArray("db.monitoring.contents");
	}

	/**
	 * Server에 연결하여 모니터링할 내용을 반환한다.
	 */
	@Override
	public String[] getServerMonitoringContents() {
		return combinedConfig.getStringArray("server.monitoring.contents");
	}

	/**
	 * Oracle Driver ComboBox의 값을 반환한다.
	 */
	@Override
	public String[] getOracleDrivers() {
		return combinedConfig.getStringArray("db.setting.oracle.driver.combo");
	}

	/**
	 * 최근 사용한 접속정보 파일명을 반환한다.
	 */
	@Override
	public String getLastUseConnInfoFilePath() {
		return combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property 파일에 작성된 Monitoring Preset리스트를 반환한다.
	 */
	@Override
	public List<String> getMonitoringPresetNameList() {
		List<String> presetList = new ArrayList<>();
		Configuration monitoringConfig = connInfoConfig.subset("monitoring.setting.preset");
		monitoringConfig.getKeys().forEachRemaining(s -> {
			if (!s.startsWith("lastuse")) {
				presetList.add(s.substring(0, s.indexOf(".")));
			}
		});
		return presetList;
	}

	/**
	 * ConnectionInfo Property 파일에 작성된 Monitoring Preset리스트를 반환한다.
	 */
	@Override
	public Map<String, String> getMonitoringPresetMap() {
		Map<String, String> presetMap = new LinkedHashMap<>();
		Configuration monitoringConfig = connInfoConfig.subset("monitoring.setting.preset");
		monitoringConfig.getKeys().forEachRemaining(s -> {
			if (!s.startsWith("lastuse")) {
				presetMap.put(s.substring(0, s.indexOf(".")), monitoringConfig.getString(s));
			}
		});
		return presetMap;
	}

	/**
	 * 최근 사용한 Monitoring Preset 이름을 반환한다. 단, 최근 사용한 Preset이 없을 때, NULL을 반환한다.
	 * 
	 * @return
	 */
	@Override
	public String getLastUseMonitoringPresetName() {
		return connInfoConfig.subset("monitoring.setting.preset.lastuse").getString("");
	}
	
	/**
	 * 최근 사용한 Monitoring Preset 이름을 반환한다. 단, 최근 사용한 Preset이 없을 때, NULL을 반환한다.
	 * 
	 * @return
	 */
	@Override
	public String getLastUseMonitoringPresetName(String filePath) {
		load(filePath);
		return connInfoConfig.subset("monitoring.setting.preset.lastuse").getString("");
	}

	/**
	 * 모니터링할 DB명 배열을 반환한다.
	 */
	@Override
	public String[] getMonitoringDBNames() {
		return connInfoConfig.getStringArray("dbnames");
	}

	/**
	 * 모니터링할 Server명 배열을 반환한다.
	 */
	@Override
	public String[] getMonitoringServerNames() {
		return connInfoConfig.getStringArray("servernames");
	}

	@Override
	public boolean isMonitoringContent(String toggleId) {
		return monitoringConfig.containsKey(toggleId) == false ? true : monitoringConfig.getBoolean(toggleId);
	}

	/**
	 * 지정된 경로에 새로운 파일을 생성한다.
	 * 
	 * @param filePath
	 */
	public void createNewPropertiesFile(String filePath, String type) {
		try {
			File newFile = new File(filePath);

			// 파일 및 디렉터리 생성
			new File(newFile.getParent()).mkdirs();
			FileUtils.touch(newFile);

			if (type.equals("ConnectionInfo")) {
				String connInfoConfigFileName = newFile.getName().substring(0,
						newFile.getName().indexOf(".properties"));

				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));

				// Default properties
				bw.append("monitoring.setting.preset.lastuse=").append("default").append("\n");
				bw.append("monitoring.setting.preset.default.filepath=").append("./config/monitoring/")
						.append(connInfoConfigFileName).append("/default.properties").append("\n");

				bw.append("dbnames=").append("\n");
				bw.append("servernames=").append("\n");

				bw.flush();
				bw.close();

			} else if (type.equals("Monitoring")) {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Properties 파일에서 DB별 JdbcConnectionInfo를 읽어와 객체를 생성
	 * 
	 * @param dbName
	 * @return
	 */
	@Override
	public JdbcConnectionInfo getJdbcConnectionInfo(String dbName) {
		String jdbcAlias = connInfoConfig.getString(dbName + ".jdbc.alias");
		String jdbcDriver = connInfoConfig.getString(dbName + ".jdbc.driver");
		String jdbcUrl = connInfoConfig.getString(dbName + ".jdbc.url");
		String jdbcId = connInfoConfig.getString(dbName + ".jdbc.id");
		String jdbcPw = connInfoConfig.getString(dbName + ".jdbc.pw");
		String jdbcValidation = connInfoConfig.getString(dbName + ".jdbc.validation");
		int jdbcConnections = connInfoConfig.getInt(dbName + ".jdbc.connections");
		return new JdbcConnectionInfo(jdbcAlias, jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation, jdbcConnections);
	}

	/**
	 * Properties 파일에서 Server별 JschConnectionInfo를 읽어와 객체를 생성
	 * 
	 * @param serverName
	 * @return
	 */
	@Override
	public JschConnectionInfo getJschConnectionInfo(String serverName) {
		String serverHost = connInfoConfig.getString(serverName + ".server.host");
		ServerOS serverOS = null;
		try {
			serverOS = ServerOS.valueOf(connInfoConfig.getString(serverName + ".server.os"));	
		} catch (Exception e) {
		}
		String serverPort = connInfoConfig.getString(serverName + ".server.port");
		String serverUserName = connInfoConfig.getString(serverName + ".server.username");
		String serverPassword = connInfoConfig.getString(serverName + ".server.password");
		AlertLogCommand alc = getAlertLogCommand(serverName);
		return new JschConnectionInfo(serverName, serverOS, serverHost, serverPort, serverUserName, serverPassword,
				alc);
	}

	/**
	 * 서버별 AlertLog 파일에 대한 정보를 반환한다.
	 * 
	 * @param serverName
	 * @return
	 */
	@Override
	public AlertLogCommand getAlertLogCommand(String serverName) {
		String alertLogFilePath = connInfoConfig.getString(serverName + ".server.alertlog.filepath");
		int alertLogReadLine = connInfoConfig.getInt(serverName + ".server.alertlog.readline");
		AlertLogCommand alc = new AlertLogCommand(alertLogReadLine, alertLogFilePath);
		return alc;
	}

	@Override
	public String getMonitoringConfigResource(String key) {
		return monitoringConfig.getString(key);
	}
}
