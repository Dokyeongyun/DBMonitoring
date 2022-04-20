package root.javafx.CustomView;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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
	TextArea logContentTA;

	private Text textHolder = new Text();

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

			logContentTA.widthProperty().addListener((observable, oldValue, newValue) -> {
				Text text = new Text();
				text.setWrappingWidth(newValue.doubleValue());
				text.setText(logContentTA.getText());
				logContentTA.setPrefHeight(text.getLayoutBounds().getHeight() * 1.35);
			});

			logTimeLabel.setText(logObj.getLogTimeStamp());
			logContentTA.setText(logObj.getFullLogString());
			logContentTA.setEditable(false);
			logContentTA.setWrapText(true);

			textHolder.textProperty().bind(logContentTA.textProperty());

			setText(null);
			setGraphic(rootAP);
		}
	}
}
