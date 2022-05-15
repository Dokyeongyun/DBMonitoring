package root.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;
import root.common.database.contracts.AbstractDatabase;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.core.batch.DBCheckBatch;
import root.core.batch.ServerCheckBatch;
import root.core.domain.exceptions.PropertyNotLoadedException;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.repository.implement.DBCheckRepositoryImpl;
import root.repository.implement.LinuxServerMonitoringRepository;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.service.implement.FilePropertyService;
import root.utils.CsvUtils;
import root.utils.DateUtils;
import root.utils.PatternUtils;

/**
 * ConsoleApp은 Console을 통해 입/출력을 수행합니다.
 * 
 * @author DKY
 *
 */
public class ConsoleApp {

	public static final String DEFAULT_CONFIG_DIR = "./config/connectioninfo";

	private static PropertyService propService;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// STEP1: 접속정보 설정파일 선택
		String selectedFile = "";
		while (true) {
			System.out.println("접속정보 설정파일을 선택해주세요.");
			List<String> configFiles = Arrays.asList(new File(DEFAULT_CONFIG_DIR).list()).stream()
					.filter(fileName -> fileName.endsWith(".properties")).collect(Collectors.toList());

			if (configFiles.size() == 0) {
				System.out.println("접속정보 설정파일이 존재하지 않습니다. 프로그램을 종료합니다.");
				return;
			}

			for (int i = 0; i < configFiles.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), configFiles.get(i)));
			}

			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > configFiles.size()) {
				System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
				continue;
			}

			selectedFile = configFiles.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("선택된 파일은 [%s] 입니다.", selectedFile));

		// STEP2: 선택된 접속정보 설정파일 Load
		String propertiesFilePath = DEFAULT_CONFIG_DIR + "/" + selectedFile;
		try {
			PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
			propRepo.loadConnectionInfoConfig(propertiesFilePath);
			propService = new FilePropertyService(propRepo);
		} catch (Exception e) {
			System.out.println("configuration loading error\n" + e + "\n");
			return;
		}

		// STEP3: 접속정보 설정파일 내, 모니터링여부 설정파일 찾기 및 LOAD
		String selectedPreset = "";
		while (true) {
			System.out.println(String.format("사용하실 모니터링여부 설정을 선택해주세요."));

			List<String> presetNames = null;
			try {
				presetNames = propService.getMonitoringPresetNameList();
			} catch (PropertyNotLoadedException e) {
				e.printStackTrace();
			}
			if (presetNames == null || presetNames.size() == 0) {
				System.out.println("모니터링여부 설정파일이 존재하지 않습니다. 프로그램을 종료합니다.");
				return;
			}

			for (int i = 0; i < presetNames.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), presetNames.get(i)));
			}

			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("잘못 입력하셨습니다. 모니터링여부 설정파일을 다시 선택해주세요.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > presetNames.size()) {
				System.out.println("잘못 입력하셨습니다. 모니터링여부 설정파일을 다시 선택해주세요.");
				continue;
			}

			selectedPreset = presetNames.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("선택된 파일은 [%s] 입니다.", selectedPreset));

		// STEP4: 설정파일의 접속정보를 읽어 DB,Server 객체 생성 및 출력
		List<String> dbNames = propService.getMonitoringDBNameList();
		List<JdbcConnectionInfo> jdbcConnectionList = propService.getJdbcConnInfoList(dbNames);
		System.out.println("저장된 DB접속정보는 다음과 같습니다.");
		TextTable dbTable = new TextTable(
				new CsvTableModel(CsvUtils.toCsvString(jdbcConnectionList, JdbcConnectionInfo.class)));
		dbTable.printTable(System.out, 2);

		List<String> serverNames = propService.getMonitoringServerNameList();
		List<JschConnectionInfo> jschConnectionList = propService.getJschConnInfoList(serverNames);
		System.out.println("저장된 Server접속정보는 다음과 같습니다.");
		TextTable serverTable = new TextTable(
				new CsvTableModel(CsvUtils.toCsvString(jschConnectionList, JschConnectionInfo.class)));
		serverTable.printTable(System.out, 2);

		// TODO STEP5: 모니터링여부 설정 읽기

		// STEP6: 모니터링 수행
		System.out.println("DB 모니터링을 수행합니다.");
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			AbstractDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
			DBCheckBatch dbBatch = new DBCheckBatch(usecase);
			dbBatch.startBatchArchiveUsageCheck();
			dbBatch.startBatchTableSpaceUsageCheck();
			dbBatch.startBatchASMDiskUsageCheck();
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring End ]\n\n");
		}

		System.out.println("Server 모니터링을 수행합니다.");
		for (JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschServer server = new JschServer(jsch);
			server.init();
			ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
			ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());
			ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);

			String startDate = DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1);
			String endDate = DateUtils.getToday("yyyy-MM-dd");
			serverBatch.startBatchAlertLogCheckDuringPeriod(jsch.getAlc(), startDate, endDate);
			serverBatch.startBatchOSDiskUsageCheck();
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring End ]\n\n");
		}
	}
}
