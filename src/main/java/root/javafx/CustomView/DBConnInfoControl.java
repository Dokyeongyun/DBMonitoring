package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import root.core.domain.JdbcConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;
import root.javafx.Service.ConnectionTestService;
import root.javafx.Service.DatabaseConnectService;

public class DBConnInfoControl implements ConnInfoControl<JdbcConnectionInfo> {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@Override
	public void save(String configFilePath, Collection<StatefulAP> statefulAP) {

		Map<String, JdbcConnectionInfo> config = new HashMap<>();

		for (StatefulAP childAP : statefulAP) {
			DBConnectionInfoAnchorPane dbConnAP = (DBConnectionInfoAnchorPane) childAP.getAp();
			JdbcConnectionInfo jdbc = dbConnAP.getInputValues();
			config.put(jdbc.getJdbcDBName().toUpperCase(), jdbc);
		}
		propertyRepository.saveDBConnectionInfo(configFilePath, config);
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
