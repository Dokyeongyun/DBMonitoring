package root.javafx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.ArchiveUsage;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.Log;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerCheckRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.repository.implement.ServerCheckRepositoryImpl;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerCheckUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerCheckUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
import root.javafx.CustomView.dateCell.DisableAfterTodayDateCell;
import root.utils.AlertUtils;

public class HistoryMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());

	/* View Binding */
	@FXML
	JFXComboBox<String> runConnInfoFileComboBox;

	@FXML
	JFXComboBox<String> alertLogServerComboBox;

	@FXML
	DatePicker alertLogStartDayDP;

	@FXML
	DatePicker alertLogEndDayDP;

	@FXML
	AnchorPane archiveUsageTabAP;

	@FXML
	AnchorPane tableSpaceUsageTabAP;

	@FXML
	AnchorPane asmDiskUsageTabAP;

	@FXML
	AnchorPane osDiskUsageTabAP;

	@FXML
	AnchorPane alertLogUsageTabAP;

	@FXML
	JFXListView<Log> alertLogLV;

	/* Custom View */
	MonitoringAPController<ArchiveUsage> archiveUsageMAP;
	MonitoringAPController<TableSpaceUsage> tableSpaceUsageMAP;
	MonitoringAPController<ASMDiskUsage> asmDiskUsageMAP;
	MonitoringAPController<OSDiskUsage> osDiskUsageMAP;
	Map<String, AlertLog> alertLogMonitoringResultMap;

	public HistoryMenuController() {
		archiveUsageMAP = new MonitoringAPController<>(ArchiveUsage.class);
		tableSpaceUsageMAP = new MonitoringAPController<>(TableSpaceUsage.class);
		asmDiskUsageMAP = new MonitoringAPController<>(ASMDiskUsage.class);
		osDiskUsageMAP = new MonitoringAPController<>(OSDiskUsage.class);
		alertLogMonitoringResultMap = new HashMap<>();
	}

	/**
	 * 실행메뉴 화면 진입시 초기화를 수행한다.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 접속정보 설정 프로퍼티 파일
		List<String> connInfoFiles = propService.getConnectionInfoList();
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

		// AlertLog 화면의 UI 요소를 초기화한다.
		initAlertLogMonitoringElements();
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

	private void changeAlertLogListViewData(String serverID) {
		alertLogLV.getItems().clear();
		AlertLog al = alertLogMonitoringResultMap.get(serverID);
		if (al != null) {
			alertLogLV.getItems().addAll(al.getAlertLogs());
		}
	}

	/**
	 * AlertLog AnchorPane의 UI 요소들의 값을 초기화한다.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox 변경 이벤트
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeAlertLogListViewData(newValue);
		});
		alertLogServerComboBox.getItems().addAll(propService.getMonitoringServerNameList());
		alertLogServerComboBox.getSelectionModel().selectFirst();

		// AlertLog 조회기간 기본값 설정
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());

		// AlertLog 조회기간 오늘 이후 날짜 선택 불가
		alertLogStartDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		alertLogEndDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());

		// AlertLog 조회기간 변경 이벤트
		alertLogStartDayDP.valueProperty().addListener((ov, oldValue, newValue) -> {
			if (alertLogEndDayDP.getValue().isBefore(newValue)) {
				alertLogEndDayDP.setValue(newValue);
			}
		});
		alertLogEndDayDP.valueProperty().addListener((ov, oldValue, newValue) -> {
			if (alertLogStartDayDP.getValue().isAfter(newValue)) {
				alertLogStartDayDP.setValue(newValue);
			}
		});

		// AlertLog ListView
		alertLogLV.setCellFactory(categoryList -> new AlertLogListViewCell());
	}

	/**
	 * [실행] - 모니터링을 시작한다.
	 * 
	 * @param e
	 */
	@SuppressWarnings("unused")
	public void runMonitoring(ActionEvent e) {
		if (!validateInput()) {
			return;
		}

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

		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();
		List<JschConnectionInfo> jschConnectionList = propService
				.getJschConnInfoList(propService.getMonitoringServerNameList());
		for (JschConnectionInfo jsch : jschConnectionList) {
			JschServer server = new JschServer(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo, ReportFileRepo.getInstance());

			osDiskUsageMAP.addTableData(server.getServerName(), usecase.getCurrentOSDiskUsage());

			// TODO AlertLog 조회
			// alertLogMonitoringResultMap.put(server.getServerName(),
			// usecase.getAlertLogDuringPeriod(alcp));
		}

		archiveUsageMAP.syncTableData(archiveUsageMAP.getSelectedAliasComboBoxItem(), 0);
		tableSpaceUsageMAP.syncTableData(tableSpaceUsageMAP.getSelectedAliasComboBoxItem(), 0);
		asmDiskUsageMAP.syncTableData(asmDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		osDiskUsageMAP.syncTableData(osDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());
	}

	/**
	 * [실행] - 모니터링 실행 시, 입력값 검사
	 * 
	 * @return
	 */
	private boolean validateInput() {
		String alertHeaderText = "";
		String alertContentText = "";

		// 1. AlertLog 조회기간
		alertHeaderText = "AlertLog 조회기간";

		LocalDate alertLogStartDay = alertLogStartDayDP.getValue();
		LocalDate alertLogEndDay = alertLogEndDayDP.getValue();
		if (alertLogStartDay == null || alertLogEndDay == null) {
			alertContentText = "조회기간을 입력해주세요.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		try {
			if (!alertLogStartDay.isBefore(alertLogEndDay) && !alertLogStartDay.isEqual(alertLogEndDay)) {
				alertContentText = "조회시작일은 조회종료일보다 이전 날짜여야 합니다.";
				AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
				return false;
			}
		} catch (Exception e) {
			alertContentText = "조회기간이 올바르지 않습니다.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		return true;
	}

	public void monitoringAlertLog(ActionEvent e) {
		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();

		String selectedServer = alertLogServerComboBox.getSelectionModel().getSelectedItem();
		JschConnectionInfo connInfo = propService.getJschConnInfo(selectedServer);

		JschServer server = new JschServer(connInfo);
		server.init();
		ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
		ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo, ReportFileRepo.getInstance());

		AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(connInfo.getAlc(), alertLogStartDay, alertLogEndDay);
		alertLogMonitoringResultMap.put(selectedServer, usecase.getAlertLogDuringPeriod(alcp));

		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());
	}
}
