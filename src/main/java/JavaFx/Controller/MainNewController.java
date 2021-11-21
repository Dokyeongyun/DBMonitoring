package JavaFx.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;

import Root.Application.Application;
import Root.Model.AlertLogCommand;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;
import Root.Utils.PropertiesUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainNewController implements Initializable {
	private static Logger logger = Logger.getLogger(MainNewController.class);

	@FXML SplitPane rootSplitPane;
	
	// Left SplitPane Region
	@FXML Button homeBtn;
	@FXML Button settingMenuBtn;
	@FXML Button runMenuBtn;
	
	// Right SplitPane Region
	@FXML StackPane rightStackPane;
	
	// Setting Menu Region
	@FXML ScrollPane settingScrollPane;
	@FXML AnchorPane settingMainContentAnchorPane;
	@FXML AnchorPane noPropertiesFileAP1;		// [설정] - [모니터링 여부 설정] 설정파일이 지정되지 않았을 때 보여줄 AnchorPane
	@FXML AnchorPane noPropertiesFileAP2;		// [설정] - [접속정보 설정] 설정파일이 지정되지 않았을 때 보여줄 AnchorPane
	@FXML VBox monitoringElementsVBox;
	@FXML JFXButton settingSaveBtn;
	@FXML Button fileChooserBtn;				// 설정파일을 선택하기 위한 FileChooser
	@FXML TextField fileChooserText;			// 설정파일 경로를 입력/출력하는 TextField
	@FXML AnchorPane connectInfoAnchorPane;		
	@FXML FlowPane connectInfoFlowPane;			// DB접속정보 VBOX, Server접속정보 VOX를 담는 컨테이너
	@FXML StackPane dbConnInfoStackPane;		// DB접속정보 설정 그리드를 담는 컨테이너
	@FXML AnchorPane dbConnInfoNoDataAP;		// DB접속정보 No Data AchorPane
	@FXML AnchorPane dbConnInfoSampleAP;		// DB접속정보 Blank AnchorPane
	@FXML Text dbInfoCntText;					// DB접속정보 인덱스 텍스트
	@FXML StackPane serverConnInfoStackPane;	// 서버접속정보 설정 그리드를 담는 컨테이너
	@FXML AnchorPane serverConnInfoNoDataAP;	// 서버접속정보 No Data AchorPane
	@FXML AnchorPane serverConnInfoSampleAP;	// 서버접속정보 Blank AnchorPane
	@FXML Text serverInfoCntText;				// 서버접속정보 인덱스 텍스트
	
	// Run Menu Region
	@FXML Button monitoringRunBtn;

	String[] dbMonitorings;
	String[] serverMonitorings;
	
	String[] dbNames;
	String[] serverNames;
	
	// dbConnInfo
	List<JdbcConnectionInfo> jdbcConnInfoList;
	List<JschConnectionInfo> jschConnInfoList;
	Map<String, AlertLogCommand> alcMap;

	// dbConnInfo Index 배열
	Map<Integer, AnchorPane> dbConnInfoIdxMap = new HashMap<>();
	int dbConnInfoIdx = 0;
	
	// serverConnInfo Index 배열
	Map<Integer, AnchorPane> serverConnInfoIdxMap = new HashMap<>();
	int serverConnInfoIdx = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BasicConfigurator.configure();

		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
		logger.debug("최근 사용된 프로퍼티파일: " + lastUsePropertiesFile);
		
		if(lastUsePropertiesFile != null) {
			loadSelectedConfigFile(lastUsePropertiesFile);
		}
	}
	
	/**
	 * [설정] - [접속정보 설정] - 새로운 DB/Server 접속정보 작성 폼을 생성한다.
	 * @param newConnInfoAPId
	 * @param parentSP
	 * @param connInfo
	 * @param connInfoIdx
	 * @param connInfoMap
	 * @param cntText
	 */
	private void addConnInfo(String newConnInfoAPId, StackPane parentSP, Object connInfo, int connInfoIdx, Map<Integer, AnchorPane> connInfoMap, Text cntText) {
		if(connInfo instanceof JdbcConnectionInfo) {
			createJdbcConnInfoElements(parentSP, (JdbcConnectionInfo) connInfo, newConnInfoAPId);	
		} else if(connInfo instanceof JschConnectionInfo) {
			createJschConnInfoElements(parentSP, (JschConnectionInfo) connInfo, newConnInfoAPId);
		}
		cntText.setText("(" + (connInfoIdx + 1) + "/" + connInfoMap.size() + ")");
		bringFrontConnInfoAnchorPane(connInfoMap, connInfoIdx, cntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 새로운 DB 접속정보 작성 폼을 생성한다.
	 * 이 때, 새로 생성되는 AnchorPane의 ID는 현재시간을 이용하여 설정한다.
	 * @param e
	 */
	public void addDbConnInfo(ActionEvent e) {
		String newConnInfoAPId = "dbConnInfo" + new Date().getTime() + "AP";
		dbConnInfoIdx = dbConnInfoIdxMap.size();
		addConnInfo(newConnInfoAPId, dbConnInfoStackPane, new JdbcConnectionInfo(), dbConnInfoIdx, dbConnInfoIdxMap, dbInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 새로운 DB 접속정보 작성 폼을 생성한다.
	 * 이 때, 새로 생성되는 AnchorPane의 ID는 현재시간을 이용하여 설정한다.
	 * @param e
	 */
	public void addServerConnInfo(ActionEvent e) {
		String newConnInfoAPId = "serverConnInfo" + new Date().getTime() + "AP";
		serverConnInfoIdx = serverConnInfoIdxMap.size();
		addConnInfo(newConnInfoAPId, serverConnInfoStackPane, new JschConnectionInfo(), serverConnInfoIdx, serverConnInfoIdxMap, serverInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 해당되는 DB/서버 접속정보 AnchorPane을 가장 앞으로 가져온다.
	 * @param connInfoMap
	 * @param connInfoIdx
	 * @param cntText
	 */
	private void bringFrontConnInfoAnchorPane(Map<Integer, AnchorPane> connInfoMap, int connInfoIdx, Text cntText) {
		connInfoMap.get(connInfoIdx).toFront();
		cntText.setText("(" + (connInfoIdx + 1) + "/" + connInfoMap.size() + ")");
	}

	/**
	 * [설정] - [접속정보 설정] - 이전 DB 접속정보 페이지로 이동한다.
	 * @param e
	 */
	public void prevDbConnInfo(ActionEvent e) {
		if(dbConnInfoIdxMap.size() == 0) return;
		dbConnInfoIdx = dbConnInfoIdx == 0 ? dbConnInfoIdxMap.size() - 1 : dbConnInfoIdx - 1;
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 이전 서버 접속정보 페이지로 이동한다.
	 * @param e
	 */
	public void prevServerConnInfo(ActionEvent e) {
		if(serverConnInfoIdxMap.size() == 0) return;
		serverConnInfoIdx = serverConnInfoIdx == 0 ? serverConnInfoIdxMap.size() - 1 : serverConnInfoIdx - 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 다음 DB 접속정보 페이지로 이동한다.
	 * @param e
	 */
	public void nextDbConnInfo(ActionEvent e) {
		if(dbConnInfoIdxMap.size() == 0) return;
		dbConnInfoIdx = dbConnInfoIdx == dbConnInfoIdxMap.size() - 1 ? 0 : dbConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - 다음 서버 접속정보 페이지로 이동한다.
	 * @param e
	 */
	public void nextServerConnInfo(ActionEvent e) {
		if(serverConnInfoIdxMap.size() == 0) return;
		serverConnInfoIdx = serverConnInfoIdx == serverConnInfoIdxMap.size() - 1 ? 0 : serverConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
	}
	
	/**
	 * [설정] - [접속정보 설정] - .properties 파일을 선택하기 위한 FileChooser를 연다.
	 * 사용자가 선택한 파일의 경로에서 파일을 읽은 후, 올바른 설정파일이라면 해당 경로를 remember.properties에 저장한다.
	 * 그렇지 않다면, '잘못된 파일입니다'라는 경고를 띄우고 접속정보를 직접 설정하는 화면으로 이동시킨다.
	 * @param e
	 */
	public void openFileChooser(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		
		// 선택가능한 Extension 제한
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Properties", ".properties"));
		
		// 초기 파일경로 지정
		fileChooser.setInitialDirectory(new File("./config"));
		
		// 파일 선택창 열고, 선택된 파일 반환받음
		File selectedFile = fileChooser.showOpenDialog((Stage) rootSplitPane.getScene().getWindow());

		if(selectedFile == null) {
			// NOTHING 
		} else {
			if(selectedFile.isFile() && selectedFile.exists()) {
				// 올바른 파일
				String filePath = selectedFile.getAbsolutePath();
				loadSelectedConfigFile(filePath);
			} else {
				// 잘못된 파일
				fileChooserText.setText("");
			}
		}
	}
	
	/**
	 * [설정] - 프로퍼티파일을 읽는다.
	 * @param filePath
	 */
	private void loadSelectedConfigFile(String absoluteFilePath) {
		boolean loadResult = false;
		
		try {
			// TODO 추후 Utils 클래스로 추출 필요
			// 1. 절대경로를 상대경로로 변환한다.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);	
			
			// 2. 파일경로에서 접속정보 프로퍼티파일을 읽는다.
			PropertiesUtils.loadAppConfiguration(filePath, "connInfoConfig");
			
			// 3. 접속정보 프로퍼티 파일에서 모니터링 세팅 설정을 읽는다.
			// 최근 사용된 모니터링 설정 읽기
			String monitoringSettingFile = PropertiesUtils.connInfoConfig.getString("monitoring.setting.lastuse.filepath");
			if(monitoringSettingFile == null) {
				// 최근 사용된 설정이 없다면, 첫번째 설정 읽기
				monitoringSettingFile = PropertiesUtils.connInfoConfig.getString("monitoring.setting.1.filepath");
			}
			// 첫번째 설정도 없다면 읽지 않음
			if(monitoringSettingFile != null) {
				PropertiesUtils.loadAppConfiguration(monitoringSettingFile, "monitoringConfig");	
			}

			// 4. 프로퍼티파일에 작성된 내용에 따라 동적 요소를 생성한다.
			createSettingDynamicElements();
			
			// 5. remember.properties 파일에 최근 사용된 설정파일 경로를 저장한다.
			PropertiesConfiguration rememberConfig = PropertiesUtils.getConfig("rememberConfig");
		    rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
		    PropertiesUtils.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

		    // 6. fileChooserText의 텍스트를 현재 선택된 파일경로로 변경한다.
			fileChooserText.setText(filePath);	

			loadResult = true;
		} catch (Exception e1) {
			e1.printStackTrace();
			loadResult = false;
		} finally {
			
			// 7. 파일 load가 완료되었다는 메시지를 띄운다.
			if(loadResult) {
				Alert successAlert = new Alert(AlertType.INFORMATION);
				successAlert.setHeaderText("설정파일 불러오기");
				successAlert.setContentText("설정파일을 정상적으로 불러왔습니다.");
				successAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
				successAlert.show();
			} else {
				Alert failAlert = new Alert(AlertType.ERROR);
				failAlert.setHeaderText("설정파일 불러오기");
				failAlert.setContentText("설정파일 불러오기에 실패했습니다. 설정파일을 확인해주세요.");
				failAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
				failAlert.show();
			}
		}
	}
	
	/**
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		Application.main(new String[] {});
	}
	
	/**
	 * 우측 StackPane의 top layer를 변경한다.
	 * @param containerPaneName
	 */
	public void changeStackPaneFrontMenu(ActionEvent e) {
		String thisMenuBtnId = ((Button)e.getSource()).getId();
		String thisBorderPaneId = thisMenuBtnId.substring(0, thisMenuBtnId.indexOf("Btn")) + "BorderPane";
		
		ObservableList<Node> childs = rightStackPane.getChildren();
		for(Node n : childs) {
			if(n.getId().equals(thisBorderPaneId)) {
				n.toFront();
				break;
			}
		}
	}
	
	/**
	 * [설정] - [접속정보 설정] - 변경사항을 .properties파일에 저장한다.
	 * @param e
	 */
	public void saveConnInfoSettings(ActionEvent e) {
		
	}

	/**
	 * [설정] - [모니터링 여부 설정] - 사용자가 선택한 설정에 따라 설정파일(.properties)을 생성 또는 수정한다.
	 * @param e
	 */
	public void saveSettings(ActionEvent e) {
		
		// TODO 설정파일을 저장할 경로 및 파일명을 지정할 수 있도록 UI 생성하기
		
//		try {
//			File settingFile = new File(PropertiesUtils.configurationPath);
//			if(settingFile.exists() == false) {
//				settingFile.createNewFile();
//			}
//	
//			for(Node n : monitoringElementsVBox.lookupAll("JFXToggleButton")) {
//				JFXToggleButton thisToggle = (JFXToggleButton) n;
//				PropertiesUtils.propConfig.setProperty(thisToggle.getId(), thisToggle.isSelected());
//			}
//			PropertiesUtils.save();
//			
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}

	/**
	 * 좌측 상단 Home Icon(fxId: homeBtn) onAction Event
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		Scene originalScene = homeBtn.getScene();
		Parent home = FXMLLoader.load(getClass().getResource("../resources/fxml/Home.fxml"));
        Scene homeScene = new Scene(home, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage) homeBtn.getScene().getWindow();
        primaryStage.setScene(homeScene);
	}
	
	/**
	 * 모니터링 여부 설정할 요소들 동적 생성
	 * @param rootVBox
	 * @param monitoringElements
	 * @param elementContents
	 */
	private void createMonitoringElements(VBox rootVBox, String[] monitoringElements, String[] elementContents) {
		for(String mName : monitoringElements) {
			String headerToggleId = mName.replaceAll("\\s", "") + "TotalToggleBtn";
			
			// Header
			VBox eachWrapVBox = new VBox();
			eachWrapVBox.setFillWidth(true);
			
			HBox headerHBox = new HBox();
			headerHBox.setFillHeight(true);
			headerHBox.setPrefHeight(40);
			
			Label headerLabel = new Label();
			headerLabel.setText(mName);
			headerLabel.setTextAlignment(TextAlignment.LEFT);
			headerLabel.setAlignment(Pos.CENTER_LEFT);
			headerLabel.setPrefWidth(200);
			headerLabel.setPrefHeight(40);
			headerLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-weight: bold; -fx-font-size: 14px;");
			
			JFXToggleButton headerToggleBtn = new JFXToggleButton();
			headerToggleBtn.setId(headerToggleId);
			headerToggleBtn.setSize(6);
			headerToggleBtn.setToggleColor(Paint.valueOf("#0132ac"));
			headerToggleBtn.setToggleLineColor(Paint.valueOf("#6e93ea"));
			headerToggleBtn.setAlignment(Pos.CENTER);
			headerToggleBtn.setSelected(PropertiesUtils.monitoringConfig.containsKey(headerToggleId) == false ? true : PropertiesUtils.monitoringConfig.getBoolean(headerToggleId));
			headerToggleBtn.setOnAction((ActionEvent e) -> {
				boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();
				for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
					((JFXToggleButton) n).setSelected(isSelected);
				}
			});
			
			headerHBox.getChildren().addAll(headerToggleBtn, headerLabel);
			
			// Content
			FlowPane contentFlowPane = new FlowPane();
			contentFlowPane.prefWidthProperty().bind(rootVBox.widthProperty());
			contentFlowPane.minWidthProperty().bind(rootVBox.minWidthProperty());
			
			for(String s : elementContents) {
				String contentToggleId = mName.replaceAll("\\s", "") + s + "ToggleBtn";
				HBox contentHBox = new HBox();
				Label contentLabel = new Label();
				contentLabel.setText(s);
				contentLabel.setTextAlignment(TextAlignment.LEFT);
				contentLabel.setAlignment(Pos.CENTER_LEFT);
				contentLabel.setMinWidth(80);
				contentLabel.setMaxWidth(80);
				contentLabel.setPrefHeight(40);
				contentLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-size: 12px;");
				
				JFXToggleButton contentToggleBtn = new JFXToggleButton();
				contentToggleBtn.setId(contentToggleId);
				contentToggleBtn.setSize(4);
				contentToggleBtn.setMinWidth(40);
				contentToggleBtn.setMaxWidth(40);
				contentToggleBtn.setPrefHeight(40);
				contentToggleBtn.setAlignment(Pos.CENTER);
				contentToggleBtn.setSelected(PropertiesUtils.monitoringConfig.containsKey(contentToggleId) == false ? true : PropertiesUtils.monitoringConfig.getBoolean(contentToggleId));
				contentToggleBtn.setOnAction((ActionEvent e) -> {
					boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();
					
					/*
						1. 하위요소가 선택되었을 때, 
							1.1. 부모요소가 선택되었는지 확인
						 		1.1.1. 선택됨 	- break;
						 		1.1.2. 선택안됨 	- isSelected = true
						2. 하위요소가 선택되지 않았을 때,
							2.1. 부모요소가 선택되었는지 확인
								2.1.1. 선택안됨	- break;
								2.1.2. 선택됨		
									2.1.2.1. 모든 하위요소 선택여부 확인
										2.1.2.1.1. 모든 하위요소 선택되지않음 - 부모요소 isSelected = false;
					*/
					if(isSelected == true) {
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
								if(thisToggle.isSelected() == false) {
									thisToggle.setSelected(true);
									break;
								}
							} 
						}
					} else {
						boolean isNotAllSelected = false;
						boolean isParentSelected = true;
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(headerToggleId) == false) { // 자식 Toggle
								if(thisToggle.isSelected() == true) {
									isNotAllSelected = true;
									break;
								}
							} else {
								if(thisToggle.isSelected() == false) {
									isParentSelected = false;
									break;
								}
							}
						}
						
						if(isNotAllSelected == false && isParentSelected == true) {
							for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
								JFXToggleButton thisToggle = (JFXToggleButton) n;
								if(thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
									thisToggle.setSelected(false);
									break;
								} 
							}
						}
					}
				});
				
				contentHBox.getChildren().addAll(contentToggleBtn, contentLabel);
				contentFlowPane.getChildren().addAll(contentHBox);
			}
			
			eachWrapVBox.getChildren().addAll(headerHBox, contentFlowPane);

			rootVBox.getChildren().addAll(eachWrapVBox);
		}
		
		rootVBox.getChildren().add(new Separator());
	}

	/**
	 * DB 접속정보 동적 생성
	 * @param rootStackPane 접속정보 Layout을 담을 StackPane
	 */
	private void createJdbcConnInfoElements(StackPane rootStackPane, JdbcConnectionInfo connInfo, String elementId) {

		AnchorPane dbConnInfoDetailAP = new AnchorPane();
		dbConnInfoDetailAP.setId(elementId);
		dbConnInfoDetailAP.setStyle("-fx-background-color: white");
		dbConnInfoIdxMap.put(dbConnInfoIdxMap.size(), dbConnInfoDetailAP);

		// GridPane Margin Setting
		GridPane dbConnInfoDetailGP = new GridPane();
		AnchorPane.setTopAnchor(dbConnInfoDetailGP, 5.0);
		AnchorPane.setRightAnchor(dbConnInfoDetailGP, 5.0);
		AnchorPane.setBottomAnchor(dbConnInfoDetailGP, 5.0);
		AnchorPane.setLeftAnchor(dbConnInfoDetailGP, 5.0);
		dbConnInfoDetailGP.setPadding(new Insets(5, 10, 5, 10));
		dbConnInfoDetailGP.setStyle("-fx-background-color: white;");
		
		// GridPane Col/Row Constraints Setting
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		ColumnConstraints col3 = new ColumnConstraints();
		ColumnConstraints col4 = new ColumnConstraints();
		col1.setPercentWidth(25);
		col2.setPercentWidth(40);
		col3.setPercentWidth(15);
		col4.setPercentWidth(20);
		col2.setFillWidth(false);
		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		RowConstraints row4 = new RowConstraints();
		RowConstraints row5 = new RowConstraints();
		RowConstraints row6 = new RowConstraints();
		row1.setPercentHeight(24);
		row2.setPercentHeight(24);
		row3.setPercentHeight(4);
		row4.setPercentHeight(24);
		row5.setPercentHeight(24);
		row6.setPercentHeight(24);
		dbConnInfoDetailGP.getColumnConstraints().addAll(col1, col2, col3, col4);
		dbConnInfoDetailGP.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6);

		// Create Label
		Label dbHostLabel = new Label("Host :");
		Label dbSIDLabel = new Label("SID :");
		Label dbUserLabel = new Label("User :");
		Label dbPasswordLabel = new Label("Password :");
		Label dbUrlLabel = new Label("URL :");
		Label dbPortLabel = new Label("Port :");
		Label dbDriverLabel = new Label("Driver :");
		dbPortLabel.setPadding(new Insets(0, 0, 0, 10));
		dbDriverLabel.setPadding(new Insets(0, 0, 0, 10));
		setLabelsStyleClass("gridTitleLabel", dbHostLabel, dbSIDLabel, dbUserLabel, dbPasswordLabel, dbUrlLabel, dbPortLabel, dbDriverLabel);

		// Create TextField
		TextField dbHostTextField = new TextField();
		TextField dbSIDTextField = new TextField();
		TextField dbUserTextField = new TextField();
		PasswordField dbPasswordTextField = new PasswordField();
		TextField dbUrlTextField = new TextField();
		TextField dbPortTextField = new TextField();
		JFXComboBox<String> dbDriverComboBox = new JFXComboBox<String>();
		dbUserTextField.setPrefWidth(200.0);
		dbPasswordTextField.setPrefWidth(200.0);
		dbUrlTextField.setPrefWidth(424.0);
		dbPasswordTextField.setPromptText("<hidden>");
		dbDriverComboBox.getItems().addAll(PropertiesUtils.combinedConfig.getStringArray("db.setting.oracle.driver.combo"));
		
		// TextField Value Setting
		dbHostTextField.setText(connInfo.getJdbcHost());
		dbSIDTextField.setText(connInfo.getJdbcSID());
		dbUserTextField.setText(connInfo.getJdbcId());
		dbPasswordTextField.setText(connInfo.getJdbcPw());
		dbUrlTextField.setText(connInfo.getJdbcUrl());
		dbPortTextField.setText(connInfo.getJdbcPort());
		dbDriverComboBox.getSelectionModel().select(connInfo.getJdbcOracleDriver());
		
		// TextField onKeyPressedListener
		String dbms = "oracle";
		dbDriverComboBox.setOnInputMethodTextChanged(s -> dbUrlTextField.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(), dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbHostTextField.setOnKeyReleased(s -> dbUrlTextField.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(), dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbPortTextField.setOnKeyReleased(s -> dbUrlTextField.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(), dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbSIDTextField.setOnKeyReleased(s -> dbUrlTextField.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(), dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbUrlTextField.setOnKeyReleased(s -> {
			String text = ((TextField)s.getSource()).getText();
			Pattern p = Pattern.compile("(.*):(.*):" + dbDriverComboBox.getSelectionModel().getSelectedItem() + ":@(.*):(.*)/(.*)");
			Matcher m = p.matcher(text);
			if(m.matches()) {
				dbHostTextField.setText(m.group(3));
				dbPortTextField.setText(m.group(4));
				dbSIDTextField.setText(m.group(5));
			} else {
				dbUrlTextField.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(), dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText()));
			}
		});	
		
		// GridPane Value Setting
		dbConnInfoDetailGP.addRow(0, dbHostLabel, dbHostTextField, dbPortLabel, dbPortTextField);
		dbConnInfoDetailGP.addRow(1, dbSIDLabel, dbSIDTextField, dbDriverLabel, dbDriverComboBox);
		dbConnInfoDetailGP.addRow(3, dbUserLabel, dbUserTextField);
		dbConnInfoDetailGP.addRow(4, dbPasswordLabel, dbPasswordTextField);
		dbConnInfoDetailGP.addRow(5, dbUrlLabel, dbUrlTextField);

		GridPane.setColumnSpan(dbUserTextField, 2);
		GridPane.setColumnSpan(dbPasswordTextField, 2);
		GridPane.setColumnSpan(dbUrlTextField, 3);
		
		dbConnInfoDetailAP.getChildren().add(dbConnInfoDetailGP);
		rootStackPane.getChildren().add(dbConnInfoDetailAP);
	}
	
	/**
	 * 서버 접속정보 동적 생성
	 * @param rootStackPane 접속정보 Layout을 담을 StackPane
	 */
	private void createJschConnInfoElements(StackPane rootStackPane, JschConnectionInfo connInfo, String elementId) {
		AnchorPane connInfoDetailAP = new AnchorPane();
		connInfoDetailAP.setId(elementId);
		connInfoDetailAP.setStyle("-fx-background-color: white");
		serverConnInfoIdxMap.put(serverConnInfoIdxMap.size(), connInfoDetailAP);

		// GridPane Margin Setting
		GridPane connInfoDetailGP = new GridPane();
		AnchorPane.setTopAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setRightAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setBottomAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setLeftAnchor(connInfoDetailGP, 5.0);
		connInfoDetailGP.setPadding(new Insets(5, 10, 5, 10));
		connInfoDetailGP.setStyle("-fx-background-color: white;");
		
		// GridPane Col/Row Constraints Setting
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		ColumnConstraints col3 = new ColumnConstraints();
		ColumnConstraints col4 = new ColumnConstraints();
		col1.setPercentWidth(25);
		col2.setPercentWidth(40);
		col3.setPercentWidth(15);
		col4.setPercentWidth(20);
		col2.setFillWidth(false);
		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		RowConstraints row4 = new RowConstraints();
		RowConstraints row5 = new RowConstraints();
		RowConstraints row6 = new RowConstraints();
		RowConstraints row7 = new RowConstraints();
		row1.setPercentHeight(24);
		row2.setPercentHeight(24);
		row3.setPercentHeight(4);
		row4.setPercentHeight(24);
		row5.setPercentHeight(24);
		row6.setPercentHeight(24);
		row7.setPercentHeight(24);
		connInfoDetailGP.getColumnConstraints().addAll(col1, col2, col3, col4);
		connInfoDetailGP.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6, row7);

		// Create Label
		Label serverHostLabel = new Label("Host :");
		Label serverNameLabel = new Label("ServerName :");
		Label serverUserNameLabel = new Label("User :");
		Label serverPasswordLabel = new Label("Password :");
		Label serverAlertLogFilePathLabel = new Label("AlertLog FilePath :");
		Label serverPortLabel = new Label("Port :");
		Label serverAlertLogDateFormatLabel = new Label("AlertLog DateFormat :");
		serverPortLabel.setPadding(new Insets(0, 0, 0, 10));
		setLabelsStyleClass("gridTitleLabel", serverHostLabel, serverNameLabel, serverUserNameLabel, serverPasswordLabel, serverAlertLogFilePathLabel, serverPortLabel, serverAlertLogDateFormatLabel);

		// Create TextField
		TextField serverHostTextField = new TextField();
		TextField serverNameTextField = new TextField();
		TextField serverUserNameTextField = new TextField();
		PasswordField serverPasswordTextField = new PasswordField();
		TextField serverAlertLogFilePathTextField = new TextField();
		TextField serverPortTextField = new TextField();
		JFXComboBox<String> serverAlertLogDateFormatComboBox = new JFXComboBox<>();
		serverUserNameTextField.setPrefWidth(200.0);
		serverPasswordTextField.setPrefWidth(200.0);
		serverAlertLogFilePathTextField.setPrefWidth(424.0);
		serverAlertLogDateFormatComboBox.setPrefWidth(424.0);
		serverAlertLogDateFormatComboBox.getItems().addAll(PropertiesUtils.combinedConfig.getStringArray("server.setting.dateformat.combo"));
		serverPasswordTextField.setPromptText("<hidden>");
		
		
		// TextField Value Setting
		serverHostTextField.setText(connInfo.getHost());
		serverNameTextField.setText(connInfo.getServerName());
		serverUserNameTextField.setText(connInfo.getUserName());
		serverPasswordTextField.setText(connInfo.getPassword());
		serverAlertLogFilePathTextField.setText(connInfo.getAlc() == null ? "" : connInfo.getAlc().getReadFilePath());
		serverPortTextField.setText(String.valueOf(connInfo.getPort()).equals("0") ? "" : String.valueOf(connInfo.getPort()));
		serverAlertLogDateFormatComboBox.getSelectionModel().select(connInfo.getAlc() == null ? "" : connInfo.getAlc().getDateFormat());
		
		// GridPane Value Setting
		connInfoDetailGP.addRow(0, serverHostLabel, serverHostTextField, serverPortLabel, serverPortTextField);
		connInfoDetailGP.addRow(1, serverNameLabel, serverNameTextField);
		connInfoDetailGP.addRow(3, serverUserNameLabel, serverUserNameTextField);
		connInfoDetailGP.addRow(4, serverPasswordLabel, serverPasswordTextField);
		connInfoDetailGP.addRow(5, serverAlertLogFilePathLabel, serverAlertLogFilePathTextField);
		connInfoDetailGP.addRow(6, serverAlertLogDateFormatLabel, serverAlertLogDateFormatComboBox);

		GridPane.setColumnSpan(serverUserNameTextField, 2);
		GridPane.setColumnSpan(serverPasswordTextField, 2);
		GridPane.setColumnSpan(serverAlertLogFilePathTextField, 3);
		GridPane.setColumnSpan(serverAlertLogDateFormatComboBox, 3);
		
		connInfoDetailAP.getChildren().add(connInfoDetailGP);
		rootStackPane.getChildren().add(connInfoDetailAP);
	}

	/**
	 * Label의 클래스를 설정한다.
	 * @param styleClass
	 * @param labels
	 */
	private void setLabelsStyleClass(String styleClass, Label...labels) {
		for(Label l : labels) {
			l.getStyleClass().add(styleClass);
		}
	}
	
	/**
	 * [설정] - [접속정보 설정] - URL TextField의 Value를 결정한다.
	 * @param dbms
	 * @param driver
	 * @param host
	 * @param port
	 * @param sid
	 * @return
	 */
	private String generateJdbcURLString(String dbms, String driver, String host, String port, String sid) {
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:").append(dbms).append(":").append(driver).append(":@")
		.append(host).append(":").append(port).append("/").append(sid);
		return sb.toString();
	}
	
	/**
	 * [설정] - 설정파일을 불러온 후, 동적 UI를 생성한다.
	 */
	private void createSettingDynamicElements() {

		dbNames = PropertiesUtils.connInfoConfig.getString("dbnames").split("/");
		serverNames = PropertiesUtils.connInfoConfig.getString("servernames").split("/");
		
		jdbcConnInfoList = PropertiesUtils.getJdbcConnectionMap();
		jschConnInfoList = PropertiesUtils.getJschConnectionMap();
		alcMap = PropertiesUtils.getAlertLogCommandMap();
		dbMonitorings = PropertiesUtils.combinedConfig.getStringArray("db.monitoring.contents");
		serverMonitorings = PropertiesUtils.combinedConfig.getStringArray("server.monitoring.contents");
		
		// [설정] - [모니터링 여부 설정] TAB 동적 요소 생성
		createMonitoringElements(monitoringElementsVBox, dbMonitorings, dbNames);
		createMonitoringElements(monitoringElementsVBox, serverMonitorings, serverNames);
		
		// [설정] - [접속정보 설정] TAB 동적 요소 생성
		if(jdbcConnInfoList.size() == 0) { // DB 접속정보 없음
			dbConnInfoNoDataAP.setVisible(true);	
			dbInfoCntText.setText("※프로퍼티파일을 열거나 접속정보를 추가해주세요.");
		} else {
			dbConnInfoNoDataAP.setVisible(true);
			jdbcConnInfoList.forEach(info -> {
				String dbConnInfoDetailAPId = "dbConnInfo" + info.getJdbcDBName() + "AP";
				createJdbcConnInfoElements(dbConnInfoStackPane, info, dbConnInfoDetailAPId);
			});
			dbInfoCntText.setText("(" + (dbConnInfoIdx + 1) + "/" + dbConnInfoIdxMap.size() + ")");
		}
		
		if(jschConnInfoList.size() == 0) { // 서버 접속정보 없음
			serverConnInfoNoDataAP.setVisible(true);	
			serverInfoCntText.setText("※프로퍼티파일을 열거나 접속정보를 추가해주세요.");
		} else {
			serverConnInfoNoDataAP.setVisible(true);
			jschConnInfoList.forEach(info -> {
				info.setAlc(alcMap.get(info.getServerName()));
				String serverConnInfoDetailAPId = "serverConnInfo" + info.getServerName() + "AP";
				createJschConnInfoElements(serverConnInfoStackPane, info, serverConnInfoDetailAPId);
			});
			serverInfoCntText.setText("(" + (serverConnInfoIdx + 1) + "/" + serverConnInfoIdxMap.size() + ")");
		}
		
		// Hide No Properties File AnchorPane 
		noPropertiesFileAP1.setVisible(false);
		noPropertiesFileAP2.setVisible(false);
	}
}
