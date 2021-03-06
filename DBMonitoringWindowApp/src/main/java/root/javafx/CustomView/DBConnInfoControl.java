package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import root.common.database.implement.JdbcConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;
import root.javafx.Service.ConnectionTestService;
import root.javafx.Service.DatabaseConnectService;
import root.javafx.utils.AlertUtils;
import root.repository.implement.PropertyRepositoryImpl;

public class DBConnInfoControl implements ConnInfoControl<JdbcConnectionInfo> {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@Override
	public boolean save(String configFilePath, Collection<StatefulAP> statefulAP) {

		Map<String, JdbcConnectionInfo> config = new HashMap<>();

		for (StatefulAP childAP : statefulAP) {
			DBConnectionInfoAnchorPane dbConnAP = (DBConnectionInfoAnchorPane) childAP.getAp();
			if (dbConnAP.isAnyEmptyInput()) {
				AlertUtils.showAlert(AlertType.ERROR, "접속정보 설정 저장", "DB 접속정보를 모두 입력해주세요");
				return false;
			}
			JdbcConnectionInfo jdbc = dbConnAP.getInputValues();
			config.put(jdbc.getJdbcDBName().toUpperCase(), jdbc);
		}

		propertyRepository.saveDBConnectionInfo(configFilePath, config);

		return true;
	}

	@Override
	public boolean canConnectionTest(ConnectionInfoAP curAP) {
		return curAP.isAnyEmptyInputForConnectionTest();
	}

	@Override
	public ConnectionTestService getConnectionTestService(ConnectionInfoAP curAP) {
		String jdbcUrl = ((TextField) curAP.lookup("#urlTF")).getText();
		String jdbcId = ((TextField) curAP.lookup("#userTF")).getText();
		String jdbcPw = ((PasswordField) curAP.lookup("#passwordPF")).getText();

		// TODO JdbcDriver, Validation Query 하드코딩 변경 - DBMS에 따라 다르게 해야 함
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo("oracle.jdbc.driver.OracleDriver", jdbcUrl, jdbcId, jdbcPw,
				"SELECT 1 FROM DUAL", 1);
		return new DatabaseConnectService(jdbc);
	}

	@Override
	public ConnectionInfoAP getNewConnInfoAP() {
		DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
		dbConnAP.init();
		dbConnAP.setInitialValue(new JdbcConnectionInfo());
		return dbConnAP;
	}

	@Override
	public ConnectionInfoAP getConnInfoAP(JdbcConnectionInfo connInfo) {
		DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
		dbConnAP.init();
		dbConnAP.setInitialValue(connInfo);
		return dbConnAP;
	}
}
