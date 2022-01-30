package root.javafx.Controller;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Popup;
import javafx.stage.Stage;
import root.core.domain.AlertLogCommand;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox;
import root.javafx.CustomView.DBConnectionInfoAnchorPane;
import root.javafx.CustomView.ServerConnectionInfoAnchorPane;
import root.utils.PropertiesUtils;

public class SettingMenuController implements Initializable {
	private static Logger logger = Logger.getLogger(SettingMenuController.class);

	/**
	 * Pattern 객체를 정적필드로 선언한 이유 
	 * - Pattern 객체는 입력받은 정규표현식에 해당하는 유한상태머신(finite statemachine)을 
	 *   생성하기 때문에 인스턴스 생성비용이 높다. 
	 * - 따라서, 한 번 생성하두고 이를 재사용하는 것이 효과적이다. 
	 * - 뿐만 아니라, 패턴 객체에 이름을 부여하여 해당 객체의 의미가 명확해진다.
	 */

	/* Dependency Injection */
	PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	/* View Binding */
	@FXML
	SplitPane rootSplitPane;

	@FXML
	AnchorPane noConnInfoConfigAP; // [설정] - [접속정보 설정] 설정파일이 지정되지 않았을 때 보여줄 AnchorPane

	@FXML
	AnchorPane noMonitoringConfigAP; // [설정] - [모니터링 여부 설정] 설정파일이 지정되지 않았을 때 보여줄 AnchorPane

	@FXML
	VBox monitoringElementsVBox;

	@FXML
	JFXButton settingSaveBtn;

	@FXML
	Button fileChooserBtn; // 설정파일을 선택하기 위한 FileChooser

	@FXML
	TextField fileChooserText; // 설정파일 경로를 입력/출력하는 TextField

	@FXML
	VBox connInfoVBox;

	@FXML
	JFXComboBox<String> monitoringPresetComboBox; // 모니터링여부 설정 Preset ComboBox

	/* Common Data */
	String[] dbMonitorings;
	String[] serverMonitorings;

	String[] dbNames;
	String[] serverNames;

	List<JdbcConnectionInfo> jdbcConnInfoList;
	List<JschConnectionInfo> jschConnInfoList;
	Map<String, AlertLogCommand> alcMap;

	Map<String, String> monitoringPresetMap = new HashMap<>();

