package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import root.core.domain.JdbcConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;

public class DBConnInfoControl implements ConnInfoControl<ConnectionInfoAP> {

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
	public void test() {
		// TODO Auto-generated method stub
		System.out.println("DB test()");
	}

	@Override
	public void getNewConnInfoAP() {
		// TODO Auto-generated method stub
		System.out.println("DB getNewConnInfoAP()");
	}

}
