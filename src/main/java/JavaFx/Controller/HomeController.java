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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HomeController implements Initializable {
	
	@FXML AnchorPane rootAnchorPane;
	@FXML JFXButton startBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void goMainStage(ActionEvent e) throws IOException {
		Scene originalScene = rootAnchorPane.getScene();
		Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/Main_new.fxml"));
        Scene scene = new Scene(root, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage) startBtn.getScene().getWindow();
        primaryStage.setScene(scene);
	}
}
