package root.javafx.CustomView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;

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
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.Service.DatabaseConnectService;
import root.utils.AlertUtils;
import root.utils.PropertiesUtils;

@EqualsAndHashCode(callSuper = false)
@Data
public class ConnectionInfoVBox extends VBox {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();
	
	@FXML
	Label menuTitleLB;

	@FXML
	FontAwesomeIconView menuIconIV;

	@FXML
	StackPane connInfoStackPane; // �������� ���� �׸��带 ��� �����̳�

	@FXML
	AnchorPane connInfoNoDataAP; // �������� No Data AchorPane

	@FXML
	Text connInfoText; // �������� �ε��� �ؽ�Ʈ

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
			// ������ ����
			setConnectionBtnIcon(4);

			AnchorPane curAP = connInfoAPMap.get(connInfoIdx);

			String jdbcUrl = ((TextField) curAP.lookup("#urlTF")).getText();
			String jdbcId = ((TextField) curAP.lookup("#userTF")).getText();
			String jdbcPw = ((PasswordField) curAP.lookup("#passwordPF")).getText();

			// TODO JdbcDriver, Validation Query �ϵ��ڵ� ���� - DBMS�� ���� �ٸ��� �ؾ� ��
			JdbcConnectionInfo jdbc = new JdbcConnectionInfo("oracle.jdbc.driver.OracleDriver", jdbcUrl, jdbcId, jdbcPw,
					"SELECT 1 FROM DUAL", 1);

			DatabaseConnectService dbConnService = new DatabaseConnectService(jdbc);
			dbConnService.setOnSucceeded(s -> {
				AlertUtils.showAlert(AlertType.INFORMATION, "DB �����׽�Ʈ",
						String.format(DatabaseConnectService.SUCCESS_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
				setConnectionBtnIcon(2);
			});

			dbConnService.setOnFailed(f -> {
				AlertUtils.showAlert(AlertType.ERROR, "DB �����׽�Ʈ",
						String.format(DatabaseConnectService.FAIL_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
				setConnectionBtnIcon(3);
			});

			dbConnService.start();
		} else if (childAPClazz == ServerConnectionInfoAnchorPane.class) {
			
		}
	}
	
	public void saveConnInfoSettings(String configFilePath) {
		PropertiesConfiguration config = PropertiesUtils.connInfoConfig;

		if(childAPClazz == DBConnectionInfoAnchorPane.class) {
			
		}
		for (AnchorPane childAP : this.connInfoAPMap.values()) {
			DBConnectionInfoAnchorPane dbConnAP = (DBConnectionInfoAnchorPane) childAP;
			JdbcConnectionInfo jdbc = dbConnAP.getInputValues();
			System.out.println(jdbc);
			
			String dbName = jdbc.getJdbcDBName().toLowerCase();
			config.setProperty("#DB", dbName);
			config.setProperty(dbName + ".jdbc.alias", jdbc.getJdbcDBName());
			config.setProperty(dbName + ".jdbc.id", jdbc.getJdbcId());
			config.setProperty(dbName + ".jdbc.pw", jdbc.getJdbcPw());
			config.setProperty(dbName + ".jdbc.url", jdbc.getJdbcUrl());
			// TODO ���õ� Oracle Driver Type�� ����, Driver �� �����ϱ�, ����� �ӽ÷� ��� ������ �� �Է�
			config.setProperty(dbName + ".jdbc.driver", "oracle.jdbc.driver.OracleDriver");
			config.setProperty(dbName + ".jdbc.validation", jdbc.getJdbcValidation());
			config.setProperty(dbName + ".jdbc.connections", jdbc.getJdbcConnections());
		}
		
		propertyRepository.save(configFilePath, config);
	}
}
