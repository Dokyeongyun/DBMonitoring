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

	public static PropertiesConfiguration propConfig = null;		// DB, Server 접속정보 Configuration
	public static PropertiesConfiguration connInfoConfig = null; 	// DB, Server 접속정보 Configuration
	public static PropertiesConfiguration monitoringConfig = null; 	// 모니터링여부 Configuration
	public static CombinedConfiguration combinedConfig = null;		// 공통 Configuration

	/**
	 * [/config/config_definition.xml] 파일을 읽어 CombinedConfiguration 객체를 초기화한다.
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
	 * 매개변수로 주어진 경로에 저장된 설정파일을 읽어 [propConfig] PropertiesConfiguration 객체를 초기화한다.
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
	 * 매개변수로 주어진 경로에 저장된 설정파일을 읽어 configName과 동일한 이름의 PropertiesConfiguration 객체를 초기화한다.
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
	 * CombinedConfiguration 객체에서 특정 Configuration 객체를 가져온다.
	 * @param name
	 * @return
	 */
	public static PropertiesConfiguration getConfig(String name) {
		return (PropertiesConfiguration) combinedConfig.getConfiguration(name);
	}
	
	/**
	 * Properties 파일에서 모니터링할 Server명을 읽어온 후, 각 DB별 JSchConnection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 후 Server Name 순으로 정렬한 리스트
	 */
	public static List<JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		List<JschConnectionInfo> jschList = new ArrayList<>();
		for(String serverName : serverNames) jschList.add(getJschConnectionInfo(serverName));
		Collections.sort(jschList, (o1, o2) -> o1.getServerName().compareTo(o2.getServerName()) < 0 ?  -1 : 1);
		return jschList;
	}
	
	/**
	 * Properties 파일에서 Server별 JschConnectionInfo를 읽어와 객체를 생성 
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
	 * Properties 파일에서 모니터링할 DB명을 읽어온 후, 각 DB별 JDBC Connection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 후 DB Name 순으로 정렬한 리스트
	 */
	public static List<JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = connInfoConfig.getStringArray("dbnames");
		List<JdbcConnectionInfo> jdbcList = new ArrayList<>();
		for(String dbName : dbNames) jdbcList.add(getJdbcConnectionInfo(dbName));
		Collections.sort(jdbcList, (o1, o2) -> o1.getJdbcDBName().compareTo(o2.getJdbcDBName()) < 0 ?  -1 : 1);
		return jdbcList;
	}
	
	/**
	 * Properties 파일에서 DB별 JdbcConnectionInfo를 읽어와 객체를 생성 
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
	 * Properties 파일에서 모니터링할 서버명을 읽어온 후, 각 서버별 AlertLogCommand 객체를 생성한다.
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 후 DB Name 순으로 정렬한 리스트
	 */
	public static Map<String, AlertLogCommand> getAlertLogCommandMap() {
		String[] serverNames = connInfoConfig.getStringArray("servernames");
		Map<String, AlertLogCommand> alcMap = new HashMap<>();
		for(String serverName : serverNames) alcMap.put(serverName, getAlertLogCommand(serverName));
		return alcMap;
	}
	
	/**
	 * 서버별 AlertLog 파일에 대한 정보를 반환한다.
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
	 * 주석을 포함하여 현재 Configuration 상태에 따라 프로퍼티파일을 재작성한다.
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
	        
	        logger.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
        	e.printStackTrace();
	        logger.info("[" + filePath + "] 파일 저장에 실패했습니다.");
        } 
	}
}
