package root.javafx.CustomView;

import java.util.ArrayList;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class TagBar extends FlowPane {

	private final ObservableList<String> tags;
	private final TextField inputTextField;

	public TagBar() {
		getStyleClass().setAll("tag-bar");
		getStylesheets().add(getClass().getResource("/css/javaFx.css").toExternalForm());

		// Initialize
		tags = FXCollections.observableArrayList();
		inputTextField = new TextField();
		
		// Set on action event on TextField (Add tag to tags and clear TextField text)    
		inputTextField.setOnAction(e -> {
			String text = inputTextField.getText();
			if (!text.isEmpty() && !tags.contains(text)) {
				tags.add(text);
				inputTextField.clear();
			}
		});
		getChildren().add(inputTextField);

		// Focusing when mouse click this FlowPane
		setOnMouseClicked(e -> {
			inputTextField.requestFocus();
		});
		
		// Change TextField width according to length of input text
		inputTextField.textProperty().addListener((ob, oldValue, newValue) -> {
			Text textWrapper = new Text(newValue);
			textWrapper.setFont(inputTextField.getFont());
			inputTextField.setPrefWidth(Math.ceil(textWrapper.getLayoutBounds().getWidth()) + 20);
		});

		// Set ListChangeListener on tags list
		tags.addListener((ListChangeListener.Change<? extends String> change) -> {
			while (change.next()) {
				if (change.wasPermutated()) {
					ArrayList<Node> newSublist = new ArrayList<>(change.getTo() - change.getFrom());
					for (int i = change.getFrom(), end = change.getTo(); i < end; i++) {
						newSublist.add(null);
					}

					for (int i = change.getFrom(), end = change.getTo(); i < end; i++) {
						newSublist.set(change.getPermutation(i), getChildren().get(i));
					}

					getChildren().subList(change.getFrom(), change.getTo()).clear();
					getChildren().addAll(change.getFrom(), newSublist);
				} else {
					if (change.wasRemoved()) {
						getChildren().subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
					}

					if (change.wasAdded()) {
						getChildren().addAll(change.getFrom(),
								change.getAddedSubList().stream().map(Tag::new).collect(Collectors.toList()));
					}
				}
			}
		});
	}

	public ObservableList<String> getTags() {
		return tags;
	}

	private class Tag extends HBox {

		public Tag(String tag) {
			getStyleClass().setAll("tag");
			FlowPane.setMargin(this, new Insets(2.5));

			FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
			icon.setFill(Paint.valueOf("gray"));

			Button removeButton = new Button("", icon);
			removeButton.setOnAction((e) -> tags.remove(tag));

			Text text = new Text(tag);
			HBox.setMargin(text, new Insets(0, 0, 0, 5));

			getChildren().addAll(text, removeButton);
		}
	}

}