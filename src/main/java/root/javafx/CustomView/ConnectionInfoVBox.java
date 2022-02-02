package root.javafx.CustomView;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.Service.DatabaseConnectService;
import root.utils.AlertUtils;

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

	@FXML
	JFXButton connInfoAddBtn;

	@FXML
	JFXButton connInfoRemoveBtn;

	@FXML
	JFXButton prevConnInfoBtn;

	@FXML
	JFXButton nextConnInfoBtn;

	private Class<? extends AnchorPane> childAPClazz;

	private ConnInfoAPMap connInfoAPMap = new ConnInfoAPMap();
	
	private long connInfoIdx = -1;

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
		connInfoIdx = -1;
	}

	public void addConnectionInfoAP(int type, Node connInfoAP) {
		long newIdx = connInfoAPMap.put(new StatefulAP(type, (AnchorPane) connInfoAP));
		connInfoAP.setId(String.valueOf(newIdx));
		connInfoStackPane.getChildren().add(connInfoAP);

		if (connInfoIdx == -1) {
			connInfoIdx = this.connInfoAPMap.getFirstActiveIdx();
		}

		if (type == 1) {
			bringFrontConnInfoAnchorPane(connInfoIdx);
		} else if (type == 2) {
			bringFrontConnInfoAnchorPane(this.connInfoAPMap.getLastActiveIdx());
		}
	}

	// TODO 다형성을 이용해 클래스 타입체크 제거하기
	public void saveConnInfoSettings(String configFilePath) {
		if (childAPClazz == DBConnectionInfoAnchorPane.class) {
			Map<String, JdbcConnectionInfo> config = new HashMap<>();

			for (StatefulAP childAP : this.connInfoAPMap.getActiveAPs().values()) {
				DBConnectionInfoAnchorPane dbConnAP = (DBConnectionInfoAnchorPane) childAP.getAp();
				JdbcConnectionInfo jdbc = dbConnAP.getInputValues();
				config.put(jdbc.getJdbcDBName().toUpperCase(), jdbc);
			}
			propertyRepository.saveDBConnectionInfo(configFilePath, config);
		} else {
			Map<String, JschConnectionInfo> config = new HashMap<>();

			for (StatefulAP childAP : this.connInfoAPMap.getActiveAPs().values()) {
				ServerConnectionInfoAnchorPane serverConnAP = (ServerConnectionInfoAnchorPane) childAP.getAp();
				JschConnectionInfo jsch = serverConnAP.getInputValues();
				config.put(jsch.getServerName().toUpperCase(), jsch);
			}
			propertyRepository.saveServerConnectionInfo(configFilePath, config);
		}
	}
	
	/* Button Click Listener */
	
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
	
	public void addNewConnInfo(ActionEvent e) {
		if (childAPClazz == DBConnectionInfoAnchorPane.class) {
			DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
			dbConnAP.init();
			dbConnAP.setInitialValue(new JdbcConnectionInfo());
			addConnectionInfoAP(2, dbConnAP);
			connTestBtn.setDisable(true);

		} else if (childAPClazz == ServerConnectionInfoAnchorPane.class) {
			ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
			serverConnAP.setInitialValue(new JschConnectionInfo());
			addConnectionInfoAP(2, serverConnAP);

		}
	}

	public void removeConnInfo(ActionEvent e) {
		// Remove view
		Node removeNode = this.connInfoStackPane.lookup("#"+connInfoIdx);
		this.connInfoStackPane.getChildren().remove(removeNode);
		
		// Remove data
		connInfoIdx = this.connInfoAPMap.remove(connInfoIdx);
		bringFrontConnInfoAnchorPane(connInfoIdx);
	}
	
	public void prevConnInfo(ActionEvent e) {
		if (connInfoAPMap.getActiveAPCnt() == 0) {
			return;
		}

		connInfoIdx = this.connInfoAPMap.getPrevActiveIdx(connInfoIdx);
		setConnectionBtnIcon(1);
		bringFrontConnInfoAnchorPane(connInfoIdx);
	}

	public void nextConnInfo(ActionEvent e) {
		if (connInfoAPMap.getActiveAPCnt() == 0) {
			return;
		}

		connInfoIdx = this.connInfoAPMap.getNextActiveIdx(connInfoIdx);
		setConnectionBtnIcon(1);
		bringFrontConnInfoAnchorPane(connInfoIdx);
	}

	
	/* View Control Method */
	
	public void setMenuTitle(String menuTitle, FontAwesomeIcon menuIcon) {
		menuTitleLB.setText(menuTitle);
		menuIconIV.setIcon(menuIcon);
	}
	
	// When connectionInfo index changed, this method always will be invoked.
	private void bringFrontConnInfoAnchorPane(long index) {
		connInfoIdx = index;
		
		if(connInfoStackPane.lookup("#" + (index)) != null) {
			connInfoStackPane.lookup("#" + (index)).toFront();
		}
		
		// Set ConnectionInfo index text
		setConnInfoIndexText();
		
		// Button disabled when there is no active ConnectionInfoAP
		if (this.connInfoAPMap.getActiveAPCnt() == 0) {
			connTestBtn.setDisable(true);
			connInfoRemoveBtn.setDisable(true);
			prevConnInfoBtn.setDisable(true);
			nextConnInfoBtn.setDisable(true);
		} else {
			connTestBtn.setDisable(false);
			connInfoRemoveBtn.setDisable(false);
			prevConnInfoBtn.setDisable(false);
			nextConnInfoBtn.setDisable(false);
		}
		
		// Index logging
		// this.connInfoAPMap.print(connInfoIdx);
	}

	private void setConnInfoIndexText() {
		long curIdxTxt = this.connInfoAPMap.getActiveCurIdx(connInfoIdx);
		long maxIdxTxt = this.connInfoAPMap.getActiveAPCnt();
		
		if(curIdxTxt == 0 && maxIdxTxt == 0) {
			connInfoText.setText("※접속정보를 추가해주세요.");	
		} else {
			connInfoText.setText(String.format("(%d/%d)", curIdxTxt, maxIdxTxt));
		}
			
	}
	
	// TODO Convert to Enum class
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
	
	@AllArgsConstructor
	@Data
	private static class StatefulAP {
		private int status; // 1: 기존, 2: 신규, 3: 제거
		private AnchorPane ap;
	}

	private static class ConnInfoAPMap {
		private ObservableMap<Long, StatefulAP> map = FXCollections.observableHashMap();
		
		public long put(StatefulAP ap) {
			this.map.put((long) this.map.size(), ap);
			return this.map.size() - 1;
		}
		
		public long remove(long index) {
			// 상태 변경 (→ 삭제)
			this.map.get(index).setStatus(3);
			
			// 현재 인덱스 뒤에 삭제되지 않은 AnchorPane 갯수 카운트
			long count = this.map.keySet()
					.stream()
					.filter(key -> key >= index)
					.filter(key -> map.get(key).getStatus() != 3)
					.count();

			// 인덱스 업데이트
			return count > 0 ? getNextActiveIdx(index) : getPrevActiveIdx(index);
		}
		
		public StatefulAP get(long index) {
			return this.map.get(index);
		}
		
		public Map<Long, StatefulAP> getActiveAPs() {
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.collect(Collectors.toMap(key -> key, key -> map.get(key)));
		}
		
		public long getActiveAPCnt() {
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.count();
		}
		
		public void clear() {
			this.map.clear();
		}
		
		public long getActiveCurIdx(long connInfoIdx) {
			return this.map.keySet()
					.stream()
					.filter(key -> key <= connInfoIdx)
					.filter(key -> map.get(key).getStatus() != 3)
					.count();
		}
		
		public long getFirstActiveIdx() {
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.findFirst()
					.orElse((long) -1);
		}
		
		public long getLastActiveIdx() {
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.sorted(Collections.reverseOrder())
					.findFirst()
					.orElse((long) -1);
		}
		
		public long getPrevActiveIdx(long connInfoIdx) {
			if(connInfoIdx == getFirstActiveIdx()) {
				return getLastActiveIdx();
			}
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.filter(key -> key < connInfoIdx)
					.sorted(Collections.reverseOrder())
					.findFirst()
					.orElse(getFirstActiveIdx());
		}
		
		public long getNextActiveIdx(long connInfoIdx) {
			if(connInfoIdx == getLastActiveIdx()) {
				return getFirstActiveIdx();
			}
			return this.map.keySet()
					.stream()
					.filter(key -> map.get(key).getStatus() != 3)
					.filter(key -> key > connInfoIdx)
					.findFirst()
					.orElse(getLastActiveIdx());
		}
		
		public void print(long index) {
			System.out.println("Current Index: " + index);
			for (Long key : map.keySet()) {
				if(key == index) {
					System.out.print(key + "[:" + map.get(key).getStatus() + ":], ");	
				} else {
					System.out.print(key + "[" + map.get(key).getStatus() + "], ");
				}
				
			}
			System.out.println();
		}
	}
}
