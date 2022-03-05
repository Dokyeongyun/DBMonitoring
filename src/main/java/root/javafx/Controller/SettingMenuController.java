package root.javafx.Controller;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jfoenix.controls.JFXComboBox;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.javafx.CustomView.ConnectionInfoVBox;
import root.javafx.CustomView.DBConnInfoControl;
import root.javafx.CustomView.MonitoringYNVBox;
import root.javafx.CustomView.ServerConnInfoControl;
import root.javafx.CustomView.dialogUI.CustomTextInputDialog;
import root.utils.AlertUtils;
import root.utils.UnitUtils.FileSize;

public class SettingMenuController implements Initializable {
	private static Logger logger = Logger.getLogger(SettingMenuController.class);

	/**
	 * Pattern 객체를 정적필드로 선언한 이유 - Pattern 객체는 입력받은 정규표현식에 해당하는 유한상태머신(finite state
	 * machine)을 생성하기 때문에 인스턴스 생성비용이 높다. - 따라서, 한 번 생성하두고 이를 재사용하는 것이 효과적이다. - 뿐만
	 * 아니라, 패턴 객체에 이름을 부여하여 해당 객체의 의미가 명확해진다.
	 */

	/* Dependency Injection */
	PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
	PropertyService propService = new FilePropertyService(propRepo);

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
	TextField fileChooserText; // 설정파일 경로를 입력/출력하는 TextField

	@FXML
	VBox connInfoVBox;

	@FXML
	JFXComboBox<String> monitoringPresetComboBox; // 모니터링여부 설정 Preset ComboBox

	@FXML
	JFXComboBox<FileSize> fileSizeCB;

	@FXML
	JFXComboBox<RoundingDigits> roundingDigitsCB;

	@FXML
	JFXComboBox<UsageUIType> usageUICB;

	/* Common Data */
	List<JdbcConnectionInfo> jdbcConnInfoList;
	List<JschConnectionInfo> jschConnInfoList;

	Map<String, String> monitoringPresetMap = new HashMap<>();

	MonitoringYNVBox monitoringYNVBox = new MonitoringYNVBox();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = propRepo.getLastUseConnInfoFilePath();
		logger.debug("최근 사용된 프로퍼티파일: " + lastUsePropertiesFile);
		if (propRepo.isFileExist(lastUsePropertiesFile)) {
			loadSelectedConfigFile(lastUsePropertiesFile);

			// [설정] - [모니터링 여부 설정] - Preset 변경 Event
			monitoringPresetComboBox.valueProperty().addListener((options, oldValue, newValue) -> {
				loadMonitoringConfigFile(monitoringPresetMap.get(newValue));
			});
		} else {
			setVisible(noConnInfoConfigAP, true);
			setVisible(noMonitoringConfigAP, true);
		}

		/* 실행 설정 탭 - 조회결과 단위 콤보박스 */
		// 조회결과 단위 콤보박스 아이템 설정
		fileSizeCB.getItems().addAll(FileSize.values());

