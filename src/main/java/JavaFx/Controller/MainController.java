package JavaFx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import Root.Utils.PropertiesUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainController implements Initializable {
	
	@FXML Button homeBtn;
	@FXML JFXToggleButton archiveUsageTBtn;
	@FXML JFXToggleButton tableSpaceUsageTBtn;
	@FXML JFXToggleButton asmDiskUsageTBtn;
	@FXML JFXToggleButton osDiskUsageTBtn;
	@FXML JFXToggleButton alertLogTBtn;
	@FXML VBox settingsContentVBox;
	@FXML HBox archiveUsageBelowHBox;
	@FXML HBox tableSpaceUsageBelowHBox;
	@FXML HBox asmDiskUsageBelowHBox;
	@FXML HBox osDiskUsageBelowHBox;
	@FXML HBox alertLogBelowHBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setToggleDefaultValue();
		
		String[] dbNames = PropertiesUtils.propConfig.getString("dbnames").split("/");
		
		double hBoxWidth = settingsContentVBox.getPrefWidth();
		double hBoxPaddingLeft = archiveUsageBelowHBox.getPadding().getLeft();
		double hBoxPaddingRight = archiveUsageBelowHBox.getPadding().getRight();
		double childrenHboxWidth = (hBoxWidth - hBoxPaddingLeft - hBoxPaddingRight) / dbNames.length;
		
		HBox[] dbHBoxs = new HBox[] {archiveUsageBelowHBox, tableSpaceUsageBelowHBox, asmDiskUsageBelowHBox};
		for(HBox hBox : dbHBoxs) {
			for(String dbName : dbNames) {
				HBox dbNameHBox = new HBox();
				dbNameHBox.setPrefHeight(40);
				
				Label dbNameLabel = new Label(dbName);
				dbNameLabel.setText(dbName);
				dbNameLabel.setPrefWidth(childrenHboxWidth);
				dbNameLabel.setMaxHeight(40);
				dbNameLabel.setTextAlignment(TextAlignment.CENTER);
				dbNameLabel.setAlignment(Pos.CENTER);
				dbNameLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: WHITE; -fx-font-weight: bold;");
				
				JFXToggleButton dbNameToggleBtn = new JFXToggleButton();
				dbNameToggleBtn.setSize(6);
				dbNameToggleBtn.setPrefHeight(40);
				dbNameToggleBtn.setAlignment(Pos.CENTER);
				dbNameToggleBtn.setSelected(true);
				
				dbNameHBox.getChildren().addAll(dbNameLabel, dbNameToggleBtn);
				hBox.getChildren().add(dbNameHBox);	
			}
		}
	}
	
	public void goHomeStage(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/Home.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) homeBtn.getScene().getWindow();
        primaryStage.setScene(scene);
	}
	
	public void setToggleDefaultValue() {
		archiveUsageTBtn.setSelected(true);
		tableSpaceUsageTBtn.setSelected(true);
		asmDiskUsageTBtn.setSelected(true);
		osDiskUsageTBtn.setSelected(true);
		alertLogTBtn.setSelected(true);
	}
}
