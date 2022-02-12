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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import lombok.EqualsAndHashCode;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.repository.implement.ReportFileRepo;
import root.core.usecase.constracts.ReportUsecase;
import root.core.usecase.implement.ReportUsecaseImpl;
import root.javafx.Model.TypeAndFieldName;
import root.utils.AlertUtils;
import root.utils.UnitUtils.FileSize;

@EqualsAndHashCode(callSuper = false)
@Data
public class MonitoringAnchorPane<T extends MonitoringResult> extends AnchorPane {

	private ReportUsecase reportUsecase;
	
	@FXML
	Label label;

	@FXML
	JFXComboBox<String> comboBox;

	// TODO Button�� List<Button>���� ����� ����, ���� ���Թ��� �� �ֵ��� �����ϱ�
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
		this.reportUsecase = new ReportUsecaseImpl(ReportFileRepo.getInstance());
		
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
	public void addTableData(String id, T data) {
		if (tableDataMap.get(id) == null) {
			tableDataMap.put(id, new ArrayList<>(Arrays.asList(data)));
		} else {
			tableDataMap.get(id).add(data);
		}
	}

	/**
	 * �ش� Ŭ������ tableData�� �����ͼ��� �߰��Ѵ�. ��, sync�� ���� ������ ���� TableView�� �����Ͱ� ��µ����� �ʴ´�.
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
	 * �ش� Ŭ������ tableData�� TableView�� �����͸� ����ȭ�Ѵ�.
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
	 * TableView�� TableColumn�� �߰��Ѵ�.
	 * 
	 * @param <E>
	 * @param type
	 * @param tcHeaderText
	 */
	public <E> void addTableColumn(E type, String tcHeaderText) {
		monitoringResultTV.getColumns().add(new TableColumn<T, E>(tcHeaderText));
	}

	/**
	 * TableView�� TableColumn�� Property�� �����Ѵ�.
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
	 * TableView�� TableColumn�� �߰��ϰ� Property�� �����Ѵ�.
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
	 * TableView�� �߰��� TableColumn�� ��´�.
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
		String selected = getComboBox().getSelectionModel().getSelectedItem();

		// TODO Show Progress UI

		// Clear data
		clearTableData(selected);

		// Acquire data
		List<T> allDataList = reportUsecase.getMonitoringReportData(getClazz(), selected, FileSize.GB, 2);
		if (allDataList == null) {
			AlertUtils.showAlert(AlertType.INFORMATION, "��ȸ��� ����",
					String.format("%s �� ����͸� ����� �����ϴ�.\n�����͸� Ȯ�����ּ���.", selected));
			return;
		}
		
		// Find data on inquiry date
		List<T> tableDataList = allDataList.stream()
				.filter(data -> data.getMonitoringDate().equals(inquiryDate))
				.collect(Collectors.toList());

		if (tableDataList.size() == 0) {
			AlertUtils.showAlert(AlertType.INFORMATION, "��ȸ��� ����", "�ش������� ����͸� ����� �����ϴ�.");
		}

		// Add and Sync data
		addTableDataSet(selected, tableDataList);
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
}