		// 아이템 변경 리스너
		fileSizeCB.valueProperty().addListener((options, oldValue, newValue) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("unit.filesize", newValue);
			propRepo.saveCommonConfig(map);
		});

		// 초기값 - 설정된 값 없다면 기본값 GB
		FileSize fileSize = FileSize.find(propRepo.getCommonResource("unit.filesize"));
		fileSizeCB.getSelectionModel().select(fileSize);

		/* 실행 설정 탭 - 반올림 자릿수 콤보박스 */
		// 반올림 자릿수 콤보박스 아이템 설정
		roundingDigitsCB.getItems().addAll(RoundingDigits.values());

		// 아이템 변경 리스너
		roundingDigitsCB.valueProperty().addListener((options, oldValue, newValue) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("unit.rounding", newValue.getDigits());
			propRepo.saveCommonConfig(map);
		});

		// Converter
		roundingDigitsCB.setConverter(new StringConverter<RoundingDigits>() {
			@Override
			public String toString(RoundingDigits digits) {
				return String.valueOf(digits.getDigits());
			}

			@Override
			public RoundingDigits fromString(String digits) {
				return RoundingDigits.find(digits);
			}
		});

		// 초기값 - 설정된 값 없다면 기본값 2
		RoundingDigits roundingDigits = RoundingDigits.find(propRepo.getCommonResource("unit.rounding"));
		roundingDigitsCB.getSelectionModel().select(roundingDigits);

		/* 실행 설정 탭 - 사용량 표시방법 콤보박스 */
		// 사용량 표시방법 콤보박스 아이템 설정
		usageUICB.getItems().addAll(UsageUIType.values());

		// 아이템 변경 리스너
		usageUICB.valueProperty().addListener((options, oldValue, newValue) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("usage-ui-type", newValue.getCode());
			propRepo.saveCommonConfig(map);
		});

		// Converter
		usageUICB.setConverter(new StringConverter<UsageUIType>() {
			@Override
			public String toString(UsageUIType uiType) {
				return uiType.getName();
			}

			@Override
			public UsageUIType fromString(String string) {
				return UsageUIType.find(string);
			}
		});

		// 초기값 - 설정된 값 없다면 기본값 GRAPHIC_BAR
		String usageUICode = propRepo.getCommonResource("usage-ui-type");
		usageUICB.getSelectionModel().select(UsageUIType.find(usageUICode));
	}

	/**
	 * [설정] - [모니터링 여부 설정] - Preset명 입력 팝업 띄우기
	 * 
	 * @param e
	 */
	public void showMonitoringPresetPopup(ActionEvent e) {

		// Create input dialog
		String dialogTitle = "Preset 생성";
		String dialogHeaderText = "새로운 Monitoring Preset 이름을 입력해주세요.";
		String dialogContentText = "Preset 이름: ";
		CustomTextInputDialog presetInputDialog = new CustomTextInputDialog(dialogTitle, dialogHeaderText,
				dialogContentText);

		// Process input result
		Optional<String> result = presetInputDialog.showAndWait();
		result.ifPresent(input -> {
			// TODO validate input value

			// TODO move this logic to propertyService
			// 1. Preset명 이용하여 설정파일 생성 (./config/monitoring/{접속정보설정파일명}/{preset명}.properties
			String connInfoFilePath = fileChooserText.getText();
			String connInfoFileName = connInfoFilePath.substring(0, connInfoFilePath.indexOf(".properties"));
			String filePath = "./config/monitoring/" + connInfoFileName + "/" + input + ".properties";
			propRepo.createNewPropertiesFile(filePath, "Monitoring");

			// 2. 접속정보설정파일에 Preset 추가
			PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");
			config.addProperty("monitoring.setting.preset." + input + ".filepath", filePath);
			propRepo.save(fileChooserText.getText(), config);

			// 3. 모니터링 여부 Config and Preset ComboBox 재로딩
			reloadingMonitoringSetting(input);

			// 4. 성공 Alert 띄우기
			String successTitle = "Preset 생성";
			String successContent = "모니터링여부 설정 Preset이 생성되었습니다.";
			AlertUtils.showAlert(AlertType.INFORMATION, successTitle, successContent);
		});
	}

	/**
	 * [설정] - [접속정보 설정] - .properties 파일을 선택하기 위한 FileChooser를 연다. 사용자가 선택한 파일의 경로에서
	 * 파일을 읽은 후, 올바른 설정파일이라면 해당 경로를 remember.properties에 저장한다. 그렇지 않다면, '잘못된파일입니다'라는
	 * 경고를 띄우고 접속정보를 직접 설정하는 화면으로 이동시킨다.
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

		if (selectedFile != null) {
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
		try {
			// 1. 절대경로를 상대경로로 변환한다.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);

			// 2. fileChooserText의 텍스트를 현재 선택된 파일경로로 변경한다.
			fileChooserText.setText(filePath);

			// 3. 파일경로에서 접속정보 프로퍼티파일을 읽는다.
			propRepo.loadConnectionInfoConfig(filePath);

			// 4. 프로퍼티파일에 작성된 내용에 따라 동적 요소를 생성한다.
			createSettingDynamicElements();

			// TODO move this logic to PropertyService
			// 5. remember.properties 파일에 최근 사용된 설정파일 경로를 저장한다.
			PropertiesConfiguration rememberConfig = propRepo.getConfiguration("rememberConfig");
			rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
			propRepo.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

		} catch (Exception e) {
			e.printStackTrace();
			// 6. 파일 load가 실패 시, Alert 메시지를 띄운다.
			String headerText = "설정파일 불러오기";
			String contentText = "설정파일 불러오기에 실패했습니다. 설정파일을 확인해주세요.";
			AlertUtils.showAlert(AlertType.ERROR, headerText, contentText);
		}
	}

	/**
	 * [설정] - [모니터링 여부 설정] - 모니터링 여부 설정파일을 불러온다.
	 * 
	 * @param filePath
	 */
	private void loadMonitoringConfigFile(String filePath) {
		monitoringElementsVBox.getChildren().clear();

		String presetConfigFileName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();

		List<MonitoringYN> dbYnList = propService.getDBMonitoringYnList(presetConfigFileName);
		List<MonitoringYN> serverYnList = propService.getServerMonitoringYnList(presetConfigFileName);

		createMonitoringElements(monitoringElementsVBox, dbYnList, serverYnList);
	}

	/**
	 * [설정] - [접속정보 설정] - 변경사항을 .properties파일에 저장한다.
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	public void saveConnInfoSettings(ActionEvent e) {
		// TODO 입력값 검사

		String configFilePath = fileChooserText.getText();

		ConnectionInfoVBox<JdbcConnectionInfo> dbConnVBox = (ConnectionInfoVBox<JdbcConnectionInfo>) connInfoVBox
				.lookup("#dbConnVBox");

		boolean isDBSaveSucceed = dbConnVBox.saveConnInfoSettings(configFilePath);
		if (!isDBSaveSucceed) {
			return;
		}

		ConnectionInfoVBox<JschConnectionInfo> serverConnVBox = (ConnectionInfoVBox<JschConnectionInfo>) connInfoVBox
				.lookup("#serverConnVBox");

		boolean isServerSaveSucceed = serverConnVBox.saveConnInfoSettings(configFilePath);
		if (!isServerSaveSucceed) {
			return;
		}

		// 설정파일 ReLoading
		loadSelectedConfigFile(configFilePath);
	}

	/**
	 * [설정] - [모니터링 여부 설정] - 사용자가 선택한 설정에 따라 설정파일(.properties)을 생성 또는 수정한다.
	 * 
	 * @param e
	 */
	public void saveMonitoringSettings(ActionEvent e) {
		// TODO move this logic to PropertyService
		PropertiesConfiguration config = new PropertiesConfiguration();
		String presetName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();
		String monitoringFilePath = monitoringPresetMap.get(presetName);

		if (!monitoringFilePath.isEmpty()) {

			Map<MonitoringType, Map<String, Boolean>> selection = monitoringYNVBox.getToggleSelection();

			for (MonitoringType type : selection.keySet()) {
				Map<String, Boolean> aliasMap = selection.get(type);
				for (String alias : aliasMap.keySet()) {
					String key = StringUtils.join(type.getName().replace(" ", "_"), ".", alias);
					config.setProperty(key, aliasMap.get(alias) ? "Y" : "N");
				}
			}
			propRepo.save(monitoringFilePath, config);

			loadMonitoringConfigFile(monitoringFilePath);

			String headerText = "설정 저장";
			String contentText = "모니터링여부 설정이 저장되었습니다.";
			AlertUtils.showAlert(AlertType.INFORMATION, headerText, contentText);
		}
	}

	/**
	 * 모니터링 여부 설정할 요소들 동적 생성
	 * 
	 * @param rootVBox
	 * @param dbYnList
	 * @param serverYnList
	 */
	private void createMonitoringElements(VBox rootVBox, List<MonitoringYN> dbYnList, List<MonitoringYN> serverYnList) {
		monitoringYNVBox = new MonitoringYNVBox();

		for (MonitoringYN dbYn : dbYnList) {
			for (MonitoringTypeAndYN typeAndYn : dbYn.getMonitoringTypeList()) {
				MonitoringType type = typeAndYn.getMonitoringType();
				monitoringYNVBox.addParentToggle(type, type.getName());
				monitoringYNVBox.addChildToggle(type, dbYn.getMonitoringAlias());
			}
		}
		monitoringYNVBox.initSelection(dbYnList);

		for (MonitoringYN serverYn : serverYnList) {
			for (MonitoringTypeAndYN typeAndYn : serverYn.getMonitoringTypeList()) {
				MonitoringType type = typeAndYn.getMonitoringType();
				monitoringYNVBox.addParentToggle(type, type.getName());
				monitoringYNVBox.addChildToggle(type, serverYn.getMonitoringAlias());
			}
		}
		monitoringYNVBox.initSelection(serverYnList);

		rootVBox.getChildren().add(monitoringYNVBox);
	}

	/**
	 * [설정] - 설정파일을 불러온 후, 동적 UI를 생성한다.
	 */
	@SuppressWarnings("unchecked")
	private void createSettingDynamicElements() {

		jdbcConnInfoList = propService.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		jschConnInfoList = propService.getJschConnInfoList(propService.getMonitoringServerNameList());

		ConnectionInfoVBox<JdbcConnectionInfo> dbConnVBox = null;
		if (connInfoVBox.lookup("#dbConnVBox") != null) {
			dbConnVBox = (ConnectionInfoVBox<JdbcConnectionInfo>) connInfoVBox.lookup("#dbConnVBox");
			dbConnVBox.clearConnInfoMap();
		} else {
			// DB 접속정보 UI
			dbConnVBox = new ConnectionInfoVBox<>(new DBConnInfoControl());
			dbConnVBox.setMenuTitle("DB 접속정보", FontAwesomeIcon.DATABASE);
			dbConnVBox.setId("dbConnVBox");
			connInfoVBox.getChildren().add(dbConnVBox);
		}

		dbConnVBox.addConnInfoList(jdbcConnInfoList);

		ConnectionInfoVBox<JschConnectionInfo> serverConnVBox = null;
		if (connInfoVBox.lookup("#serverConnVBox") != null) {
			serverConnVBox = (ConnectionInfoVBox<JschConnectionInfo>) connInfoVBox.lookup("#serverConnVBox");
			serverConnVBox.clearConnInfoMap();
		} else {
			// Server 접속정보 UI
			serverConnVBox = new ConnectionInfoVBox<>(new ServerConnInfoControl());
			serverConnVBox.setMenuTitle("서버 접속정보", FontAwesomeIcon.SERVER);
			serverConnVBox.setId("serverConnVBox");
			connInfoVBox.getChildren().add(serverConnVBox);
		}

		serverConnVBox.addConnInfoList(jschConnInfoList);

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
		monitoringPresetMap = propRepo.getMonitoringPresetMap();
		String lastUsePresetName = propRepo.getLastUseMonitoringPresetName();

		monitoringPresetComboBox.getItems().addAll(monitoringPresetMap.keySet());
		logger.debug("monitoringPresetMap : " + monitoringPresetMap);

		// 지정된 Preset이 없다면 최근 사용된 Preset으로 세팅한다.
		// 만약 최근 사용된 Preset이 없다면 첫번째 Preset으로 세팅한다.
		if (presetName.isEmpty()) {
			// 최근 사용된 모니터링 설정 읽기
			if (StringUtils.isEmpty(lastUsePresetName) && monitoringPresetComboBox.getItems().size() != 0) {
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
		if (!StringUtils.isEmpty(readPresetName)) {
			monitoringPresetComboBox.getSelectionModel().select(readPresetName);
			loadMonitoringConfigFile(monitoringPresetMap.get(readPresetName));
		}
	}

	private void setVisible(Node node, boolean isVisible) {
		node.setVisible(isVisible);
		if (isVisible) {
			node.toFront();
		} else {
			node.toBack();
		}
	}

	/**
	 * [설정] - [접속정보 설정] - 새로운 접속정보 설정파일을 생성한다.
	 * 
	 * @param e
	 */
	public void createNewConfigFile(ActionEvent e) {
		// TextInputDialog 생성
		TextInputDialog configInputDialog = new TextInputDialog();
		// ICON
		configInputDialog.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL, "30"));
		// CSS
		configInputDialog.getDialogPane().getStylesheets().add(
				getClass().getResource(System.getProperty("resourceBaseDir") + "/css/dialog.css").toExternalForm());
		configInputDialog.getDialogPane().getStyleClass().add("textInputDialog");
		// Dialog ICON
		Stage stage = (Stage) configInputDialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(
				this.getClass().getResource(System.getProperty("resourceBaseDir") + "/image/add_icon.png").toString()));
		// Button Custom
		ButtonType okButton = new ButtonType("입력", ButtonData.OK_DONE);
		configInputDialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK, ButtonType.CANCEL);
		configInputDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		// Content
		configInputDialog.setTitle("접속정보 설정파일 생성");
		configInputDialog.setHeaderText("새로운 접속정보 설정파일의 이름을 입력해주세요.");
		configInputDialog.setContentText("설정파일명: ");
		// Result
		Optional<String> result = configInputDialog.showAndWait();
		result.ifPresent(input -> {
			if (input.length() == 0) {
				AlertUtils.showAlert(AlertType.ERROR, "접속정보 설정파일 생성", "설정파일명을 입력해주세요.");
				return;
			}

			// TODO 입력값 검사 (영어만)

			// 1. 접속정보 설정파일 생성 (./config/connectioninfo/{접속정보설정파일명}.properties
			String filePath = "./config/connectioninfo/" + input + ".properties";
			propRepo.createNewPropertiesFile(filePath, "ConnectionInfo");

			// 2. 모니터링여부 Preset 설정파일 생성
			// (./config/monitoring/{접속정보설정파일명}/{default}.properties
			String presetConfigPath = "./config/monitoring/" + input + "/default.properties";
			propRepo.createNewPropertiesFile(presetConfigPath, "Monitoring");

			// 3. Set Node Visible
			setVisible(noConnInfoConfigAP, false);
			setVisible(noMonitoringConfigAP, false);

			// 4. 생성된 설정파일 Load
			loadSelectedConfigFile(filePath);
		});
	}
}
