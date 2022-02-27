package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.javafx.Model.MonitoringYN;

public class RunMenuController implements Initializable {

	@FXML
	AnchorPane connInfoSettingAP;
	
	@FXML
	AnchorPane presetSettingAP;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 접속정보 리스트 TreeView
		CustomTreeView connInfoCtv = new CustomTreeView("접속정보 리스트", FontAwesomeIcon.LIST, true);
		connInfoCtv.addTreeItem("DB", new ArrayList<>(Arrays.asList("DB1", "DB2", "DB3")),
				FontAwesomeIcon.DATABASE);
		connInfoCtv.addTreeItem("Server",
				new ArrayList<>(Arrays.asList("Server1", "Server2", "Server3", "Server4")), FontAwesomeIcon.SERVER);
		AnchorPane.setTopAnchor(connInfoCtv, 80.0);
		AnchorPane.setBottomAnchor(connInfoCtv, 0.0);
		AnchorPane.setLeftAnchor(connInfoCtv, 0.0);
		AnchorPane.setRightAnchor(connInfoCtv, 0.0);
		connInfoSettingAP.getChildren().add(connInfoCtv);
		
		// 모니터링 여부 리스트 TreeTableView
		List<MonitoringYN> list1 = new ArrayList<>();
		list1.add(new MonitoringYN("DB1", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB2", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB3", "N", "N", "N", "N"));
		List<MonitoringYN> list2 = new ArrayList<>();
		list2.add(new MonitoringYN("Server1", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server2", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server3", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server4", "Y", "Y", "Y", "Y"));
		CustomTreeTableView presetCtv = new CustomTreeTableView("모니터링 여부 리스트", FontAwesomeIcon.LIST);
		presetCtv.addTreeTableItem("DB", list1, FontAwesomeIcon.DATABASE);
		presetCtv.addTreeTableItem("Server", list2, FontAwesomeIcon.SERVER);
		AnchorPane.setTopAnchor(presetCtv, 80.0);
		AnchorPane.setBottomAnchor(presetCtv, 0.0);
		AnchorPane.setLeftAnchor(presetCtv, 0.0);
		AnchorPane.setRightAnchor(presetCtv, 0.0);
		presetSettingAP.getChildren().add(presetCtv);
	}
}
