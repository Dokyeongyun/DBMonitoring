package root.javafx.CustomView;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import root.core.domain.MonitoringResult;

public class MonitoringTableView<T> extends TableView<T> {

	private Class<? extends MonitoringResult> clazz;

	public MonitoringTableView(Class<? extends MonitoringResult> clazz) {

		this.clazz = clazz;

		getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/tableview.css");
		getStyleClass().add("tableView");
		
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setTableMenuButtonVisible(true);

	}
	
	public void addColumn(String title, String fieldName) {
		TableColumn<T, String> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(fieldName));
//		column.setCellFactory(param -> {
//		});
		getColumns().add(column);
	}
}
