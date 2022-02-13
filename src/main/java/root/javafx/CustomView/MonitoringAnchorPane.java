package root.javafx.CustomView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import root.core.domain.MonitoringResult;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.usecase.constracts.ReportUsecase;
import root.core.usecase.implement.ReportUsecaseImpl;
import root.utils.AlertUtils;
import root.utils.UnitUtils.FileSize;

public class MonitoringAnchorPane<T extends MonitoringResult> extends AnchorPane {

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

	private Class<T> clazz;
	
	private Map<String, List<T>> tableDataMap = new HashMap<>();

	public MonitoringAnchorPane(Class<T> clazz) {
		this.reportUsecase = new ReportUsecaseImpl(ReportFileRepo.getInstance());
		
		try {
			this.clazz = clazz;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MonitoringAnchorPane.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

			// Add comoboBox click listner
			this.aliasComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
				monitoringResultTV.getItems().clear();
				if (tableDataMap != null && tableDataMap.get(newValue) != null) {
					monitoringResultTV.getItems().addAll(tableDataMap.get(newValue));
				}
			});

			// Setting inquiry datepicker initial value
			this.inquiryDatePicker.setValue(LocalDate.now().minusDays(1));

			this.unitComboBox.getItems().addAll(FileSize.values());
			FileSize defaultFileSizeUnit = FileSize.valueOf(propertyRepo.getCommonResource("unit.filesize"));
			this.unitComboBox.getSelectionModel().select(defaultFileSizeUnit);
			
			this.roundComboBox.getItems().addAll(List.of(1, 2, 3, 4, 5));
			int defaultRoundingDigits = propertyRepo.getIntegerCommonResource("unit.rounding");
			this.roundComboBox.getSelectionModel().select(Integer.valueOf(defaultRoundingDigits));
		} catch (IOException e) {
		}
	}

	/**
	 * 해당 클래스의 tableData의 데이터를 초기화한다.
	 * 
	 * @param id
	 */
	public void clearTableData(String id) {
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
	public void addTableData(String id, T data) {
		if (tableDataMap.get(id) == null) {
			tableDataMap.put(id, new ArrayList<>(Arrays.asList(data)));
		} else {
			tableDataMap.get(id).add(data);
		}
	}

	/**
	 * 해당 클래스의 tableData에 데이터셋을 추가한다. 단, sync를 하지 않으면 실제 TableView에 데이터가 출력되지는 않는다.
	 * 
	 * @param id
	 * @param dataList
	 */
	public void addTableDataSet(String id, List<T> dataList) {
		if (tableDataMap.get(id) == null) {
			tableDataMap.put(id, dataList);
		} else {
			tableDataMap.get(id).addAll(dataList);
		}
	}

	/**
	 * 해당 클래스의 tableData로 TableView의 데이터를 동기화한다.
	 * 
	 * @param id
	 */
	public void syncTableData(String id) {
		if (tableDataMap.get(id) == null) {
			return;
		}
		monitoringResultTV.setItems(null);
		monitoringResultTV.setItems(FXCollections.observableList(tableDataMap.get(id)));
		aliasComboBox.getSelectionModel().select(id);
	}
	
	/**
	 * TableView에 TableColumn을 추가한다.
	 * 
	 * @param <E>
	 * @param type
	 * @param tcHeaderText
	 */
	public <E> void addTableColumn(E type, String tcHeaderText) {
		monitoringResultTV.getColumns().add(new TableColumn<T, E>(tcHeaderText));
	}

	/*
	 * TableView에 TableColumn을 추가하고 Property를 설정한다.
	 * 
	 * @param <E>
	 * @param t
	 * @param tcHeaderText
	 */
	@SuppressWarnings("unchecked")
	public <E> void addAndSetPropertyTableColumn(Class<E> fieldType, String fieldName, String tcHeaderText) {
		TableColumn<T, E> tc = new TableColumn<T, E>(tcHeaderText);
		tc.setCellValueFactory(new PropertyValueFactory<>(fieldName));

		if (fieldName.equals("usedPercent")) {
			tc.setCellFactory(col -> {
				TableCell<T, Double> cell = new TableCell<>();
				cell.itemProperty().addListener((observableValue, o, newValue) -> {
					if (newValue != null) {
						ProgressBar usage = new ProgressBar(newValue / 100.0);
						ProgressBar noUsage = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
						cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then(noUsage).otherwise(usage));
					}
				});
			    return (TableCell<T, E>) cell;
			});
		}

		monitoringResultTV.getColumns().add(tc);
	}

	/**
	 * 해당 AnchorPane의 부모 Node에 Anchor를 설정한다.
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
	 * Generic Type Parameter <T>의 모니터링 History를 읽어 테이블에 출력한다.
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

		// Acquire data
		List<T> allDataList = reportUsecase.getMonitoringReportData(this.clazz, selected, selectedUnit, selectedRoundUnit);
		if (allDataList == null) {
			AlertUtils.showAlert(AlertType.INFORMATION, "조회결과 없음",
					String.format("%s 의 모니터링 기록이 없습니다.\n데이터를 확인해주세요.", selected));
			return;
		}
		
		// Find data on inquiry date
		List<T> tableDataList = allDataList.stream()
				.filter(data -> data.getMonitoringDate().equals(inquiryDate))
				.collect(Collectors.toList());

		if (tableDataList.size() == 0) {
			AlertUtils.showAlert(AlertType.INFORMATION, "조회결과 없음", "해당일자의 모니터링 기록이 없습니다.");
		}

		// Add and Sync data
		addTableDataSet(selected, tableDataList);
		syncTableData(selected);
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
