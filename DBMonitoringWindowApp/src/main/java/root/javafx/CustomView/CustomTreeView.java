package root.javafx.CustomView;

import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Paint;

public class CustomTreeView extends TreeView<String> {

	private static TreeItem<String> rootItem;

	private static final String DEFAULT_ICON_COLOR = "#003b8e";
	private static final int DEFAULT_ICON_SIZE = 17;

	private static final String LEAF_ICON_COLOR = "black";
	private static final int LEAF_ICON_SIZE = 3;

	public CustomTreeView(String rootTitle, FontAwesomeIcon rootIcon, boolean isShowRoot) {
		getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/treeView.css");
		getStyleClass().add("treeView");

		rootItem = new TreeItem<>(rootTitle, getIconView(rootIcon));
		rootItem.setExpanded(true);
		setRoot(rootItem);
		setShowRoot(isShowRoot);

		// this.setCellFactory(treeView -> new MyTreeCell());
	}

	public void addTreeItem(String title, List<String> items, FontAwesomeIcon icon) {
		TreeItem<String> newTreeItem = new TreeItem<>(title, getIconView(icon));
		for (String item : items) {
			newTreeItem.getChildren()
					.add(new TreeItem<>(item, getIconView(FontAwesomeIcon.CIRCLE, LEAF_ICON_SIZE, LEAF_ICON_COLOR)));
		}
		newTreeItem.setExpanded(true);
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
}
