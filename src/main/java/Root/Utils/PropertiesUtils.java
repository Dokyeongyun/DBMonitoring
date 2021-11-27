package Root.Utils;

import java.io.File;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
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
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.log4j.Logger;

import JavaFx.Controller.MainNewController;
import Root.Model.AlertLogCommand;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;

public class PropertiesUtils {
	private static Logger logger = Logger.getLogger(MainNewController.class);

	public static PropertiesConfiguration propConfig = null;		// DB, Server �������� Configuration
	public static PropertiesConfiguration connInfoConfig = null; 	// DB, Server �������� Configuration
	public static PropertiesConfiguration monitoringConfig = null; 	// ����͸����� Configuration
	public static CombinedConfiguration combinedConfig = null;		// ���� Configuration

	/**
	 * [/config/config_definition.xml] ������ �о� CombinedConfiguration ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param path
	 * @throws Exception
	 */
	public static void loadCombinedConfiguration() throws Exception{
		Parameters params = new Parameters();
		
		CombinedConfigurationBuilder builder = new CombinedConfigurationBuilder();
		XMLBuilderParameters xmlParams = params.xml().setListDelimiterHandler(new DefaultListDelimiterHandler(','));
		XMLBuilderParameters definitionParams = params.xml().setFile(new File("./config/config_definition.xml"));
		CombinedBuilderParameters combinedParameters = params.combined()
			    .setDefinitionBuilderParameters(definitionParams)
			    .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
			    .registerChildDefaultsHandler(XMLBuilderProperties.class, new CopyObjectDefaultHandler(xmlParams));
		builder.configure(combinedParameters);
		combinedConfig = builder.getConfiguration();
	}
	
	/**
	 * �Ű������� �־��� ��ο� ����� ���������� �о� [propConfig] PropertiesConfiguration ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param path
	 * @throws Exception
	 */
	public static void loadAppConfiguration(String path) throws Exception{
		File file = new File(path);
		ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(',');
		
		PropertiesBuilderParameters propertyParameters = new Parameters().properties();
		propertyParameters.setFile(file);
		propertyParameters.setThrowExceptionOnMissing(true);
		propertyParameters.setListDelimiterHandler(delimiter);
		
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
		builder.configure(propertyParameters);

		propConfig = builder.getConfiguration();
	}
	
	/**
	 * �Ű������� �־��� ��ο� ����� ���������� �о� configName�� ������ �̸��� PropertiesConfiguration ��ü�� �ʱ�ȭ�Ѵ�.
	 * @param path
	 * @param configName
	 * @throws Exception
	 */
	public static void loadAppConfiguration(String path, String configName) throws Exception{
		Parameters param = new Parameters();
		PropertiesBuilderParameters propertyParameters = param.properties()
				.setListDelimiterHandler(new DefaultListDelimiterHandler(','))
				.setThrowExceptionOnMissing(false)
				.setFile(new File(path));
		
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
		builder.configure(propertyParameters);

		if(configName.equals("connInfoConfig")) {
			connInfoConfig = builder.getConfiguration();	
		} else if (configName.equals("monitoringConfig")) {
			monitoringConfig = builder.getConfiguration();	
		}
	}
	
	/**
	 * CombinedConfiguration ��ü���� Ư�� Configuration ��ü�� �����´�.
	 * @param name
	 * @return
	 */
	public static PropertiesConfiguration getConfig(String name) {
		return (PropertiesConfiguration) combinedConfig.getConfiguration(name);
	}
	
