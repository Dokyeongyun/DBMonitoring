package root.javafx.CustomView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import root.core.domain.JdbcConnectionInfo;
import root.javafx.Service.DatabaseConnectService;
import root.utils.AlertUtils;

@EqualsAndHashCode(callSuper = false)
@Data
public class ConnectionInfoVBox extends VBox {

	@FXML
	Label menuTitleLB;

	@FXML
	FontAwesomeIconView menuIconIV;

	@FXML
	StackPane connInfoStackPane; // 접속정보 설정 그리드를 담는 컨테이너

	@FXML
	AnchorPane connInfoNoDataAP; // 접속정보 No Data AchorPane

	@FXML
	Text connInfoText; // 접속정보 인덱스 텍스트

	@FXML
	JFXButton connTestBtn;

	private Class<? extends AnchorPane> childAPClazz;

	private Map<Integer, AnchorPane> connInfoAPMap = new HashMap<>();

	private int connInfoIdx = 0;

	public ConnectionInfoVBox(Class<? extends AnchorPane> childAPClazz) {
		this.childAPClazz = childAPClazz;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConnectionInfoVBox.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMenuTitle(String menuTitle, FontAwesomeIcon menuIcon) {
		menuTitleLB.setText(menuTitle);
		menuIconIV.setIcon(menuIcon);
	}

	public void addConnectionInfoAP(Node connInfoAP) {
		connInfoAPMap.put(connInfoAPMap.size(), (AnchorPane) connInfoAP);
		connInfoStackPane.getChildren().add(connInfoAP);
		bringFrontConnInfoAnchorPane(connInfoAPMap.size() - 1);
	}

	public void addConnInfo(ActionEvent e) {
		connInfoIdx = connInfoAPMap.size();

		if (childAPClazz == DBConnectionInfoAnchorPane.class) {
			addConnectionInfoAP(new DBConnectionInfoAnchorPane());
		} else if (childAPClazz == ServerConnectionInfoAnchorPane.class) {
			addConnectionInfoAP(new ServerConnectionInfoAnchorPane());
		}
	}

	public void bringFrontConnInfoAnchorPane(int connInfoIdx) {
		this.connInfoIdx = connInfoIdx;
		connInfoAPMap.get(connInfoIdx).toFront();
		connInfoText.setText(String.format("(%d/%d)", connInfoIdx + 1, connInfoAPMap.size()));
	}

	public void prevConnInfo(ActionEvent e) {
		if (connInfoAPMap.size() == 0) {
			return;
		}

		connInfoIdx = connInfoIdx == 0 ? connInfoAPMap.size() - 1 : connInfoIdx - 1;
		setConnectionBtnIcon(1);
		bringFrontConnInfoAnchorPane(connInfoIdx);
	}

	public void nextConnInfo(ActionEvent e) {
		if (connInfoAPMap.size() == 0) {
			return;
		}

		connInfoIdx = connInfoIdx == connInfoAPMap.size() - 1 ? 0 : connInfoIdx + 1;
		setConnectionBtnIcon(1);
		bringFrontConnInfoAnchorPane(connInfoIdx);
	}

	private void setConnectionBtnIcon(int type) {
		FontAwesomeIconView icon = (FontAwesomeIconView) connTestBtn.lookup("#icon");
		switch (type) {
		case 1:
			icon.setIcon(FontAwesomeIcon.PLUG);
			icon.setFill(Paint.valueOf("#000000"));
			break;
		case 2:
			icon.setIcon(FontAwesomeIcon.CHECK);
			icon.setFill(Paint.valueOf("#49a157"));
			break;
		case 3:
			icon.setIcon(FontAwesomeIcon.TIMES);
			icon.setFill(Paint.valueOf("#c40a0a"));
			break;
		case 4:
			icon.setIcon(FontAwesomeIcon.SPINNER);
			icon.setFill(Paint.valueOf("#484989"));
			icon.getStyleClass().add("fa-spin");
			break;
		}
	}

	public void testConnection(ActionEvent e) {
		if (childAPClazz == DBConnectionInfoAnchorPane.class) {
			// 아이콘 변경
			setConnectionBtnIcon(4);

			AnchorPane curAP = connInfoAPMap.get(connInfoIdx);

			String jdbcUrl = ((TextField) curAP.lookup("#urlTF")).getText();
			String jdbcId = ((TextField) curAP.lookup("#userTF")).getText();
			String jdbcPw = ((PasswordField) curAP.lookup("#passwordPF")).getText();

			// TODO JdbcDriver, Validation Query 하드코딩 변경 - DBMS에 따라 다르게 해야 함
			JdbcConnectionInfo jdbc = new JdbcConnectionInfo("oracle.jdbc.driver.OracleDriver", jdbcUrl, jdbcId, jdbcPw,
					"SELECT 1 FROM DUAL", 1);

			DatabaseConnectService dbConnService = new DatabaseConnectService(jdbc);
			dbConnService.setOnSucceeded(s -> {
				AlertUtils.showAlert(AlertType.INFORMATION, "DB 연동테스트",
						String.format(DatabaseConnectService.SUCCESS_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
				setConnectionBtnIcon(2);
			});

			dbConnService.setOnFailed(f -> {
				AlertUtils.showAlert(AlertType.ERROR, "DB 연동테스트",
						String.format(DatabaseConnectService.FAIL_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
				setConnectionBtnIcon(3);
			});

			dbConnService.start();
		} else if (childAPClazz == ServerConnectionInfoAnchorPane.class) {
			
		}
	}
}
