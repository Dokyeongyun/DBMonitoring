package root.javafx.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import root.javafx.DI.DependencyInjection;
import root.utils.SceneUtils;

public class LeftMenuController {
	
	// Left SplitPane Region
	@FXML Button homeBtn;
	@FXML Button settingMenuBtn;
	@FXML Button runMenuBtn;
	
	/**
	 * ���� ��� Home Icon(fxId: homeBtn) onAction Event
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/Home.fxml"));
	}
	
	/**
	 * ���� �޴��� �̵��Ѵ�.
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/SettingMenu.fxml"));
	}

	/**
	 * ���� �޴��� �̵��Ѵ�.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goRunMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/RunMenu.fxml"));
	}
	

	/**
	 * ����͸� ��� ��ȸ �޴��� �̵��Ѵ�.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goHistoryMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/HistoryMenu.fxml"));
	}
	
	public void goMenu2(ActionEvent e) {
		
	}

	public void goMenu3(ActionEvent e) {
	
	}
}
