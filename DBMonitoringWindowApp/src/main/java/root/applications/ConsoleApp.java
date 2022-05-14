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
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.LinuxServerMonitoringRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.utils.CsvUtils;
import root.utils.DateUtils;
import root.utils.PatternUtils;

/**
 * ConsoleApp�� Console�� ���� ��/����� �����մϴ�.
 * 
 * @author DKY
 *
 */
public class ConsoleApp {

	public static final String DEFAULT_CONFIG_DIR = "./config/connectioninfo";

	private static PropertyService propService;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// STEP1: �������� �������� ����
		String selectedFile = "";
		while (true) {
			System.out.println("�������� ���������� �������ּ���.");
			List<String> configFiles = Arrays.asList(new File(DEFAULT_CONFIG_DIR).list()).stream()
					.filter(fileName -> fileName.endsWith(".properties")).collect(Collectors.toList());

			if (configFiles.size() == 0) {
				System.out.println("�������� ���������� �������� �ʽ��ϴ�. ���α׷��� �����մϴ�.");
				return;
			}

			for (int i = 0; i < configFiles.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), configFiles.get(i)));
			}

			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. �������� ���������� �ٽ� �������ּ���.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > configFiles.size()) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. �������� ���������� �ٽ� �������ּ���.");
				continue;
			}

			selectedFile = configFiles.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("���õ� ������ [%s] �Դϴ�.", selectedFile));

		// STEP2: ���õ� �������� �������� Load
		String propertiesFilePath = DEFAULT_CONFIG_DIR + "/" + selectedFile;
		try {
			PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
			propRepo.loadConnectionInfoConfig(propertiesFilePath);
			propService = new FilePropertyService(propRepo);
		} catch (Exception e) {
			System.out.println("configuration loading error\n" + e + "\n");
			return;
		}

		// STEP3: �������� �������� ��, ����͸����� �������� ã�� �� LOAD
		String selectedPreset = "";
		while (true) {
			System.out.println(String.format("����Ͻ� ����͸����� ������ �������ּ���."));

			List<String> presetNames = propService.getMonitoringPresetNameList();
			if (presetNames.size() == 0) {
				System.out.println("����͸����� ���������� �������� �ʽ��ϴ�. ���α׷��� �����մϴ�.");
				return;
			}

			for (int i = 0; i < presetNames.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), presetNames.get(i)));
			}

			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. ����͸����� ���������� �ٽ� �������ּ���.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > presetNames.size()) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. ����͸����� ���������� �ٽ� �������ּ���.");
				continue;
			}

			selectedPreset = presetNames.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("���õ� ������ [%s] �Դϴ�.", selectedPreset));

		// STEP4: ���������� ���������� �о� DB,Server ��ü ���� �� ���
		List<String> dbNames = propService.getMonitoringDBNameList();
		List<JdbcConnectionInfo> jdbcConnectionList = propService.getJdbcConnInfoList(dbNames);
		System.out.println("����� DB���������� ������ �����ϴ�.");
		TextTable dbTable = new TextTable(
				new CsvTableModel(CsvUtils.toCsvString(jdbcConnectionList, JdbcConnectionInfo.class)));
		dbTable.printTable(System.out, 2);

		List<String> serverNames = propService.getMonitoringServerNameList();
		List<JschConnectionInfo> jschConnectionList = propService.getJschConnInfoList(serverNames);
		System.out.println("����� Server���������� ������ �����ϴ�.");
		TextTable serverTable = new TextTable(
				new CsvTableModel(CsvUtils.toCsvString(jschConnectionList, JschConnectionInfo.class)));
		serverTable.printTable(System.out, 2);

		// TODO STEP5: ����͸����� ���� �б�

		// STEP6: ����͸� ����
		System.out.println("DB ����͸��� �����մϴ�.");
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			AbstractDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
			DBCheckBatch dbBatch = new DBCheckBatch(usecase);
			dbBatch.startBatchArchiveUsageCheck();
			dbBatch.startBatchTableSpaceUsageCheck();
			dbBatch.startBatchASMDiskUsageCheck();
			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring End ]\n\n");
		}

		System.out.println("Server ����͸��� �����մϴ�.");
		for (JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("�� [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschServer server = new JschServer(jsch);
			server.init();
			ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
			ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());
			ServerCheckBatch serverBatch = new ServerCheckBatch(usecase);

			String startDate = DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1);
			String endDate = DateUtils.getToday("yyyy-MM-dd");
			serverBatch.startBatchAlertLogCheckDuringPeriod(jsch.getAlc(), startDate, endDate);
			serverBatch.startBatchOSDiskUsageCheck();
			System.out.println("�� [ " + jsch.getServerName() + " Monitoring End ]\n\n");
		}
	}
}
