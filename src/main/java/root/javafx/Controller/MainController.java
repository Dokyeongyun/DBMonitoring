package root.javafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import root.utils.PropertiesUtils;

public class MainController implements Initializable {
	
	@FXML GridPane rootGridPane;
	@FXML Button homeBtn;
	@FXML JFXToggleButton archiveUsageTBtn;
	@FXML JFXToggleButton tableSpaceUsageTBtn;
	@FXML JFXToggleButton asmDiskUsageTBtn;
	@FXML JFXToggleButton osDiskUsageTBtn;
	@FXML JFXToggleButton alertLogTBtn;
	@FXML StackPane contentStackPane;
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
		String[] serverNames = PropertiesUtils.propConfig.getString("servernames").split("/");

		HBox[] dbHBoxs = new HBox[] {archiveUsageBelowHBox, tableSpaceUsageBelowHBox, asmDiskUsageBelowHBox};
		HBox[] serverHBoxs = new HBox[] {osDiskUsageBelowHBox, alertLogBelowHBox};
		
		createMonitoringElements(dbHBoxs, dbNames);
		createMonitoringElements(serverHBoxs, serverNames);
	}
	
	public void goHomeStage(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
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
	
	public void createMonitoringElements(HBox[] monitoringHBoxs, String[] monitoringNames) {

		for(HBox hBox : monitoringHBoxs) {
			
			GridPane monitoringWrapGridPane = new GridPane();
			
			for(int i=0; i<monitoringNames.length; i++) {
				ColumnConstraints c = new ColumnConstraints();
				c.setPercentWidth(100/monitoringNames.length);
				monitoringWrapGridPane.getColumnConstraints().add(c);
			}

			for(int i=0; i<monitoringNames.length; i++) {
				String monitoringName = monitoringNames[i];
				
				GridPane monitoringGridPane = new GridPane();
				ColumnConstraints labelCol = new ColumnConstraints();
				labelCol.setPercentWidth(50);
				ColumnConstraints toggleBtnCol = new ColumnConstraints();
				toggleBtnCol.setPercentWidth(50);
				monitoringGridPane.getColumnConstraints().addAll(labelCol, toggleBtnCol);
				
				Label monitoringNameLabel = new Label(monitoringName);
				monitoringNameLabel.setText(monitoringName);
				monitoringNameLabel.setTextAlignment(TextAlignment.LEFT);
				monitoringNameLabel.setAlignment(Pos.CENTER_LEFT);
				monitoringNameLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: WHITE; -fx-font-weight: bold;");
				
				JFXToggleButton monitoringNameToggleBtn = new JFXToggleButton();
				monitoringNameToggleBtn.setSize(5);
				monitoringNameToggleBtn.setAlignment(Pos.CENTER);
				monitoringNameToggleBtn.setSelected(true);

				monitoringGridPane.addColumn(0, monitoringNameLabel);
				monitoringGridPane.addColumn(1, monitoringNameToggleBtn);
				
				monitoringWrapGridPane.addColumn(i, monitoringGridPane);
			}
			hBox.getChildren().add(monitoringWrapGridPane);
		}
	}
}
