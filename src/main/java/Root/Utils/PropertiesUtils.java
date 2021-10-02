package Root.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;

public class PropertiesUtils {
	
	public static PropertiesConfiguration propConfig = null;

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

}
