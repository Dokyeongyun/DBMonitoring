package root.javafx.CustomView;

import java.util.HashSet;
import java.util.Set;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ExpandableListView<E> extends ListView<E> {

	private ContentProvider<E> contentProvider = new ContentProvider<E>() {
		@Override
		public String getTitleOf(final E item) {
			return item.toString();
		}

		@Override
		public String getContentOf(final E item) {
			return getTitleOf(item);
		}
	};

	private final Set<E> expandedItems = new HashSet<E>();

	public ExpandableListView(FontAwesomeIcon icon, Paint iconColor) {
		setSelectionModel(null);
		setCellFactory(new Callback<ListView<E>, ListCell<E>>() {
			@Override
			public ListCell<E> call(final ListView<E> param) {
				final TitledPane titledPane = new TitledPane();
				final Text contentArea = new Text();
				final FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
				iconView.setFill(Paint.valueOf("#183279"));
				
				titledPane.setAnimated(false);
				titledPane.setCollapsible(true);
				titledPane.setExpanded(false);
				titledPane.setGraphic(iconView);
				titledPane.setContent(contentArea);

				final BorderPane contentAreaWrapper = new BorderPane();
				contentAreaWrapper.setLeft(contentArea);
				titledPane.setContent(contentAreaWrapper);

				titledPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@SuppressWarnings("unchecked")
					@Override
					public void handle(final MouseEvent event) {
						final boolean expanded = titledPane.isExpanded();
						final E item = (E) titledPane.getUserData();
						if (item == null) {
							return;
						}
						if (expanded) {
							expandedItems.add(item);
						} else {
							expandedItems.remove(item);
						}
					}
				});

				return new ListCell<E>() {
					@Override
					protected void updateItem(final E item, final boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							titledPane.setText("");
							contentArea.setText("");
							return;
						}
						final boolean expanded = isExpanded(item);
						titledPane.setUserData(item);
						titledPane.setExpanded(expanded);
						titledPane.setText(contentProvider.getTitleOf(item));
						contentArea.setText(contentProvider.getContentOf(item));
						setGraphic(titledPane);
					}
				};
			}
		});
		getStyleClass().addAll("expandable-listview", "gray-scrollbar");
	}

	public void setContentProvider(final ContentProvider<E> contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void expand(E item) {
		expand(item, true);
	}

	public void collapse(E item) {
		expand(item, false);
	}

	private void expand(E item, boolean expand) {
		if (expand) {
			this.expandedItems.add(item);
		} else {
			this.expandedItems.remove(item);
		}

		ObservableList<E> o = getItems();
		setItems(null);
		setItems(o);
	}

	public boolean isExpanded(E item) {
		return this.expandedItems.contains(item);
	}

	public static interface ContentProvider<E> {
		String getTitleOf(E item);

		String getContentOf(E item);
	}

	public static class Item {
		String title;
		String content;

		public Item(String title, String content) {
			this.title = title;
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}
	}
}