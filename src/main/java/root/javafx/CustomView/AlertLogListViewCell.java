package root.javafx.CustomView;

import java.io.IOException;

import org.fxmisc.richtext.StyleClassedTextArea;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import root.core.domain.Log;
import root.javafx.DI.DependencyInjection;

@Slf4j
public class AlertLogListViewCell extends ListCell<Log> {
	private FXMLLoader loader;

	@FXML
	AnchorPane rootAP;
	@FXML
	Label logTimeLabel;
	@FXML
	Label logIndexLabel;
	@FXML
	FontAwesomeIconView logStatusIcon;
	@FXML
	HBox logContentHBox;

	public AlertLogListViewCell(String... highlightKeywords) {
	}

	@Override
	protected void updateItem(Log logObj, boolean empty) {
		super.updateItem(logObj, empty);

		if (empty || logObj == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (loader == null) {
				FXMLLoader loader = DependencyInjection.getLoader("/fxml/AlertLogListViewCell.fxml");
				loader.setController(this);
				try {
					loader.load();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}

			boolean isErrorLog = isErrorLog(logObj.getFullLogString());

			// logTimeStamp
			logTimeLabel.setText(logObj.getLogTimeStamp());

			// logIndex
			logIndexLabel.setText(String.valueOf(logObj.getIndex() + 1));

			// logStatusIcon
			logStatusIcon.setFill(Paint.valueOf(isErrorLog ? "#d92a2a" : "#4d9c84"));

			// logContent
			StyleClassedTextArea codeArea = new StyleClassedTextArea();
			codeArea.setPadding(new Insets(2, 5, 0, 5));
			codeArea.setWrapText(false);
			codeArea.setEditable(false);
			codeArea.autosize();
			codeArea.setFocusTraversable(true);
			HBox.setHgrow(codeArea, Priority.ALWAYS);

			String baseDir = System.getProperty("resourceBaseDir");
			codeArea.getStylesheets()
					.add(getClass().getResource(baseDir + "/css/alertLogListViewCell.css").toExternalForm());
			codeArea.getStyleClass().add("code-area");

			codeArea.replaceText(0, 0, logObj.getFullLogString());
			if (isErrorLog) {
				codeArea.getStyleClass().add(".error");
			}

			// Set initial height
			Text text = new Text();
			text.setWrappingWidth(logContentHBox.widthProperty().doubleValue());
			text.setText(codeArea.getText());
			codeArea.setPrefHeight(text.getLayoutBounds().getHeight());
			logContentHBox.getChildren().addAll(codeArea);

			setText(null);
			setGraphic(rootAP);
			setStyle("-fx-padding: 0");

			// Propagate scroll event to parent listview
			codeArea.addEventFilter(ScrollEvent.ANY, scroll -> {
				codeArea.getParent().getParent().getParent().fireEvent(scroll);
				scroll.consume();
			});
		}
	}

	private boolean isErrorLog(String logContent) {
		// TODO Remove hard-coding that identifying error log
		return logContent.contains("ORA-");
	}
}
