package root.javafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.common.server.implement.ServerOS;
import root.core.domain.AlertLog;
import root.core.domain.Log;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
import root.javafx.CustomView.AlertLogMonitoringSummaryAP;
import root.javafx.CustomView.NumberTextFormatter;
import root.javafx.CustomView.TagBar;
import root.javafx.CustomView.dateCell.DisableAfterTodayDateCell;
import root.javafx.DI.DependencyInjection;
import root.javafx.utils.AlertUtils;
import root.javafx.utils.SceneUtils;
import root.repository.implement.LinuxServerMonitoringRepository;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.repository.implement.WindowServerMonitoringRepository;
import root.service.implement.FilePropertyService;

@Slf4j
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
	HBox searchKeywordHBox;

	@FXML
	TextField navigatorTF;

	@FXML
	TextField statusTF;

	@FXML
	AnchorPane mainNodataAP;

	@FXML
	AnchorPane summaryNodataAP;
	
	@FXML
	AnchorPane topMenuBar;
	
	@FXML
	AnchorPane noPropertyFileAP;
	
	@FXML
	Button fileOpenBtn;

	TagBar tagBar = new TagBar();

	Map<String, AlertLog> alertLogMonitoringResultMap;

	public AlertLogMonitoringMenuController() {
		this.propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());
		this.alertLogMonitoringResultMap = new HashMap<>();
	}

	/**
	 * ???????????? ?????? ????????? ???????????? ????????????.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// ???????????? ?????? ???????????? ??????
		List<String> connInfoFiles;
		try {
			connInfoFiles = propService.getConnectionInfoList();
			if (connInfoFiles != null && connInfoFiles.size() != 0) {
				// Connection Info ComboBox
				runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
				runConnInfoFileComboBox.getSelectionModel().selectFirst();

				// remember.properties ????????????, ?????? ????????? ???????????? ????????? ????????? ?????? ??????????????? ????????????.
				String lastUseConnInfoFilePath = propService.getLastUseConnectionInfoFilePath();
				if (lastUseConnInfoFilePath != null) {
					runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);
				}

				System.out.println(propService.getMonitoringServerNameList());
				if(propService.getMonitoringServerNameList().size() == 0) {
					setNoPropertyUIVisible(true);	
				} else {
					setNoPropertyUIVisible(false);
				}
				
			} else {
				setNoPropertyUIVisible(true);
				return;
			}
		} catch (PropertyNotFoundException e) {
			log.error(e.getMessage());
			setNoPropertyUIVisible(true);
			return;
		}

		// ComboBox ?????? ?????????
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO ??? Tab??? ???????????? ????????? ??????
				});

		// AlertLog ????????? UI ????????? ???????????????.
		initAlertLogMonitoringElements();
	}

	private void changeAlertLogListViewData(String serverID) {
		// AlertLog ListView
		String[] hightlightKeywords = tagBar.getTags().toArray(new String[0]);
		alertLogLV.setCellFactory(categoryList -> new AlertLogListViewCell(hightlightKeywords));

		alertLogLV.getItems().clear();
		AlertLog alertLog = alertLogMonitoringResultMap.get(serverID);
		if (alertLog != null) {
			// Alert Log ListView
			alertLogLV.getItems().addAll(alertLog.getAlertLogs());
			Platform.runLater(() -> {
				alertLogLV.scrollTo(0);
				alertLogLV.getSelectionModel().select(0);
			});

			// Alert Log Summary
			alertLogSummarySP.getChildren().add(new AlertLogMonitoringSummaryAP(alertLog));
		} else {
			// TODO There is no alert log monitoring result
		}
	}

	/**
	 * AlertLog AnchorPane??? UI ???????????? ?????? ???????????????.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox ?????? ?????????
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeAlertLogListViewData(newValue);
		});
		alertLogServerComboBox.getItems().addAll(propService.getMonitoringServerNameList());
		alertLogServerComboBox.getSelectionModel().selectFirst();

		// AlertLog ???????????? ????????? ??????
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());

		// AlertLog ???????????? ?????? ?????? ?????? ?????? ??????
		alertLogStartDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		alertLogEndDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());

		// AlertLog ???????????? ?????? ?????????
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

		// AlertLog Navigator
		navigatorTF.setTextFormatter(new NumberTextFormatter());
		navigatorTF.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if (e.getCode().equals(KeyCode.ENTER)) {
					focusAlertLog(e);
					e.consume();
				}
			}
		});

		// Search Keyword Tagbar
		ScrollPane tagBarWrapper = new ScrollPane(tagBar);
		tagBarWrapper.setStyle("-fx-border-width: 0.2px; -fx-border-color: gray;");
		tagBarWrapper.getStyleClass().add("gray-scrollbar");
		tagBarWrapper.setMaxWidth(375);
		tagBarWrapper.setMinHeight(45);
		tagBarWrapper.setFitToHeight(true);
		tagBarWrapper.prefHeightProperty().bind(searchKeywordHBox.heightProperty());
		tagBarWrapper.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		HBox.setMargin(tagBarWrapper, new Insets(0, 0, 0, 25));
		searchKeywordHBox.getChildren().add(tagBarWrapper);

		tagBar.setMaxWidth(355);
		tagBarWrapper.vvalueProperty().bind(tagBar.heightProperty());

		// Set view visible
		mainNodataAP.setVisible(true);
		alertLogLV.setVisible(false);
		summaryNodataAP.setVisible(true);
	}

	/**
	 * [??????] - ???????????? ?????? ???, ????????? ??????
	 * 
	 * @return
	 */
	private boolean validateInput() {
		String alertHeaderText = "";
		String alertContentText = "";

		// 1. AlertLog ????????????
		alertHeaderText = "AlertLog ????????????";

		LocalDate alertLogStartDay = alertLogStartDayDP.getValue();
		LocalDate alertLogEndDay = alertLogEndDayDP.getValue();
		if (alertLogStartDay == null || alertLogEndDay == null) {
			alertContentText = "??????????????? ??????????????????.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		try {
			if (!alertLogStartDay.isBefore(alertLogEndDay) && !alertLogStartDay.isEqual(alertLogEndDay)) {
				alertContentText = "?????????????????? ????????????????????? ?????? ???????????? ?????????.";
				AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
				return false;
			}
		} catch (Exception e) {
			alertContentText = "??????????????? ???????????? ????????????.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		return true;
	}

	public void monitoringAlertLog(ActionEvent e) throws Exception {
		// ????????? ??????
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

		String[] searchKeywords = tagBar.getTags().toArray(new String[0]);
		AlertLog result = usecase.getAlertLogDuringPeriod(connInfo.getAlc(), alertLogStartDay, alertLogEndDay,
				searchKeywords);
		alertLogMonitoringResultMap.put(selectedServer, result);

		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());

		// Set view visible
		mainNodataAP.setVisible(false);
		alertLogLV.setVisible(true);
		summaryNodataAP.setVisible(false);
		summaryNodataAP.toBack();
	}

	public void prevAlertLog(ActionEvent e) {
		String input = navigatorTF.getText();
		if (!validateAlertLogNavigatorInput(input)) {
			return;
		}

		int toIndex = Integer.parseInt(input) - 1;
		if (toIndex == 0) {
			updateStatusMessage("????????? Log?????????.");
			return;
		}

		navigatorTF.setText(String.valueOf(toIndex));
		focusAlertLog(e);
	}

	public void nextAlertLog(ActionEvent e) {
		String input = navigatorTF.getText();
		if (!validateAlertLogNavigatorInput(input)) {
			return;
		}

		int toIndex = Integer.parseInt(input) + 1;
		if (toIndex > alertLogLV.getItems().size()) {
			updateStatusMessage("????????? Log?????????.");
			return;
		}

		navigatorTF.setText(String.valueOf(toIndex));
		focusAlertLog(e);
	}

	public void focusAlertLog(Event e) {
		String input = navigatorTF.getText();
		if (!validateAlertLogNavigatorInput(input)) {
			return;
		}

		int toIndex = Integer.parseInt(input);
		alertLogLV.scrollTo(toIndex - 1);
		alertLogLV.getSelectionModel().select(toIndex - 1);
		updateStatusMessage(String.format("[%d]?????? Log??? ???????????????.", toIndex));
	}

	private boolean validateAlertLogNavigatorInput(String input) {
		if (StringUtils.isEmpty(input)) {
			updateStatusMessage("????????? ????????? Log index??? ??????????????????.");
			return false;
		}

		int toIndex = 0;
		try {
			toIndex = Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			updateStatusMessage("????????? ???????????? ??? ????????????.");
			return false;
		}

		int alertLogSize = alertLogLV.getItems().size();
		if (alertLogSize == 0) {
			updateStatusMessage("Alert Log ?????? ??? ??????????????????.");
			return false;
		}

		if (toIndex <= 0 || toIndex > alertLogSize) {
			updateStatusMessage(String.format("Log index??? ???????????? ??????????????????. (????????? ????????? ??????: 1 ~ %d)", alertLogSize));
			return false;
		}

		return true;
	}

	/**
	 * Update message at bottom status TextField region
	 * 
	 * @param message
	 */
	private void updateStatusMessage(String message) {
		Thread statusTextUpdateThread = new Thread(() -> {
			Platform.runLater(() -> {
				statusTF.setText(message);
			});

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
			Platform.runLater(() -> {
				statusTF.setText("");
			});
		});
		statusTextUpdateThread.setDaemon(true);
		statusTextUpdateThread.start();
	}
	
	/**
	 * ?????? ????????? ??????
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/SettingMenu.fxml"));
	}
	
	/**
	 * ???????????? ???????????? ??????
	 * 
	 * @param e
	 */
	public void openPropertiesFile(ActionEvent e) {
	}
	
	/**
	 * ???????????? ?????? ????????? ?????? ??? UI Visible ??????
	 * 
	 * @param isVisible
	 */
	private void setNoPropertyUIVisible(boolean isVisible) {
		noPropertyFileAP.setVisible(isVisible);
		topMenuBar.setVisible(!isVisible);
	}
}
