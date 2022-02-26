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

		CustomTreeView ctv = new CustomTreeView();
		ctv.addTreeItem("DB 立加沥焊", new ArrayList<>(Arrays.asList("DB1", "DB2", "DB3")), FontAwesomeIcon.DATABASE);
		ctv.addTreeItem("Server 立加沥焊", new ArrayList<>(Arrays.asList("Server1", "Server2", "Server3", "Server4")),
				FontAwesomeIcon.SERVER);
		AnchorPane.setTopAnchor(ctv, 40.0);
		AnchorPane.setBottomAnchor(ctv, 0.0);
		AnchorPane.setLeftAnchor(ctv, 0.0);
		AnchorPane.setRightAnchor(ctv, 0.0);
		connInfoSettingAP.getChildren().add(ctv);
	}
}
