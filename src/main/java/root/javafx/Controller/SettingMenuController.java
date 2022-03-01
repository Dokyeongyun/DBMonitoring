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
import com.jfoenix.controls.JFXToggleButton;

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
import root.core.domain.ASMDiskUsage;
import root.core.domain.AlertLog;
import root.core.domain.ArchiveUsage;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
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
	 * Pattern ��ü�� �����ʵ�� ������ ���� - Pattern ��ü�� �Է¹��� ����ǥ���Ŀ� �ش��ϴ� ���ѻ��¸ӽ�(finite state
	 * machine)�� �����ϱ� ������ �ν��Ͻ� ��������� ����. - ����, �� �� �����ϵΰ� �̸� �����ϴ� ���� ȿ�����̴�. - �Ӹ�
	 * �ƴ϶�, ���� ��ü�� �̸��� �ο��Ͽ� �ش� ��ü�� �ǹ̰� ��Ȯ������.
	 */

	/* Dependency Injection */
	PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
	PropertyService propService = new FilePropertyService(propRepo);

	/* View Binding */
	@FXML
	SplitPane rootSplitPane;

	@FXML
	AnchorPane noConnInfoConfigAP; // [����] - [�������� ����] ���������� �������� �ʾ��� �� ������ AnchorPane

	@FXML
	AnchorPane noMonitoringConfigAP; // [����] - [����͸� ���� ����] ���������� �������� �ʾ��� �� ������ AnchorPane

	@FXML
	VBox monitoringElementsVBox;

	@FXML
	TextField fileChooserText; // �������� ��θ� �Է�/����ϴ� TextField

	@FXML
	VBox connInfoVBox;

	@FXML
	JFXComboBox<String> monitoringPresetComboBox; // ����͸����� ���� Preset ComboBox

	@FXML
	JFXComboBox<FileSize> fileSizeCB;

	@FXML
	JFXComboBox<RoundingDigits> roundingDigitsCB;

	@FXML
	JFXComboBox<UsageUIType> usageUICB;

	/* Common Data */
	private static final Map<Class<?>, String> DB_MONITORING_CONTENTS = new HashMap<>();
	private static final Map<Class<?>, String> SERVER_MONITORING_CONTENTS = new HashMap<>();
	static {
		DB_MONITORING_CONTENTS.put(ArchiveUsage.class, "Archive Usage");
		DB_MONITORING_CONTENTS.put(TableSpaceUsage.class, "TableSpace Usage");
		DB_MONITORING_CONTENTS.put(ASMDiskUsage.class, "ASM Disk Usage");
		SERVER_MONITORING_CONTENTS.put(OSDiskUsage.class, "OS Disk Usage");
		SERVER_MONITORING_CONTENTS.put(AlertLog.class, "Alert Log");
	}

	List<JdbcConnectionInfo> jdbcConnInfoList;
	List<JschConnectionInfo> jschConnInfoList;

	Map<String, String> monitoringPresetMap = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = propRepo.getLastUseConnInfoFilePath();
		logger.debug("�ֱ� ���� ������Ƽ����: " + lastUsePropertiesFile);
		if (propRepo.isFileExist(lastUsePropertiesFile)) {
			loadSelectedConfigFile(lastUsePropertiesFile);

			// [����] - [����͸� ���� ����] - Preset ���� Event
			monitoringPresetComboBox.valueProperty().addListener((options, oldValue, newValue) -> {
				loadMonitoringConfigFile(monitoringPresetMap.get(newValue));
			});
		} else {
			setVisible(noConnInfoConfigAP, true);
			setVisible(noMonitoringConfigAP, true);
		}

		/* ���� ���� �� - ��ȸ��� ���� �޺��ڽ� */
		// ��ȸ��� ���� �޺��ڽ� ������ ����
		fileSizeCB.getItems().addAll(FileSize.values());

		// ������ ���� ������
		fileSizeCB.valueProperty().addListener((options, oldValue, newValue) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("unit.filesize", newValue);
			propRepo.saveCommonConfig(map);
		});

		// �ʱⰪ - ������ �� ���ٸ� �⺻�� GB
		FileSize fileSize = FileSize.find(propRepo.getCommonResource("unit.filesize"));
		fileSizeCB.getSelectionModel().select(fileSize);

		/* ���� ���� �� - �ݿø� �ڸ��� �޺��ڽ� */
		// �ݿø� �ڸ��� �޺��ڽ� ������ ����
		roundingDigitsCB.getItems().addAll(RoundingDigits.values());

		// ������ ���� ������
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

		// �ʱⰪ - ������ �� ���ٸ� �⺻�� 2
		RoundingDigits roundingDigits = RoundingDigits.find(propRepo.getCommonResource("unit.rounding"));
		roundingDigitsCB.getSelectionModel().select(roundingDigits);

		/* ���� ���� �� - ��뷮 ǥ�ù�� �޺��ڽ� */
		// ��뷮 ǥ�ù�� �޺��ڽ� ������ ����
		usageUICB.getItems().addAll(UsageUIType.values());

		// ������ ���� ������
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

		// �ʱⰪ - ������ �� ���ٸ� �⺻�� GRAPHIC_BAR
		String usageUICode = propRepo.getCommonResource("usage-ui-type");
		usageUICB.getSelectionModel().select(UsageUIType.find(usageUICode));
	}

	/**
	 * [����] - [����͸� ���� ����] - Preset�� �Է� �˾� ����
	 * 
	 * @param e
	 */
	public void showMonitoringPresetPopup(ActionEvent e) {

		// Create input dialog
		String dialogTitle = "Preset ����";
		String dialogHeaderText = "���ο� Monitoring Preset �̸��� �Է����ּ���.";
		String dialogContentText = "Preset �̸�: ";
		CustomTextInputDialog presetInputDialog = new CustomTextInputDialog(dialogTitle, dialogHeaderText,
				dialogContentText);

		// Process input result
		Optional<String> result = presetInputDialog.showAndWait();
		result.ifPresent(input -> {
			// TODO validate input value

			// TODO move this logic to propertyService
			// 1. Preset�� �̿��Ͽ� �������� ���� (./config/monitoring/{���������������ϸ�}/{preset��}.properties
			String connInfoFilePath = fileChooserText.getText();
			String connInfoFileName = connInfoFilePath.substring(0, connInfoFilePath.indexOf(".properties"));
			String filePath = "./config/monitoring/" + connInfoFileName + "/" + input + ".properties";
			propRepo.createNewPropertiesFile(filePath, "Monitoring");

			// 2. ���������������Ͽ� Preset �߰�
			PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");
			config.addProperty("monitoring.setting.preset." + input + ".filepath", filePath);
			propRepo.save(fileChooserText.getText(), config);

			// 3. ����͸� ���� Config and Preset ComboBox ��ε�
			reloadingMonitoringSetting(input);

			// 4. ���� Alert ����
			String successTitle = "Preset ����";
			String successContent = "����͸����� ���� Preset�� �����Ǿ����ϴ�.";
			AlertUtils.showAlert(AlertType.INFORMATION, successTitle, successContent);
		});
	}

	/**
	 * [����] - [�������� ����] - .properties ������ �����ϱ� ���� FileChooser�� ����. ����ڰ� ������ ������ ��ο���
	 * ������ ���� ��, �ùٸ� ���������̶�� �ش� ��θ� remember.properties�� �����Ѵ�. �׷��� �ʴٸ�, '�߸��������Դϴ�'���
	 * ��� ���� ���������� ���� �����ϴ� ȭ������ �̵���Ų��.
	 * 
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

		if (selectedFile != null) {
			if (selectedFile.isFile() && selectedFile.exists()) {
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
	 * 
	 * @param filePath
	 */
	private void loadSelectedConfigFile(String absoluteFilePath) {
		try {
			// 1. �����θ� ����η� ��ȯ�Ѵ�.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);

			// 2. ���ϰ�ο��� �������� ������Ƽ������ �д´�.
			propRepo.loadConnectionInfoConfig(filePath);

			// 3. ������Ƽ���Ͽ� �ۼ��� ���뿡 ���� ���� ��Ҹ� �����Ѵ�.
			createSettingDynamicElements();

			// TODO move this logic to PropertyService
			// 4. remember.properties ���Ͽ� �ֱ� ���� �������� ��θ� �����Ѵ�.
			PropertiesConfiguration rememberConfig = propRepo.getConfiguration("rememberConfig");
			rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
			propRepo.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

			// 5. fileChooserText�� �ؽ�Ʈ�� ���� ���õ� ���ϰ�η� �����Ѵ�.
			fileChooserText.setText(filePath);

		} catch (Exception e) {
			e.printStackTrace();
			// 6. ���� load�� ���� ��, Alert �޽����� ����.
			String headerText = "�������� �ҷ�����";
			String contentText = "�������� �ҷ����⿡ �����߽��ϴ�. ���������� Ȯ�����ּ���.";
			AlertUtils.showAlert(AlertType.ERROR, headerText, contentText);
		}
	}

	/**
	 * [����] - [����͸� ���� ����] - ����͸� ���� ���������� �ҷ��´�.
	 * 
	 * @param filePath
	 */
	private void loadMonitoringConfigFile(String filePath) {
		monitoringElementsVBox.getChildren().clear();

		propRepo.loadMonitoringInfoConfig(filePath);

		String[] dbNames = propRepo.getMonitoringDBNames();
		String[] serverNames = propRepo.getMonitoringServerNames();

		createMonitoringElements(monitoringElementsVBox, dbNames, serverNames);
	}

	/**
	 * [����] - [�������� ����] - ��������� .properties���Ͽ� �����Ѵ�.
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	public void saveConnInfoSettings(ActionEvent e) {
		// TODO �Է°� �˻�

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

		// �������� ReLoading
		loadSelectedConfigFile(configFilePath);
	}

	/**
	 * [����] - [����͸� ���� ����] - ����ڰ� ������ ������ ���� ��������(.properties)�� ���� �Ǵ� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void saveMonitoringSettings(ActionEvent e) {
		// TODO move this logic to PropertyService
		PropertiesConfiguration config = propRepo.getConfiguration("monitoringConfig");
		String presetName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();
		String monitoringFilePath = monitoringPresetMap.get(presetName);

		if (!monitoringFilePath.isEmpty()) {
			for (Node n : monitoringElementsVBox.lookupAll("JFXToggleButton")) {
				JFXToggleButton thisToggle = (JFXToggleButton) n;
				config.setProperty(thisToggle.getId(), thisToggle.isSelected());
			}
			propRepo.save(monitoringFilePath, config);

			loadMonitoringConfigFile(monitoringFilePath);

			String headerText = "���� ����";
			String contentText = "����͸����� ������ ����Ǿ����ϴ�.";
			AlertUtils.showAlert(AlertType.INFORMATION, headerText, contentText);
		}
	}

	/**
	 * ����͸� ���� ������ ��ҵ� ���� ����
	 * 
	 * @param rootVBox
	 * @param monitoringElements
	 * @param monitoringAlias
	 */
	private void createMonitoringElements(VBox rootVBox, String[] dbAlias, String[] serverAlias) {

		MonitoringYNVBox monitoringYNVBox = new MonitoringYNVBox();

		for (Class<?> monitoringType : DB_MONITORING_CONTENTS.keySet()) {
			monitoringYNVBox.addParentToggle(monitoringType, DB_MONITORING_CONTENTS.get(monitoringType));
			for (String alias : dbAlias) {
				// TODO �ʱⰪ ����
				monitoringYNVBox.addChildToggle(monitoringType, alias, true);
			}
		}

		for (Class<?> monitoringType : SERVER_MONITORING_CONTENTS.keySet()) {

			monitoringYNVBox.addParentToggle(monitoringType, SERVER_MONITORING_CONTENTS.get(monitoringType));
			for (String alias : serverAlias) {
				// TODO �ʱⰪ ����
				monitoringYNVBox.addChildToggle(monitoringType, alias, false);
			}
		}
		rootVBox.getChildren().add(monitoringYNVBox);
	}

	/**
	 * [����] - ���������� �ҷ��� ��, ���� UI�� �����Ѵ�.
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
			// DB �������� UI
			dbConnVBox = new ConnectionInfoVBox<>(new DBConnInfoControl());
			dbConnVBox.setMenuTitle("DB ��������", FontAwesomeIcon.DATABASE);
			dbConnVBox.setId("dbConnVBox");
			connInfoVBox.getChildren().add(dbConnVBox);
		}

		dbConnVBox.addConnInfoList(jdbcConnInfoList);

		ConnectionInfoVBox<JschConnectionInfo> serverConnVBox = null;
		if (connInfoVBox.lookup("#serverConnVBox") != null) {
			serverConnVBox = (ConnectionInfoVBox<JschConnectionInfo>) connInfoVBox.lookup("#serverConnVBox");
			serverConnVBox.clearConnInfoMap();
		} else {
			// Server �������� UI
			serverConnVBox = new ConnectionInfoVBox<>(new ServerConnInfoControl());
			serverConnVBox.setMenuTitle("���� ��������", FontAwesomeIcon.SERVER);
			serverConnVBox.setId("serverConnVBox");
			connInfoVBox.getChildren().add(serverConnVBox);
		}

		serverConnVBox.addConnInfoList(jschConnInfoList);

		// [����] - [����͸� ���� ����]
		reloadingMonitoringSetting("");
	}

	/**
	 * [����] - [����͸����μ���] - Preset�� �ٽ� �ҷ��´�.
	 * 
	 * @param curPresetName
	 */
	private void reloadingMonitoringSetting(String presetName) {

		// ���� ���� ���� ���
		String readPresetName = "";
		String readPresetFilePath = "";

		// Preset Map & Preset Combo Clear
		monitoringPresetMap.clear();
		monitoringPresetComboBox.getItems().clear();

		// ����͸����� ���� Preset Map �� �ʱ�ȭ
		monitoringPresetMap = propRepo.getMonitoringPresetMap();
		String lastUsePresetName = propRepo.getLastUseMonitoringPresetName();

		monitoringPresetComboBox.getItems().addAll(monitoringPresetMap.keySet());
		logger.debug("monitoringPresetMap : " + monitoringPresetMap);

		// ������ Preset�� ���ٸ� �ֱ� ���� Preset���� �����Ѵ�.
		// ���� �ֱ� ���� Preset�� ���ٸ� ù��° Preset���� �����Ѵ�.
		if (presetName.isEmpty()) {
			// �ֱ� ���� ����͸� ���� �б�
			if (StringUtils.isEmpty(lastUsePresetName) && monitoringPresetComboBox.getItems().size() != 0) {
				// �ֱ� ���� ������ ���ٸ�, ù��° ���� �б�
				readPresetName = monitoringPresetComboBox.getItems().get(0);
				logger.debug("ù��° Preset: " + readPresetName);
			} else {
				readPresetName = lastUsePresetName;
				logger.debug("�ֱ� ���� ����͸� Preset: " + readPresetName);
			}
		} else {
			readPresetName = presetName;
			logger.debug("���õ� Preset: " + readPresetName);
		}

		logger.debug("readPresetName : " + readPresetName);
		logger.debug("readPresetFilePath : " + readPresetFilePath);

		// ComboBox ���� �� Preset ���� �б�
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
	 * [����] - [�������� ����] - ���ο� �������� ���������� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void createNewConfigFile(ActionEvent e) {
		// TextInputDialog ����
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
		ButtonType okButton = new ButtonType("�Է�", ButtonData.OK_DONE);
		configInputDialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK, ButtonType.CANCEL);
		configInputDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		// Content
		configInputDialog.setTitle("�������� �������� ����");
		configInputDialog.setHeaderText("���ο� �������� ���������� �̸��� �Է����ּ���.");
		configInputDialog.setContentText("�������ϸ�: ");
		// Result
		Optional<String> result = configInputDialog.showAndWait();
		result.ifPresent(input -> {
			if (input.length() == 0) {
				AlertUtils.showAlert(AlertType.ERROR, "�������� �������� ����", "�������ϸ��� �Է����ּ���.");
				return;
			}

			// TODO �Է°� �˻� (���)

			// 1. �������� �������� ���� (./config/connectioninfo/{���������������ϸ�}.properties
			String filePath = "./config/connectioninfo/" + input + ".properties";
			propRepo.createNewPropertiesFile(filePath, "ConnectionInfo");

			// 2. ����͸����� Preset �������� ����
			// (./config/monitoring/{���������������ϸ�}/{default}.properties
			String presetConfigPath = "./config/monitoring/" + input + "/default.properties";
			propRepo.createNewPropertiesFile(presetConfigPath, "Monitoring");

			// 3. Set Node Visible
			setVisible(noConnInfoConfigAP, false);
			setVisible(noMonitoringConfigAP, false);

			// 4. ������ �������� Load
			loadSelectedConfigFile(filePath);
		});
	}
}
