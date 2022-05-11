package root.javafx.Controller;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import root.javafx.DI.DependencyInjection;
import root.utils.SceneUtils;

public class HomeController {

	@FXML
	AnchorPane rootAnchorPane;
	@FXML
	JFXButton startBtn;

	public void goMainStage(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/RunMenu.fxml"));
	}
}
