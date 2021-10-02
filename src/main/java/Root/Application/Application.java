package Root.Application;

import java.util.List;
import java.util.Map;

import Root.Batch.DBCheckBatch;
import Root.Batch.ServerCheckBatch;
import Root.Database.DatabaseUtil;
import Root.Model.AlertLogCommand;
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
import Root.Utils.ConsoleUtils;
import Root.Utils.PropertiesUtils;

public class Application {

	public static void main(String[] args) {
    	try {    		
    		String propertyFilePathName = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\config\\application.properties";
    		
    		try {
    			PropertiesUtils.loadAppConfiguration(propertyFilePathName);
    		}catch(Exception e) {
    			System.out.println("configuration loading error\n"+e+"\n");
    			return;
    		}

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
        		DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
        		DBCheckBatch dbBatch = new DBCheckBatch(usecase);
    			dbBatch.startBatchArchiveUsageCheck();
    			//dbBatch.startBatchTableSpaceUsageCheck();
    			//dbBatch.startBatchASMDiskUsageCheck();
    			//System.out.println("�� [ " + dbName + " Monitoring End ]\n\n");
    		} 
    		
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
        		AlertLogCommand alc = new AlertLogCommand("tail", "3000", alertLogFilePath);
    			//serverBatch.startBatchAlertLogCheck(alc);
    			//serverBatch.startBatchOSDiskUsageCheck("df -Ph | column -t");
    			//System.out.println("�� [ " + serverName + " Monitoring End ]\n\n");
    		} 
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("END");
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
 7. DB ��ȸ��� ���̺����·� ���
*/
