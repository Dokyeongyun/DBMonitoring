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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.stage.Stage;
import root.core.domain.AlertLogCommand;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.CustomView.ConnectionInfoVBox;
import root.javafx.CustomView.DBConnInfoControl;
import root.javafx.CustomView.DBConnectionInfoAnchorPane;
import root.javafx.CustomView.ServerConnInfoControl;
import root.javafx.CustomView.ServerConnectionInfoAnchorPane;
import root.utils.AlertUtils;
import root.utils.PropertiesUtils;

public class SettingMenuController implements Initializable {
	private static Logger logger = Logger.getLogger(SettingMenuController.class);

	/**
	 * Pattern ��ü�� �����ʵ�� ������ ���� 
	 * - Pattern ��ü�� �Է¹��� ����ǥ���Ŀ� �ش��ϴ� ���ѻ��¸ӽ�(finite state machine)�� 
	 *   �����ϱ� ������ �ν��Ͻ� ��������� ����. 
	 * - ����, �� �� �����ϵΰ� �̸� �����ϴ� ���� ȿ�����̴�. 
	 * - �Ӹ� �ƴ϶�, ���� ��ü�� �̸��� �ο��Ͽ� �ش� ��ü�� �ǹ̰� ��Ȯ������.
	 */

	/* Dependency Injection */
	PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

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

	/* Common Data */
	String[] dbMonitorings;
	String[] serverMonitorings;

	String[] dbNames;
	String[] serverNames;

	List<JdbcConnectionInfo> jdbcConnInfoList;
	List<JschConnectionInfo> jschConnInfoList;
	Map<String, AlertLogCommand> alcMap;

	Map<String, String> monitoringPresetMap = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = propertyRepository.getLastUseConnInfoFilePath();
		logger.debug("�ֱ� ���� ������Ƽ����: " + lastUsePropertiesFile);
		if (propertyRepository.isFileExist(lastUsePropertiesFile)) {
			loadSelectedConfigFile(lastUsePropertiesFile);

			// [����] - [����͸� ���� ����] - Preset ���� Event
			monitoringPresetComboBox.getSelectionModel().selectedItemProperty()
					.addListener((options, oldValue, newValue) -> {
						loadMonitoringConfigFile(monitoringPresetMap.get(newValue));
					});
		} else {
			setVisible(noConnInfoConfigAP, true);
			setVisible(noMonitoringConfigAP, true);
		}
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
			PropertiesUtils.createNewPropertiesFile(filePath, "Monitoring");

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
	 * [����] - [�������� ����] - .properties ������ �����ϱ� ���� FileChooser�� ����. 
	 * ����ڰ� ������ ������ ��ο��� ������ ���� ��, �ùٸ� ���������̶�� �ش� ��θ� remember.properties�� �����Ѵ�. 
	 * �׷��� �ʴٸ�, '�߸��������Դϴ�'��� ��� ���� ���������� ���� �����ϴ� ȭ������ �̵���Ų��.
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

			// 4. remember.properties ���Ͽ� �ֱ� ���� �������� ��θ� �����Ѵ�.
			PropertiesConfiguration rememberConfig = propertyRepository.getConfiguration("rememberConfig");
			rememberConfig.setProperty("filepath.config.lastuse", filePath.replace("\\", "/"));
			propertyRepository.save(rememberConfig.getString("filepath.config.remember"), rememberConfig);

			// 5. fileChooserText�� �ؽ�Ʈ�� ���� ���õ� ���ϰ�η� �����Ѵ�.
			fileChooserText.setText(filePath);

			loadResult = true;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {

			// 6. ���� load�� �Ϸ�Ǿ��ٴ� �޽����� ����.
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

	/**
	 * [����] - [����͸� ���� ����] - ����͸� ���� ���������� �ҷ��´�.
	 * 
	 * @param filePath
	 */
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

		ConnectionInfoVBox dbConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#dbConnVBox");
		dbConnVBox.saveConnInfoSettings(configFilePath);
		
		ConnectionInfoVBox serverConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#serverConnVBox");
		serverConnVBox.saveConnInfoSettings(configFilePath);
		
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

		ConnectionInfoVBox dbConnVBox = null;
		if (connInfoVBox.lookup("#dbConnVBox") != null) {
			dbConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#dbConnVBox");
			dbConnVBox.clearConnInfoMap();
		} else {
			// DB �������� UI
			dbConnVBox = new ConnectionInfoVBox(new DBConnInfoControl());
			dbConnVBox.setMenuTitle("DB ��������", FontAwesomeIcon.DATABASE);
			dbConnVBox.setId("dbConnVBox");
			connInfoVBox.getChildren().add(dbConnVBox);
		}
		
		if (jdbcConnInfoList.size() == 0) {
			DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
			dbConnAP.init();
			dbConnAP.setInitialValue(new JdbcConnectionInfo());
			dbConnVBox.addConnectionInfoAP(1, dbConnAP);
		} else {
			for (JdbcConnectionInfo jdbc : jdbcConnInfoList) {
				DBConnectionInfoAnchorPane dbConnAP = new DBConnectionInfoAnchorPane();
				dbConnAP.init();
				dbConnAP.setInitialValue(jdbc);
				dbConnVBox.addConnectionInfoAP(1, dbConnAP);
			}
		}
		
		ConnectionInfoVBox serverConnVBox = null;
		if(connInfoVBox.lookup("#serverConnVBox") != null) {
			serverConnVBox = (ConnectionInfoVBox) connInfoVBox.lookup("#serverConnVBox");
			serverConnVBox.clearConnInfoMap();
		} else {
			// Server �������� UI
			serverConnVBox = new ConnectionInfoVBox(new ServerConnInfoControl());
			serverConnVBox.setMenuTitle("���� ��������", FontAwesomeIcon.SERVER);
			serverConnVBox.setId("serverConnVBox");
			connInfoVBox.getChildren().add(serverConnVBox);
		}
		
		if (jschConnInfoList.size() == 0) {
			ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
			serverConnAP.setInitialValue(new JschConnectionInfo());
			serverConnVBox.addConnectionInfoAP(1, serverConnAP);
		} else {
			for (JschConnectionInfo  jsch : jschConnInfoList) {
				ServerConnectionInfoAnchorPane serverConnAP = new ServerConnectionInfoAnchorPane();
				serverConnAP.setInitialValue(jsch);
				serverConnVBox.addConnectionInfoAP(1, serverConnAP);
			}
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
	 * @param e
	 */
	public void createNewConfigFile(ActionEvent e) {
		// TextInputDialog ����
		TextInputDialog configInputDialog = new TextInputDialog();
		// ICON
		configInputDialog.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL, "30"));
		// CSS
		configInputDialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/css/dialog.css").toExternalForm());
		configInputDialog.getDialogPane().getStyleClass().add("textInputDialog");
		// Dialog ICON
		Stage stage = (Stage) configInputDialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/image/add_icon.png").toString()));
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
			PropertiesUtils.createNewPropertiesFile(filePath, "ConnectionInfo");
			
			// 2. ����͸����� Preset �������� ���� (./config/monitoring/{���������������ϸ�}/{default}.properties
			String presetConfigPath = "./config/monitoring/" + input + "/default.properties";
			PropertiesUtils.createNewPropertiesFile(presetConfigPath, "Monitoring");
			
			// 3. Set Node Visible
			setVisible(noConnInfoConfigAP, false);
			setVisible(noMonitoringConfigAP, false);
			
			// 4. ������ �������� Load 
			loadSelectedConfigFile(filePath);
		});
	}
}
