package root.javafx.Service;

import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.javafx.utils.AlertUtils;

public class ServerConnectService extends ConnectionTestService {

	public static final String SUCCESS_MSG = "���ݼ����� ���������� �����Ǿ����ϴ�.\n Host: %s:%s";
	public static final String FAIL_MSG = "���ݼ��� ������ �����߽��ϴ�.";

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
		AlertUtils.showAlert(AlertType.INFORMATION, "Server �����׽�Ʈ",
				String.format(ServerConnectService.SUCCESS_MSG, jsch.getHost(), jsch.getPort()));
	}

	@Override
	public void alertFailed() {
		AlertUtils.showAlert(AlertType.ERROR, "Server �����׽�Ʈ",
				String.format(ServerConnectService.FAIL_MSG, jsch.getHost(), jsch.getPort()));
	}
}
