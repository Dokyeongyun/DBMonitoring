package Root.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.CopyObjectDefaultHandler;
import org.apache.commons.configuration2.builder.XMLBuilderProperties;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.CombinedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import Root.Model.AlertLogCommand;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;

public class PropertiesUtils {
	
	public static PropertiesConfiguration propConfig = new PropertiesConfiguration();
	public static CombinedConfiguration combinedConfig = null;
	public static String configurationPath;

	public static void loadAppConfiguration(String path) throws Exception{
		Parameters params = new Parameters();
		
		CombinedConfigurationBuilder builder = new CombinedConfigurationBuilder();
		XMLBuilderParameters xmlParams = params.xml().setListDelimiterHandler(new DefaultListDelimiterHandler(','));
		XMLBuilderParameters definitionParams = params.xml().setFile(new File("./config/config_definition.xml"));
		CombinedBuilderParameters combinedParameters = params.combined()
			    .setDefinitionBuilderParameters(definitionParams)
			    .registerChildDefaultsHandler(XMLBuilderProperties.class, new CopyObjectDefaultHandler(xmlParams));
		builder.configure(combinedParameters);
		combinedConfig = builder.getConfiguration();
		
		List<Configuration> configList = combinedConfig.getConfigurations();
		
		for(Configuration c : configList) {
			Iterator<String> iter = c.getKeys();
			while(iter.hasNext()){ 
				String key = iter.next();
				String value = c.getString(key);
				String[] values = value.split(",");
				propConfig.setProperty(key, values);	
			}
		}
	}
	
	public static PropertiesConfiguration getConfig(String name) {
		return (PropertiesConfiguration) combinedConfig.getConfiguration(name);
	}
	
	/**
	 * Properties ���Ͽ��� ����͸��� Server���� �о�� ��, �� DB�� JSchConnection ���� ������ ��ü ����
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� Server Name ������ ������ ����Ʈ
	 */
	public static List<JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = propConfig.getString("servernames").split("/");
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
		String serverHost = propConfig.getString(serverName + ".server.host");
		int serverPort = propConfig.getInt(serverName + ".server.port");
		String serverUserName = propConfig.getString(serverName + ".server.username");
		String serverPassword = propConfig.getString(serverName + ".server.password");
		return new JschConnectionInfo(serverName.toUpperCase(), serverHost, serverPort, serverUserName, serverPassword);
	}
	
	/**
	 * Properties ���Ͽ��� ����͸��� DB���� �о�� ��, �� DB�� JDBC Connection ���� ������ ��ü ����
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	public static List<JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = propConfig.getString("dbnames").split("/");
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
		String jdbcDriver = propConfig.getString(dbName + ".jdbc.driver");
		String jdbcUrl = propConfig.getString(dbName + ".jdbc.url");
		String jdbcId = propConfig.getString(dbName + ".jdbc.id");
		String jdbcPw = propConfig.getString(dbName + ".jdbc.pw");
		String jdbcValidataion = propConfig.getString(dbName + ".jdbc.validation");
		int erpConnections = propConfig.getInt(dbName + ".jdbc.connections");
		return new JdbcConnectionInfo(dbName.toUpperCase(), jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidataion, erpConnections);
	}
	
	/**
	 * Properties ���Ͽ��� ����͸��� �������� �о�� ��, �� ������ AlertLogCommand ��ü�� �����Ѵ�.
	 * @return �� DB�� JdbcConnectionInfo ��ü�� ���� �� DB Name ������ ������ ����Ʈ
	 */
	public static Map<String, AlertLogCommand> getAlertLogCommandMap() {
		String[] serverNames = propConfig.getString("servernames").split("/");
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
		String alertLogFilePath = PropertiesUtils.propConfig.getString(serverName.toLowerCase() + ".server.alertlog.filepath");
		String alertLogReadLine = PropertiesUtils.propConfig.getString(serverName.toLowerCase() + ".server.alertlog.readline");
		String alertLogDateFormat = PropertiesUtils.propConfig.getString(serverName.toLowerCase() + ".server.alertlog.dateformat");
		String alertLogDateFormatRegex = PropertiesUtils.propConfig.getString(serverName.toLowerCase() + ".server.alertlog.dateformatregex");
		AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
		return alc;
	}

	public static void save() {
		try {
			propConfig.write(new FileWriter(configurationPath, false));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
