package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;

public class ServerConnInfoControl implements ConnInfoControl<ConnectionInfoAP> {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@Override
	public void save(String configFilePath, Collection<StatefulAP> statefulAP) {
		Map<String, JschConnectionInfo> config = new HashMap<>();

		for (StatefulAP childAP : statefulAP) {
			ServerConnectionInfoAnchorPane serverConnAP = (ServerConnectionInfoAnchorPane) childAP.getAp();
			JschConnectionInfo jsch = serverConnAP.getInputValues();
			config.put(jsch.getServerName().toUpperCase(), jsch);
		}
		propertyRepository.saveServerConnectionInfo(configFilePath, config);
	}

	@Override
	public void test() {
		// TODO Auto-generated method stub
		System.out.println("Server test()");
	}

	@Override
	public void getNewConnInfoAP() {
		// TODO Auto-generated method stub
		System.out.println("Server getNewConnInfoAP()");
	}
}