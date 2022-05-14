package root.javafx.Service;

import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.javafx.utils.AlertUtils;

public class ServerConnectService extends ConnectionTestService {

	public static final String SUCCESS_MSG = "원격서버에 성공적으로 연동되었습니다.\n Host: %s:%s";
	public static final String FAIL_MSG = "원격서버 연동에 실패했습니다.";

	private JschConnectionInfo jsch;
	private JschServer jschServer;

	public ServerConnectService(JschConnectionInfo jsch) {
		this.jsch = jsch;
		this.jschServer = new JschServer(jsch);
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				jschServer.init();
				boolean isConn = jschServer.validateConn(jschServer.getSession());
				if (!isConn) {
					throw new Exception("Server connection test Failed");
				}
				return isConn;
			}
		};
	}

	@Override
	public void alertSucceed() {
		AlertUtils.showAlert(AlertType.INFORMATION, "Server 연동테스트",
				String.format(ServerConnectService.SUCCESS_MSG, jsch.getHost(), jsch.getPort()));
	}

	@Override
	public void alertFailed() {
		AlertUtils.showAlert(AlertType.ERROR, "Server 연동테스트",
				String.format(ServerConnectService.FAIL_MSG, jsch.getHost(), jsch.getPort()));
	}
}
