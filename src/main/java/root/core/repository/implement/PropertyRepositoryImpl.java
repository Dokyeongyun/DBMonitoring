package root.core.repository.implement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import com.mchange.v2.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.AlertLogCommand;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;

@Slf4j
public class PropertyRepositoryImpl implements PropertyRepository {

	// Private �ʵ�� ���� �� Singletone���� ����
	private static PropertyRepository propRepo = new PropertyRepositoryImpl();

	// �����ڸ� Private���� ���������ν� �ش� ��ü�� ������ �� �ִ� ����� ���ֹ��� => �������� Singletone �������
	private PropertyRepositoryImpl() {
		loadCombinedConfiguration();
	}

	// propertyService Field�� ������ �� �ִ� ������ ��� (Static Factory Pattern)
	public static PropertyRepository getInstance() {
		return propRepo;
	}

	private PropertiesConfiguration connInfoConfig; // �������� ���� Configuration
	private PropertiesConfiguration monitoringConfig; // ����͸����� Configuration
	private CombinedConfiguration combinedConfig; // ���� Configuration

	private static Pattern dbPropPattern = Pattern.compile("(.*).jdbc.(.*)");
	private static Pattern serverPropPattern = Pattern.compile("(.*).server.(.*)");

	/****************************************************************************/

	@Override
	public boolean isFileExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * Configuration ��ü�� ��ȯ�Ѵ�. TODO ���� �޼��带 Wrapping �ؼ� ȣ���� �ʿ䰡 ������..? Controller��
	 * ������ ���Ÿ������� �ϴ� �̷��� ��..
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
	 * �־��� ��ο� PropertyConfiguration�� ������ Key-Value�� �����Ѵ�.
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

			log.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
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

			log.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
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
			config.setProperty(serverName + ".server.servername", jsch.getServerName());
			config.setProperty(serverName + ".server.host", jsch.getHost());
			config.setProperty(serverName + ".server.port", jsch.getPort());
			config.setProperty(serverName + ".server.username", jsch.getUserName());
			config.setProperty(serverName + ".server.password", jsch.getPassword());

			String dateFormat = jsch.getAlc().getDateFormat();
			String dateFormatRegex = "";

			if (dateFormat.equals("EEE MMM dd HH:mm:ss yyyy")) {
				dateFormatRegex = "...\\s...\\s([0-2][0-9]|1[012])\\s\\d\\d:\\d\\d:\\d\\d\\s\\d{4}";
			} else if (dateFormat.equals("yyyy-MM-dd")) {
				dateFormatRegex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T";
			}

			config.setProperty(serverName + ".server.alertlog.dateformat", dateFormat);
			config.setProperty(serverName + ".server.alertlog.dateformatregex", dateFormatRegex);
			config.setProperty(serverName + ".server.alertlog.filepath", jsch.getAlc().getReadFilePath());
			config.setProperty(serverName + ".server.alertlog.readLine", 500);
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

			log.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
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
	 * [/config/config_definition.xml] ������ �о� CombinedConfiguration ��ü�� �ʱ�ȭ�Ѵ�.
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
	 * �������� ������Ƽ ������ Load�Ѵ�.
	 * 
	 * @throws ConfigurationException
	 */
	@Override
	public void loadConnectionInfoConfig(String filePath) {
		connInfoConfig = load(filePath);
	}

	/**
	 * ����͸����� ������Ƽ ������ Load�Ѵ�.
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
	 * commons.properties���� ���� �о� ��ȯ�Ѵ�.
	 */
	@Override
	public String getCommonResource(String key) {
		return combinedConfig.getString(key);
	}

	/**
	 * commons.properties���� ���� �о� ��ȯ�Ѵ�.
	 */
	@Override
	public int getIntegerCommonResource(String key) {
		return combinedConfig.getInt(key);
	}

