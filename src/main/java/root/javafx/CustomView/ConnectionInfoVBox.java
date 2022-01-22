package root.javafx.CustomView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
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
	StackPane connInfoStackPane; // 접속정보 설정 그리드를 담는 컨테이너

	@FXML
	AnchorPane connInfoNoDataAP; // 접속정보 No Data AchorPane

	@FXML
	Text connInfoText; // 접속정보 인덱스 텍스트

	@FXML
	JFXButton connTestBtn;

	private Class<? extends AnchorPane> childAPClazz;

	private Map<Integer, StatefulAP> connInfoAPMap = new HashMap<>();

	private static int connInfoIdx = 0;

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
	
	public void clearConnInfoMap() {
		this.connInfoAPMap.clear();
		connInfoIdx = 0;
	}

	public void setMenuTitle(String menuTitle, FontAwesomeIcon menuIcon) {
		menuTitleLB.setText(menuTitle);
		menuIconIV.setIcon(menuIcon);
	}

	public void addConnectionInfoAP(Node connInfoAP) {
		connInfoAPMap.put(connInfoAPMap.size(), new StatefulAP(1, (AnchorPane) connInfoAP));
		connInfoStackPane.getChildren().add(connInfoAP);
		bringFrontConnInfoAnchorPane(connInfoAPMap.size() - 1);
	}

	public void addNewConnInfo(ActionEvent e) {
		if (childAPClazz == DBConnectionInfoAnchorPane.class) {
			DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
			dbConnAP.setInitialValue(new JdbcConnectionInfo());
			addConnectionInfoAP(dbConnAP);

		} else if (childAPClazz == ServerConnectionInfoAnchorPane.class) {
			ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
			serverConnAP.setInitialValue(new JschConnectionInfo());
			addConnectionInfoAP(serverConnAP);

		}
	}

	public void removeConnInfo(ActionEvent e) {

	}

	public void bringFrontConnInfoAnchorPane(int index) {
		connInfoAPMap.get(index).getAp().toFront();
		connInfoText.setText(String.format("(%d/%d)", index + 1, connInfoAPMap.size()));
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

			AnchorPane curAP = connInfoAPMap.get(connInfoIdx).getAp();

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

	public void saveConnInfoSettings(String configFilePath) {

		PropertiesConfiguration config = PropertiesUtils.connInfoConfig;

		if (childAPClazz == DBConnectionInfoAnchorPane.class) {

			List<String> dbNames = new ArrayList<>();
			for (StatefulAP childAP : this.connInfoAPMap.values()) {
				DBConnectionInfoAnchorPane dbConnAP = (DBConnectionInfoAnchorPane) childAP.getAp();
				JdbcConnectionInfo jdbc = dbConnAP.getInputValues();

				String dbName = jdbc.getJdbcDBName().toLowerCase();
				config.setProperty("#DB", dbName);
				config.setProperty(dbName + ".jdbc.alias", jdbc.getJdbcDBName());
				config.setProperty(dbName + ".jdbc.id", jdbc.getJdbcId());
				config.setProperty(dbName + ".jdbc.pw", jdbc.getJdbcPw());
				config.setProperty(dbName + ".jdbc.url", jdbc.getJdbcUrl());
				// TODO 선택된 Oracle Driver Type에 따라서, Driver 값 변경하기, 현재는 임시로 모두 동일한 값 입력
				config.setProperty(dbName + ".jdbc.driver", "oracle.jdbc.driver.OracleDriver");
				config.setProperty(dbName + ".jdbc.validation", jdbc.getJdbcValidation());
				config.setProperty(dbName + ".jdbc.connections", jdbc.getJdbcConnections());

				dbNames.add(dbName);
			}

			config.setProperty("dbnames", dbNames);
		} else {

			List<String> serverNames = new ArrayList<>();

			for (StatefulAP childAP : this.connInfoAPMap.values()) {
				ServerConnectionInfoAnchorPane serverConnAP = (ServerConnectionInfoAnchorPane) childAP.getAp();
				JschConnectionInfo jsch = serverConnAP.getInputValues();

				String serverName = jsch.getServerName().toLowerCase();
				config.setProperty(serverName + ".server.servername", jsch.getServerName());
				config.setProperty(serverName + ".server.host", jsch.getHost());
				config.setProperty(serverName + ".server.port", jsch.getPort());
				config.setProperty(serverName + ".server.username", jsch.getUserName());
				config.setProperty(serverName + ".server.password", jsch.getPassword());

				String dateFormat = jsch.getAlc().getDateFormat();
				String dateFormatRegex = "";

				if (dateFormat.equals("EEE MMM dd HH:mm:ss yyyy")) {
					dateFormatRegex = "...\\s...\\s([0-2][0-9]|1[012])\\s\\d\\d:\\d\\d:\\d\\d\\s\\d{4}";
				} else if (dateFormat.equals("yyyy-MM-dd")) {
					dateFormatRegex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T";
				}

				config.setProperty(serverName + ".server.alertlog.dateformat", dateFormat);
				config.setProperty(serverName + ".server.alertlog.dateformatregex", dateFormatRegex);
				config.setProperty(serverName + ".server.alertlog.filepath", jsch.getAlc().getReadFilePath());
				config.setProperty(serverName + ".server.alertlog.readLine", 500);

				serverNames.add(serverName);
			}

			config.setProperty("servernames", serverNames);
		}

		propertyRepository.save(configFilePath, config);
	}


	@AllArgsConstructor
	@Data
	private static class StatefulAP {
		private int status; // 1: 신규, 2: 기존, 3: 제거
		private AnchorPane ap;
	}

	private static class ConnInfoAPMap {
		private Map<Integer, StatefulAP> map;

		public ConnInfoAPMap() {
			this.map = new HashMap<>();
		}
		
		public void put(int index, StatefulAP ap) {
			this.map.put(index, ap);
			System.out.println(connInfoIdx+ " " + this.map.keySet());
		}
		
		public void remove(int index) {
			this.map.get(index).setStatus(3);
			
			String values = "[";
			for(Integer key : this.map.keySet()) {
				if(map.get(key).getStatus() != 3) {
					values += key +" ";
				}
			}
			values += "]";
			System.out.println(connInfoIdx+ " " + values);
		}
		
		public StatefulAP get(int index) {
			return this.map.get(index);
		}
		
		public int getActiveAPCnt() {
			int count = 0;
			for (StatefulAP ap : map.values()) {
				if (ap.getStatus() != 3) {
					count++;
				}
			}
			return count;
		}
		
		public Collection<StatefulAP> getActiveAPs() {
			List<StatefulAP> result = new ArrayList<>();	
			for (StatefulAP ap : map.values()) {
				if (ap.getStatus() != 3) {
					result.add(ap);
				}
			}
			return result;
		}
		
		public void clear() {
			this.map.clear();
		}
	}
}
