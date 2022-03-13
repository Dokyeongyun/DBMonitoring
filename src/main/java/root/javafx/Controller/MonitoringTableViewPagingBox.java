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
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.enums.UsageUIType;
import root.javafx.CustomView.MonitoringTableView;
import root.javafx.CustomView.MonitoringTableViewFactory;
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

	private static final Map<Class<? extends MonitoringResult>, String> titleMap = new HashMap<>();
	static {
		titleMap.put(ArchiveUsage.class, "Archive 사용량");
		titleMap.put(TableSpaceUsage.class, "TableSpace 사용량");
		titleMap.put(ASMDiskUsage.class, "ASM Disk 사용량");
		titleMap.put(OSDiskUsage.class, "OS Disk 사용량");
	}

	private Map<Class<? extends MonitoringResult>, MonitoringTableView<? extends MonitoringResult>> tableViewMap = new HashMap<>();

	private Map<Class<? extends MonitoringResult>, List<Object>> tableDataListMap = new HashMap<>();

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
	private void addMonitoringTableView(Class<? extends MonitoringResult> type, UsageUIType usageUIType) {
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

		MonitoringTableView<? extends MonitoringResult> tableView = MonitoringTableViewFactory.create(type, usageUIType);
		AnchorPane.setTopAnchor(tableView, 20.0);
		AnchorPane.setLeftAnchor(tableView, 0.0);
		AnchorPane.setRightAnchor(tableView, 0.0);
		AnchorPane.setBottomAnchor(tableView, 0.0);
		tableViewMap.put(type, tableView);

		tableViewWrapper.getChildren().addAll(titleLabel, tableView);
		tableViewHBox.getChildren().add(tableViewWrapper);
	}

	@SuppressWarnings("unchecked")
	public <T extends MonitoringResult> void setTableData(String alias, Class<T> type, List<T> dataList,
			UsageUIType usageUIType) {
		List<Object> list = tableDataListMap.get(type);
		if (list == null) {
			list = new ArrayList<>();
		}

		list.clear();
		list.addAll(dataList);

		if (tableViewMap.get(type) == null) {
			addMonitoringTableView(type, usageUIType);
		}

		MonitoringTableView<T> tableView = (MonitoringTableView<T>) tableViewMap.get(type);
		tableView.setItems(FXCollections.observableArrayList(dataList));
	}
}
