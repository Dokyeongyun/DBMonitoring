package root.javafx.Controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

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
import lombok.extern.slf4j.Slf4j;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.javafx.CustomView.ConnectionInfoVBox;
import root.javafx.CustomView.DBConnInfoControl;
import root.javafx.CustomView.MonitoringYNVBox;
import root.javafx.CustomView.ServerConnInfoControl;
import root.javafx.CustomView.dialogUI.CustomTextInputDialog;
import root.javafx.utils.AlertUtils;
import root.utils.UnitUtils.FileSize;

@Slf4j
public class SettingMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());

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

	MonitoringYNVBox monitoringYNVBox = new MonitoringYNVBox();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = propService.getLastUseConnectionInfoFilePath();
		log.debug("Last use properties file: " + lastUsePropertiesFile);
		if (lastUsePropertiesFile != null) {
			loadSelectedConfigFile(lastUsePropertiesFile);

			// [����] - [����͸� ���� ����] - Preset ���� Event
			monitoringPresetComboBox.valueProperty().addListener((options, oldValue, newValue) -> {
				if(newValue != null) {
					loadMonitoringConfigFile(propService.getMonitoringPresetFilePath(newValue));	
				}
			});
		} else {
			setVisible(noConnInfoConfigAP, true);
			setVisible(noMonitoringConfigAP, true);
		}

		/* ���� ���� �� - ��ȸ��� ���� �޺��ڽ� */
		fileSizeCB.getItems().addAll(FileSize.values());
		fileSizeCB.getSelectionModel().select(propService.getDefaultFileSizeUnit());
		fileSizeCB.valueProperty().addListener((options, oldValue, newValue) -> {
			propService.saveCommonConfig("unit.filesize", newValue.getUnit());
		});

		/* ���� ���� �� - �ݿø� �ڸ��� �޺��ڽ� */
		// �ݿø� �ڸ��� �޺��ڽ� ������ ����
		roundingDigitsCB.getItems().addAll(RoundingDigits.values());
		roundingDigitsCB.getSelectionModel().select(propService.getDefaultRoundingDigits());
		roundingDigitsCB.valueProperty().addListener((options, oldValue, newValue) -> {
			propService.saveCommonConfig("unit.rounding", String.valueOf(newValue.getDigits()));
		});
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

		/* ���� ���� �� - ��뷮 ǥ�ù�� �޺��ڽ� */
		usageUICB.getItems().addAll(UsageUIType.values());
		usageUICB.getSelectionModel().select(propService.getDefaultUsageUIType());
		usageUICB.valueProperty().addListener((options, oldValue, newValue) -> {
			propService.saveCommonConfig("usage-ui-type", newValue.getCode());
		});
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
			// 1. Preset�� �̿��Ͽ� �������� ���� + ���������������Ͽ� Preset �������� ��� �߰�
			propService.addMonitoringPreset(fileChooserText.getText(), input);

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

			// 2. fileChooserText�� �ؽ�Ʈ�� ���� ���õ� ���ϰ�η� �����Ѵ�.
			fileChooserText.setText(filePath);

			// 3. ���ϰ�ο��� �������� ������Ƽ������ �д´�.
			propService.loadConnectionInfoConfig(filePath);

			// 4. ������Ƽ���Ͽ� �ۼ��� ���뿡 ���� ���� ��Ҹ� �����Ѵ�.
			createSettingDynamicElements();

			// 5. remember.properties ���Ͽ� �ֱ� ���� �������� ��θ� �����Ѵ�.
			propService.saveLastUseConnectionInfoSetting(filePath);
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
		log.debug("Load monitoring config file: " + filePath);
		monitoringElementsVBox.getChildren().clear();

		String presetConfigFileName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();

		List<MonitoringYN> dbYnList = propService.getDBMonitoringYnList(presetConfigFileName);
		List<MonitoringYN> serverYnList = propService.getServerMonitoringYnList(presetConfigFileName);

		createMonitoringElements(monitoringElementsVBox, dbYnList, serverYnList);
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
		String headerText = "���� ����";
		String contentText = "����͸����� ������ ����Ǿ����ϴ�.";
		AlertType alertType = AlertType.INFORMATION;

		try {
			String presetName = monitoringPresetComboBox.getSelectionModel().getSelectedItem();
			propService.saveMonitoringPresetSetting(presetName, monitoringYNVBox.getToggleSelection());
		} catch (Exception ex) {
			contentText = "����͸����� ���� ���忡 �����߽��ϴ�.";
			alertType = AlertType.ERROR;
		} finally {
			AlertUtils.showAlert(alertType, headerText, contentText);
		}
	}

	/**
	 * ����͸� ���� ������ ��ҵ� ���� ����
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
	 * [����] - ���������� �ҷ��� ��, ���� UI�� �����Ѵ�.
	 */
	@SuppressWarnings("unchecked")
	private void createSettingDynamicElements() {

		List<JdbcConnectionInfo> jdbcConnInfoList = propService
				.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		List<JschConnectionInfo> jschConnInfoList = propService
				.getJschConnInfoList(propService.getMonitoringServerNameList());

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

		// Preset Combo Clear
		monitoringPresetComboBox.getItems().clear();
		monitoringPresetComboBox.getItems().addAll(propService.getMonitoringPresetNameList());

		// ������ Preset�� ���ٸ� �ֱ� ���� Preset���� �����Ѵ�.
		// ���� �ֱ� ���� Preset�� ���ٸ� ù��° Preset���� �����Ѵ�.
		if (presetName.isEmpty()) {
			// �ֱ� ���� ����͸� ���� �б�
			String lastUsePresetName = propService.getLastUsePresetFileName(fileChooserText.getText());
			if (StringUtils.isEmpty(lastUsePresetName) && monitoringPresetComboBox.getItems().size() != 0) {
				// �ֱ� ���� ������ ���ٸ�, ù��° ���� �б�
				readPresetName = monitoringPresetComboBox.getItems().get(0);
			} else {
				readPresetName = lastUsePresetName;
			}
		} else {
			readPresetName = presetName;
		}

		// ComboBox ���� �� Preset ���� �б�
		if (!StringUtils.isEmpty(readPresetName)) {
			monitoringPresetComboBox.getSelectionModel().select(readPresetName);
			loadMonitoringConfigFile(propService.getMonitoringPresetFilePath(readPresetName));
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
			// 1. �������� �������� ���� + default ����͸����� Preset �������� ����
			String newSettingFile = propService.addConnectionInfoSetting(input);

			// 2. Set Node Visible
			setVisible(noConnInfoConfigAP, false);
			setVisible(noMonitoringConfigAP, false);

			// 3. ������ �������� Load
			loadSelectedConfigFile(newSettingFile);
		});
	}
}
