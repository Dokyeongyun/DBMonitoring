package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import root.javafx.CustomView.CustomTreeView;

public class RunMenuController implements Initializable {

	@FXML
	AnchorPane connInfoSettingAP;

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
	}
}
