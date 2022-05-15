package root.javafx.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.utils.AlertUtils;
import root.repository.implement.DBCheckRepositoryImpl;
import root.repository.implement.LinuxServerMonitoringRepository;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.service.implement.FilePropertyService;

@Slf4j
public class HistoryMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService;

	/* View Binding */
	@FXML
	JFXComboBox<String> runConnInfoFileComboBox;

	@FXML
	AnchorPane archiveUsageTabAP;

	@FXML
	AnchorPane tableSpaceUsageTabAP;

	@FXML
	AnchorPane asmDiskUsageTabAP;

	@FXML
	AnchorPane osDiskUsageTabAP;

	/* Custom View */
	MonitoringAPController<ArchiveUsage> archiveUsageMAP;
	MonitoringAPController<TableSpaceUsage> tableSpaceUsageMAP;
	MonitoringAPController<ASMDiskUsage> asmDiskUsageMAP;
	MonitoringAPController<OSDiskUsage> osDiskUsageMAP;

	public HistoryMenuController() {
		this.propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());
		this.archiveUsageMAP = new MonitoringAPController<>(ArchiveUsage.class);
		this.tableSpaceUsageMAP = new MonitoringAPController<>(TableSpaceUsage.class);
		this.asmDiskUsageMAP = new MonitoringAPController<>(ASMDiskUsage.class);
		this.osDiskUsageMAP = new MonitoringAPController<>(OSDiskUsage.class);
	}

	/**
	 * 실행메뉴 화면 진입시 초기화를 수행한다.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 접속정보 설정 프로퍼티 파일
		List<String> connInfoFiles;
		try {
			connInfoFiles = propService.getConnectionInfoList();
			if (connInfoFiles != null && connInfoFiles.size() != 0) {
				// Connection Info ComboBox
				runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
				runConnInfoFileComboBox.getSelectionModel().selectFirst();

				// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
				String lastUseConnInfoFilePath = propService.getLastUseConnectionInfoFilePath();
				if (lastUseConnInfoFilePath != null) {
					runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);
				}
			} else {
				AlertUtils.showAlert(AlertType.INFORMATION, "접속정보 설정", "설정된 DB/Server 접속정보가 없습니다.\n[설정]메뉴로 이동합니다.");
				return;
			}
		} catch (PropertyNotFoundException e) {
			log.error(e.getMessage());
			AlertUtils.showAlert(AlertType.INFORMATION, "접속정보 설정", "설정된 DB/Server 접속정보가 없습니다.\n[설정]메뉴로 이동합니다.");
			return;
		}

		// ComboBox 변경 이벤트
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO 각 Tab별 콤보박스 아이템 변경
				});

		String dbComboBoxLabel = "DB 선택";
		List<String> dbComboBoxItems = propService.getMonitoringDBNameList();
		String serverComboBoxLabel = "Server 선택";
		List<String> serverComboBoxItems = propService.getMonitoringServerNameList();

		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems);
	}

	/**
	 * 모니터링 AnchorPane 추가하고 요소를 초기화한다.
	 * 
	 * @param <T>
	 * @param monitoringAP
	 * @param parentAP
	 * @param labelText
	 * @param comboBoxItems
	 * @param tableColumns
	 */
	private <T extends MonitoringResult> void initAndAddMonitoringAnchorPane(MonitoringAPController<T> monitoringAP,
			AnchorPane parentAP, String labelText, List<String> comboBoxItems) {
		monitoringAP.setAliasComboBoxLabelText(labelText); // ComboBox 좌측 Lebel Text 설정
		monitoringAP.setAliasComboBoxItems(comboBoxItems); // ComboBox Items 설정
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane을 부모 Node에 추가
	}

	/**
	 * [실행] - 모니터링을 시작한다.
	 * 
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		// DB Usage Check
		List<JdbcConnectionInfo> jdbcConnectionList = propService
				.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			JdbcDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
			archiveUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentArchiveUsage());
			tableSpaceUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentTableSpaceUsage());
			asmDiskUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentASMDiskUsage());
			db.uninit();
		}

		List<JschConnectionInfo> jschConnectionList = propService
				.getJschConnInfoList(propService.getMonitoringServerNameList());
		for (JschConnectionInfo jsch : jschConnectionList) {
			JschServer server = new JschServer(jsch);
			server.init();
			ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
			ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());

			osDiskUsageMAP.addTableData(server.getServerName(), usecase.getCurrentOSDiskUsage());
		}

		archiveUsageMAP.syncTableData(archiveUsageMAP.getSelectedAliasComboBoxItem(), 0);
		tableSpaceUsageMAP.syncTableData(tableSpaceUsageMAP.getSelectedAliasComboBoxItem(), 0);
		asmDiskUsageMAP.syncTableData(asmDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		osDiskUsageMAP.syncTableData(osDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
	}
}
