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
import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.JschConnectionInfo;
import root.core.domain.Log;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.repository.implement.LinuxServerMonitoringRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
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

	Map<String, AlertLog> alertLogMonitoringResultMap;

	public AlertLogMonitoringMenuController() {
		this.propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());
		this.alertLogMonitoringResultMap = new HashMap<>();
	}

	/**
	 * ����޴� ȭ�� ���Խ� �ʱ�ȭ�� �����Ѵ�.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// �������� ���� ������Ƽ ����
		List<String> connInfoFiles = propService.getConnectionInfoList();
		if (connInfoFiles != null && connInfoFiles.size() != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();

			// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
			String lastUseConnInfoFilePath = propService.getLastUseConnectionInfoFilePath();
			if (lastUseConnInfoFilePath != null) {
				runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);
			}
		} else {
			AlertUtils.showAlert(AlertType.INFORMATION, "�������� ����", "������ ���������� �����ϴ�.\n[����]�޴��� �̵��մϴ�.");
			return;
		}

		// ComboBox ���� �̺�Ʈ
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO �� Tab�� �޺��ڽ� ������ ����
				});

		// AlertLog ȭ���� UI ��Ҹ� �ʱ�ȭ�Ѵ�.
		initAlertLogMonitoringElements();
	}

	private void changeAlertLogListViewData(String serverID) {
		alertLogLV.getItems().clear();
		AlertLog al = alertLogMonitoringResultMap.get(serverID);
		if (al != null) {
			alertLogLV.getItems().addAll(al.getAlertLogs());
		}
	}

	/**
	 * AlertLog AnchorPane�� UI ��ҵ��� ���� �ʱ�ȭ�Ѵ�.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox ���� �̺�Ʈ
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeAlertLogListViewData(newValue);
		});
		alertLogServerComboBox.getItems().addAll(propService.getMonitoringServerNameList());
		alertLogServerComboBox.getSelectionModel().selectFirst();

		// AlertLog ��ȸ�Ⱓ �⺻�� ����
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());

		// AlertLog ��ȸ�Ⱓ ���� ���� ��¥ ���� �Ұ�
		alertLogStartDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		alertLogEndDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());

		// AlertLog ��ȸ�Ⱓ ���� �̺�Ʈ
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
	 * [����] - ����͸� ���� ��, �Է°� �˻�
	 * 
	 * @return
	 */
	private boolean validateInput() {
		String alertHeaderText = "";
		String alertContentText = "";

		// 1. AlertLog ��ȸ�Ⱓ
		alertHeaderText = "AlertLog ��ȸ�Ⱓ";

		LocalDate alertLogStartDay = alertLogStartDayDP.getValue();
		LocalDate alertLogEndDay = alertLogEndDayDP.getValue();
		if (alertLogStartDay == null || alertLogEndDay == null) {
			alertContentText = "��ȸ�Ⱓ�� �Է����ּ���.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		try {
			if (!alertLogStartDay.isBefore(alertLogEndDay) && !alertLogStartDay.isEqual(alertLogEndDay)) {
				alertContentText = "��ȸ�������� ��ȸ�����Ϻ��� ���� ��¥���� �մϴ�.";
				AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
				return false;
			}
		} catch (Exception e) {
			alertContentText = "��ȸ�Ⱓ�� �ùٸ��� �ʽ��ϴ�.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		return true;
	}

	public void monitoringAlertLog(ActionEvent e) {
		// �Է°� �˻�
		if (!validateInput()) {
			return;
		}

		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();

		String selectedServer = alertLogServerComboBox.getSelectionModel().getSelectedItem();
		JschConnectionInfo connInfo = propService.getJschConnInfo(selectedServer);

		JschServer server = new JschServer(connInfo);
		server.init();
		ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
		ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());

		alertLogMonitoringResultMap.put(selectedServer,
				usecase.getAlertLogDuringPeriod(connInfo.getAlc(), alertLogStartDay, alertLogEndDay));

		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());
	}
}