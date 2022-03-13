package root.javafx.CustomView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.enums.UsageUIType;

public class MonitoringTableViewContainer extends HBox {

	private Map<Class<? extends MonitoringResult>, MonitoringTableView<? extends MonitoringResult>> tableViewMap = new HashMap<>();

	private Map<Class<? extends MonitoringResult>, List<Object>> tableDataListMap = new HashMap<>();

	private static final Map<Class<? extends MonitoringResult>, String> titleMap = new HashMap<>();
	static {
		titleMap.put(ArchiveUsage.class, "Archive 사용량");
		titleMap.put(TableSpaceUsage.class, "TableSpace 사용량");
		titleMap.put(ASMDiskUsage.class, "ASM Disk 사용량");
		titleMap.put(OSDiskUsage.class, "OS Disk 사용량");
	}

	public MonitoringTableViewContainer() {

	}

	/**
	 * 모니터링 결과 TableView를 추가한다.
	 * 
	 * @param type
	 */
	public void addMonitoringTableView(Class<? extends MonitoringResult> type) {
		addMonitoringTableView(type, true);
	}
	
	/**
	 * 모니터링 결과 TableView를 추가한다.
	 * 
	 * @param type
	 */
	public void addMonitoringTableView(Class<? extends MonitoringResult> type, boolean isSimpleTable) {
		VBox tableViewWrapper = new VBox();
		tableViewWrapper.setMinWidth(350);

		Label titleLabel = createTitleLabel(type);
		
		MonitoringTableView<? extends MonitoringResult> tableView = MonitoringTableViewFactory.create(type, isSimpleTable);
		tableViewMap.put(type, tableView);
		tableDataListMap.put(type, new ArrayList<>());

		tableViewWrapper.getChildren().addAll(titleLabel, tableView);
		setHgrow(tableViewWrapper, Priority.ALWAYS);
		getChildren().add(tableViewWrapper);
	}

	private Label createTitleLabel(Class<? extends MonitoringResult> type) {
		Label titleLabel = new Label(titleMap.get(type), new FontAwesomeIconView(FontAwesomeIcon.ASTERISK, "9"));
		titleLabel.setFont(Font.font("Noto Sans Korean Regular"));
		return titleLabel;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MonitoringResult> void setTableData(Class<T> type, List<T> dataList) {
		List<Object> list = tableDataListMap.get(type);
		if (list == null) {
			list = new ArrayList<>();
		}

		clearTableData(type);
		if(dataList != null) {
			list.addAll(dataList);	
		}

		if (tableViewMap.get(type) == null) {
			addMonitoringTableView(type);
		}

		MonitoringTableView<T> tableView = (MonitoringTableView<T>) tableViewMap.get(type);
		tableView.setItems(FXCollections.observableArrayList(dataList));
		tableView.refresh();
	}
	
	public void setUsageUIType(Class<? extends MonitoringResult> type, UsageUIType uiType) {
		tableViewMap.get(type).setUsageUIType(uiType);
	}
	
	public void clearTableData(Class<? extends MonitoringResult> type) {
		tableDataListMap.get(type).clear();
	}
}
