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
import javafx.scene.layout.StackPane;
import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.JschConnectionInfo;
import root.core.domain.Log;
import root.core.domain.enums.ServerOS;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.repository.implement.LinuxServerMonitoringRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.repository.implement.WindowServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
import root.javafx.CustomView.AlertLogMonitoringSummaryAP;
import root.javafx.CustomView.dateCell.DisableAfterTodayDateCell;
import root.utils.AlertUtils;

public class AlertLogMonitoringMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService;

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
	JFXListView<Log> alertLogLV;

	@FXML
	StackPane alertLogSummarySP;

	@FXML
	AnchorPane mainNodataAP;

	@FXML
	AnchorPane summaryNodataAP;

	Map<String, AlertLog> alertLogMonitoringResultMap;

	public AlertLogMonitoringMenuController() {
		this.propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());
		this.alertLogMonitoringResultMap = new HashMap<>();
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
			AlertUtils.showAlert(AlertType.INFORMATION, "접속정보 설정", "설정된 접속정보가 없습니다.\n[설정]메뉴로 이동합니다.");
			return;
		}

		// ComboBox 변경 이벤트
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO 각 Tab별 콤보박스 아이템 변경
				});

		// AlertLog 화면의 UI 요소를 초기화한다.
		initAlertLogMonitoringElements();
	}

	private void changeAlertLogListViewData(String serverID) {
		alertLogLV.getItems().clear();
		AlertLog alertLog = alertLogMonitoringResultMap.get(serverID);
		if (alertLog != null) {
			// Alert Log ListView
			alertLogLV.getItems().addAll(alertLog.getAlertLogs());

			// Alert Log Summary
			alertLogSummarySP.getChildren().add(new AlertLogMonitoringSummaryAP(alertLog));
		} else {
			// TODO There is no alert log monitoring result
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

		// Set view visible
		mainNodataAP.setVisible(true);
		alertLogLV.setVisible(false);
		summaryNodataAP.setVisible(true);
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

	public void monitoringAlertLog(ActionEvent e) throws Exception {
		// 입력값 검사
		if (!validateInput()) {
			return;
		}

		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();

		String selectedServer = alertLogServerComboBox.getSelectionModel().getSelectedItem();
		JschConnectionInfo connInfo = propService.getJschConnInfo(selectedServer);

		JschServer server = new JschServer(connInfo);
		server.init();
		ServerMonitoringRepository repo = null;
		if (connInfo.getServerOS() == ServerOS.WINDOW) {
			repo = new WindowServerMonitoringRepository(server);
		} else if (connInfo.getServerOS() == ServerOS.LINUX) {
			repo = new LinuxServerMonitoringRepository(server);
		} else {
			throw new Exception("Server OS is not valid");
		}
		ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());

		AlertLog result = usecase.getAlertLogDuringPeriod(connInfo.getAlc(), alertLogStartDay, alertLogEndDay);
		alertLogMonitoringResultMap.put(selectedServer, result);

		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());

		// Set view visible
		mainNodataAP.setVisible(false);
		alertLogLV.setVisible(true);
		summaryNodataAP.setVisible(false);
		summaryNodataAP.toBack();
	}
}
