package JavaFx.CustomView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import JavaFx.Model.TypeAndFieldName;
import Root.Model.ArchiveUsage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class MonitoringAnchorPane<T> extends AnchorPane {
	
	@FXML Label label;
	@FXML JFXComboBox<String> comboBox;
	
	// TODO Button을 List<Button>으로 만들어 놓고, 각자 주입받을 수 있도록 구현하기 
	@FXML JFXButton refreshBtn;
	@FXML JFXButton excelDownBtn;
	@FXML JFXButton showGraphBtn;
	@FXML TableView<T> monitoringResultTV;
	
	private Map<String, List<T>> tableDataMap = new HashMap<>();
	
	public MonitoringAnchorPane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFx/resources/fxml/MonitoringAnchorPane.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
			
			this.comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
				monitoringResultTV.getItems().clear();
				if(tableDataMap != null && tableDataMap.get(newValue) != null) {
					monitoringResultTV.getItems().addAll(tableDataMap.get(newValue));
				}
			});
		} catch (IOException e) {
		}
	}
	
	/**
	 * 해당 클래스의 tableData에 데이터를 추가한다.
	 * 단, sync를 하지 않으면 실제 TableView에 데이터가 출력되지는 않는다.
	 * @param id
	 * @param data
	 */
	public void addTableData(String id, T data) {
		if(tableDataMap.get(id) == null) {
			tableDataMap.put(id, new ArrayList<>(Arrays.asList(data)));
		} else {
			tableDataMap.get(id).add(data);	
		}
	}
	
	/**
	 * 해당 클래스의 tableData에 데이터셋을 추가한다.
	 * 단, sync를 하지 않으면 실제 TableView에 데이터가 출력되지는 않는다.
	 * @param id
	 * @param dataList
	 */
	public void addTableDataSet(String id, List<T> dataList) {
		if(tableDataMap.get(id) == null) {
			tableDataMap.put(id, dataList);
		} else {
			tableDataMap.get(id).addAll(dataList);
		}
	}
	
	/**
	 * 해당 클래스의 tableData로 TableView의 데이터를 동기화한다.
	 * @param id
	 */
	public void syncTableData(String id) {
		if(tableDataMap.get(id) == null) {
			return;
		}
		monitoringResultTV.getItems().clear();
		monitoringResultTV.getItems().setAll(tableDataMap.get(id));
		comboBox.getSelectionModel().select(id);
	}
	
	/**
	 * TableView에 TableColumn을 추가한다.
	 * @param <E>
	 * @param type
	 * @param tcHeaderText
	 */
	public <E> void addTableColumn(E type, String tcHeaderText) {
		monitoringResultTV.getColumns().add(new TableColumn<T, E>(tcHeaderText));
	}
	
	/**
	 * TableView의 TableColumn에 Property를 설정한다.
	 * @param <E>
	 * @param index
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public <E> void setTableColumnProperty(int index, TypeAndFieldName t) {
		TableColumn<T, E> tc = (TableColumn<T, E>) this.monitoringResultTV.getColumns().get(index);
		((TableColumn<ArchiveUsage, E>) tc)
		.setCellValueFactory(new PropertyValueFactory<>(t.getFieldName()));
	}
	
	/*
	 * TableView에 TableColumn을 추가하고 Property를 설정한다.
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
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TableColumn<T, ?> getTableColumn(int index) {
		TableColumn<T, ?> tc = this.monitoringResultTV.getColumns().get(index);
		if(tc.getUserData() instanceof String) {
			return (TableColumn<T, String>) tc;
		} else if(tc.getUserData() instanceof Integer) {
			return (TableColumn<T, Integer>) tc;
		} else if(tc.getUserData() instanceof Double) {
			return (TableColumn<T, Double>)  tc;
		}
		
		return tc;
	}
	
	/**
	 * 해당 AnchorPane의 부모 Node에 Anchor를 설정한다. 
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
}
