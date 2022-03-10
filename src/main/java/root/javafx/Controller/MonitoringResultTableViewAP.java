package root.javafx.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.MonitoringType;
import root.javafx.CustomView.MonitoringTableView;
import root.javafx.DI.DependencyInjection;

public class MonitoringResultTableViewAP extends AnchorPane {

	@FXML
	private Label aliasLabel;

	@FXML
	private Label monitoringTimeLabel;

	@FXML
	private StackPane tableViewSP;

	@FXML
	private HBox tableViewHBox;

	private static final Map<MonitoringType, String> titleMap = new HashMap<>();
	static {
		titleMap.put(MonitoringType.ARCHIVE, "Archive 사용량");
		titleMap.put(MonitoringType.TABLE_SPACE, "TableSpace 사용량");
		titleMap.put(MonitoringType.ASM_DISK, "ASM Disk 사용량");
		titleMap.put(MonitoringType.OS_DISK, "OS Disk 사용량");
	}

	private Map<MonitoringType, MonitoringTableView<? extends MonitoringResult>> tableViewMap = new HashMap<>();

	private Map<MonitoringType, List<Object>> tableDataListMap = new HashMap<>();

	public MonitoringResultTableViewAP() {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/MonitoringResultTableViewAP.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 모니터링 결과 TableView를 추가한다.
	 * 
	 * @param type
	 */
	private void addMonitoringTableView(MonitoringType type) {
		AnchorPane tableViewWrapper = new AnchorPane();
		tableViewWrapper.setMinWidth(350);

		Label titleLabel = new Label();
		titleLabel.setText(titleMap.get(type));
		titleLabel.setFont(Font.font("Noto Sans Korean Regular"));

		FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.ASTERISK, "9");
		titleLabel.setGraphic(icon);

		AnchorPane.setTopAnchor(titleLabel, 0.0);
		AnchorPane.setLeftAnchor(titleLabel, 0.0);
		AnchorPane.setRightAnchor(titleLabel, 0.0);

		MonitoringTableView<? extends MonitoringResult> tableView = new MonitoringTableView<>();
		AnchorPane.setTopAnchor(tableView, 20.0);
		AnchorPane.setLeftAnchor(tableView, 0.0);
		AnchorPane.setRightAnchor(tableView, 0.0);
		AnchorPane.setBottomAnchor(tableView, 0.0);
		tableViewMap.put(type, tableView);

		tableViewWrapper.getChildren().addAll(titleLabel, tableView);
		tableViewHBox.getChildren().add(tableViewWrapper);
	}

	/**
	 * TableView 컬럼을 추가한다.
	 * 
	 * @param type
	 * @param title
	 * @param fieldName
	 */
	public void addTableViewColumn(MonitoringType type, String title, String fieldName) {
		if (tableViewMap.get(type) == null) {
			addMonitoringTableView(type);
		}
		tableViewMap.get(type).addColumn(title, fieldName);
	}

	public <T extends MonitoringResult> void setTableData(MonitoringType type, List<T> dataList) {
		List<Object> list = tableDataListMap.get(type);
		if (list == null) {
			list = new ArrayList<>();
		}
		
		list.clear();
		list.addAll(dataList);

		MonitoringTableView<T> tableView = (MonitoringTableView<T>) tableViewMap.get(type);
		tableView.setItems(FXCollections.observableArrayList(dataList));
	}
}
