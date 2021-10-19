package JavaFx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController implements Initializable {
	
	@FXML Button homeBtn;
	@FXML JFXToggleButton archiveUsageTBtn;
	@FXML JFXToggleButton tableSpaceUsageTBtn;
	@FXML JFXToggleButton asmDiskUsageTBtn;
	@FXML JFXToggleButton osDiskUsageTBtn;
	@FXML JFXToggleButton alertLogTBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setToggleDefaultValue();
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
