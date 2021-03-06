package root.javafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.RoundingDigits;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.ReportUsecase;
import root.core.usecase.implement.ReportUsecaseImpl;
import root.javafx.CustomView.MonitoringTableViewContainer;
import root.javafx.CustomView.dateCell.MonitoringHistoryDateCell;
import root.javafx.CustomView.prequencyUI.PrequencyButton;
import root.javafx.DI.DependencyInjection;
import root.javafx.utils.AlertUtils;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.service.implement.FilePropertyService;
import root.utils.DateUtils;
import root.utils.UnitUtils.FileSize;

public class MonitoringAPController<T extends MonitoringResult> extends BorderPane implements Initializable {

	private static final String MONITORING_HISTORY_DEFAULT_TEXT = "기록을 조회해주세요.";

	private ReportUsecase reportUsecase;

	private PropertyService propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());

	@FXML
	Label label;

	@FXML
	JFXComboBox<String> aliasComboBox;

	@FXML
	JFXComboBox<FileSize> unitComboBox;

	@FXML
	JFXComboBox<RoundingDigits> roundComboBox;

	@FXML
	AnchorPane tableViewRegion;

	@FXML
	DatePicker inquiryDatePicker;

	@FXML
	Label historyDateTimeLabel;

	@FXML
	JFXButton prevHistoryBtn;

	@FXML
	JFXButton nextHistoryBtn;

	@FXML
	Pagination pagination;

	@FXML
	HBox prequencyHBox;

	@FXML
	Button prequencyTimeDivBtn;

	private Class<T> clazz;

	private MonitoringTableViewContainer tableViewContainer;

	// Map<Alias, Map<MonitoringDateTime, MonitoringResults>>
	private Map<String, Map<String, List<T>>> tableDataMap = new HashMap<>();

	private static Map<Integer, List<String>> countByTime = new HashMap<>();
	static {
		for (int i = 0; i < 24; i++) {
			countByTime.put(i, new ArrayList<>());
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.Z)) {
					prevHistoryBtn.fire();
					ke.consume();
				} else if (ke.getCode().equals(KeyCode.X)) {
					nextHistoryBtn.fire();
					ke.consume();
				}
			}
		});
	}

	public MonitoringAPController(Class<T> clazz) {
		this.reportUsecase = new ReportUsecaseImpl(ReportFileRepo.getInstance());

		try {
			this.clazz = clazz;
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/MonitoringAP.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

			setAnchor(this, 0, 0, 0, 0);

			// Add comoboBox click listner
			aliasComboBox.valueProperty().addListener((options, oldValue, newValue) -> {
				inquiryDatePicker.setDayCellFactory(picker -> new MonitoringHistoryDateCell(
						reportUsecase.getMonitoringHistoryDays(clazz, getSelectedAliasComboBoxItem())));
				if (oldValue == null) {
					syncTableData(newValue, 0);
				} else {
					showMonitoringHistory(0);
				}
			});

			// Setting inquiry datepicker initial value
			inquiryDatePicker.setValue(LocalDate.now().minusDays(0));

			unitComboBox.getItems().addAll(FileSize.values());
			unitComboBox.getSelectionModel().select(propService.getDefaultFileSizeUnit());
			unitComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue != null) {
					showMonitoringHistory(0);
				}
			});

			roundComboBox.getItems().addAll(RoundingDigits.values());
			roundComboBox.getSelectionModel().select(propService.getDefaultRoundingDigits());
			roundComboBox.setConverter(new StringConverter<RoundingDigits>() {
				@Override
				public String toString(RoundingDigits digits) {
					return String.valueOf(digits.getDigits());
				}

				@Override
				public RoundingDigits fromString(String digits) {
					return RoundingDigits.find(digits);
				}
			});
			roundComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue != null) {
					showMonitoringHistory(0);
				}
			});

			// Set pagination property
			pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
				String selected = aliasComboBox.getSelectionModel().getSelectedItem();
				syncTableData(selected, newValue.intValue());
			});

			// Set prequency div initial value
			String amOrPm = Integer.valueOf(DateUtils.getToday("HH")) < 12 ? "AM" : "PM";
			prequencyTimeDivBtn.setText(amOrPm);

			// Set historyDateTimeLabel change listener
			historyDateTimeLabel.setText(MONITORING_HISTORY_DEFAULT_TEXT);
			historyDateTimeLabel.textProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue == null || newValue.equals(MONITORING_HISTORY_DEFAULT_TEXT)) {
					prevHistoryBtn.setDisable(true);
					nextHistoryBtn.setDisable(true);
				} else {
					prevHistoryBtn.setDisable(false);
					nextHistoryBtn.setDisable(false);
				}
			});

			initMonitoringTableView();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 모니터링 기록을 출력할 TableView를 생성한다.
	 */
	private void initMonitoringTableView() {
		tableViewContainer = new MonitoringTableViewContainer();
		setAnchor(tableViewContainer, 0, 0, 0, 0);
		tableViewRegion.getChildren().add(tableViewContainer);
		tableViewContainer.addMonitoringTableView(clazz, false);
		tableViewContainer.setUsageUIType(clazz, propService.getDefaultUsageUIType());
	}

	/**
	 * 해당 클래스의 tableData의 데이터를 초기화한다.
	 * 
	 * @param id
	 */
	public void clearTableData(String id) {
		tableViewContainer.clearTableData(clazz);
		if (tableDataMap.get(id) != null) {
			tableDataMap.get(id).clear();
		}
	}

	/**
	 * 해당 클래스의 tableData에 데이터를 추가한다. 단, sync를 하지 않으면 실제 TableView에 데이터가 출력되지는 않는다.
	 * 
	 * @param id
	 * @param data
	 */
	public void addTableData(String id, List<T> data) {
		Map<String, List<T>> map = data.stream().collect(
				Collectors.groupingBy(m -> m.getMonitoringDateTime(), Collectors.mapping(m -> m, Collectors.toList())));

		if (tableDataMap.get(id) == null) {
			tableDataMap.put(id, map);
		} else {
			tableDataMap.get(id).putAll(map);
		}
	}

	/**
	 * 해당 클래스의 tableData에 데이터셋을 추가한다. 단, sync를 하지 않으면 실제 TableView에 데이터가 출력되지는 않는다.
	 * 
	 * @param id
	 * @param dataList
	 */
	public void addTableDataSet(String id, Map<String, List<T>> dataList) {
		if (tableDataMap.get(id) == null) {
			tableDataMap.put(id, dataList);
		} else {
			tableDataMap.get(id).putAll(dataList);
		}
	}

	/**
	 * 해당 클래스의 tableData로 TableView의 데이터를 동기화한다.
	 * 
	 * @param id
	 */
	public void syncTableData(String id, int index) {
		// Set Alias comboBox
		aliasComboBox.getSelectionModel().select(id);

		// Clear tableView items
		tableViewContainer.clearTableData(clazz);

		if (tableDataMap.get(id) == null) {
			pagination.setPageCount(getTableDataCount(id));
			return;
		}

		// Set pagination
		pagination.setPageCount(getTableDataCount(id));

		// Add tableView items
		Map<String, List<T>> data = tableDataMap.get(id);
		List<T> tableData = data.get(new ArrayList<>(data.keySet()).get(index));
		tableViewContainer.setTableData(clazz, tableData);

		// Sync history monitoring datetime
		if (tableData != null && !tableData.isEmpty()) {
			String monitoringDateTime = tableData.get(0).getMonitoringDateTime();
			historyDateTimeLabel.setText(DateUtils.convertDateFormat("yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss",
					monitoringDateTime, Locale.KOREA));

			// Set datepicker value
			inquiryDatePicker
					.setValue(LocalDate.parse(monitoringDateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

			// Sync monitoring prequency UI
			syncPrequency(prequencyTimeDivBtn.getText());

		} else {
			historyDateTimeLabel.setText(MONITORING_HISTORY_DEFAULT_TEXT);
		}
	}

	/**
	 * tableData에 세팅된 데이터의 Row수를 반환한다.
	 * 
	 * @param id
	 * @return
	 */
	private int getTableDataCount(String id) {
		return tableDataMap.get(id) == null ? 1 : tableDataMap.get(id).size();
	}

	/**
	 * 해당 AnchorPane의 부모 Node에 Anchor를 설정한다.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setAnchor(Node node, double left, double top, double right, double bottom) {
		AnchorPane.setLeftAnchor(node, left);
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
	}

	/**
	 * Generic Type Parameter <T>의 모니터링 History를 읽어 테이블에 출력한다.
	 * 
	 * @param e
	 */
	public void run(ActionEvent e) {
		showMonitoringHistory(0);
	}

	public void showPrevHistory(ActionEvent e) {
		showMonitoringHistory(-1);
	}

	public void showNextHistory(ActionEvent e) {
		showMonitoringHistory(1);
	}

	private void showMonitoringHistory(int type) {
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();

		// Clear data
		clearTableData(selected);

		Map<String, List<T>> allDataList = inquiryMonitoringHistory(type);
		if (allDataList == null || allDataList.size() == 0) {
			AlertUtils.showAlert(AlertType.INFORMATION, "조회결과 없음", "해당일자의 모니터링 기록이 없습니다.");
			historyDateTimeLabel.setText(MONITORING_HISTORY_DEFAULT_TEXT);
			return;
		}

		// Add and Sync data
		addTableDataSet(selected, allDataList);
		syncTableData(selected, 0);
	}

	private Map<String, List<T>> inquiryMonitoringHistory(int type) {
		// Get selected inquiry condition
		String inquiryDate = this.inquiryDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();
		FileSize selectedUnit = unitComboBox.getSelectionModel().getSelectedItem();
		RoundingDigits selectedRoundingDigit = roundComboBox.getSelectionModel().getSelectedItem();

		// TODO Show Progress UI

		// Acquire data on inquiry date
		if (type == 0) {
			return reportUsecase.getMonitoringReportDataByTime(this.clazz, selected, selectedUnit,
					selectedRoundingDigit.getDigits(), inquiryDate);
		}

		String current = historyDateTimeLabel.getText();
		if (current.equals(MONITORING_HISTORY_DEFAULT_TEXT)) {
			return null;
		}

		String currentDateTime = DateUtils.convertDateFormat("yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", current,
				Locale.KOREA);
		if (type == -1) {
			return reportUsecase.getPrevMonitoringReportDataByTime(this.clazz, selected, selectedUnit,
					selectedRoundingDigit.getDigits(), currentDateTime);
		} else {
			return reportUsecase.getNextMonitoringReportDataByTime(this.clazz, selected, selectedUnit,
					selectedRoundingDigit.getDigits(), currentDateTime);
		}
	}

	private Map<Integer, List<String>> inquiryMonitoringHistoryTimesByTime() {
		// Get selected inquiry condition
		String inquiryDate = this.inquiryDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();
		FileSize selectedUnit = unitComboBox.getSelectionModel().getSelectedItem();
		RoundingDigits selectedRoundingDigit = roundComboBox.getSelectionModel().getSelectedItem();

		// Acquire data on inquiry date
		return reportUsecase.getMonitoringReportTimesByTime(this.clazz, selected, selectedUnit,
				selectedRoundingDigit.getDigits(), inquiryDate);
	}

	/**
	 * 현재 선택된 조회조건으로 재검색한다.
	 * 
	 * @param e
	 */
	public void refresh(ActionEvent e) {
		run(e);
	}

	/**
	 * 현재 TableView에 세팅된 값을 Excel파일로 다운로드한다.
	 * 
	 * @param e
	 */
	public void excelDownload(ActionEvent e) {

	}

	/**
	 * 모니터링 기록 빈도를 나타내는 UI바의 AM/PM 구분을 변경한다.
	 * 
	 * @param e
	 */
	public void prequencyTimeDivToggle(ActionEvent e) {
		String text = prequencyTimeDivBtn.getText();
		prequencyTimeDivBtn.setText(text.equals("AM") ? "PM" : "AM");
		syncPrequency(prequencyTimeDivBtn.getText());
	}

	/**
	 * 모니터링 기록 빈도 데이터와 UI의 Sync를 맞춘다.
	 * 
	 * @param timeDiv
	 */
	private void syncPrequency(String timeDiv) {
		countByTime.putAll(inquiryMonitoringHistoryTimesByTime());

		prequencyHBox.getChildren().clear();
		List<Integer> keys = new ArrayList<>(countByTime.keySet());
		Collections.sort(keys);

		int startIdx = timeDiv.equals("AM") ? 0 : 12;
		int endIdx = timeDiv.equals("AM") ? 12 : 24;
		for (int i = startIdx; i < endIdx; i++) {
			prequencyHBox.getChildren().add(new PrequencyButton(countByTime.get(i)));
		}
	}

	/*
	 * =============================================================================
	 */

	public void setAliasComboBoxLabelText(String text) {
		this.label.setText(text);
	}

	public void setAliasComboBoxItems(List<String> items) {
		this.aliasComboBox.getItems().addAll(items);
		this.aliasComboBox.getSelectionModel().selectFirst();
	}

	public String getSelectedAliasComboBoxItem() {
		return this.aliasComboBox.getSelectionModel().getSelectedItem();
	}

}
