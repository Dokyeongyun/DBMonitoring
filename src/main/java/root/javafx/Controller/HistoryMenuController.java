package root.javafx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.PropertiesConfiguration;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.ArchiveUsage;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.Log;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.constracts.ReportRepository;
import root.core.repository.constracts.ServerCheckRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.repository.implement.ServerCheckRepositoryImpl;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerCheckUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerCheckUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
import root.javafx.CustomView.DisableAfterTodayDateCell;
import root.javafx.Model.TypeAndFieldName;
import root.utils.AlertUtils;

public class HistoryMenuController implements Initializable {


	/* Dependency Injection */
	PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();
	ReportRepository reportRepository = ReportFileRepo.getInstance();
	PropertyService propService = new FilePropertyService(propRepo);

	/* View Binding */
	@FXML JFXComboBox<String> runConnInfoFileComboBox;
	@FXML JFXComboBox<String> runMonitoringPresetComboBox;
	@FXML JFXComboBox<String> alertLogServerComboBox;
	
	@FXML DatePicker alertLogStartDayDP;
	@FXML DatePicker alertLogEndDayDP;
	
	@FXML AnchorPane archiveUsageTabAP;
	@FXML AnchorPane tableSpaceUsageTabAP;
	@FXML AnchorPane asmDiskUsageTabAP;
	@FXML AnchorPane osDiskUsageTabAP;
	@FXML AnchorPane alertLogUsageTabAP;
	
	@FXML JFXListView<Log> alertLogLV;
	
	/* Custom View */
	MonitoringAPController<ArchiveUsage> archiveUsageMAP;
	MonitoringAPController<TableSpaceUsage> tableSpaceUsageMAP;
	MonitoringAPController<ASMDiskUsage> asmDiskUsageMAP;
	MonitoringAPController<OSDiskUsage> osDiskUsageMAP;
	Map<String, AlertLog> alertLogMonitoringResultMap;
	
	/* Common Data */
	String lastUseConnInfoFilePath = null;
	String lastUseMonitoringPresetName = null;
	String[] dbNames = null;
	String[] serverNames = null; 
	String[] connInfoFiles = null;
	List<String> presetList = null;
	
	public HistoryMenuController() {
		archiveUsageMAP = new MonitoringAPController<>(ArchiveUsage.class);
		tableSpaceUsageMAP = new MonitoringAPController<>(TableSpaceUsage.class);
		asmDiskUsageMAP = new MonitoringAPController<>(ASMDiskUsage.class);
		osDiskUsageMAP = new MonitoringAPController<>(OSDiskUsage.class);
		alertLogMonitoringResultMap = new HashMap<>();
	}
	
