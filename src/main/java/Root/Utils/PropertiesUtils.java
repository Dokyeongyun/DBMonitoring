package Root.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;

public class PropertiesUtils {
	
	public static PropertiesConfiguration propConfig = new PropertiesConfiguration();
	public static CombinedConfiguration combinedConfig = null;
	public static String configurationPath;

	public static void loadAppConfiguration(String path) throws Exception{
		Parameters params = new Parameters();
		CombinedConfigurationBuilder builder = new CombinedConfigurationBuilder()
				.configure(params.fileBased().setFile((new File("config\\config_definition.xml"))));
		combinedConfig = builder.getConfiguration();
		
		List<Configuration> configList = combinedConfig.getConfigurations();
		
		for(Configuration c : configList) {
			Iterator<String> iter = c.getKeys();
			while(iter.hasNext()){ 
				String key = iter.next();
				Object value = c.getProperty(key);
				propConfig.setProperty(key, value);
			}
		}
	}
	
	public static PropertiesConfiguration getConfig(String name) {
		return (PropertiesConfiguration) combinedConfig.getConfiguration(name);
	}
	
	/**
	 * Properties 파일에서 모니터링할 Server명을 읽어온 후, 각 DB별 JSchConnection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 후 Server Name 순으로 정렬한 리스트
	 */
	public static List<JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = propConfig.getString("servernames").split("/");
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
		String serverHost = propConfig.getString(serverName + ".server.host");
		int serverPort = propConfig.getInt(serverName + ".server.port");
		String serverUserName = propConfig.getString(serverName + ".server.username");
		String serverPassword = propConfig.getString(serverName + ".server.password");
		return new JschConnectionInfo(serverName.toUpperCase(), serverHost, serverPort, serverUserName, serverPassword);
	}
	
	/**
	 * Properties 파일에서 모니터링할 DB명을 읽어온 후, 각 DB별 JDBC Connection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 후 DB Name 순으로 정렬한 리스트
	 */
	public static List<JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = propConfig.getString("dbnames").split("/");
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
		String jdbcDriver = propConfig.getString(dbName + ".jdbc.driver");
		String jdbcUrl = propConfig.getString(dbName + ".jdbc.url");
		String jdbcId = propConfig.getString(dbName + ".jdbc.id");
		String jdbcPw = propConfig.getString(dbName + ".jdbc.pw");
		String jdbcValidataion = propConfig.getString(dbName + ".jdbc.validation");
		int erpConnections = propConfig.getInt(dbName + ".jdbc.connections");
		return new JdbcConnectionInfo(dbName.toUpperCase(), jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidataion, erpConnections);
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
