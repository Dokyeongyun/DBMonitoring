package root.javafx.CustomView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;
import root.javafx.Service.ConnectionTestService;
import root.javafx.Service.ServerConnectService;
import root.javafx.utils.AlertUtils;
import root.repository.implement.PropertyRepositoryImpl;

@Slf4j
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
			config.put(jsch.getServerName(), jsch);
			log.debug(jsch.toString());
		}

		propertyRepository.saveServerConnectionInfo(configFilePath, config);

		return true;
	}

	@Override
	public boolean canConnectionTest(ConnectionInfoAP curAP) {
		return curAP.isAnyEmptyInputForConnectionTest();
	}

	@Override
	public ConnectionTestService getConnectionTestService(ConnectionInfoAP curAP) {
		String host = ((TextField) curAP.lookup("#hostTF")).getText();
		String port = ((TextField) curAP.lookup("#portTF")).getText();
		String id = ((TextField) curAP.lookup("#userTF")).getText();
		String pw = ((PasswordField) curAP.lookup("#passwordPF")).getText();

		return new ServerConnectService(new JschConnectionInfo(host, port, id, pw));
	}

	@Override
	public ConnectionInfoAP getNewConnInfoAP() {
		ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
		serverConnAP.init();
		serverConnAP.setInitialValue(new JschConnectionInfo());
		return serverConnAP;
	}

	@Override
	public ConnectionInfoAP getConnInfoAP(JschConnectionInfo connInfo) {
		ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
		serverConnAP.init();
		serverConnAP.setInitialValue(connInfo);
		return serverConnAP;
	}
}
