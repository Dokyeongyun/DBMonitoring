//package root.applications;
//
//import java.util.List;
//
//import root.common.database.contracts.AbstractDatabase;
//import root.common.database.implement.JdbcDatabase;
//import root.common.server.implement.JschServer;
//import root.core.batch.DBCheckBatch;
//import root.core.batch.ServerCheckBatch;
//import root.core.domain.JdbcConnectionInfo;
//import root.core.domain.JschConnectionInfo;
//import root.core.repository.constracts.DBCheckRepository;
//import root.core.repository.constracts.PropertyRepository;
//import root.core.repository.constracts.ServerCheckRepository;
//import root.core.repository.implement.DBCheckRepositoryImpl;
//import root.core.repository.implement.PropertyRepositoryImpl;
//import root.core.repository.implement.ReportFileRepo;
//import root.core.repository.implement.ServerCheckRepositoryImpl;
//import root.core.usecase.constracts.DBCheckUsecase;
//import root.core.usecase.constracts.ServerCheckUsecase;
//import root.core.usecase.implement.DBCheckUsecaseImpl;
//import root.core.usecase.implement.ServerCheckUsecaseImpl;
//import root.utils.ConsoleUtils;
//
//public class Application {
//	private static PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
//
//	public static void main(String[] args) {
//
//		try {    		
//    		String propertiesFilePath = ".\\config\\application.properties";
//    		
//    		try {
//    			propRepo.loadCombinedConfiguration();
//    			String lastUsePropertiesFile = propRepo.getCommonResource("filepath.config.lastuse");
//    			propRepo.loadConnectionInfoConfig(propertiesFilePath);
//    			propRepo.loadCombinedConfiguration();
//    		}catch(Exception e) {
//    			System.out.println("configuration loading error\n"+e+"\n");
//    			return;
//    		}
////    		
////    		String dbMonitoring = propRepo.getMonitoringDBNames();
////    		String serverMonitoring = propRepo.propConfig.getString("monitoring.server");
////    		
////    		if("on".equals(dbMonitoring)) {
////    			dbMonitoring();	
////    		}
////
////    		if("on".equals(serverMonitoring)) {
////    		//	serverMonitoring();
////    		}
//    		
//    	} catch (Exception e) {
//    		e.printStackTrace(); 
//    	}
//    	
//    	System.out.println("END");
//	}
//	
//	public static void dbMonitoring() {
//		System.out.println("\n==================================================================================================================================");
//		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "�� DB Monitoring�� �����մϴ�." + ConsoleUtils.RESET);
//		System.out.println("==================================================================================================================================\n");
//
//		// DB Usage Check   		
//		List<JdbcConnectionInfo> jdbcConnectionList = propRepo.getJdbcConnectionMap();
//		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
//			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
//			AbstractDatabase db = new JdbcDatabase(jdbc);
//			db.init();
//			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
//			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
//			DBCheckBatch dbBatch = new DBCheckBatch(usecase);
//			dbBatch.startBatchArchiveUsageCheck();
//			dbBatch.startBatchTableSpaceUsageCheck();
//			dbBatch.startBatchASMDiskUsageCheck();
//			//System.out.println("�� [ " + dbName + " Monitoring End ]\n\n");
//		} 
//	}
//
//	public static void serverMonitoring() {
//		System.out.println("\n==================================================================================================================================");
//		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "�� Server Monitoring�� �����մϴ�." + ConsoleUtils.RESET);
//		System.out.println("==================================================================================================================================\n");
//
//		// Server Usage Check   		
//		List<JschConnectionInfo> jschConnectionList = propRepo.getJschConnectionMap();
//		for(JschConnectionInfo jsch : jschConnectionList) {
//			System.out.println("�� [ " + jsch.getServerName() + " Monitoring Start ]\n");
//			JschServer server = new JschServer(jsch);
//			server.init();
//			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
//			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
//			ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);
//
////			String alertLogFilePath = propRepo..getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
////			String alertLogReadLine = propRepo.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
////			String alertLogDateFormat = propRepo.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
////			String alertLogDateFormatRegex = propRepo.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
////			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
////			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1), DateUtils.getToday("yyyy-MM-dd"));
////			serverBatch.startBatchAlertLogCheckDuringPeriod(alcp);
////			serverBatch.startBatchOSDiskUsageCheck();
//			//System.out.println("�� [ " + serverName + " Monitoring End ]\n\n");
//		} 
//	}
//}
//
///* 
//[�߰��ؾ� �� ����]
// 1. ��ȸ��� �������� �ۼ�
// 2. �������� �о� ����͸� ��Ȳ ����ȭ
// 3. AlertLog �� Error �߻� Ű���� ����
// 4. AlertLog Error �߻� ��, �ش� �κ��� �α� ���
// 5. �� ��ȸ��� ��뷮 ����ġ �ʰ� �� �˶� ����
// 6. Console ��³��� ���� ���·� ����
// 7. DB ��ȸ��� ���̺����·� ��� (j-text-utils ���̺귯�� �̿��Ͽ� ����. �� ���� ���̺귯�� ���� �ʿ�)
//*/