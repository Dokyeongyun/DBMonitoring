package root.javafx.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.RoundingDigits;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.ReportUsecase;
import root.core.usecase.implement.ReportUsecaseImpl;
import root.javafx.CustomView.MonitoringTableViewContainer;
import root.javafx.CustomView.dateCell.MonitoringHistoryDateCell;
import root.javafx.CustomView.prequencyUI.PrequencyButton;
import root.javafx.DI.DependencyInjection;
import root.utils.AlertUtils;
import root.utils.DateUtils;
import root.utils.UnitUtils.FileSize;

public class MonitoringAPController<T extends MonitoringResult> extends BorderPane {

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
				if(oldValue == null) {
					syncTableData(newValue, 0);
				} else {
					runMonitoring();	
				}
			});

			// Setting inquiry datepicker initial value
			inquiryDatePicker.setValue(LocalDate.now().minusDays(0));
			inquiryDatePicker.setDayCellFactory(picker -> new MonitoringHistoryDateCell(
					reportUsecase.getMonitoringHistoryDays(clazz, getSelectedAliasComboBoxItem())));

			unitComboBox.getItems().addAll(FileSize.values());
			unitComboBox.getSelectionModel().select(propService.getDefaultFileSizeUnit());
			unitComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
				if(oldValue != null) {
					runMonitoring();	
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
				if(oldValue != null) {
					runMonitoring();	
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

			initMonitoringTableView();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����͸� ����� ����� TableView�� �����Ѵ�.
	 */
	private void initMonitoringTableView() {
		tableViewContainer = new MonitoringTableViewContainer();
		setAnchor(tableViewContainer, 0, 0, 0, 0);
		tableViewRegion.getChildren().add(tableViewContainer);
		tableViewContainer.addMonitoringTableView(clazz, false);
		tableViewContainer.setUsageUIType(clazz, propService.getDefaultUsageUIType());
	}

	/**
	 * �ش� Ŭ������ tableData�� �����͸� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param id
	 */
	public void clearTableData(String id) {
		if (tableDataMap.get(id) != null) {
			tableDataMap.get(id).clear();
		}
	}

	/**
	 * �ش� Ŭ������ tableData�� �����͸� �߰��Ѵ�. ��, sync�� ���� ������ ���� TableView�� �����Ͱ� ��µ����� �ʴ´�.
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
	 * �ش� Ŭ������ tableData�� �����ͼ��� �߰��Ѵ�. ��, sync�� ���� ������ ���� TableView�� �����Ͱ� ��µ����� �ʴ´�.
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
	 * �ش� Ŭ������ tableData�� TableView�� �����͸� ����ȭ�Ѵ�.
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
		List<String> times = new ArrayList<>(data.keySet());
		Collections.sort(times);

		List<T> tableData = data.get(times.get(index));
		tableViewContainer.setTableData(clazz, tableData);

		// Sync monitoring prequency UI
		syncPrequency(prequencyTimeDivBtn.getText());
	}

	/**
	 * tableData�� ���õ� �������� Row���� ��ȯ�Ѵ�.
	 * 
	 * @param id
	 * @return
	 */
	private int getTableDataCount(String id) {
		return tableDataMap.get(id) == null ? 1 : tableDataMap.get(id).size();
	}

	/**
	 * �ش� AnchorPane�� �θ� Node�� Anchor�� �����Ѵ�.
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
	 * Generic Type Parameter <T>�� ����͸� History�� �о� ���̺� ����Ѵ�.
	 * 
	 * @param e
	 */
	public void run(ActionEvent e) {
		runMonitoring();
	}
	
	private void runMonitoring() {
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();

		// Clear data
		clearTableData(selected);

		Map<String, List<T>> allDataList = inquiryMonitoringHistory();
		if (allDataList == null || allDataList.size() == 0) {
			AlertUtils.showAlert(AlertType.INFORMATION, "��ȸ��� ����", "�ش������� ����͸� ����� �����ϴ�.");
			return;
		}

		// Add and Sync data
		addTableDataSet(selected, allDataList);
		syncTableData(selected, 0);
	}

	private Map<String, List<T>> inquiryMonitoringHistory() {
		// Get selected inquiry condition
		String inquiryDate = this.inquiryDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();
		FileSize selectedUnit = unitComboBox.getSelectionModel().getSelectedItem();
		RoundingDigits selectedRoundingDigit = roundComboBox.getSelectionModel().getSelectedItem();

		// TODO Show Progress UI

		// Acquire data on inquiry date
		return reportUsecase.getMonitoringReportDataByTime(this.clazz, selected, selectedUnit,
				selectedRoundingDigit.getDigits(), inquiryDate);
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
	 * ���� ���õ� ��ȸ�������� ��˻��Ѵ�.
	 * 
	 * @param e
	 */
	public void refresh(ActionEvent e) {
		run(e);
	}

	/**
	 * ���� TableView�� ���õ� ���� Excel���Ϸ� �ٿ�ε��Ѵ�.
	 * 
	 * @param e
	 */
	public void excelDownload(ActionEvent e) {

	}

	/**
	 * ����͸� ��� �󵵸� ��Ÿ���� UI���� AM/PM ������ �����Ѵ�.
	 * 
	 * @param e
	 */
	public void prequencyTimeDivToggle(ActionEvent e) {
		String text = prequencyTimeDivBtn.getText();
		prequencyTimeDivBtn.setText(text.equals("AM") ? "PM" : "AM");
		syncPrequency(prequencyTimeDivBtn.getText());
	}

	/**
	 * ����͸� ��� �� �����Ϳ� UI�� Sync�� �����.
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
