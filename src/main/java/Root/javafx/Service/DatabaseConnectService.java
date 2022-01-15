package root.javafx.Service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import root.common.database.implement.JdbcDatabase;
import root.core.domain.JdbcConnectionInfo;
import root.utils.AlertUtils;

public class DatabaseConnectService extends Service<Boolean> {

	private static final String SUCCESS_MSG = "�����ͺ��̽��� ���������� �����Ǿ����ϴ�.\n URL: %s \n Driver: %s";
	private static final String FAIL_MSG = "�����ͺ��̽� ������ �����߽��ϴ�.\n URL: %s \n Driver: %s";

	private JdbcConnectionInfo jdbc;
	private JdbcDatabase db;

	public DatabaseConnectService(JdbcConnectionInfo jdbc) {
		this.jdbc = jdbc;
		this.db = new JdbcDatabase(jdbc);

		setOnSucceeded(s -> {
			AlertUtils.showAlert(AlertType.INFORMATION, "DB �����׽�Ʈ",
					String.format(SUCCESS_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
		});

		setOnFailed(f -> {
			AlertUtils.showAlert(AlertType.ERROR, "DB �����׽�Ʈ",
					String.format(FAIL_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
		});

		setOnCancelled(c -> {
		});
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				db.init();
				boolean isConn = JdbcDatabase.validateConn(db.getConn(), jdbc.getJdbcValidation());
				if (!isConn) {
					throw new Exception("Database connection test Failed");
				}
				return isConn;
			}
		};
	}
}
