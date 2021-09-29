package Root.Database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

import Root.Batch.DBCheckBatch;
import Root.Model.JdbcConnectionInfo;
import Root.Repository.DBCheckRepository;
import Root.Repository.DBCheckRepositoryImpl;
import Root.Usecases.DBCheckUsecase;
import Root.Usecases.DBCheckUsecaseImpl;

public class Main {

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
   		
    		Map<String, JdbcConnectionInfo> jdbcConnectionMap = getJdbcConnectionMap();
    		
    		for(String key : jdbcConnectionMap.keySet()) {
    			System.out.println("бс [ " + key + " Monitoring Start ]\n");
    			DatabaseUtil db = new DatabaseUtil(jdbcConnectionMap.get(key));
    			db.init();
        		DBCheckRepository repo = new DBCheckRepositoryImpl(db);
        		DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
        		DBCheckBatch dbBatch = new DBCheckBatch(usecase);

    			dbBatch.startBatchArchiveUsageCheck();
    			//dbBatch.startBatchTableSpaceUsageCheck();
    			//dbBatch.startBatchASMDiskUsageCheck();
    			//System.out.println("бс [ " + key + " Monitoring End ]\n\n");
    		} 
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public static Map<String, JdbcConnectionInfo> getJdbcConnectionMap() {
		Map<String, JdbcConnectionInfo> jdbcMap = new HashMap<>();
		
		// ERP
		String erpJdbcDriver = propConfig.getString("erp.jdbc.driver");
		String erpJdbcUrl = propConfig.getString("erp.jdbc.url");
		String erpJdbcId = propConfig.getString("erp.jdbc.id");
		String erpJdbcPw = propConfig.getString("erp.jdbc.pw");
		String erpJdbcValidataion = propConfig.getString("erp.jdbc.validation");
		String erpConnections = propConfig.getString("erp.jdbc.connections");
		jdbcMap.put("ERP", new JdbcConnectionInfo(erpJdbcDriver, erpJdbcUrl, erpJdbcId, erpJdbcPw, erpJdbcValidataion, erpConnections));
		
		// POSSALE
		String possaleJdbcDriver = propConfig.getString("possale.jdbc.driver");
		String possaleJdbcUrl = propConfig.getString("possale.jdbc.url");
		String possaleJdbcId = propConfig.getString("possale.jdbc.id");
		String possaleJdbcPw = propConfig.getString("possale.jdbc.pw");
		String possaleJdbcValidataion = propConfig.getString("possale.jdbc.validation");
		String possaleConnections = propConfig.getString("possale.jdbc.connections");
		jdbcMap.put("POSSALE", new JdbcConnectionInfo(possaleJdbcDriver, possaleJdbcUrl, possaleJdbcId, possaleJdbcPw, possaleJdbcValidataion, possaleConnections));
		
		// GPOSSALE
		String gPossaleJdbcDriver = propConfig.getString("gPossale.jdbc.driver");
		String gPossaleJdbcUrl = propConfig.getString("gPossale.jdbc.url");
		String gPossaleJdbcId = propConfig.getString("gPossale.jdbc.id");
		String gPossaleJdbcPw = propConfig.getString("gPossale.jdbc.pw");
		String gPossaleJdbcValidataion = propConfig.getString("gPossale.jdbc.validation");
		String gPossaleConnections = propConfig.getString("gPossale.jdbc.connections");
		jdbcMap.put("GPOSSALE", new JdbcConnectionInfo(gPossaleJdbcDriver, gPossaleJdbcUrl, gPossaleJdbcId, gPossaleJdbcPw, gPossaleJdbcValidataion, gPossaleConnections));

		return jdbcMap;
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
