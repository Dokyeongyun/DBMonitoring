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
	@FXML AnchorPane noPropertiesFileAP1;		// [����] - [����͸� ���� ����] ���������� �������� �ʾ��� �� ������ AnchorPane
	@FXML AnchorPane noPropertiesFileAP2;		// [����] - [�������� ����] ���������� �������� �ʾ��� �� ������ AnchorPane
	@FXML VBox monitoringElementsVBox;
	@FXML JFXButton settingSaveBtn;
	@FXML Button fileChooserBtn;				// ���������� �����ϱ� ���� FileChooser
	@FXML TextField fileChooserText;			// �������� ��θ� �Է�/����ϴ� TextField
	@FXML AnchorPane connectInfoAnchorPane;		
	@FXML FlowPane connectInfoFlowPane;			// DB�������� VBOX, Server�������� VOX�� ��� �����̳�
	@FXML StackPane dbConnInfoStackPane;		// DB�������� ���� �׸��带 ��� �����̳�
	@FXML AnchorPane dbConnInfoNoDataAP;		// DB�������� No Data AchorPane
	@FXML AnchorPane dbConnInfoSampleAP;		// DB�������� Blank AnchorPane
	@FXML Text dbInfoCntText;					// DB�������� �ε��� �ؽ�Ʈ
	@FXML StackPane serverConnInfoStackPane;	// ������������ ���� �׸��带 ��� �����̳�
	@FXML AnchorPane serverConnInfoNoDataAP;	// ������������ No Data AchorPane
	@FXML AnchorPane serverConnInfoSampleAP;	// ������������ Blank AnchorPane
	@FXML Text serverInfoCntText;				// ������������ �ε��� �ؽ�Ʈ
	
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

	// dbConnInfo Index �迭
	Map<Integer, AnchorPane> dbConnInfoIdxMap = new HashMap<>();
	int dbConnInfoIdx = 0;
	
	// serverConnInfo Index �迭
	Map<Integer, AnchorPane> serverConnInfoIdxMap = new HashMap<>();
	int serverConnInfoIdx = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BasicConfigurator.configure();

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
		logger.debug("�ֱ� ���� ������Ƽ����: " + lastUsePropertiesFile);
		
		if(lastUsePropertiesFile != null) {
			loadSelectedConfigFile(lastUsePropertiesFile);
		}
	}
	
	/**
	 * [����] - [�������� ����] - ���ο� DB/Server �������� �ۼ� ���� �����Ѵ�.
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
	 * [����] - [�������� ����] - ���ο� DB �������� �ۼ� ���� �����Ѵ�.
	 * �� ��, ���� �����Ǵ� AnchorPane�� ID�� ����ð��� �̿��Ͽ� �����Ѵ�.
	 * @param e
	 */
	public void addDbConnInfo(ActionEvent e) {
		String newConnInfoAPId = "dbConnInfo" + new Date().getTime() + "AP";
		dbConnInfoIdx = dbConnInfoIdxMap.size();
		addConnInfo(newConnInfoAPId, dbConnInfoStackPane, new JdbcConnectionInfo(), dbConnInfoIdx, dbConnInfoIdxMap, dbInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - ���ο� DB �������� �ۼ� ���� �����Ѵ�.
	 * �� ��, ���� �����Ǵ� AnchorPane�� ID�� ����ð��� �̿��Ͽ� �����Ѵ�.
	 * @param e
	 */
	public void addServerConnInfo(ActionEvent e) {
		String newConnInfoAPId = "serverConnInfo" + new Date().getTime() + "AP";
		serverConnInfoIdx = serverConnInfoIdxMap.size();
		addConnInfo(newConnInfoAPId, serverConnInfoStackPane, new JschConnectionInfo(), serverConnInfoIdx, serverConnInfoIdxMap, serverInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - �ش�Ǵ� DB/���� �������� AnchorPane�� ���� ������ �����´�.
	 * @param connInfoMap
	 * @param connInfoIdx
	 * @param cntText
	 */
	private void bringFrontConnInfoAnchorPane(Map<Integer, AnchorPane> connInfoMap, int connInfoIdx, Text cntText) {
		connInfoMap.get(connInfoIdx).toFront();
		cntText.setText("(" + (connInfoIdx + 1) + "/" + connInfoMap.size() + ")");
	}

	/**
	 * [����] - [�������� ����] - ���� DB �������� �������� �̵��Ѵ�.
	 * @param e
	 */
	public void prevDbConnInfo(ActionEvent e) {
		if(dbConnInfoIdxMap.size() == 0) return;
		dbConnInfoIdx = dbConnInfoIdx == 0 ? dbConnInfoIdxMap.size() - 1 : dbConnInfoIdx - 1;
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - ���� ���� �������� �������� �̵��Ѵ�.
	 * @param e
	 */
	public void prevServerConnInfo(ActionEvent e) {
		if(serverConnInfoIdxMap.size() == 0) return;
		serverConnInfoIdx = serverConnInfoIdx == 0 ? serverConnInfoIdxMap.size() - 1 : serverConnInfoIdx - 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - ���� DB �������� �������� �̵��Ѵ�.
	 * @param e
	 */
	public void nextDbConnInfo(ActionEvent e) {
		if(dbConnInfoIdxMap.size() == 0) return;
		dbConnInfoIdx = dbConnInfoIdx == dbConnInfoIdxMap.size() - 1 ? 0 : dbConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - ���� ���� �������� �������� �̵��Ѵ�.
	 * @param e
	 */
	public void nextServerConnInfo(ActionEvent e) {
		if(serverConnInfoIdxMap.size() == 0) return;
		serverConnInfoIdx = serverConnInfoIdx == serverConnInfoIdxMap.size() - 1 ? 0 : serverConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
	}
	
	/**
	 * [����] - [�������� ����] - .properties ������ �����ϱ� ���� FileChooser�� ����.
	 * ����ڰ� ������ ������ ��ο��� ������ ���� ��, �ùٸ� ���������̶�� �ش� ��θ� remember.properties�� �����Ѵ�.
	 * �׷��� �ʴٸ�, '�߸��� �����Դϴ�'��� ��� ���� ���������� ���� �����ϴ� ȭ������ �̵���Ų��.
	 * @param e
	 */
	public void openFileChooser(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		
		// ���ð����� Extension ����
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Properties", ".properties"));
		
		// �ʱ� ���ϰ�� ����
		fileChooser.setInitialDirectory(new File("./config"));
		
		// ���� ����â ����, ���õ� ���� ��ȯ����
		File selectedFile = fileChooser.showOpenDialog((Stage) rootSplitPane.getScene().getWindow());

		if(selectedFile == null) {
			// NOTHING 
		} else {
			if(selectedFile.isFile() && selectedFile.exists()) {
				// �ùٸ� ����
				String filePath = selectedFile.getAbsolutePath();
				loadSelectedConfigFile(filePath);
			} else {
				// �߸��� ����
				fileChooserText.setText("");
			}
		}
	}
	
	/**
	 * [����] - ������Ƽ������ �д´�.
	 * @param filePath
	 */
	private void loadSelectedConfigFile(String absoluteFilePath) {
		boolean loadResult = false;
		
		try {
			// TODO ���� Utils Ŭ������ ���� �ʿ�
			// 1. �����θ� ����η� ��ȯ�Ѵ�.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);	
			
			// 2. ���ϰ�ο��� �������� ������Ƽ������ �д´�.
			PropertiesUtils.loadAppConfiguration(filePath, "connInfoConfig");
			
			// 3. �������� ������Ƽ ���Ͽ��� ����͸� ���� ������ �д´�.
			// �ֱ� ���� ����͸� ���� �б�
			String monitoringSettingFile = PropertiesUtils.connInfoConfig.getString("monitoring.setting.lastuse.filepath");
			if(monitoringSettingFile == null) {
				// �ֱ� ���� ������ ���ٸ�, ù��° ���� �б�
				monitoringSettingFile = PropertiesUtils.connInfoConfig.getString("monitoring.setting.1.filepath");
			}
			// ù��° ������ ���ٸ� ���� ����
			if(monitoringSettingFile != null) {
				PropertiesUtils.loadAppConfiguration(monitoringSettingFile, "monitoringConfig");	
			}

			// 4. ������Ƽ���Ͽ� �ۼ��� ���뿡 ���� ���� ��Ҹ� �����Ѵ�.
			createSettingDynamicElements();
			
			// 5. remember.properties ���Ͽ� �ֱ� ���� �������� ��θ� �����Ѵ�.
			PropertiesConfiguration rememberConfig = PropertiesUtils.getConfig("rememberConfig");
		    rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
		    PropertiesUtils.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

		    // 6. fileChooserText�� �ؽ�Ʈ�� ���� ���õ� ���ϰ�η� �����Ѵ�.
			fileChooserText.setText(filePath);	

			loadResult = true;
		} catch (Exception e1) {
			e1.printStackTrace();
			loadResult = false;
		} finally {
			
			// 7. ���� load�� �Ϸ�Ǿ��ٴ� �޽����� ����.
			if(loadResult) {
				Alert successAlert = new Alert(AlertType.INFORMATION);
				successAlert.setHeaderText("�������� �ҷ�����");
				successAlert.setContentText("���������� ���������� �ҷ��Խ��ϴ�.");
				successAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
				successAlert.show();
			} else {
				Alert failAlert = new Alert(AlertType.ERROR);
				failAlert.setHeaderText("�������� �ҷ�����");
				failAlert.setContentText("�������� �ҷ����⿡ �����߽��ϴ�. ���������� Ȯ�����ּ���.");
				failAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
				failAlert.show();
			}
		}
	}
	
	/**
	 * [����] - ����͸��� �����Ѵ�.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		Application.main(new String[] {});
	}
	
	/**
	 * ���� StackPane�� top layer�� �����Ѵ�.
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
	 * [����] - [�������� ����] - ��������� .properties���Ͽ� �����Ѵ�.
	 * @param e
	 */
	public void saveConnInfoSettings(ActionEvent e) {
		
	}

	/**
	 * [����] - [����͸� ���� ����] - ����ڰ� ������ ������ ���� ��������(.properties)�� ���� �Ǵ� �����Ѵ�.
	 * @param e
	 */
	public void saveSettings(ActionEvent e) {
		
		// TODO ���������� ������ ��� �� ���ϸ��� ������ �� �ֵ��� UI �����ϱ�
		
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
	 * ���� ��� Home Icon(fxId: homeBtn) onAction Event
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
	 * ����͸� ���� ������ ��ҵ� ���� ����
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
						1. ������Ұ� ���õǾ��� ��, 
							1.1. �θ��Ұ� ���õǾ����� Ȯ��
						 		1.1.1. ���õ� 	- break;
						 		1.1.2. ���þȵ� 	- isSelected = true
						2. ������Ұ� ���õ��� �ʾ��� ��,
							2.1. �θ��Ұ� ���õǾ����� Ȯ��
								2.1.1. ���þȵ�	- break;
								2.1.2. ���õ�		
									2.1.2.1. ��� ������� ���ÿ��� Ȯ��
										2.1.2.1.1. ��� ������� ���õ������� - �θ��� isSelected = false;
					*/
					if(isSelected == true) {
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(headerToggleId)) { // �θ� Toggle
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
							if(thisToggle.getId().equals(headerToggleId) == false) { // �ڽ� Toggle
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
								if(thisToggle.getId().equals(headerToggleId)) { // �θ� Toggle
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
	 * DB �������� ���� ����
	 * @param rootStackPane �������� Layout�� ���� StackPane
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
	 * ���� �������� ���� ����
	 * @param rootStackPane �������� Layout�� ���� StackPane
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
	 * Label�� Ŭ������ �����Ѵ�.
	 * @param styleClass
	 * @param labels
	 */
	private void setLabelsStyleClass(String styleClass, Label...labels) {
		for(Label l : labels) {
			l.getStyleClass().add(styleClass);
		}
	}
	
	/**
	 * [����] - [�������� ����] - URL TextField�� Value�� �����Ѵ�.
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
	 * [����] - ���������� �ҷ��� ��, ���� UI�� �����Ѵ�.
	 */
	private void createSettingDynamicElements() {

		dbNames = PropertiesUtils.connInfoConfig.getString("dbnames").split("/");
		serverNames = PropertiesUtils.connInfoConfig.getString("servernames").split("/");
		
		jdbcConnInfoList = PropertiesUtils.getJdbcConnectionMap();
		jschConnInfoList = PropertiesUtils.getJschConnectionMap();
		alcMap = PropertiesUtils.getAlertLogCommandMap();
		dbMonitorings = PropertiesUtils.combinedConfig.getStringArray("db.monitoring.contents");
		serverMonitorings = PropertiesUtils.combinedConfig.getStringArray("server.monitoring.contents");
		
		// [����] - [����͸� ���� ����] TAB ���� ��� ����
		createMonitoringElements(monitoringElementsVBox, dbMonitorings, dbNames);
		createMonitoringElements(monitoringElementsVBox, serverMonitorings, serverNames);
		
		// [����] - [�������� ����] TAB ���� ��� ����
		if(jdbcConnInfoList.size() == 0) { // DB �������� ����
			dbConnInfoNoDataAP.setVisible(true);	
			dbInfoCntText.setText("��������Ƽ������ ���ų� ���������� �߰����ּ���.");
		} else {
			dbConnInfoNoDataAP.setVisible(true);
			jdbcConnInfoList.forEach(info -> {
				String dbConnInfoDetailAPId = "dbConnInfo" + info.getJdbcDBName() + "AP";
				createJdbcConnInfoElements(dbConnInfoStackPane, info, dbConnInfoDetailAPId);
			});
			dbInfoCntText.setText("(" + (dbConnInfoIdx + 1) + "/" + dbConnInfoIdxMap.size() + ")");
		}
		
		if(jschConnInfoList.size() == 0) { // ���� �������� ����
			serverConnInfoNoDataAP.setVisible(true);	
			serverInfoCntText.setText("��������Ƽ������ ���ų� ���������� �߰����ּ���.");
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