	/**
	 * ����޴� ȭ�� ���Խ� �ʱ�ȭ�� �����Ѵ�.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*
		 * 1. �������� ������Ƽ ���� ComboBox�� �����Ѵ�.
		 * 2. �������� ������Ƽ ���� ������ Ȯ���Ѵ�.
		 * 	2-1. ������ [3]���� �̵�
		 * 	2-2. �Ѱ��� ������ ���� �޴��� �̵��Ͽ� ���������� �����ϵ��� �Ѵ�. [END]
		 * 3. �ֱ� ����� �������� ������Ƽ ������ �ִ��� Ȯ���Ѵ�.
		 * 	3-1. ������ �ش� ������ Load�Ѵ�. 
		 * 	3-2. ������ ù ��° ������ Load�Ѵ�. 
		 */
		// �������� ���� ������Ƽ ���� 
		connInfoFiles = propRepo.getConnectionInfoFileNames();
		if(connInfoFiles != null && connInfoFiles.length != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();			
			// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
			lastUseConnInfoFilePath = propRepo.getLastUseConnInfoFilePath();
			if(propRepo.isFileExist(lastUseConnInfoFilePath)) {
				runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);			
				loadConnectionInfoProperties(lastUseConnInfoFilePath);
			}
		} else {
			AlertUtils.showAlert(AlertType.INFORMATION, "�������� ����", "������ DB/Server ���������� �����ϴ�.\n[����]�޴��� �̵��մϴ�.");
			return;
		}
		
		// ComboBox ���� �̺�Ʈ
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			loadConnectionInfoProperties(newValue);
		});

		String dbComboBoxLabel = "DB ����";
		String[] dbComboBoxItems = dbNames;
		String serverComboBoxLabel = "Server ����";
		String[] serverComboBoxItems = serverNames;
		
		// Archive Usage TableView Setting
		Map<String, TypeAndFieldName> archiveUsageTCM = new LinkedHashMap<>(); // LinkedHashMap�� ������ ����ȴ�.
		archiveUsageTCM.put("Archive��", new TypeAndFieldName(String.class, "archiveName"));
		archiveUsageTCM.put("���� ����", new TypeAndFieldName(Integer.class, "numberOfFiles"));
		archiveUsageTCM.put("��ü ����", new TypeAndFieldName(Double.class, "totalSpace"));
		archiveUsageTCM.put("���� ����", new TypeAndFieldName(Double.class, "reclaimableSpace"));
		archiveUsageTCM.put("������� ����", new TypeAndFieldName(Double.class, "usedSpace"));
		archiveUsageTCM.put("��뷮(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		archiveUsageTCM.put("����͸��Ͻ�", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems, archiveUsageTCM);

		// TableSpace Usage TableView Setting
		Map<String, TypeAndFieldName> tableSpaceUsageTCM = new LinkedHashMap<>();
		tableSpaceUsageTCM.put("���̺����̽���", new TypeAndFieldName(String.class, "tableSpaceName"));
		tableSpaceUsageTCM.put("��ü ����", new TypeAndFieldName(Double.class, "totalSpace"));
		tableSpaceUsageTCM.put("���� ����", new TypeAndFieldName(Double.class, "freeSpace"));
		tableSpaceUsageTCM.put("������� ����", new TypeAndFieldName(Double.class, "usedSpace"));
		tableSpaceUsageTCM.put("��뷮(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		tableSpaceUsageTCM.put("����͸��Ͻ�", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems, tableSpaceUsageTCM);

		// ASM Disk USage TableView Setting
		Map<String, TypeAndFieldName> asmDiskUsageTCM = new LinkedHashMap<>();
		asmDiskUsageTCM.put("��ũ �׷�", new TypeAndFieldName(String.class, "asmDiskGroupName"));
		asmDiskUsageTCM.put("��ũ Ÿ��", new TypeAndFieldName(String.class, "asmDiskGroupType"));
		asmDiskUsageTCM.put("��ü ����(Raw)", new TypeAndFieldName(Double.class, "totalRawSpace"));
		asmDiskUsageTCM.put("��ü ����(Actual)", new TypeAndFieldName(Double.class, "totalFreeSpace"));
		asmDiskUsageTCM.put("���� ����", new TypeAndFieldName(Double.class, "freeSpace"));
		asmDiskUsageTCM.put("������� ����", new TypeAndFieldName(Double.class, "usedSpace"));
		asmDiskUsageTCM.put("��뷮(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		asmDiskUsageTCM.put("����͸��Ͻ�", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems, asmDiskUsageTCM);

		// OS Disk Usage TableView Setting
		Map<String, TypeAndFieldName> osDiskUsageTCM = new LinkedHashMap<>();
		osDiskUsageTCM.put("���� �ý���", new TypeAndFieldName(String.class, "fileSystem"));
		osDiskUsageTCM.put("����Ʈ ��ġ", new TypeAndFieldName(String.class, "mountedOn"));
		osDiskUsageTCM.put("��ü ����", new TypeAndFieldName(Double.class, "totalSpace"));
		osDiskUsageTCM.put("���� ����", new TypeAndFieldName(Double.class, "freeSpace"));
		osDiskUsageTCM.put("������� ����", new TypeAndFieldName(Double.class, "usedSpace"));
		osDiskUsageTCM.put("��뷮(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		osDiskUsageTCM.put("����͸��Ͻ�", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems, osDiskUsageTCM);

		// TODO TableColumn �Ӽ��� �����ϴ� �޼��带 ���� �����غ���. ��ü�� �����ؼ� �����ϴ� ����� ����ϱ�
		// ex) TableColumnHeaderText, Width, Align
		
		// AlertLog ȭ���� UI ��Ҹ� �ʱ�ȭ�Ѵ�.
		initAlertLogMonitoringElements();
	}
	
	/**
	 * ����͸� AnchorPane �߰��ϰ� ��Ҹ� �ʱ�ȭ�Ѵ�.
	 * @param <T>
	 * @param monitoringAP
	 * @param parentAP
	 * @param labelText
	 * @param comboBoxItems
	 * @param tableColumns
	 */
	private <T extends MonitoringResult> void initAndAddMonitoringAnchorPane(MonitoringAPController<T> monitoringAP,
			AnchorPane parentAP, String labelText, String[] comboBoxItems, Map<String, TypeAndFieldName> tableColumns) {

		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint ����
		monitoringAP.setAliasComboBoxLabelText(labelText); // ComboBox ���� Lebel Text ����
		monitoringAP.setAliasComboBoxItems(comboBoxItems); // ComboBox Items ����
		for (String key : tableColumns.keySet()) { // TableView�� ����� Column ����
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key).getClazz(),
					tableColumns.get(key).getFieldName(), key);
		}
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane�� �θ� Node�� �߰�
	}
	
	private void changeAlertLogListViewData(String serverID) {
		alertLogLV.getItems().clear();
		AlertLog al = alertLogMonitoringResultMap.get(serverID);
		if(al != null) {
			alertLogLV.getItems().addAll(al.getAlertLogs());				
		}
	}
	
	/**
	 * AlertLog AnchorPane�� UI ��ҵ��� ���� �ʱ�ȭ�Ѵ�.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox ���� �̺�Ʈ
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeAlertLogListViewData(newValue);
		});
		alertLogServerComboBox.getItems().addAll(serverNames);
		alertLogServerComboBox.getSelectionModel().selectFirst();

		// AlertLog ��ȸ�Ⱓ �⺻�� ����
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());
		
		// AlertLog ��ȸ�Ⱓ ���� ���� ��¥ ���� �Ұ�
		alertLogStartDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		alertLogEndDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		
		// AlertLog ��ȸ�Ⱓ ���� �̺�Ʈ
		alertLogStartDayDP.valueProperty().addListener((ov, oldValue, newValue) -> {
			if(alertLogEndDayDP.getValue().isBefore(newValue)) {
				alertLogEndDayDP.setValue(newValue);
			}
		});
		alertLogEndDayDP.valueProperty().addListener((ov, oldValue, newValue) -> {
			if(alertLogStartDayDP.getValue().isAfter(newValue)) {
				alertLogStartDayDP.setValue(newValue);
			}
		});
		
		// AlertLog ListView
		alertLogLV.setCellFactory(categoryList -> new AlertLogListViewCell());
	}

	/**
	 * [����] - �������� ���������� �а�, ����͸����� Preset�� �д´�.
	 * @param connInfoConfigFilePath
	 */
	private void loadConnectionInfoProperties(String connInfoConfigFilePath) {
		// �������� ������Ƽ ���� Load
		propRepo.loadConnectionInfoConfig(connInfoConfigFilePath);
		// ����͸����� ���� Preset
		presetList = propRepo.getMonitoringPresetNameList();
		lastUseMonitoringPresetName = propRepo.getLastUseMonitoringPresetName();
		// DB/Server Names
		dbNames = propRepo.getMonitoringDBNames();
		serverNames = propRepo.getMonitoringServerNames();
		// Monitoring Preset ComboBox 
		runMonitoringPresetComboBox.getItems().clear();
		runMonitoringPresetComboBox.getItems().addAll(presetList);
		runMonitoringPresetComboBox.getSelectionModel().selectFirst();
		if(lastUseMonitoringPresetName != null) {
			runMonitoringPresetComboBox.getSelectionModel().select(lastUseMonitoringPresetName);
		}
	}
	
	/**
	 * [����] - ����͸��� �����Ѵ�.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		if(!validateInput()) return;

		// DB Usage Check
		List<JdbcConnectionInfo> jdbcConnectionList = propService
				.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			JdbcDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, reportRepository);
			archiveUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentArchiveUsage());
			tableSpaceUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentTableSpaceUsage());
			asmDiskUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentASMDiskUsage());
			db.uninit();
		}

		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();
		List<JschConnectionInfo> jschConnectionList = propService
				.getJschConnInfoList(propService.getMonitoringServerNameList());
		for (JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("�� [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschServer server = new JschServer(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
			
			PropertiesConfiguration config = propRepo.getConfiguration("connInfoConfig");
			String alertLogFilePath =  config.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
			String alertLogReadLine = config.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
			String alertLogDateFormat = config.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
			String alertLogDateFormatRegex = config.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, alertLogStartDay, alertLogEndDay);

			osDiskUsageMAP.addTableData(server.getServerName(), usecase.getCurrentOSDiskUsage());
			alertLogMonitoringResultMap.put(server.getServerName(), usecase.getAlertLogDuringPeriod(alcp));
		}
		
		archiveUsageMAP.syncTableData(archiveUsageMAP.getSelectedAliasComboBoxItem(), 0);
		tableSpaceUsageMAP.syncTableData(tableSpaceUsageMAP.getSelectedAliasComboBoxItem(), 0);
		asmDiskUsageMAP.syncTableData(asmDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		osDiskUsageMAP.syncTableData(osDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		changeAlertLogListViewData(alertLogServerComboBox.getSelectionModel().getSelectedItem());
	}
	
	
	/**
	 * [����] - ����͸� ���� ��, �Է°� �˻�
	 * @return
	 */
	private boolean validateInput() {
		String alertHeaderText = "";
		String alertContentText = "";

		// 1. AlertLog ��ȸ�Ⱓ
		alertHeaderText = "AlertLog ��ȸ�Ⱓ";
		
		LocalDate alertLogStartDay = alertLogStartDayDP.getValue();
		LocalDate alertLogEndDay = alertLogEndDayDP.getValue();
		if(alertLogStartDay == null || alertLogEndDay == null) {
			alertContentText = "��ȸ�Ⱓ�� �Է����ּ���.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		try {
			if(!alertLogStartDay.isBefore(alertLogEndDay) && !alertLogStartDay.isEqual(alertLogEndDay)) {
				alertContentText = "��ȸ�������� ��ȸ�����Ϻ��� ���� ��¥���� �մϴ�.";
				AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
				return false;
			}				
		} catch (Exception e) {
			alertContentText = "��ȸ�Ⱓ�� �ùٸ��� �ʽ��ϴ�.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}
		
		return true;
	}

}
