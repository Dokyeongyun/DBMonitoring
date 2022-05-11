package root.javafx.Service;

import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import root.common.database.implement.JdbcDatabase;
import root.core.domain.JdbcConnectionInfo;
import root.utils.AlertUtils;

public class DatabaseConnectService extends ConnectionTestService {

	public static final String SUCCESS_MSG = "데이터베이스가 성공적으로 연동되었습니다.\n URL: %s \n Driver: %s";
	public static final String FAIL_MSG = "데이터베이스 연동에 실패했습니다.\n URL: %s \n Driver: %s";

	private JdbcConnectionInfo jdbc;
	private JdbcDatabase db;

	public DatabaseConnectService(JdbcConnectionInfo jdbc) {
		this.jdbc = jdbc;
		this.db = new JdbcDatabase(jdbc);
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

	@Override
	public void alertSucceed() {
		AlertUtils.showAlert(AlertType.INFORMATION, "DB 연동테스트",
				String.format(DatabaseConnectService.SUCCESS_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
	}

	@Override
	public void alertFailed() {
		AlertUtils.showAlert(AlertType.ERROR, "DB 연동테스트",
				String.format(DatabaseConnectService.FAIL_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
	}
}
