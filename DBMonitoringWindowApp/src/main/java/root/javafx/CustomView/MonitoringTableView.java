package root.javafx.CustomView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.UsageUIType;
import root.javafx.CustomView.UsageUI.UsageUI;
import root.javafx.CustomView.UsageUI.UsageUIFactory;
import root.utils.DateUtils;

public class MonitoringTableView<T extends MonitoringResult> extends TableView<T> {

	private Map<String, TableColumn<T, Object>> tableColumnMap = new HashMap<>();

	public MonitoringTableView() {

		getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/tableview.css");
		getStyleClass().add("tableView");

		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setTableMenuButtonVisible(true);
	}

	public void addColumn(String title, String fieldName) {
		TableColumn<T, Object> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(fieldName));
		tableColumnMap.put(fieldName, column);
		getColumns().add(column);
	}

	public void setUsageUIType(UsageUIType usageUIType) {
		TableColumn<T, Object> tc = tableColumnMap.get("usedPercent");
		if(tc == null) {
			return;
		}
		
		tc.setCellFactory(col -> {
			TableCell<T, Object> cell = new TableCell<>();
			cell.itemProperty().addListener((observableValue, o, newValue) -> {
				if (newValue != null) {
					UsageUI usageUI = UsageUIFactory.create(usageUIType, Double.parseDouble(newValue.toString()), 90);
					cell.graphicProperty()
							.bind(Bindings.when(cell.emptyProperty()).then((UsageUI) null).otherwise(usageUI));
				}
			});
			return cell;
		});
	}

	public void setMonitoringDateTimeFormat(String format) {
		TableColumn<T, Object> tc = tableColumnMap.get("monitoringDateTime");
		if(tc == null) {
			return;
		}
		
		tc.setCellValueFactory(cell -> {
			String value = DateUtils.convertDateFormat("yyyyMMddHHmmss", format,
					cell.getValue().getMonitoringDateTime(), Locale.KOREA);
			SimpleObjectProperty<Object> result = new SimpleObjectProperty<>();
			result.setValue(value);
			return result;
		});
	}
}