	// 모니터링 여부 설정 Preset Popup
	Popup presetInputPopup = new Popup();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = propertyRepository.getLastUseConnInfoFilePath();
		logger.debug("최근 사용된 프로퍼티파일: " + lastUsePropertiesFile);
		if (lastUsePropertiesFile != null && propertyRepository.isFileExist(lastUsePropertiesFile)) {
			loadSelectedConfigFile(lastUsePropertiesFile);

			// [설정] - [모니터링 여부 설정] - Preset 변경 Event
			monitoringPresetComboBox.getSelectionModel().selectedItemProperty()
					.addListener((options, oldValue, newValue) -> {
						loadMonitoringConfigFile(monitoringPresetMap.get(newValue));
					});
		} else {
			noConnInfoConfigAP.toFront();
			noMonitoringConfigAP.toFront();
		}
	}

	/**
	 * [설정] - [모니터링 여부 설정] - Preset명 입력 팝업 띄우기
	 * 
	 * @param e
	 */
	public void showMonitoringPresetPopup(ActionEvent e) {

		// TextInputDialog 생성
		TextInputDialog presetInputDialog = new TextInputDialog();
		// ICON
		presetInputDialog.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL, "30"));
		// CSS
		presetInputDialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/css/dialog.css").toExternalForm());
		presetInputDialog.getDialogPane().getStyleClass().add("textInputDialog");
		// Dialog ICON
		Stage stage = (Stage) presetInputDialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/image/add_icon.png").toString()));
		// Button Custom
		ButtonType okButton = new ButtonType("입력", ButtonData.OK_DONE);
		presetInputDialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK, ButtonType.CANCEL);
		presetInputDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		// Content
		presetInputDialog.setTitle("Preset 생성");
		presetInputDialog.setHeaderText("새로운 Monitoring Preset 이름을 입력해주세요.");
		presetInputDialog.setContentText("Preset 이름: ");
		// Result
		Optional<String> result = presetInputDialog.showAndWait();
		result.ifPresent(input -> {
			logger.debug("Monitoring Preset 생성 Input: " + input);

			// 1. Preset명 이용하여 설정파일 생성 (./config/monitoring/{접속정보설정파일명}/{preset명}.properties
			File connInfoFile = new File(fileChooserText.getText());
			String connInfoFileName = connInfoFile.getName().substring(0,
					connInfoFile.getName().indexOf(".properties"));
			String filePath = "./config/monitoring/" + connInfoFileName + "/" + input + ".properties";
			PropertiesUtils.createNewPropertiesFile(filePath);

			// 2. 접속정보설정파일에 Preset 추가
			PropertiesConfiguration config = PropertiesUtils.connInfoConfig;
			config.addProperty("monitoring.setting.preset." + input + ".filepath", filePath);
			PropertiesUtils.save(fileChooserText.getText(), config);

			// 3. 모니터링 여부 Config and Preset ComboBox 재로딩
			reloadingMonitoringSetting(input);

			// 4. 성공 Alert 띄우기
			Alert successAlert = new Alert(AlertType.INFORMATION);
			successAlert.setHeaderText("Preset 생성");
			successAlert.setContentText("모니터링여부 설정 Preset이 생성되었습니다.");
			successAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
			successAlert.show();
		});
	}

	/**
	 * [설정] - [접속정보 설정] - .properties 파일을 선택하기 위한 FileChooser를 연다. 
	 * 사용자가 선택한 파일의 경로에서 파일을 읽은 후, 올바른 설정파일이라면 해당 경로를 remember.properties에 저장한다. 
	 * 그렇지 않다면, '잘못된파일입니다'라는 경고를 띄우고 접속정보를 직접 설정하는 화면으로 이동시킨다.
	 * 
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

		if (selectedFile == null) {
			// NOTHING
		} else {
			if (selectedFile.isFile() && selectedFile.exists()) {
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
	 * 
	 * @param filePath
	 */
	private void loadSelectedConfigFile(String absoluteFilePath) {
		boolean loadResult = false;

		try {
			// 1. 절대경로를 상대경로로 변환한다.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);

			// 2. 파일경로에서 접속정보 프로퍼티파일을 읽는다.
			propertyRepository.loadConnectionInfoConfig(filePath);

			// 3. 프로퍼티파일에 작성된 내용에 따라 동적 요소를 생성한다.
			createSettingDynamicElements();

			// 5. remember.properties 파일에 최근 사용된 설정파일 경로를 저장한다.
			PropertiesConfiguration rememberConfig = propertyRepository.getConfiguration("rememberConfig");
			rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
			propertyRepository.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

			// 6. fileChooserText의 텍스트를 현재 선택된 파일경로로 변경한다.
			fileChooserText.setText(filePath);

			loadResult = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {

			// 7. 파일 load가 완료되었다는 메시지를 띄운다.
			if (loadResult) {
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

	private void loadMonitoringConfigFile(String filePath) {
		monitoringElementsVBox.getChildren().clear();
		dbMonitorings = propertyRepository.getDBMonitoringContents();
		serverMonitorings = propertyRepository.getServerMonitoringContents();

		propertyRepository.loadMonitoringInfoConfig(filePath);
		
		dbNames = propertyRepository.getMonitoringDBNames();
		serverNames = propertyRepository.getMonitoringServerNames();
		
		createMonitoringElements(monitoringElementsVBox, dbMonitorings, dbNames);
		createMonitoringElements(monitoringElementsVBox, serverMonitorings, serverNames);
	}

	/**
	 * [설정] - [접속정보 설정] - 변경사항을 .properties파일에 저장한다.
	 * 
	 * @param e
	 */
	public void saveConnInfoSettings(ActionEvent e) {
		// TODO 입력값 검사

		String configFilePath = fileChooserText.getText();

		ConnectionInfoVBox dbConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#dbConnVBox");
		dbConnVBox.saveConnInfoSettings(configFilePath);
		
		ConnectionInfoVBox serverConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#serverConnVBox");
		serverConnVBox.saveConnInfoSettings(configFilePath);
		
		// 설정파일 ReLoading
		loadSelectedConfigFile(configFilePath);
	}

	/**
	 * [설정] - [모니터링 여부 설정] - 사용자가 선택한 설정에 따라 설정파일(.properties)을 생성 또는 수정한다.
	 * 
	 * @param e
	 */
	public void saveMonitoringSettings(ActionEvent e) {
		PropertiesConfiguration config = PropertiesUtils.monitoringConfig;
		String presetName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();
		String monitoringFilePath = monitoringPresetMap.get(presetName);

		if (!monitoringFilePath.isEmpty()) {
			for (Node n : monitoringElementsVBox.lookupAll("JFXToggleButton")) {
				JFXToggleButton thisToggle = (JFXToggleButton) n;
				config.setProperty(thisToggle.getId(), thisToggle.isSelected());
			}
			propertyRepository.save(monitoringFilePath, config);
			loadMonitoringConfigFile(monitoringFilePath);

			Alert failAlert = new Alert(AlertType.INFORMATION);
			failAlert.setHeaderText("설정 저장");
			failAlert.setContentText("모니터링여부 설정이 저장되었습니다.");
			failAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
			failAlert.show();
		}
	}

	/**
	 * 모니터링 여부 설정할 요소들 동적 생성
	 * 
	 * @param rootVBox
	 * @param monitoringElements
	 * @param elementContents
	 */
	private void createMonitoringElements(VBox rootVBox, String[] monitoringElements, String[] elementContents) {
		for (String mName : monitoringElements) {
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
			headerLabel.setStyle(
					"-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-weight: bold; -fx-font-size: 14px;");

			JFXToggleButton headerToggleBtn = new JFXToggleButton();
			headerToggleBtn.setId(headerToggleId);
			headerToggleBtn.setSize(6);
			headerToggleBtn.setToggleColor(Paint.valueOf("#0132ac"));
			headerToggleBtn.setToggleLineColor(Paint.valueOf("#6e93ea"));
			headerToggleBtn.setAlignment(Pos.CENTER);
			headerToggleBtn.setSelected(propertyRepository.isMonitoringContent(headerToggleId));
			headerToggleBtn.setOnAction((ActionEvent e) -> {
				boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();
				for (Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
					((JFXToggleButton) n).setSelected(isSelected);
				}
			});

			headerHBox.getChildren().addAll(headerToggleBtn, headerLabel);

			// Content
			FlowPane contentFlowPane = new FlowPane();
			contentFlowPane.prefWidthProperty().bind(rootVBox.widthProperty());
			contentFlowPane.minWidthProperty().bind(rootVBox.minWidthProperty());

			for (String s : elementContents) {
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
				contentToggleBtn.setSelected(propertyRepository.isMonitoringContent(contentToggleId));
				contentToggleBtn.setOnAction((ActionEvent e) -> {
					boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();

					/*
					 * 1. 하위요소가 선택되었을 때, 1.1. 부모요소가 선택되었는지 확인 1.1.1. 선택됨 - break; 1.1.2. 선택안됨 -
					 * isSelected = true 2. 하위요소가 선택되지 않았을 때, 2.1. 부모요소가 선택되었는지 확인 2.1.1. 선택안됨 -
					 * break; 2.1.2. 선택됨 2.1.2.1. 모든 하위요소 선택여부 확인 2.1.2.1.1. 모든 하위요소 선택되지않음 - 부모요소
					 * isSelected = false;
					 */
					if (isSelected == true) {
						for (Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if (thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
								if (thisToggle.isSelected() == false) {
									thisToggle.setSelected(true);
									break;
								}
							}
						}
					} else {
						boolean isNotAllSelected = false;
						boolean isParentSelected = true;
						for (Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if (thisToggle.getId().equals(headerToggleId) == false) { // 자식 Toggle
								if (thisToggle.isSelected() == true) {
									isNotAllSelected = true;
									break;
								}
							} else {
								if (thisToggle.isSelected() == false) {
									isParentSelected = false;
									break;
								}
							}
						}

						if (isNotAllSelected == false && isParentSelected == true) {
							for (Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
								JFXToggleButton thisToggle = (JFXToggleButton) n;
								if (thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
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
	 * [설정] - 설정파일을 불러온 후, 동적 UI를 생성한다.
	 */
	private void createSettingDynamicElements() {

		jdbcConnInfoList = PropertiesUtils.getJdbcConnectionMap();
		jschConnInfoList = PropertiesUtils.getJschConnectionMap();
		alcMap = PropertiesUtils.getAlertLogCommandMap();

		ConnectionInfoVBox dbConnVBox = null;
		if (connInfoVBox.lookup("#dbConnVBox") != null) {
			dbConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#dbConnVBox");
			dbConnVBox.clearConnInfoMap();
		} else {
			// DB 접속정보 UI
			dbConnVBox = new ConnectionInfoVBox(DBConnectionInfoAnchorPane.class);
			dbConnVBox.setMenuTitle("DB 접속정보", FontAwesomeIcon.DATABASE);
			dbConnVBox.setId("dbConnVBox");
			connInfoVBox.getChildren().add(dbConnVBox);
		}
		
		for (JdbcConnectionInfo jdbc : jdbcConnInfoList) {
			DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
			dbConnAP.setInitialValue(jdbc);
			dbConnVBox.addConnectionInfoAP(1, dbConnAP);
		}
		
		ConnectionInfoVBox serverConnVBox = null;
		if(connInfoVBox.lookup("#serverConnVBox") != null) {
			serverConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#serverConnVBox");
			serverConnVBox.clearConnInfoMap();
		} else {
			// Server 접속정보 UI
			serverConnVBox = new ConnectionInfoVBox(ServerConnectionInfoAnchorPane.class);
			serverConnVBox.setMenuTitle("서버 접속정보", FontAwesomeIcon.SERVER);
			serverConnVBox.setId("serverConnVBox");
			connInfoVBox.getChildren().add(serverConnVBox);
		}

		for (JschConnectionInfo jsch : jschConnInfoList) {
			ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
			serverConnAP.setInitialValue(jsch);
			serverConnVBox.addConnectionInfoAP(1, serverConnAP);
		}

		// [설정] - [모니터링 여부 설정]
		reloadingMonitoringSetting("");
	}

	/**
	 * [설정] - [모니터링여부설정] - Preset을 다시 불러온다.
	 * 
	 * @param curPresetName
	 */
	private void reloadingMonitoringSetting(String presetName) {

		// 최종 읽을 파일 경로
		String readPresetName = "";
		String readPresetFilePath = "";

		// Preset Map & Preset Combo Clear
		monitoringPresetMap.clear();
		monitoringPresetComboBox.getItems().clear();

		// 모니터링여부 설정 Preset Map 값 초기화
		monitoringPresetMap = propertyRepository.getMonitoringPresetMap();
		String lastUsePresetName = propertyRepository.getLastUseMonitoringPresetName();

		monitoringPresetComboBox.getItems().addAll(monitoringPresetMap.keySet());
		logger.debug("monitoringPresetMap : " + monitoringPresetMap);

		// 지정된 Preset이 없다면 최근 사용된 Preset으로 세팅한다. 만약 최근 사용된 Preset이 없다면 첫번째 Preset으로
		// 세팅한다.
		if (presetName.isEmpty()) {
			// 최근 사용된 모니터링 설정 읽기
			if (lastUsePresetName.isEmpty() && monitoringPresetComboBox.getItems().size() != 0) {
				// 최근 사용된 설정이 없다면, 첫번째 설정 읽기
				readPresetName = monitoringPresetComboBox.getItems().get(0);
				logger.debug("첫번째 Preset: " + readPresetName);
			} else {
				readPresetName = lastUsePresetName;
				logger.debug("최근 사용된 모니터링 Preset: " + readPresetName);
			}
		} else {
			readPresetName = presetName;
			logger.debug("선택된 Preset: " + readPresetName);
		}

		logger.debug("readPresetName : " + readPresetName);
		logger.debug("readPresetFilePath : " + readPresetFilePath);

		// ComboBox 선택 및 Preset 파일 읽기
		if (!readPresetName.isEmpty()) {
			monitoringPresetComboBox.getSelectionModel().select(readPresetName);
			loadMonitoringConfigFile(monitoringPresetMap.get(readPresetName));
		}
	}
}
