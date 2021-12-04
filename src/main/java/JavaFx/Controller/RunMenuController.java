package JavaFx.Controller;

import Root.Application.Application;
import javafx.event.ActionEvent;

public class RunMenuController {
	/**
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		Application.main(new String[] {});
	}
}
