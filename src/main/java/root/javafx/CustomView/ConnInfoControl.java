package root.javafx.CustomView;

import java.util.Collection;

import root.javafx.CustomView.ConnectionInfoVBox.StatefulAP;

public interface ConnInfoControl<T extends ConnectionInfoAP> {

	void save(String configFilePath, Collection<StatefulAP> statefulAP);

	void test();

	void getNewConnInfoAP();
}
