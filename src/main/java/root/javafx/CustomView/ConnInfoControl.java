package root.javafx.CustomView;

import java.util.Collection;

import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;
import root.javafx.Service.ConnectionTestService;

public interface ConnInfoControl<T> {

	void save(String configFilePath, Collection<StatefulAP> statefulAP);

	ConnectionTestService getConnectionTestService(ConnectionInfoAP curAP);
	
	ConnectionInfoAP getNewConnInfoAP();
	
	ConnectionInfoAP getConnInfoAP(T connInfo);
}
