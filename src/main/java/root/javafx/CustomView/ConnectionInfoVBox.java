package root.javafx.CustomView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.Data;
import root.javafx.DI.DependencyInjection;
import root.javafx.Service.ConnectionTestService;
import root.utils.AlertUtils;
import root.utils.SceneUtils;

public class ConnectionInfoVBox<T> extends VBox {

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
	
	private ConnInfoControl<T> connInfoControl;

	private ConnInfoAPMap connInfoAPMap = new ConnInfoAPMap();
	
	private long connInfoIdx = -1;
	
	public ConnectionInfoVBox(ConnInfoControl<T> connInfoControl) {
		this.connInfoControl = connInfoControl;
		
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/ConnectionInfoVBox.fxml");
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
		long newIdx = connInfoAPMap.put(new StatefulAP(type, (ConnectionInfoAP) connInfoAP));
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

	public boolean saveConnInfoSettings(String configFilePath) {
		return connInfoControl.save(configFilePath, this.connInfoAPMap.getActiveAPs().values());
	}
	
	public void addConnInfoList(List<T> connInfoList) {
		if (connInfoList.isEmpty()) {
			connInfoNoDataAP.toFront();	
			return;
		}
		
		for(T connInfo : connInfoList) {
			ConnectionInfoAP connInfoAP = connInfoControl.getConnInfoAP(connInfo);
			addConnectionInfoAP(1, connInfoAP);
		}
	}
	
	/* Button Click Listener */
	
	public void testConnection(ActionEvent e) {

		// 현재 AP에 작성된 접속정보를 이용해 연결 테스트
		ConnectionInfoAP curAP = connInfoAPMap.get(connInfoIdx).getAp();
		
		ConnectionTestService testService = connInfoControl.getConnectionTestService(curAP);
		
		if (testService != null) {
			// 아이콘 변경
			setConnectionBtnIcon(4);

			// 성공시 콜백 이벤트 설정
			testService.setOnSucceeded(s -> {
				testService.alertSucceed();
				setConnectionBtnIcon(2);
			});
			
			// 실패시 콜백 이벤트 설정
			testService.setOnFailed(f -> {
				testService.alertFailed();
				setConnectionBtnIcon(3);
			});

			// 연결테스트 시작
			testService.start();
		} else {
			AlertUtils.showAlert(AlertType.ERROR, "연결 테스트", "연결 테스트를 수행하기 위한 정보가 부족합니다.\n접속정보를 입력해주세요.");
		}
	}
	
	public void addNewConnInfo(ActionEvent e) {
		addConnectionInfoAP(2, connInfoControl.getNewConnInfoAP());
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
			connTestBtn.setDisable(connInfoControl.canConnectionTest(this.connInfoAPMap.get(index).getAp()));
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
	public static class StatefulAP {
		private int status; // 1: 기존, 2: 신규, 3: 제거
		private ConnectionInfoAP ap;
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
