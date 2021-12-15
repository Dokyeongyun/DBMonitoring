package Root.Application;

import java.util.List;

import Root.Batch.DBCheckBatch;
import Root.Batch.ServerCheckBatch;
import Root.Database.DatabaseUtil;
import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;
import Root.RemoteServer.JschUtil;
import Root.Repository.DBCheckRepository;
import Root.Repository.ReportRepository;
import Root.Repository.ServerCheckRepository;
import Root.RepositoryImpl.DBCheckRepositoryImpl;
import Root.RepositoryImpl.ReportRepositoryImpl;
import Root.RepositoryImpl.ServerCheckRepositoryImpl;
import Root.Usecase.DBCheckUsecase;
import Root.Usecase.ServerCheckUsecase;
import Root.UsecaseImpl.DBCheckUsecaseImpl;
import Root.UsecaseImpl.ServerCheckUsecaseImpl;
import Root.Utils.ConsoleUtils;
import Root.Utils.DateUtils;
import Root.Utils.PropertiesUtils;

public class Application {

	public static void main(String[] args) {
    	try {    		
    		String propertiesFilePath = ".\\config\\application.properties";
    		
    		try {
    			PropertiesUtils.loadCombinedConfiguration();
    			String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
    			PropertiesUtils.loadAppConfiguration(lastUsePropertiesFile, "connInfoConfig");
    			PropertiesUtils.loadAppConfiguration(propertiesFilePath);
    			PropertiesUtils.loadCombinedConfiguration();
    			PropertiesUtils.loadAppConfiguration(".\\config\\connectioninfo\\connection.properties", "connInfoConfig");
    		}catch(Exception e) {
    			System.out.println("configuration loading error\n"+e+"\n");
    			return;
    		}
    		
    		String dbMonitoring = PropertiesUtils.propConfig.getString("monitoring.db");
    		String serverMonitoring = PropertiesUtils.propConfig.getString("monitoring.server");
    		
    		if("on".equals(dbMonitoring)) {
    			dbMonitoring();	
    		}

    		if("on".equals(serverMonitoring)) {
    			serverMonitoring();
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace(); 
    	}
    	
    	System.out.println("END");
	}
	
	public static void dbMonitoring() {
		System.out.println("\n==================================================================================================================================");
		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "�� DB Monitoring�� �����մϴ�." + ConsoleUtils.RESET);
		System.out.println("==================================================================================================================================\n");

		// DB Usage Check   		
		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			DatabaseUtil db = new DatabaseUtil(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportRepositoryImpl.getInstance());
			DBCheckBatch dbBatch = new DBCheckBatch(usecase);
			dbBatch.startBatchArchiveUsageCheck();
			dbBatch.startBatchTableSpaceUsageCheck();
			dbBatch.startBatchASMDiskUsageCheck();
			//System.out.println("�� [ " + dbName + " Monitoring End ]\n\n");
		} 
	}

	public static void serverMonitoring() {
		System.out.println("\n==================================================================================================================================");
		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "�� Server Monitoring�� �����մϴ�." + ConsoleUtils.RESET);
		System.out.println("==================================================================================================================================\n");

		// Server Usage Check   		
		List<JschConnectionInfo> jschConnectionList = PropertiesUtils.getJschConnectionMap();
		for(JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("�� [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschUtil server = new JschUtil(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
			ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);

			String alertLogFilePath = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
			String alertLogReadLine = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
			String alertLogDateFormat = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
			String alertLogDateFormatRegex = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1), DateUtils.getToday("yyyy-MM-dd"));
			serverBatch.startBatchAlertLogCheckDuringPeriod(alcp);
			serverBatch.startBatchOSDiskUsageCheck("df -Ph");
			//System.out.println("�� [ " + serverName + " Monitoring End ]\n\n");
		} 
	}
}

/* 
[�߰��ؾ� �� ����]
 1. ��ȸ��� �������� �ۼ�
 2. �������� �о� ����͸� ��Ȳ ����ȭ
 3. AlertLog �� Error �߻� Ű���� ����
 4. AlertLog Error �߻� ��, �ش� �κ��� �α� ���
 5. �� ��ȸ��� ��뷮 ����ġ �ʰ� �� �˶� ����
 6. Console ��³��� ���� ���·� ����
 7. DB ��ȸ��� ���̺����·� ��� (j-text-utils ���̺귯�� �̿��Ͽ� ����. �� ���� ���̺귯�� ���� �ʿ�)
*/