	/**
	 * Properties ���Ͽ��� ����͸��� Server���� �о�� ��, �� DB�� JSchConnection ���� ������ ��ü ����
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� Server Name ������ ������ ����Ʈ
	 */
	public static List<JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		List<JschConnectionInfo> jschList = new ArrayList<>();
		for(String serverName : serverNames) jschList.add(getJschConnectionInfo(serverName));
		Collections.sort(jschList, (o1, o2) -> o1.getServerName().compareTo(o2.getServerName()) < 0 ?  -1 : 1);
		return jschList;
	}
	
	/**
	 * Properties ���Ͽ��� Server�� JschConnectionInfo�� �о�� ��ü�� ���� 
	 * @param serverName
	 * @return
	 */
	public static JschConnectionInfo getJschConnectionInfo(String serverName) {
		serverName = serverName.toLowerCase();
		String serverHost = connInfoConfig.getString(serverName + ".server.host");
		int serverPort = connInfoConfig.getInt(serverName + ".server.port");
		String serverUserName = connInfoConfig.getString(serverName + ".server.username");
		String serverPassword = connInfoConfig.getString(serverName + ".server.password");
		return new JschConnectionInfo(serverName.toUpperCase(), serverHost, serverPort, serverUserName, serverPassword);
	}
	
	/**
	 * Properties ���Ͽ��� ����͸��� DB���� �о�� ��, �� DB�� JDBC Connection ���� ������ ��ü ����
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	public static List<JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = connInfoConfig.getStringArray("dbnames");
		List<JdbcConnectionInfo> jdbcList = new ArrayList<>();
		for(String dbName : dbNames) jdbcList.add(getJdbcConnectionInfo(dbName));
		Collections.sort(jdbcList, (o1, o2) -> o1.getJdbcDBName().compareTo(o2.getJdbcDBName()) < 0 ?  -1 : 1);
		return jdbcList;
	}
	
	/**
	 * Properties ���Ͽ��� DB�� JdbcConnectionInfo�� �о�� ��ü�� ���� 
	 * @param dbName
	 * @return
	 */
	public static JdbcConnectionInfo getJdbcConnectionInfo(String dbName) {
		dbName = dbName.toLowerCase();
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
	 * Properties ���Ͽ��� ����͸��� �������� �о�� ��, �� ������ AlertLogCommand ��ü�� �����Ѵ�.
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	public static Map<String, AlertLogCommand> getAlertLogCommandMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		Map<String, AlertLogCommand> alcMap = new HashMap<>();
		for(String serverName : serverNames) alcMap.put(serverName, getAlertLogCommand(serverName));
		return alcMap;
	}
	
	/**
	 * ������ AlertLog ���Ͽ� ���� ������ ��ȯ�Ѵ�.
	 * @param serverName
	 * @return
	 */
	public static AlertLogCommand getAlertLogCommand(String serverName) {
		String alertLogFilePath = connInfoConfig.getString(serverName.toLowerCase() + ".server.alertlog.filepath");
		String alertLogReadLine = connInfoConfig.getString(serverName.toLowerCase() + ".server.alertlog.readline");
		String alertLogDateFormat = connInfoConfig.getString(serverName.toLowerCase() + ".server.alertlog.dateformat");
		String alertLogDateFormatRegex = connInfoConfig.getString(serverName.toLowerCase() + ".server.alertlog.dateformatregex");
		AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
		return alc;
	}

	public static void save() {
//		try {
//			propConfig.write(new FileWriter(configurationPath, false));
//		} catch (ConfigurationException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * �ּ��� �����Ͽ� ���� Configuration ���¿� ���� ������Ƽ������ ���ۼ��Ѵ�.
	 * @param filePath
	 * @param config
	 */
	public static void save(String filePath, PropertiesConfiguration config) {
    	PropertiesConfigurationLayout layout = config.getLayout();
        try {
        	final PropertiesConfiguration.PropertiesWriter writer = 
        			config.getIOFactory().createPropertiesWriter(new FileWriter(filePath, false), config.getListDelimiterHandler());
        	
        	// Write Header Comment;
	        writer.writeln(layout.getHeaderComment());
	        
	        for (final String key : layout.getKeys()) {
	            // Output blank lines before property
	        	for(int i=0; i < layout.getBlancLinesBefore(key); i++) {
	        		writer.writeln(null);
	        	}

	            // Output the comment
	        	if(layout.getComment(key) != null) {
		        	writer.writeln(layout.getComment(key));	        		
	        	}
	
	            // Output the property and its value
	            final boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
	            writer.setCurrentSeparator(layout.getSeparator(key));
	            writer.writeProperty(key, config.getProperty(key), singleLine);
	        }
	
	        writer.writeln(layout.getCanonicalFooterCooment(true));
	        writer.flush();
	        
	        logger.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
        } catch (Exception e) {
        	e.printStackTrace();
	        logger.info("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
        } 
	}
}
