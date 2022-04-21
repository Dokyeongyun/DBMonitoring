package root.javafx.CustomView;

import java.io.IOException;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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

		
			boolean isErrorLog = isErrorLog(logObj.getFullLogString());
			
			// logTimeStamp
			logTimeLabel.setText(logObj.getLogTimeStamp());

			// logIndex
			logIndexLabel.setText(String.valueOf(logObj.getIndex() + 1));

			// logStatusIcon
			logStatusIcon.setFill(Paint.valueOf(isErrorLog ? "#d92a2a" : "#4d9c84"));

			// logContent
			logContentTA.widthProperty().addListener((observable, oldValue, newValue) -> {
				Text text = new Text();
				text.setWrappingWidth(newValue.doubleValue());
				text.setText(logContentTA.getText());
				logContentTA.setPrefHeight(text.getLayoutBounds().getHeight() * 1.35);
			});
			
			if(isErrorLog) {
				logContentTA.setStyle("-fx-background-color: #ffbfbf");	
			}
			logContentTA.setText(logObj.getFullLogString());
			logContentTA.setEditable(false);
			logContentTA.setWrapText(true);

			textHolder.textProperty().bind(logContentTA.textProperty());

			setText(null);
			setGraphic(rootAP);
			setStyle("-fx-padding: 0");
		}
	}

	private boolean isErrorLog(String logContent) {
		// TODO Remove hard-coding that identifying error log
		return logContent.contains("ORA-");
	}
}
