package root.javafx.Controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.control.PasswordField;
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
	 * Pattern ��ü�� �����ʵ�� ������ ���� - Pattern ��ü�� �Է¹��� ����ǥ���Ŀ� �ش��ϴ� ���ѻ��¸ӽ�(finite state
	 * machine)�� �����ϱ� ������ �ν��Ͻ� ��������� ����. - ����, �� �� �����ϵΰ� �̸� �����ϴ� ���� ȿ�����̴�. - �Ӹ�
	 * �ƴ϶�, ���� ��ü�� �̸��� �ο��Ͽ� �ش� ��ü�� �ǹ̰� ��Ȯ������.
	 */
	private static Pattern DB_CONNINFO_AP_NAME_PATTERN = Pattern.compile("dbConnInfo(.*)AP");
	private static Pattern SERVER_CONNINFO_AP_NAME_PATTERN = Pattern.compile("serverConnInfo(.*)AP");

	/* Dependency Injection */
	PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	/* View Binding */
	@FXML
	SplitPane rootSplitPane;

	@FXML
	AnchorPane noPropertiesFileAP1; // [����] - [����͸� ���� ����] ���������� �������� �ʾ��� �� ������ AnchorPane

	@FXML
	AnchorPane noPropertiesFileAP2; // [����] - [�������� ����] ���������� �������� �ʾ��� �� ������ AnchorPane

	@FXML
	VBox monitoringElementsVBox;

	@FXML
	JFXButton settingSaveBtn;

	@FXML
	Button fileChooserBtn; // ���������� �����ϱ� ���� FileChooser

	@FXML
	TextField fileChooserText; // �������� ��θ� �Է�/����ϴ� TextField

	@FXML
	VBox connInfoVBox;

	@FXML
	JFXComboBox<String> monitoringPresetComboBox; // ����͸����� ���� Preset ComboBox

	@FXML
	JFXButton dbConnTestBtn;

	/* Common Data */
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

	Map<String, String> monitoringPresetMap = new HashMap<>();

	// ����͸� ���� ���� Preset Popup
	Popup presetInputPopup = new Popup();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = propertyRepository.getLastUseConnInfoFilePath();
		logger.debug("�ֱ� ���� ������Ƽ����: " + lastUsePropertiesFile);
		if (lastUsePropertiesFile != null) {
			loadSelectedConfigFile(lastUsePropertiesFile);
		}

		// [����] - [����͸� ���� ����] - Preset ���� Event
		monitoringPresetComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					loadMonitoringConfigFile(monitoringPresetMap.get(newValue));
				});
	}

	/**
	 * [����] - [����͸� ���� ����] - Preset�� �Է� �˾� ����
	 * 
	 * @param e
	 */
	public void showMonitoringPresetPopup(ActionEvent e) {

		// TextInputDialog ����
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
		ButtonType okButton = new ButtonType("�Է�", ButtonData.OK_DONE);
		presetInputDialog.getDialogPane().getButtonTypes().removeAll(ButtonType.OK, ButtonType.CANCEL);
		presetInputDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		// Content
		presetInputDialog.setTitle("Preset ����");
		presetInputDialog.setHeaderText("���ο� Monitoring Preset �̸��� �Է����ּ���.");
		presetInputDialog.setContentText("Preset �̸�: ");
		// Result
		Optional<String> result = presetInputDialog.showAndWait();
		result.ifPresent(input -> {
			logger.debug("Monitoring Preset ���� Input: " + input);

			// 1. Preset�� �̿��Ͽ� �������� ���� (./config/monitoring/{���������������ϸ�}/{preset��}.properties
			File connInfoFile = new File(fileChooserText.getText());
			String connInfoFileName = connInfoFile.getName().substring(0,
					connInfoFile.getName().indexOf(".properties"));
			String filePath = "./config/monitoring/" + connInfoFileName + "/" + input + ".properties";
			PropertiesUtils.createNewPropertiesFile(filePath);

			// 2. ���������������Ͽ� Preset �߰�
			PropertiesConfiguration config = PropertiesUtils.connInfoConfig;
			config.addProperty("monitoring.setting.preset." + input + ".filepath", filePath);
			PropertiesUtils.save(fileChooserText.getText(), config);

			// 3. ����͸� ���� Config and Preset ComboBox ��ε�
			reloadingMonitoringSetting(input);

			// 4. ���� Alert ����
			Alert successAlert = new Alert(AlertType.INFORMATION);
			successAlert.setHeaderText("Preset ����");
			successAlert.setContentText("����͸����� ���� Preset�� �����Ǿ����ϴ�.");
			successAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
			successAlert.show();
		});
	}

	/**
	 * [����] - [�������� ����] - .properties ������ �����ϱ� ���� FileChooser�� ����. ����ڰ� ������ ������ ��ο���
	 * ������ ���� ��, �ùٸ� ���������̶�� �ش� ��θ� remember.properties�� �����Ѵ�. �׷��� �ʴٸ�, '�߸���
	 * �����Դϴ�'��� ��� ���� ���������� ���� �����ϴ� ȭ������ �̵���Ų��.
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

		if (selectedFile == null) {
			// NOTHING
		} else {
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
		boolean loadResult = false;

		try {
			// 1. �����θ� ����η� ��ȯ�Ѵ�.
			int startIdx = absoluteFilePath.lastIndexOf("\\config");
			String filePath = startIdx == -1 ? absoluteFilePath : "." + absoluteFilePath.substring(startIdx);

			// 2. ���ϰ�ο��� �������� ������Ƽ������ �д´�.
			propertyRepository.loadConnectionInfoConfig(filePath);

			// 3. ������Ƽ���Ͽ� �ۼ��� ���뿡 ���� ���� ��Ҹ� �����Ѵ�.
			createSettingDynamicElements();

			// 4. ù��° ���������� �� ������ �����´�.
			// bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, 0, dbInfoCntText);
			// bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, 0, serverInfoCntText);

			// 5. remember.properties ���Ͽ� �ֱ� ���� �������� ��θ� �����Ѵ�.
			PropertiesConfiguration rememberConfig = propertyRepository.getConfiguration("rememberConfig");
			rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
			propertyRepository.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

			// 6. fileChooserText�� �ؽ�Ʈ�� ���� ���õ� ���ϰ�η� �����Ѵ�.
			fileChooserText.setText(filePath);

			loadResult = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {

			// 7. ���� load�� �Ϸ�Ǿ��ٴ� �޽����� ����.
			if (loadResult) {
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
	 * [����] - [�������� ����] - ��������� .properties���Ͽ� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void saveConnInfoSettings(ActionEvent e) {
		// TODO �Է°� �˻�

		String configFilePath = fileChooserText.getText();
		PropertiesConfiguration config = PropertiesUtils.connInfoConfig;

		/*
		 * DB �������� StackPane���� ���� �� ������
		 * 
		 * (TextField) {dbName}HostTextField {dbName}.jdbc.host (TextField)
		 * {dbName}PortTextField {dbName}.jdbc.port (TextField) {dbName}SIDTextField
		 * {dbName} (TextField) {dbName}UrlTextField {dbName}.jdbc.url (TextField)
		 * {dbName}AliasTextField {dbName}.jdbc.alias (TextField) {dbName}UserTextField
		 * {dbName}.jdbc.id (PasswordField) {dbName}PasswordTextField {dbName}.jdbc.pw
		 * (JFXComboBox) {dbName}DriverComboBox {dbName}.jdbc.driver
		 */

		List<String> dbNames = new ArrayList<String>(Arrays.asList(propertyRepository.getMonitoringDBNames()));
		for (AnchorPane ap : dbConnInfoIdxMap.values()) {
			String apId = ap.getId();

			Matcher m = DB_CONNINFO_AP_NAME_PATTERN.matcher(apId);
			if (m.matches()) {
				String elementId = m.group(1);
				String newElementId = elementId;
				boolean isNewConnInfo = false;

				// ���� �߰��� ��������
				if (!dbNames.contains(elementId)) {
					Set<Node> textFields = ap.lookupAll("TextField");
					for (Node n : textFields) {
						if (((TextField) n).getId().equals(elementId + "AliasTextField")) {
							isNewConnInfo = true;
							newElementId = ((TextField) n).getText();
							logger.debug("NEW dbName : " + newElementId);
							break;
						}
					}
				}

				String elementIdLower = newElementId.toLowerCase();

				// dbNames �߰�
				if (isNewConnInfo) {
					dbNames.add(newElementId);
					config.setProperty("#DB", newElementId);
					config.setProperty(elementIdLower + ".jdbc.validation", "select 1 from dual");
					config.setProperty(elementIdLower + ".jdbc.connections", 10);
				}

				// TextField Value Update
				Set<Node> textFields = ap.lookupAll("TextField");
				for (Node n : textFields) {
					TextField tf = (TextField) n;
					String tfId = tf.getId();
					String tfText = tf.getText();

					if (tfId.equals(elementId + "UserTextField")) {
						config.setProperty(elementIdLower + ".jdbc.id", tfText);
					} else if (tfId.equals(elementId + "UrlTextField")) {
						config.setProperty(elementIdLower + ".jdbc.url", tfText);
					} else if (tfId.equals(elementId + "AliasTextField")) {
						config.setProperty(elementIdLower + ".jdbc.alias", tfText);
					}
				}

				// PasswordField Value Update
				Set<Node> passwordFields = ap.lookupAll("PasswordField");
				for (Node n : passwordFields) {
					PasswordField pf = (PasswordField) n;
					String pfId = pf.getId();
					String pfText = pf.getText();

					if (pfId.equals(elementId + "PasswordTextField")) {
						config.setProperty(elementIdLower + ".jdbc.pw", pfText);
					}
				}

				// JFXComboBox Value Update
				Set<Node> comboBoxs = ap.lookupAll("JFXComboBox");
				for (Node n : comboBoxs) {
					@SuppressWarnings("unchecked")
					JFXComboBox<String> cb = (JFXComboBox<String>) n;
					String cbId = cb.getId();
					String cbSelectedItem = cb.getSelectionModel().getSelectedItem();

					if (cbId.equals(elementId + "DriverComboBox")) {
						if (cbSelectedItem.equals("thin")) {
							// TODO ���õ� Oracle Driver Type�� ����, Driver �� �����ϱ�, ����� �ӽ÷� ��� ������ �� �Է�
							config.setProperty(elementIdLower + ".jdbc.driver", "oracle.jdbc.driver.OracleDriver");
						}
					}
				}
			}
		}
		// dbNames Update
		config.setProperty("dbnames", dbNames);

		/*
		 * ���� �������� StackPane���� ���� �� ������
		 * 
		 * (TextField) {ServerName}HostTextField {ServerName}.server.host (TextField)
		 * {ServerName}PortTextField {ServerName}.server.port (TextField)
		 * {ServerName}NameTextField {ServerName} (TextField)
		 * {ServerName}UserNameTextField {ServerName}.server.username (TextField)
		 * {ServerName}AlertLogFilePathTextField {ServerName}.server.alertlog.filepath
		 * (PasswordField) {ServerName}PasswordTextField {ServerName}.server.password
		 * (JFXComboBox) {ServerName}AlertLogDateFormatComboBox
		 * {ServerName}.server.alertlog.dateformat
		 */
		List<String> serverNames = new ArrayList<String>(Arrays.asList(config.getStringArray("servernames")));
		for (AnchorPane ap : serverConnInfoIdxMap.values()) {
			String apId = ap.getId();

			Matcher m = SERVER_CONNINFO_AP_NAME_PATTERN.matcher(apId);
			if (m.matches()) {
				String elementId = m.group(1);
				String newElementId = elementId;
				boolean isNewConnInfo = false;

				// ���� �߰��� ��������
				if (!serverNames.contains(elementId)) {
					Set<Node> textFields = ap.lookupAll("TextField");
					for (Node n : textFields) {
						if (((TextField) n).getId().equals(elementId + "NameTextField")) {
							isNewConnInfo = true;
							newElementId = ((TextField) n).getText();
							logger.debug("NEW ServerName : " + newElementId);
							break;
						}
					}
				}

				String elementIdLower = newElementId.toLowerCase();

				// ServerNames �߰�
				if (isNewConnInfo) {
					serverNames.add(newElementId);
					config.setProperty("#Server", newElementId);
					config.setProperty(elementIdLower + ".server.alertlog.readLine", 500);
				}

				// TextField Value Update
				Set<Node> textFields = ap.lookupAll("TextField");
				for (Node n : textFields) {
					TextField tf = (TextField) n;
					String tfId = tf.getId();
					String tfText = tf.getText();

					if (tfId.equals(elementId + "HostTextField")) {
						config.setProperty(elementIdLower + ".server.host", tfText);
					} else if (tfId.equals(elementId + "PortTextField")) {
						config.setProperty(elementIdLower + ".server.port", tfText);
					} else if (tfId.equals(elementId + "UserNameTextField")) {
						config.setProperty(elementIdLower + ".server.username", tfText);
					} else if (tfId.equals(elementId + "AlertLogFilePathTextField")) {
						config.setProperty(elementIdLower + ".server.alertlog.filepath", tfText);
					}
				}

				// PasswordField Value Update
				Set<Node> passwordFields = ap.lookupAll("PasswordField");
				for (Node n : passwordFields) {
					PasswordField pf = (PasswordField) n;
					String pfId = pf.getId();
					String pfText = pf.getText();

					if (pfId.equals(elementId + "PasswordTextField")) {
						config.setProperty(elementIdLower + ".server.password", pfText);
					}
				}

				// JFXComboBox Value Update
				Set<Node> comboBoxs = ap.lookupAll("JFXComboBox");
				for (Node n : comboBoxs) {
					@SuppressWarnings("unchecked")
					JFXComboBox<String> cb = (JFXComboBox<String>) n;
					String cbId = cb.getId();
					String cbSelectedItem = cb.getSelectionModel().getSelectedItem();

					if (cbId.equals(elementId + "AlertLogDateFormatComboBox")) {
						config.setProperty(elementIdLower + ".server.alertlog.dateformat", cbSelectedItem);
						if (isNewConnInfo) {
							String dateFormatRegex = "";
							if (cbSelectedItem.equals("EEE MMM dd HH:mm:ss yyyy")) {
								dateFormatRegex = "...\\s...\\s([0-2][0-9]|1[012])\\s\\d\\d:\\d\\d:\\d\\d\\s\\d{4}";
							} else if (cbSelectedItem.equals("yyyy-MM-dd")) {
								dateFormatRegex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T";
							}
							config.setProperty(elementIdLower + ".server.alertlog.dateformatregex", dateFormatRegex);
						}
					}
				}
			}
		}
		// ServerNames Update
		config.setProperty("servernames", serverNames);

		// ������� ����
		propertyRepository.save(configFilePath, config);
		// �������� ReLoading
		loadSelectedConfigFile(configFilePath);
	}

	/**
	 * [����] - [����͸� ���� ����] - ����ڰ� ������ ������ ���� ��������(.properties)�� ���� �Ǵ� �����Ѵ�.
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
			failAlert.setHeaderText("���� ����");
			failAlert.setContentText("����͸����� ������ ����Ǿ����ϴ�.");
			failAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
			failAlert.show();
		}
	}

	/**
	 * ����͸� ���� ������ ��ҵ� ���� ����
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
					 * 1. ������Ұ� ���õǾ��� ��, 1.1. �θ��Ұ� ���õǾ����� Ȯ�� 1.1.1. ���õ� - break; 1.1.2. ���þȵ� -
					 * isSelected = true 2. ������Ұ� ���õ��� �ʾ��� ��, 2.1. �θ��Ұ� ���õǾ����� Ȯ�� 2.1.1. ���þȵ� -
					 * break; 2.1.2. ���õ� 2.1.2.1. ��� ������� ���ÿ��� Ȯ�� 2.1.2.1.1. ��� ������� ���õ������� - �θ���
					 * isSelected = false;
					 */
					if (isSelected == true) {
						for (Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if (thisToggle.getId().equals(headerToggleId)) { // �θ� Toggle
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
							if (thisToggle.getId().equals(headerToggleId) == false) { // �ڽ� Toggle
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
								if (thisToggle.getId().equals(headerToggleId)) { // �θ� Toggle
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
	 * [����] - ���������� �ҷ��� ��, ���� UI�� �����Ѵ�.
	 */
	private void createSettingDynamicElements() {

		jdbcConnInfoList = PropertiesUtils.getJdbcConnectionMap();
		jschConnInfoList = PropertiesUtils.getJschConnectionMap();
		alcMap = PropertiesUtils.getAlertLogCommandMap();

		// DB �������� UI
		ConnectionInfoVBox dbConnVBox = new ConnectionInfoVBox(DBConnectionInfoAnchorPane.class);
		dbConnVBox.setMenuTitle("DB ��������", FontAwesomeIcon.DATABASE);
		connInfoVBox.getChildren().add(dbConnVBox);

		for (JdbcConnectionInfo jdbc : jdbcConnInfoList) {
			DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
			dbConnAP.setInitialValue(jdbc);
			dbConnVBox.addConnectionInfoAP(dbConnAP);
		}

		// Server �������� UI
		ConnectionInfoVBox serverConnVBox = new ConnectionInfoVBox(ServerConnectionInfoAnchorPane.class);
		serverConnVBox.setMenuTitle("���� ��������", FontAwesomeIcon.SERVER);
		connInfoVBox.getChildren().add(serverConnVBox);

		for (JschConnectionInfo jsch : jschConnInfoList) {
			ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
			serverConnAP.setInitialValue(jsch);
			serverConnVBox.addConnectionInfoAP(serverConnAP);
		}

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
		monitoringPresetMap = propertyRepository.getMonitoringPresetMap();
		String lastUsePresetName = propertyRepository.getLastUseMonitoringPresetName();

		monitoringPresetComboBox.getItems().addAll(monitoringPresetMap.keySet());
		logger.debug("monitoringPresetMap : " + monitoringPresetMap);

		// ������ Preset�� ���ٸ� �ֱ� ���� Preset���� �����Ѵ�. ���� �ֱ� ���� Preset�� ���ٸ� ù��° Preset����
		// �����Ѵ�.
		if (presetName.isEmpty()) {
			// �ֱ� ���� ����͸� ���� �б�
			if (lastUsePresetName.isEmpty() && monitoringPresetComboBox.getItems().size() != 0) {
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
		if (!readPresetName.isEmpty()) {
			monitoringPresetComboBox.getSelectionModel().select(readPresetName);
			loadMonitoringConfigFile(monitoringPresetMap.get(readPresetName));
		}
	}
}
