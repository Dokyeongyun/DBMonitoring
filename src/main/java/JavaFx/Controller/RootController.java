package JavaFx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RootController implements Initializable {
	
	@FXML
	JFXButton startBtn;
	
	@FXML
	Button homeBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void goSettingStage(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/Main.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) startBtn.getScene().getWindow();
        primaryStage.setScene(scene);
	}
	
	public void goHomeStage(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/Home.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) homeBtn.getScene().getWindow();
        primaryStage.setScene(scene);
	}
}
