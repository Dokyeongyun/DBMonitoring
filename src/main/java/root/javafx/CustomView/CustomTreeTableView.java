package root.javafx.CustomView;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.paint.Paint;
import root.javafx.Model.MonitoringYN;

public class CustomTreeTableView extends TreeTableView<MonitoringYN> {

	private static TreeItem<MonitoringYN> rootItem;

	private static final String DEFAULT_ICON_COLOR = "#003b8e";
	private static final int DEFAULT_ICON_SIZE = 17;

	private static final String LEAF_ICON_COLOR = "black";
	private static final int LEAF_ICON_SIZE = 3;

	public CustomTreeTableView(String rootTitle, FontAwesomeIcon rootIcon) {
		getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/treeView.css");
		getStyleClass().add("treeView");

		rootItem = new TreeItem<>(new MonitoringYN());

		// TODO TableColumn을 View 외부에서 주입받을 수 있도록 수정해야 함
		addMonitoringInstanceColumn("Instance", "alias");
		addMonitoringYNTableColumn("Archive", "archiveUsageYN");
		addMonitoringYNTableColumn("Table Space", "tableSpaceUsageYN");
		addMonitoringYNTableColumn("ASM Disk", "asmDiskUsageYN");
		addMonitoringYNTableColumn("OS Disk", "osDiskUsageYN");

		setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setRoot(rootItem);
		setShowRoot(false);
	}

	private void addMonitoringInstanceColumn(String title, String fieldName) {
		TreeTableColumn<MonitoringYN, String> ttc = new TreeTableColumn<>(title);
		ttc.setPrefWidth(100);
		ttc.setCellValueFactory(new TreeItemPropertyValueFactory<>(fieldName));
		getColumns().add(ttc);
	}

	private void addMonitoringYNTableColumn(String title, String fieldName) {
		TreeTableColumn<MonitoringYN, String> ttc = new TreeTableColumn<>(title);
		ttc.setPrefWidth(70);
		ttc.setCellValueFactory(new TreeItemPropertyValueFactory<>(fieldName));
		ttc.setCellFactory(param -> {
			return new MonitoringYNCell();
		});
		getColumns().add(ttc);
	}

	public void addTreeTableItem(String title, List<MonitoringYN> items, FontAwesomeIcon icon) {
		TreeItem<MonitoringYN> newTreeItem = new TreeItem<>(new MonitoringYN(title), getIconView(icon));
		for (MonitoringYN item : items) {
			newTreeItem.getChildren()
					.add(new TreeItem<>(item, getIconView(FontAwesomeIcon.CIRCLE, LEAF_ICON_SIZE, LEAF_ICON_COLOR)));
			newTreeItem.setExpanded(true);
		}
		rootItem.getChildren().add(newTreeItem);
	}

	private FontAwesomeIconView getIconView(FontAwesomeIcon icon, int size, String color) {
		FontAwesomeIconView result = new FontAwesomeIconView(icon, String.valueOf(size));
		result.setFill(Paint.valueOf(color));
		return result;
	}

	private FontAwesomeIconView getIconView(FontAwesomeIcon icon) {
		return getIconView(icon, DEFAULT_ICON_SIZE, DEFAULT_ICON_COLOR);
	}

	/**
	 * 
	 * @author DKY
	 *
	 */
	private static class MonitoringYNCell extends TreeTableCell<MonitoringYN, String> {

		public MonitoringYNCell() {
			setAlignment(Pos.CENTER);
			itemProperty().addListener((observableValue, oldValue, newValue) -> {
				if (newValue != null) {
					FontAwesomeIconView icon = getMonitoringYNIcon(newValue);
					graphicProperty().bind(Bindings.when(emptyProperty()).then((Node) null).otherwise(icon));
				}
			});
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setStyle(null);
				graphicProperty().bind(Bindings.when(emptyProperty()).then((Node) null).otherwise((Node) null));
			} else {
				FontAwesomeIconView icon = getMonitoringYNIcon(item);
				graphicProperty().bind(Bindings.when(emptyProperty()).then((Node) null).otherwise(icon));
			}
		}

		private FontAwesomeIconView getMonitoringYNIcon(String item) {
			FontAwesomeIconView icon = new FontAwesomeIconView(
					item.equals("Y") ? FontAwesomeIcon.CHECK : FontAwesomeIcon.TIMES);
			icon.setFill(Paint.valueOf(item.equals("Y") ? "#49a157" : "#c40a0a"));
			graphicProperty().bind(Bindings.when(emptyProperty()).then((Node) null).otherwise(icon));
			return icon;
		}
	}
}
