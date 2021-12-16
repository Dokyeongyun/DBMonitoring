package JavaFx.CustomView;

import java.io.IOException;

import com.jfoenix.controls.JFXTextArea;

import Root.Model.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class AlertLogListViewCell extends ListCell<Log> {
	private FXMLLoader loader;
	
	@FXML AnchorPane rootAP;
	@FXML Label logTimeLabel;
	@FXML JFXTextArea logContentTA;
	
	@Override
	protected void updateItem(Log log, boolean empty) {
        super.updateItem(log, empty);

        if(empty || log == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
    			loader = new FXMLLoader(getClass().getResource("/JavaFx/resources/fxml/AlertLogListViewCell.fxml"));
            	loader.setController(this);

                try {
                	loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            logTimeLabel.setText(log.getLogTimeStamp());
            logContentTA.setPrefRowCount(log.getTotalLineCount());
            logContentTA.setText(log.getFullLogString());
            
            setText(null);
            setGraphic(rootAP);
        }
	}

}
