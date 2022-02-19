package root.javafx.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXComboBox;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.UsageUIType;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.usecase.constracts.ReportUsecase;
import root.core.usecase.implement.ReportUsecaseImpl;
import root.javafx.CustomView.UsageUI.UsageUI;
import root.javafx.CustomView.UsageUI.UsageUIFactory;
import root.utils.AlertUtils;
import root.utils.UnitUtils.FileSize;

public class MonitoringAPController<T extends MonitoringResult> extends BorderPane {

	private ReportUsecase reportUsecase;
	
	private PropertyRepository propertyRepo = PropertyRepositoryImpl.getInstance();
	
	@FXML
	Label label;

	@FXML
	JFXComboBox<String> aliasComboBox;
	
	@FXML
	JFXComboBox<FileSize> unitComboBox;
	
	@FXML
	JFXComboBox<Integer> roundComboBox;

	@FXML
	TableView<T> monitoringResultTV;

	@FXML
	DatePicker inquiryDatePicker;
	
	@FXML
	Pagination pagination;

	private Class<T> clazz;
	
	// Map<Alias, Map<MonitoringDateTime, MonitoringResults>>
	private Map<String, Map<String, List<T>>> tableDataMap = new HashMap<>();

	public MonitoringAPController(Class<T> clazz) {
		this.reportUsecase = new ReportUsecaseImpl(ReportFileRepo.getInstance());
		
		try {
			this.clazz = clazz;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MonitoringAP.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

			// Add comoboBox click listner
			this.aliasComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
//				monitoringResultTV.getItems().clear();
//				if (tableDataMap != null && tableDataMap.get(newValue) != null) {
				System.out.println("changed: " + oldVlaue + "->" + newValue);
				syncTableData(newValue);
//				} 
			});

			// Setting inquiry datepicker initial value
			this.inquiryDatePicker.setValue(LocalDate.now().minusDays(1));

			this.unitComboBox.getItems().addAll(FileSize.values());
			FileSize defaultFileSizeUnit = FileSize.valueOf(propertyRepo.getCommonResource("unit.filesize"));
			this.unitComboBox.getSelectionModel().select(defaultFileSizeUnit);
			
			this.roundComboBox.getItems().addAll(List.of(1, 2, 3, 4, 5));
			int defaultRoundingDigits = propertyRepo.getIntegerCommonResource("unit.rounding");
			this.roundComboBox.getSelectionModel().select(Integer.valueOf(defaultRoundingDigits));
			
			// Set pagination count
			this.pagination.setPageCount(1);
		} catch (IOException e) {
		}
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
		Map<String, List<T>> map = data
				.stream()
				.collect(Collectors.groupingBy(m -> m.getMonitoringDateTime(), 
						Collectors.mapping(m -> m, Collectors.toList())));
		
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
	public void syncTableData(String id) {
		// Set Alias comboBox
		aliasComboBox.getSelectionModel().select(id);
		
		// Clear tableView items
		monitoringResultTV.setItems(null);
				
		if(tableDataMap.get(id) == null) {
			pagination.setPageCount(getTableDataCount(id));
			return;
		}
		
		// Set pagination
		pagination.setPageCount(getTableDataCount(id));
		
		// Add tableView items
		Map<String, List<T>> data = tableDataMap.get(id);
		monitoringResultTV.setItems(FXCollections.observableList(data.values().stream().findFirst().get()));
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
	 * TableView�� TableColumn�� �߰��Ѵ�.
	 * 
	 * @param <E>
	 * @param type
	 * @param tcHeaderText
	 */
	public <E> void addTableColumn(E type, String tcHeaderText) {
		monitoringResultTV.getColumns().add(new TableColumn<T, E>(tcHeaderText));
	}

	/*
	 * TableView�� TableColumn�� �߰��ϰ� Property�� �����Ѵ�.
	 * 
	 * @param <E>
	 * @param t
	 * @param tcHeaderText
	 */
	@SuppressWarnings("unchecked")
	public <E> void addAndSetPropertyTableColumn(Class<E> fieldType, String fieldName, String tcHeaderText) {
		TableColumn<T, E> tc = new TableColumn<T, E>(tcHeaderText);
		tc.setCellValueFactory(new PropertyValueFactory<>(fieldName));

		// TODO Usage UI Type���� ��üȭ�Ǵ� TableCellFactory �����
		UsageUIType usageUIType = UsageUIType.find(propertyRepo.getCommonResource("usage-ui-type"));
		if (fieldName.equals("usedPercent")) {
			tc.setCellFactory(col -> {
				TableCell<T, Double> cell = new TableCell<>();
				cell.itemProperty().addListener((observableValue, o, newValue) -> {
					if (newValue != null) {
						UsageUI usageUI = UsageUIFactory.create(usageUIType, newValue, 90);
						cell.graphicProperty()
								.bind(Bindings.when(cell.emptyProperty()).then((UsageUI) null).otherwise(usageUI));
					}
				});
				return (TableCell<T, E>) cell;
			});
		}

		monitoringResultTV.getColumns().add(tc);
	}

	/**
	 * �ش� AnchorPane�� �θ� Node�� Anchor�� �����Ѵ�.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setAnchor(double left, double top, double right, double bottom) {
		AnchorPane.setLeftAnchor(this, left);
		AnchorPane.setTopAnchor(this, top);
		AnchorPane.setRightAnchor(this, right);
		AnchorPane.setBottomAnchor(this, bottom);
	}

	/**
	 * Generic Type Parameter <T>�� ����͸� History�� �о� ���̺� ����Ѵ�.
	 * 
	 * @param e
	 */
	public void run(ActionEvent e) {

		// Get selected inquiry condition
		String inquiryDate = this.inquiryDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String selected = aliasComboBox.getSelectionModel().getSelectedItem();
		FileSize selectedUnit = unitComboBox.getSelectionModel().getSelectedItem();
		int selectedRoundUnit = roundComboBox.getSelectionModel().getSelectedItem();

		// TODO Show Progress UI

		// Clear data
		clearTableData(selected);

		// Acquire data on inquiry date
		Map<String, List<T>> allDataList = reportUsecase.getMonitoringReportDataByTime(this.clazz, selected,
				selectedUnit, selectedRoundUnit, inquiryDate);
		if (allDataList == null || allDataList.size() == 0) {
			AlertUtils.showAlert(AlertType.INFORMATION, "��ȸ��� ����", "�ش������� ����͸� ����� �����ϴ�.");
			return;
		}

		// Add and Sync data
		addTableDataSet(selected, allDataList);
		syncTableData(selected);
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
	
	/*==========================================================================================*/
	
	public void setAliasComboBoxLabelText(String text) {
		this.label.setText(text);
	}
	
	public void setAliasComboBoxItems(String[] items) {
		this.aliasComboBox.getItems().addAll(items);
		this.aliasComboBox.getSelectionModel().selectFirst();
	}
	
	public String getSelectedAliasComboBoxItem() {
		return this.aliasComboBox.getSelectionModel().getSelectedItem();
	}
}
