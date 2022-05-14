package root.javafx.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import root.javafx.DI.DependencyInjection;
import root.javafx.utils.SceneUtils;

public class LeftMenuController {

	/**
	 * ���� ��� Home Icon(fxId: homeBtn) onAction Event
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/Home.fxml"));
	}

	/**
	 * ���� �޴��� �̵��Ѵ�.
	 * 
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

	/**
	 * Alert Log ����͸� �޴��� �̵��Ѵ�.
	 * 
	 * @param e
	 */
	public void goAlertLogMonitoringMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/AlertLogMonitoringMenu.fxml"));
	}
}
