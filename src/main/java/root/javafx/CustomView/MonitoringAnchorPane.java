package root.javafx.CustomView;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.opencsv.bean.CsvToBeanBuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import root.core.domain.ArchiveUsage;
import root.javafx.Model.TypeAndFieldName;

@EqualsAndHashCode(callSuper= false)
@Data
@Slf4j
public class MonitoringAnchorPane<T> extends AnchorPane {

	@FXML
	Label label;

	@FXML
	JFXComboBox<String> comboBox;

	// TODO Button을 List<Button>으로 만들어 놓고, 각자 주입받을 수 있도록 구현하기
	@FXML
	JFXButton refreshBtn;

	@FXML
	JFXButton excelDownBtn;

	@FXML
	JFXButton showGraphBtn;

	@FXML
	TableView<T> monitoringResultTV;
	
	@FXML
	DatePicker inquiryDatePicker;

	private Class<T> clazz;
	private String reportFilePath;
	private Map<String, List<T>> tableDataMap = new HashMap<>();

	public MonitoringAnchorPane(Class<T> clazz) {
		try {
			this.clazz = clazz;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MonitoringAnchorPane.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

			// Add comoboBox click listner
			this.comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
				monitoringResultTV.getItems().clear();
				if (tableDataMap != null && tableDataMap.get(newValue) != null) {
					monitoringResultTV.getItems().addAll(tableDataMap.get(newValue));
				}
			});
			
			// Setting inquiry datepicker initial value
			this.inquiryDatePicker.setValue(LocalDate.now().minusDays(1));
			
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
		monitoringResultTV.getItems().clear();
		monitoringResultTV.getItems().setAll(tableDataMap.get(id));
		comboBox.getSelectionModel().select(id);
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

	/**
	 * TableView의 TableColumn에 Property를 설정한다.
	 * 
	 * @param <E>
	 * @param index
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public <E> void setTableColumnProperty(int index, TypeAndFieldName t) {
		TableColumn<T, E> tc = (TableColumn<T, E>) this.monitoringResultTV.getColumns().get(index);
		((TableColumn<ArchiveUsage, E>) tc).setCellValueFactory(new PropertyValueFactory<>(t.getFieldName()));
	}

	/*
	 * TableView에 TableColumn을 추가하고 Property를 설정한다.
	 * 
	 * @param <E>
	 * @param t
	 * @param tcHeaderText
	 */
	public <E> void addAndSetPropertyTableColumn(TypeAndFieldName t, String tcHeaderText) {
		TableColumn<T, E> tc = new TableColumn<T, E>(tcHeaderText);
		tc.setCellValueFactory(new PropertyValueFactory<>(t.getFieldName()));
		monitoringResultTV.getColumns().add(tc);
	}

	/**
	 * TableView에 추가된 TableColumn을 얻는다.
	 * 
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TableColumn<T, ?> getTableColumn(int index) {
		TableColumn<T, ?> tc = this.monitoringResultTV.getColumns().get(index);
		if (tc.getUserData() instanceof String) {
			return (TableColumn<T, String>) tc;
		} else if (tc.getUserData() instanceof Integer) {
			return (TableColumn<T, Integer>) tc;
		} else if (tc.getUserData() instanceof Double) {
			return (TableColumn<T, Double>) tc;
		}

		return tc;
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
		String selected = getComboBox().getSelectionModel().getSelectedItem();
		String fileRootDir = getReportFilePath() + selected + "/";
		File reportList = new File(fileRootDir);

		// Clear data
		clearTableData(selected);

		// Read csv report file and add table data
		List<T> tableDataList = new ArrayList<>();
		for (String fileName : reportList.list()) {
			
			if(!fileName.startsWith(inquiryDate)) {
				continue;
			}
			
			String filePath = fileRootDir + fileName;
			File reportFile = new File(filePath);

			List<T> data = parseCsvReportFile(reportFile);
			if (data != null) {
				tableDataList.addAll(data);
			}
		}
		
		addTableDataSet(selected, tableDataList);
		syncTableData(selected);
	}

	/**
	 * csv 파일을 읽어 Model 객체로 변환한다.
	 * 
	 * @param file
	 * @return
	 */
	private List<T> parseCsvReportFile(File file) {
		List<T> result = null;
		try {

			result = new CsvToBeanBuilder<T>(new FileReader(file))
					.withSkipLines(1)
					.withSeparator(',')
					.withIgnoreEmptyLine(true)
					.withType(getClazz())
					.build()
					.parse();
			
		} catch (Exception e) {
			log.error("Parsing error!" + file);
		}
		return result;
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
}
