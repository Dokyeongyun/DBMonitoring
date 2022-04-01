package root.javafx.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import root.javafx.DI.DependencyInjection;
import root.utils.SceneUtils;

public class LeftMenuController {

	/**
	 * 좌측 상단 Home Icon(fxId: homeBtn) onAction Event
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/Home.fxml"));
	}

	/**
	 * 설정 메뉴로 이동한다.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/SettingMenu.fxml"));
	}

	/**
	 * 실행 메뉴로 이동한다.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goRunMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/RunMenu.fxml"));
	}

	/**
	 * 모니터링 기록 조회 메뉴로 이동한다.
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goHistoryMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/HistoryMenu.fxml"));
	}

	/**
	 * Alert Log 모니터링 메뉴로 이동한다.
	 * 
	 * @param e
	 */
	public void goAlertLogMonitoringMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/AlertLogMonitoringMenu.fxml"));
	}
}
