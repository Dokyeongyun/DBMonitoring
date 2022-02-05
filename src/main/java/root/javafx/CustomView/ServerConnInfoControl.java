package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Alert.AlertType;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;
import root.javafx.Service.ConnectionTestService;
import root.utils.AlertUtils;

public class ServerConnInfoControl implements ConnInfoControl<JschConnectionInfo> {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@Override
	public boolean save(String configFilePath, Collection<StatefulAP> statefulAP) {
		Map<String, JschConnectionInfo> config = new HashMap<>();

		for (StatefulAP childAP : statefulAP) {
			ServerConnectionInfoAnchorPane serverConnAP = (ServerConnectionInfoAnchorPane) childAP.getAp();
			if (serverConnAP.isAnyEmptyInput()) {
				AlertUtils.showAlert(AlertType.ERROR, "접속정보 설정 저장", "Server 접속정보를 모두 입력해주세요");
				return false;
			}
			JschConnectionInfo jsch = serverConnAP.getInputValues();
			config.put(jsch.getServerName().toUpperCase(), jsch);
		}
		
		propertyRepository.saveServerConnectionInfo(configFilePath, config);
		
		return true;
	}

	@Override
	public boolean canConnectionTest(ConnectionInfoAP curAP) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ConnectionTestService getConnectionTestService(ConnectionInfoAP curAP) {
		// TODO Auto-generated method stub
		System.out.println("Server test()");
		return null;
	}

	@Override
	public ConnectionInfoAP getNewConnInfoAP() {
		ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
		serverConnAP.setInitialValue(new JschConnectionInfo());
		return serverConnAP;
	}

	@Override
	public ConnectionInfoAP getConnInfoAP(JschConnectionInfo connInfo) {
		ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
		serverConnAP.setInitialValue(connInfo);
		return serverConnAP;
	}
}
