package root.javafx.Controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
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
import javafx.stage.Popup;
import javafx.stage.Stage;
import root.core.domain.AlertLogCommand;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.Service.DatabaseConnectService;
import root.utils.AlertUtils;
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

	// Right SplitPane Region
	@FXML
	StackPane rightStackPane;

	// Setting Menu Region
	@FXML
	ScrollPane settingScrollPane;

	@FXML
	AnchorPane settingMainContentAnchorPane;

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
	AnchorPane connectInfoAnchorPane;

	@FXML
	FlowPane connectInfoFlowPane; // DB�������� VBOX, Server�������� VOX�� ��� �����̳�

	@FXML
	StackPane dbConnInfoStackPane; // DB�������� ���� �׸��带 ��� �����̳�

	@FXML
	AnchorPane dbConnInfoNoDataAP; // DB�������� No Data AchorPane

	@FXML
	AnchorPane dbConnInfoSampleAP; // DB�������� Blank AnchorPane

	@FXML
	Text dbInfoCntText; // DB�������� �ε��� �ؽ�Ʈ

	@FXML
	StackPane serverConnInfoStackPane; // ������������ ���� �׸��带 ��� �����̳�

	@FXML
	AnchorPane serverConnInfoNoDataAP; // ������������ No Data AchorPane

	@FXML
	AnchorPane serverConnInfoSampleAP; // ������������ Blank AnchorPane

	@FXML
	Text serverInfoCntText; // ������������ �ε��� �ؽ�Ʈ

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
	 * [����] - [�������� ����] - ���ο� DB/Server �������� �ۼ� ���� �����Ѵ�.
	 * 
	 * @param parentSP
	 * @param connInfo
	 * @param connInfoIdx
	 * @param connInfoMap
	 * @param cntText
	 */
	private void addConnInfo(StackPane parentSP, Object connInfo, int connInfoIdx, Map<Integer, AnchorPane> connInfoMap,
			Text cntText) {
		String newConnInfoId = String.valueOf(new Date().getTime());
		if (connInfo instanceof JdbcConnectionInfo) {
			createJdbcConnInfoElements(parentSP, (JdbcConnectionInfo) connInfo, newConnInfoId);
		} else if (connInfo instanceof JschConnectionInfo) {
			createJschConnInfoElements(parentSP, (JschConnectionInfo) connInfo, newConnInfoId);
		}
		cntText.setText("(" + (connInfoIdx + 1) + "/" + connInfoMap.size() + ")");
		bringFrontConnInfoAnchorPane(connInfoMap, connInfoIdx, cntText);
	}

	/**
	 * [����] - [�������� ����] - ���ο� DB �������� �ۼ� ���� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void addDbConnInfo(ActionEvent e) {
		dbConnInfoIdx = dbConnInfoIdxMap.size();
		addConnInfo(dbConnInfoStackPane, new JdbcConnectionInfo(), dbConnInfoIdx, dbConnInfoIdxMap, dbInfoCntText);
	}

	/**
	 * [����] - [�������� ����] - ���ο� DB �������� �ۼ� ���� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void addServerConnInfo(ActionEvent e) {
		serverConnInfoIdx = serverConnInfoIdxMap.size();
		addConnInfo(serverConnInfoStackPane, new JschConnectionInfo(), serverConnInfoIdx, serverConnInfoIdxMap,
				serverInfoCntText);
	}

	/**
	 * [����] - [�������� ����] - �ش�Ǵ� DB/���� �������� AnchorPane�� ���� ������ �����´�.
	 * 
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
	 * 
	 * @param e
	 */
	public void prevDbConnInfo(ActionEvent e) {
		if (dbConnInfoIdxMap.size() == 0)
			return;
		dbConnInfoIdx = dbConnInfoIdx == 0 ? dbConnInfoIdxMap.size() - 1 : dbConnInfoIdx - 1;
		((FontAwesomeIconView) dbConnTestBtn.lookup("#icon")).setIcon(FontAwesomeIcon.PLUG);
		((FontAwesomeIconView) dbConnTestBtn.lookup("#icon")).setFill(Paint.valueOf("#000000"));
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}

	/**
	 * [����] - [�������� ����] - ���� ���� �������� �������� �̵��Ѵ�.
	 * 
	 * @param e
	 */
	public void prevServerConnInfo(ActionEvent e) {
		if (serverConnInfoIdxMap.size() == 0)
			return;
		serverConnInfoIdx = serverConnInfoIdx == 0 ? serverConnInfoIdxMap.size() - 1 : serverConnInfoIdx - 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
	}

	/**
	 * [����] - [�������� ����] - ���� DB �������� �������� �̵��Ѵ�.
	 * 
	 * @param e
	 */
	public void nextDbConnInfo(ActionEvent e) {
		if (dbConnInfoIdxMap.size() == 0)
			return;
		dbConnInfoIdx = dbConnInfoIdx == dbConnInfoIdxMap.size() - 1 ? 0 : dbConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, dbConnInfoIdx, dbInfoCntText);
	}

	/**
	 * [����] - [�������� ����] - ���� ���� �������� �������� �̵��Ѵ�.
	 * 
	 * @param e
	 */
	public void nextServerConnInfo(ActionEvent e) {
		if (serverConnInfoIdxMap.size() == 0)
			return;
		serverConnInfoIdx = serverConnInfoIdx == serverConnInfoIdxMap.size() - 1 ? 0 : serverConnInfoIdx + 1;
		bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, serverConnInfoIdx, serverInfoCntText);
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
			bringFrontConnInfoAnchorPane(dbConnInfoIdxMap, 0, dbInfoCntText);
			bringFrontConnInfoAnchorPane(serverConnInfoIdxMap, 0, serverInfoCntText);

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
	 * DB �������� ���� ����
	 * 
	 * @param rootStackPane �������� Layout�� ���� StackPane
	 */
	private void createJdbcConnInfoElements(StackPane rootStackPane, JdbcConnectionInfo connInfo, String elementId) {
		AnchorPane dbConnInfoDetailAP = new AnchorPane();
		dbConnInfoDetailAP.setId("dbConnInfo" + elementId + "AP");
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
		col1.setPrefWidth(140);
		col2.setPercentWidth(40);
		col3.setPrefWidth(100);
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
		row1.setMinHeight(30);
		row2.setMinHeight(30);
		row3.setMinHeight(4);
		row4.setMinHeight(30);
		row5.setMinHeight(30);
		row6.setMinHeight(30);
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
		Label dbAliasLabel = new Label("Alias :");
		dbPortLabel.setPadding(new Insets(0, 0, 0, 10));
		dbDriverLabel.setPadding(new Insets(0, 0, 0, 10));
		dbAliasLabel.setPadding(new Insets(0, 0, 0, 10));
		setLabelsStyleClass("gridTitleLabel", dbHostLabel, dbSIDLabel, dbUserLabel, dbPasswordLabel, dbUrlLabel,
				dbPortLabel, dbDriverLabel, dbAliasLabel);

		// Create TextField
		TextField dbHostTextField = new TextField();
		TextField dbSIDTextField = new TextField();
		TextField dbUserTextField = new TextField();
		PasswordField dbPasswordTextField = new PasswordField();
		TextField dbUrlTextField = new TextField();
		TextField dbPortTextField = new TextField();
		TextField dbAliasTextField = new TextField();
		JFXComboBox<String> dbDriverComboBox = new JFXComboBox<String>();
		dbUserTextField.setPrefWidth(200.0);
		dbPasswordTextField.setPrefWidth(200.0);
		dbUrlTextField.setPrefWidth(424.0);
		dbPasswordTextField.setPromptText("<hidden>");
		dbDriverComboBox.getItems().addAll(propertyRepository.getOracleDrivers());

		// Setting TextField ID
		dbHostTextField.setId(elementId + "HostTextField");
		dbSIDTextField.setId(elementId + "SIDTextField");
		dbUserTextField.setId(elementId + "UserTextField");
		dbPasswordTextField.setId(elementId + "PasswordTextField");
		dbUrlTextField.setId(elementId + "UrlTextField");
		dbPortTextField.setId(elementId + "PortTextField");
		dbAliasTextField.setId(elementId + "AliasTextField");
		dbDriverComboBox.setId(elementId + "DriverComboBox");

		// TODO ���� �߰��Ǵ� �������� AP���� Driver, URL �⺻�� ����
		// TextField Value Setting
		dbHostTextField.setText(connInfo.getJdbcHost());
		dbSIDTextField.setText(connInfo.getJdbcSID());
		dbUserTextField.setText(connInfo.getJdbcId());
		dbPasswordTextField.setText(connInfo.getJdbcPw());
		dbUrlTextField.setText(connInfo.getJdbcUrl());
		dbPortTextField.setText(connInfo.getJdbcPort());
		dbAliasTextField.setText(connInfo.getJdbcDBName());
		dbDriverComboBox.getSelectionModel().select(connInfo.getJdbcOracleDriver());

		// TextField onKeyPressedListener
		String dbms = "oracle";
		dbDriverComboBox.setOnInputMethodTextChanged(s -> dbUrlTextField
				.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(),
						dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbHostTextField.setOnKeyReleased(s -> dbUrlTextField
				.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(),
						dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbPortTextField.setOnKeyReleased(s -> dbUrlTextField
				.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(),
						dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbSIDTextField.setOnKeyReleased(s -> dbUrlTextField
				.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(),
						dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText())));
		dbUrlTextField.setOnKeyReleased(s -> {
			String text = ((TextField) s.getSource()).getText();
			Pattern p = Pattern.compile(
					"(.*):(.*):" + dbDriverComboBox.getSelectionModel().getSelectedItem() + ":@(.*):(.*)/(.*)");
			Matcher m = p.matcher(text);
			if (m.matches()) {
				dbHostTextField.setText(m.group(3));
				dbPortTextField.setText(m.group(4));
				dbSIDTextField.setText(m.group(5));
			} else {
				dbUrlTextField
						.setText(generateJdbcURLString(dbms, dbDriverComboBox.getSelectionModel().getSelectedItem(),
								dbHostTextField.getText(), dbPortTextField.getText(), dbSIDTextField.getText()));
			}
		});

		// GridPane Value Setting
		dbConnInfoDetailGP.addRow(0, dbHostLabel, dbHostTextField, dbPortLabel, dbPortTextField);
		dbConnInfoDetailGP.addRow(1, dbSIDLabel, dbSIDTextField, dbDriverLabel, dbDriverComboBox);
		dbConnInfoDetailGP.addRow(3, dbUserLabel, dbUserTextField, dbAliasLabel, dbAliasTextField);
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
	 * 
	 * @param rootStackPane �������� Layout�� ���� StackPane
	 */
	private void createJschConnInfoElements(StackPane rootStackPane, JschConnectionInfo connInfo, String elementId) {
		AnchorPane connInfoDetailAP = new AnchorPane();
		connInfoDetailAP.setId("serverConnInfo" + elementId + "AP");
		connInfoDetailAP.setStyle("-fx-background-color: white");
		serverConnInfoIdxMap.put(serverConnInfoIdxMap.size(), connInfoDetailAP);

		// GridPane Margin Setting
		GridPane connInfoDetailGP = new GridPane();
		AnchorPane.setTopAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setRightAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setBottomAnchor(connInfoDetailGP, 5.0);
		AnchorPane.setLeftAnchor(connInfoDetailGP, 5.0);
		connInfoDetailGP.setId(elementId + "connInfoGP");
		connInfoDetailGP.setPadding(new Insets(5, 10, 5, 10));
		connInfoDetailGP.setStyle("-fx-background-color: white;");

		// GridPane Col/Row Constraints Setting
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		ColumnConstraints col3 = new ColumnConstraints();
		ColumnConstraints col4 = new ColumnConstraints();
		col1.setPrefWidth(140);
		col2.setPercentWidth(40);
		col3.setPrefWidth(100);
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
		row1.setMinHeight(30);
		row2.setMinHeight(30);
		row3.setMinHeight(4);
		row4.setMinHeight(30);
		row5.setMinHeight(30);
		row6.setMinHeight(30);
		row7.setMinHeight(30);
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
		setLabelsStyleClass("gridTitleLabel", serverHostLabel, serverNameLabel, serverUserNameLabel,
				serverPasswordLabel, serverAlertLogFilePathLabel, serverPortLabel, serverAlertLogDateFormatLabel);

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
		serverAlertLogDateFormatComboBox.getItems()
				.addAll(propertyRepository.getCommonResources("server.setting.dateformat.combo"));
		serverPasswordTextField.setPromptText("<hidden>");

		// Setting TextField ID
		serverHostTextField.setId(elementId + "HostTextField");
		serverNameTextField.setId(elementId + "NameTextField");
		serverUserNameTextField.setId(elementId + "UserNameTextField");
		serverPasswordTextField.setId(elementId + "PasswordTextField");
		serverAlertLogFilePathTextField.setId(elementId + "AlertLogFilePathTextField");
		serverPortTextField.setId(elementId + "PortTextField");
		serverAlertLogDateFormatComboBox.setId(elementId + "AlertLogDateFormatComboBox");

		// TODO ���� �߰��Ǵ� �������� AP���� DateFormat ComboBox �⺻�� ����
		// TextField Value Setting
		serverHostTextField.setText(connInfo.getHost());
		serverNameTextField.setText(connInfo.getServerName());
		serverUserNameTextField.setText(connInfo.getUserName());
		serverPasswordTextField.setText(connInfo.getPassword());
		serverAlertLogFilePathTextField.setText(connInfo.getAlc() == null ? "" : connInfo.getAlc().getReadFilePath());
		serverPortTextField
				.setText(String.valueOf(connInfo.getPort()).equals("0") ? "" : String.valueOf(connInfo.getPort()));
		serverAlertLogDateFormatComboBox.getSelectionModel()
				.select(connInfo.getAlc() == null ? "" : connInfo.getAlc().getDateFormat());

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
	 * 
	 * @param styleClass
	 * @param labels
	 */
	private void setLabelsStyleClass(String styleClass, Label... labels) {
		for (Label l : labels) {
			l.getStyleClass().add(styleClass);
		}
	}

	/**
	 * [����] - [�������� ����] - URL TextField�� Value�� �����Ѵ�.
	 * 
	 * @param dbms
	 * @param driver
	 * @param host
	 * @param port
	 * @param sid
	 * @return
	 */
	private String generateJdbcURLString(String dbms, String driver, String host, String port, String sid) {
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:").append(dbms).append(":").append(driver).append(":@").append(host).append(":").append(port)
				.append("/").append(sid);
		return sb.toString();
	}

	/**
	 * [����] - ���������� �ҷ��� ��, ���� UI�� �����Ѵ�.
	 */
	private void createSettingDynamicElements() {
		boolean createResult = false;

		try {
			// ���� ��� �ʱ�ȭ
			dbConnInfoIdx = 0;
			serverConnInfoIdx = 0;
			dbConnInfoStackPane.getChildren().removeIf(c -> !c.getId().contains("noPropertiesFileAP2"));
			serverConnInfoStackPane.getChildren().removeIf(c -> !c.getId().contains("noPropertiesFileAP1"));
			dbConnInfoIdxMap.clear();
			serverConnInfoIdxMap.clear();

			dbNames = propertyRepository.getMonitoringDBNames();
			serverNames = propertyRepository.getMonitoringServerNames();

			jdbcConnInfoList = PropertiesUtils.getJdbcConnectionMap();
			jschConnInfoList = PropertiesUtils.getJschConnectionMap();
			alcMap = PropertiesUtils.getAlertLogCommandMap();

			// [����] - [����͸� ���� ����]
			reloadingMonitoringSetting("");

			// [����] - [�������� ����] TAB ���� ��� ����
			if (jdbcConnInfoList.size() == 0) { // DB �������� ����
				dbConnInfoNoDataAP.setVisible(true);
				dbInfoCntText.setText("��������Ƽ������ ���ų� ���������� �߰����ּ���.");
			} else {
				dbConnInfoNoDataAP.setVisible(true);
				jdbcConnInfoList.forEach(info -> {
					createJdbcConnInfoElements(dbConnInfoStackPane, info, info.getJdbcDBName());
				});
				dbInfoCntText.setText("(" + (dbConnInfoIdx + 1) + "/" + dbConnInfoIdxMap.size() + ")");
			}

			if (jschConnInfoList.size() == 0) { // ���� �������� ����
				serverConnInfoNoDataAP.setVisible(true);
				serverInfoCntText.setText("��������Ƽ������ ���ų� ���������� �߰����ּ���.");
			} else {
				serverConnInfoNoDataAP.setVisible(true);
				jschConnInfoList.forEach(info -> {
					info.setAlc(alcMap.get(info.getServerName()));
					createJschConnInfoElements(serverConnInfoStackPane, info, info.getServerName());
				});
				serverInfoCntText.setText("(" + (serverConnInfoIdx + 1) + "/" + serverConnInfoIdxMap.size() + ")");
			}

			createResult = true;
		} catch (Exception e) {
			createResult = false;
		} finally {
			if (createResult) {
				// Hide No Properties File AnchorPane
				noPropertiesFileAP1.setVisible(false);
				noPropertiesFileAP2.setVisible(false);
			} else {
				// Show No Properties File AnchorPane
				noPropertiesFileAP1.setVisible(true);
				noPropertiesFileAP2.setVisible(true);
			}
		}
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

	/**
	 * [����] - [������������] - �Էµ� ���� ������ DB ���� �׽�Ʈ�� �õ��Ѵ�.
	 * 
	 * @param e
	 */
	public void testDbConnection(ActionEvent e) {
		// TODO dbConnInfoAP�� ���������� �ԷµǾ��ִ��� Ȯ�� (�Է°� �˻�)
		// TODO �ԷµǾ����� ������ ��ư ��Ȱ��ȭ

		// ������ ����
		FontAwesomeIconView icon = (FontAwesomeIconView) dbConnTestBtn.lookup("#icon");
		icon.setIcon(FontAwesomeIcon.SPINNER);
		icon.setFill(Paint.valueOf("#484989"));
		icon.getStyleClass().add("fa-spin");

		AnchorPane curAP = dbConnInfoIdxMap.get(dbConnInfoIdx);

		// Get element Id
		String curAPId = curAP.getId();
		int startIdx = curAPId.indexOf("dbConnInfo") + 10;
		int endIdx = curAPId.lastIndexOf("AP");
		String elementId = curAPId.substring(startIdx, endIdx);

		// Get TextFields
		Map<String, String> textFieldMap = curAP.lookupAll("TextField").stream()
				.collect(Collectors.toMap(Node::getId, (n) -> ((TextField) n).getText()));

		String jdbcUrl = textFieldMap.get(elementId + "UrlTextField");
		String jdbcId = textFieldMap.get(elementId + "UserTextField");

		// Get PasswordField
		Map<String, String> passwordFieldMap = curAP.lookupAll("PasswordField").stream()
				.collect(Collectors.toMap(Node::getId, (n) -> ((PasswordField) n).getText()));

		String jdbcPw = passwordFieldMap.get(elementId + "PasswordTextField");

		// TODO JdbcDriver, Validation Query �ϵ��ڵ� ���� - DBMS�� ���� �ٸ��� �ؾ� ��
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo("oracle.jdbc.driver.OracleDriver", jdbcUrl, jdbcId, jdbcPw,
				"SELECT 1 FROM DUAL", 1);

		DatabaseConnectService dbConnService = new DatabaseConnectService(jdbc);
		dbConnService.setOnSucceeded(s -> {
			AlertUtils.showAlert(AlertType.INFORMATION, "DB �����׽�Ʈ",
					String.format(DatabaseConnectService.SUCCESS_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
			icon.setIcon(FontAwesomeIcon.CHECK);
			icon.setFill(Paint.valueOf("#49a157"));
		});

		dbConnService.setOnFailed(f -> {
			AlertUtils.showAlert(AlertType.ERROR, "DB �����׽�Ʈ",
					String.format(DatabaseConnectService.FAIL_MSG, jdbc.getJdbcUrl(), jdbc.getJdbcDriver()));
			icon.setIcon(FontAwesomeIcon.TIMES);
			icon.setFill(Paint.valueOf("#c40a0a"));
		});

		dbConnService.start();
	}

	/**
	 * [����] - [������������] - �Էµ� ���� ������ Server ���� �׽�Ʈ�� �õ��Ѵ�.
	 * 
	 * @param e
	 */
	public void testServerConnection(ActionEvent e) {
	}
}
