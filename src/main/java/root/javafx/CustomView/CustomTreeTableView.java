package root.javafx.CustomView;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.paint.Paint;
import root.javafx.Model.MonitoringYN;

public class CustomTreeTableView<T extends MonitoringYN> extends TreeTableView<T> {

	private TreeItem<T> rootItem;

	private static final String DEFAULT_ICON_COLOR = "#003b8e";
	private static final int DEFAULT_ICON_SIZE = 17;

	private static final String LEAF_ICON_COLOR = "black";
	private static final int LEAF_ICON_SIZE = 3;

	public CustomTreeTableView(String rootTitle, FontAwesomeIcon rootIcon) {
		getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/treeView.css");
		getStyleClass().add("treeView");

		rootItem = new TreeItem<>();

		setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setRoot(rootItem);
		setShowRoot(false);
	}

	public void addMonitoringInstanceColumn(String title, String fieldName) {
		TreeTableColumn<T, String> ttc = new TreeTableColumn<>(title);
		ttc.setCellValueFactory(new TreeItemPropertyValueFactory<>(fieldName));
		ttc.setCellFactory(param -> {
			return new MonitoringInstanceCell<>();
		});
		ttc.setPrefWidth(90);
		ttc.setMinWidth(90);
		ttc.setMaxWidth(90);
		getColumns().add(ttc);
	}

	public void addMonitoringYNTableColumn(String title, String fieldName) {
		TreeTableColumn<T, String> ttc = new TreeTableColumn<>(title);
		ttc.setCellValueFactory(new TreeItemPropertyValueFactory<>(fieldName));
		ttc.setCellFactory(param -> {
			return new MonitoringYNCell<>();
		});
		getColumns().add(ttc);
	}

	public void addTreeTableItem(T title, List<T> items, FontAwesomeIcon icon) {
		TreeItem<T> newTreeItem = new TreeItem<>(title);
		for (T item : items) {
			newTreeItem.getChildren().add(new TreeItem<>(item));
			newTreeItem.setExpanded(true);
		}
		rootItem.getChildren().add(newTreeItem);
	}

	private static FontAwesomeIconView getIconView(FontAwesomeIcon icon, int size, String color) {
		FontAwesomeIconView result = new FontAwesomeIconView(icon, String.valueOf(size));
		result.setFill(Paint.valueOf(color));
		return result;
	}

	private static FontAwesomeIconView getIconView(FontAwesomeIcon icon) {
		return getIconView(icon, DEFAULT_ICON_SIZE, DEFAULT_ICON_COLOR);
	}

	/**
	 * 
	 * @author DKY
	 *
	 */
	private static class MonitoringInstanceCell<T> extends TreeTableCell<T, String> {

		public MonitoringInstanceCell() {
			setAlignment(Pos.CENTER_LEFT);
			itemProperty().addListener((observableValue, oldValue, newValue) -> {
				if (newValue != null) {
					Label label = getMonitoringInstanceLabel(newValue);
					graphicProperty().bind(Bindings.when(emptyProperty()).then(label).otherwise(label));
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
				Label label = getMonitoringInstanceLabel(item);
				graphicProperty().bind(Bindings.when(emptyProperty()).then(label).otherwise(label));
			}
		}

		private Label getMonitoringInstanceLabel(String item) {
			Label label = new Label(item);
			FontAwesomeIconView icon;
			if (item.equals("DB")) {
				icon = getIconView(FontAwesomeIcon.DATABASE);
				label.setPadding(new Insets(0, 0, 0, 15));
			} else if (item.equals("Server")) {
				icon = getIconView(FontAwesomeIcon.SERVER);
				label.setPadding(new Insets(0, 0, 0, 15));
			} else {
				icon = getIconView(FontAwesomeIcon.CIRCLE, LEAF_ICON_SIZE, LEAF_ICON_COLOR);
				label.setPadding(new Insets(0, 0, 0, 20));
			}
			label.setGraphic(icon);
			return label;
		}
	}

	/**
	 * 
	 * @author DKY
	 *
	 */
	private static class MonitoringYNCell<T> extends TreeTableCell<T, String> {

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
