package root.javafx.Service;

import javafx.concurrent.Task;
import root.core.domain.JschConnectionInfo;

public class ServerConnectService extends ConnectionTestService {

	public static final String SUCCESS_MSG = "���ݼ����� ���������� �����Ǿ����ϴ�.";
	public static final String FAIL_MSG = "���ݼ��� ������ �����߽��ϴ�.";

	private JschConnectionInfo jsch;

	public ServerConnectService(JschConnectionInfo jsch) {
		this.jsch = jsch;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				return true;
			}
		};
	}

	@Override
	public void alertSucceed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alertFailed() {
		// TODO Auto-generated method stub
		
	}
}
