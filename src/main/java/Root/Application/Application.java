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
    		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "※ DB Monitoring을 시작합니다." + ConsoleUtils.RESET);
    		System.out.println("==================================================================================================================================\n");

    		// DB Usage Check   		
    		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
    		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
    			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
    			DatabaseUtil db = new DatabaseUtil(jdbc);
    			db.init();
        		DBCheckRepository repo = new DBCheckRepositoryImpl(db);
        		DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
        		DBCheckBatch dbBatch = new DBCheckBatch(usecase);
    			dbBatch.startBatchArchiveUsageCheck();
    			//dbBatch.startBatchTableSpaceUsageCheck();
    			//dbBatch.startBatchASMDiskUsageCheck();
    			//System.out.println("□ [ " + dbName + " Monitoring End ]\n\n");
    		} 
    		
    		System.out.println("\n==================================================================================================================================");
    		System.out.println(ConsoleUtils.BACKGROUND_CYAN + "※ Server Monitoring을 시작합니다." + ConsoleUtils.RESET);
    		System.out.println("==================================================================================================================================\n");

    		// Server Usage Check   		
    		List<JschConnectionInfo> jschConnectionList = PropertiesUtils.getJschConnectionMap();
    		for(JschConnectionInfo jsch : jschConnectionList) {
    			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
    			JschUtil server = new JschUtil(jsch);
    			server.init();
        		ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
        		ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
        		ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);

        		String alertLogFilePath = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
        		AlertLogCommand alc = new AlertLogCommand("tail", "3000", alertLogFilePath);
    			//serverBatch.startBatchAlertLogCheck(alc);
    			//serverBatch.startBatchOSDiskUsageCheck("df -Ph | column -t");
    			//System.out.println("□ [ " + serverName + " Monitoring End ]\n\n");
    		} 
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("END");
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
 7. DB 조회결과 테이블형태로 출력
*/
