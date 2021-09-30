package Root.Application;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

import com.jcraft.jsch.Session;

import Root.Batch.DBCheckBatch;
import Root.Batch.ServerCheckBatch;
import Root.Database.DatabaseUtil;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;
import Root.RemoteServer.JschUtil;
import Root.Repository.DBCheckRepository;
import Root.Repository.DBCheckRepositoryImpl;
import Root.Repository.ServerCheckRepository;
import Root.Repository.ServerCheckRepositoryImpl;
import Root.Usecases.DBCheckUsecase;
import Root.Usecases.DBCheckUsecaseImpl;
import Root.Usecases.ServerCheckUsecase;
import Root.Usecases.ServerCheckUsecaseImpl;

public class Application {

	public static PropertiesConfiguration propConfig = null;

	public static void main(String[] args) {
		
    	try {    		
    		String propertyFilePathName = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\config\\application.properties";
    		
    		try {
    			propConfig = loadAppConfiguration(propertyFilePathName);
    		}catch(Exception e) {
    			System.out.println("configuration loading error\n"+e+"\n");
    			return;
    		}
    		
    		// DB Usage Check   		
//    		Map<String, JdbcConnectionInfo> jdbcConnectionMap = getJdbcConnectionMap();
//    		for(String key : jdbcConnectionMap.keySet()) {
//    			System.out.println("■ [ " + key + " Monitoring Start ]\n");
//    			DatabaseUtil db = new DatabaseUtil(jdbcConnectionMap.get(key));
//    			db.init();
//        		DBCheckRepository repo = new DBCheckRepositoryImpl(db);
//        		DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
//        		DBCheckBatch dbBatch = new DBCheckBatch(usecase);
//    			//dbBatch.startBatchArchiveUsageCheck();
//    			//dbBatch.startBatchTableSpaceUsageCheck();
//    			//dbBatch.startBatchASMDiskUsageCheck();
//    			//System.out.println("■ [ " + key + " Monitoring End ]\n\n");
//    		} 
    		
    		// Server Usage Check   		
    		Map<String, JschConnectionInfo> jschConnectionMap = getJschConnectionMap();
    		for(String key : jschConnectionMap.keySet()) {
    			System.out.println("■ [ " + key + " Monitoring Start ]\n");
    			JschUtil server = new JschUtil(jschConnectionMap.get(key));
    			server.init();
        		ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
        		ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
        		ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);
    			serverBatch.startBatchAlertLogCheck();
    			//dbBatch.startBatchTableSpaceUsageCheck();
    			//dbBatch.startBatchASMDiskUsageCheck();
    			//System.out.println("■ [ " + key + " Monitoring End ]\n\n");
    		} 
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("END");
	}
	
	/**
	 * Properties 파일에서 모니터링할 Server명을 읽어온 후, 각 DB별 JSchConnection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 Map 객체
	 */
	public static Map<String, JschConnectionInfo> getJschConnectionMap() {
		String[] serverNames = propConfig.getString("servernames").split("/");
		Map<String, JschConnectionInfo> jschMap = new HashMap<>();
		for(String serverName : serverNames) jschMap.put(serverName, getJschConnectionInfo(serverName));	
		return jschMap;
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
		return new JschConnectionInfo(serverHost, serverPort, serverUserName, serverPassword);
	}
	
	/**
	 * Properties 파일에서 모니터링할 DB명을 읽어온 후, 각 DB별 JDBC Connection 정보 가지는 객체 생성
	 * @return 각 DB별 JdbcConnectionInfo 객체를 담은 Map 객체
	 */
	public static Map<String, JdbcConnectionInfo> getJdbcConnectionMap() {
		String[] dbNames = propConfig.getString("dbnames").split("/");
		Map<String, JdbcConnectionInfo> jdbcMap = new HashMap<>();
		for(String dbName : dbNames) jdbcMap.put(dbName, getJdbcConnectionInfo(dbName));	
		return jdbcMap;
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
		return new JdbcConnectionInfo(jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidataion, erpConnections);
	}

	public static PropertiesConfiguration loadAppConfiguration(String path) throws Exception{
		File file = new File(path);
		ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(',');
		
		PropertiesBuilderParameters propertyParameters = new Parameters().properties();
		propertyParameters.setFile(file);
		propertyParameters.setThrowExceptionOnMissing(true);
		propertyParameters.setListDelimiterHandler(delimiter);
		
		FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
		builder.configure(propertyParameters);
		
		return builder.getConfiguration();
	}
}
