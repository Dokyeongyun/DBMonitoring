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
import javafx.stage.Stage;

public class HomeController implements Initializable {
	
	@FXML JFXButton startBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void goMainStage(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/Main_new.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) startBtn.getScene().getWindow();
        primaryStage.setScene(scene);
	}
}
