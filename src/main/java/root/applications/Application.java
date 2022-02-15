package root.applications;

import java.util.List;

import root.common.database.contracts.AbstractDatabase;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschServer;
import root.core.batch.DBCheckBatch;
import root.core.batch.ServerCheckBatch;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerCheckRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.repository.implement.ServerCheckRepositoryImpl;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerCheckUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerCheckUsecaseImpl;
import root.utils.ConsoleUtils;
import root.utils.DateUtils;
import root.utils.PropertiesUtils;

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
    			PropertiesUtils.loadAppConfiguration(".\\config\\connectioninfo\\test.properties", "connInfoConfig");
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
    		//	serverMonitoring();
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace(); 
    	}
    	
    	System.out.println("END");
	}
	
	public static void dbMonitoring() {
		System.out.println("\n==================================================================================================================================");
		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "※ DB Monitoring을 시작합니다." + ConsoleUtils.RESET);
		System.out.println("==================================================================================================================================\n");

		// DB Usage Check   		
		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			AbstractDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
			DBCheckBatch dbBatch = new DBCheckBatch(usecase);
			dbBatch.startBatchArchiveUsageCheck();
			dbBatch.startBatchTableSpaceUsageCheck();
			dbBatch.startBatchASMDiskUsageCheck();
			//System.out.println("□ [ " + dbName + " Monitoring End ]\n\n");
		} 
	}

	public static void serverMonitoring() {
		System.out.println("\n==================================================================================================================================");
		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "※ Server Monitoring을 시작합니다." + ConsoleUtils.RESET);
		System.out.println("==================================================================================================================================\n");

		// Server Usage Check   		
		List<JschConnectionInfo> jschConnectionList = PropertiesUtils.getJschConnectionMap();
		for(JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschServer server = new JschServer(jsch);
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
			serverBatch.startBatchOSDiskUsageCheck();
			//System.out.println("□ [ " + serverName + " Monitoring End ]\n\n");
		} 
	}
}

/* 
[추가해야 할 사항]
 1. 조회결과 엑셀파일 작성
 2. 엑셀파일 읽어 모니터링 현황 가시화
 3. AlertLog 내 Error 발생 키워드 수정
 4. AlertLog Error 발생 시, 해당 부분의 로그 출력
 5. 각 조회결과 사용량 기준치 초과 시 알람 전송
 6. Console 출력내용 파일 형태로 저장
 7. DB 조회결과 테이블형태로 출력 (j-text-utils 라이브러리 이용하여 구현. 더 나은 라이브러리 조사 필요)
*/
