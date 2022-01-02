package root.javafx.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LeftMenuController {
	// Left SplitPane Region
	@FXML Button homeBtn;
	@FXML Button settingMenuBtn;
	@FXML Button runMenuBtn;
	
	/**
	 * 좌측 상단 Home Icon(fxId: homeBtn) onAction Event
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		Scene originalScene = homeBtn.getScene();
		Parent home = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        Scene homeScene = new Scene(home, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage) homeBtn.getScene().getWindow();
        primaryStage.setScene(homeScene);
	}
	
	/**
	 * 설정 메뉴로 이동한다.
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		Scene originalScene = homeBtn.getScene();
		Parent parent = FXMLLoader.load(getClass().getResource("/fxml/SettingMenu.fxml"));
        Scene newScene = new Scene(parent, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage)((Node) e.getSource()).getScene().getWindow();
        primaryStage.setScene(newScene);
	}
	
	/**
	 * 실행 메뉴로 이동한다.
	 * @param e
	 * @throws IOException
	 */
	public void goRunMenu(ActionEvent e) throws IOException {
		Scene originalScene = homeBtn.getScene();
		Parent parent = FXMLLoader.load(getClass().getResource("/fxml/RunMenu.fxml"));
        Scene newScene = new Scene(parent, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage)((Node) e.getSource()).getScene().getWindow();
        primaryStage.setScene(newScene);
	}
	
	public void goMenu2(ActionEvent e) {
		
	}

	public void goMenu3(ActionEvent e) {
	
	}
}