	/**
	 * commons.properties���� ���� �о� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getCommonResources(String key) {
		return combinedConfig.getStringArray(key);
	}

	/**
	 * DB�� �����Ͽ� ����͸��� ������ ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getDBMonitoringContents() {
		return combinedConfig.getStringArray("db.monitoring.contents");
	}

	/**
	 * Server�� �����Ͽ� ����͸��� ������ ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getServerMonitoringContents() {
		return combinedConfig.getStringArray("server.monitoring.contents");
	}

	/**
	 * Oracle Driver ComboBox�� ���� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getOracleDrivers() {
		return combinedConfig.getStringArray("db.setting.oracle.driver.combo");
	}

	/**
	 * �ֱ� ����� �������� ���ϸ��� ��ȯ�Ѵ�.
	 */
	@Override
	public String getLastUseConnInfoFilePath() {
		return combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property ���Ͽ� �ۼ��� Monitoring Preset����Ʈ�� ��ȯ�Ѵ�.
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
	 * ConnectionInfo Property ���Ͽ� �ۼ��� Monitoring Preset����Ʈ�� ��ȯ�Ѵ�.
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
	 * �ֱ� ����� Monitoring Preset �̸��� ��ȯ�Ѵ�. ��, �ֱ� ����� Preset�� ���� ��, NULL�� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	@Override
	public String getLastUseMonitoringPresetName() {
		return connInfoConfig.subset("monitoring.setting.preset.lastuse").getString("");
	}

	/**
	 * ����͸��� DB�� �迭�� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getMonitoringDBNames() {
		return connInfoConfig.getStringArray("dbnames");
	}

	/**
	 * ����͸��� Server�� �迭�� ��ȯ�Ѵ�.
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
	 * ������ ��ο� ���ο� ������ �����Ѵ�.
	 * 
	 * @param filePath
	 */
	public void createNewPropertiesFile(String filePath, String type) {
		try {
			File newFile = new File(filePath);

			// ���� �� ���͸� ����
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
	 * Properties ���Ͽ��� ����͸��� DB���� �о�� ��, �� DB�� JDBC Connection ���� ������ ��ü ����
	 * 
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	@Deprecated
	@Override
	public List<JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = connInfoConfig.getStringArray("dbnames");
		if (dbNames == null || dbNames.length == 0) {
			return new ArrayList<>();
		}
		List<JdbcConnectionInfo> jdbcList = new ArrayList<>();
		for (String dbName : dbNames)
			jdbcList.add(getJdbcConnectionInfo(dbName));
		Collections.sort(jdbcList, (o1, o2) -> o1.getJdbcDBName().compareTo(o2.getJdbcDBName()) < 0 ? -1 : 1);
		return jdbcList;
	}

	/**
	 * Properties ���Ͽ��� DB�� JdbcConnectionInfo�� �о�� ��ü�� ����
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
	 * Properties ���Ͽ��� ����͸��� Server���� �о�� ��, �� DB�� JSchConnection ���� ������ ��ü ����
	 * 
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� Server Name ������ ������ ����Ʈ
	 */
	@Deprecated
	@Override
	public List<JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		if (serverNames == null || serverNames.length == 0) {
			return new ArrayList<>();
		}
		List<JschConnectionInfo> jschList = new ArrayList<>();
		for (String serverName : serverNames)
			jschList.add(getJschConnectionInfo(serverName));
		Collections.sort(jschList, (o1, o2) -> o1.getServerName().compareTo(o2.getServerName()) < 0 ? -1 : 1);
		return jschList;
	}

	/**
	 * Properties ���Ͽ��� Server�� JschConnectionInfo�� �о�� ��ü�� ����
	 * 
	 * @param serverName
	 * @return
	 */
	@Override
	public JschConnectionInfo getJschConnectionInfo(String serverName) {
		String serverHost = connInfoConfig.getString(serverName + ".server.host");
		String serverPort = connInfoConfig.getString(serverName + ".server.port");
		String serverUserName = connInfoConfig.getString(serverName + ".server.username");
		String serverPassword = connInfoConfig.getString(serverName + ".server.password");
		AlertLogCommand alc = getAlertLogCommand(serverName);
		return new JschConnectionInfo(serverName.toUpperCase(), serverHost, serverPort, serverUserName, serverPassword,
				alc);
	}

	/**
	 * ������ AlertLog ���Ͽ� ���� ������ ��ȯ�Ѵ�.
	 * 
	 * @param serverName
	 * @return
	 */
	@Override
	public AlertLogCommand getAlertLogCommand(String serverName) {
		String alertLogFilePath = connInfoConfig.getString(serverName + ".server.alertlog.filepath");
		String alertLogReadLine = connInfoConfig.getString(serverName + ".server.alertlog.readline");
		String alertLogDateFormat = connInfoConfig.getString(serverName + ".server.alertlog.dateformat");
		String alertLogDateFormatRegex = connInfoConfig.getString(serverName + ".server.alertlog.dateformatregex");
		AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat,
				alertLogDateFormatRegex);
		return alc;
	}

	/**
	 * Properties ���Ͽ��� ����͸��� �������� �о�� ��, �� ������ AlertLogCommand ��ü�� �����Ѵ�.
	 * 
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	@Deprecated
	@Override
	public Map<String, AlertLogCommand> getAlertLogCommandMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		Map<String, AlertLogCommand> alcMap = new HashMap<>();
		for (String serverName : serverNames)
			alcMap.put(serverName, getAlertLogCommand(serverName));
		return alcMap;
	}
}
