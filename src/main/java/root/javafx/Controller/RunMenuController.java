package root.javafx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.constracts.ReportRepository;
import root.core.repository.constracts.ServerCheckRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportRepositoryImpl;
import root.core.repository.implement.ServerCheckRepositoryImpl;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerCheckUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerCheckUsecaseImpl;
import root.javafx.CustomView.AlertLogListViewCell;
import root.javafx.CustomView.DisableAfterTodayDateCell;
import root.javafx.CustomView.MonitoringAnchorPane;
import root.javafx.Model.TypeAndFieldName;
import root.utils.AlertUtils;
import root.utils.PropertiesUtils;

public class RunMenuController implements Initializable {
	
	/* Dependency Injection */
	PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();
	ReportRepository reportRepository = ReportRepositoryImpl.getInstance();

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
	MonitoringAnchorPane<ArchiveUsage> archiveUsageMAP = new MonitoringAnchorPane<>(ArchiveUsage.class);
	MonitoringAnchorPane<TableSpaceUsage> tableSpaceUsageMAP = new MonitoringAnchorPane<>(TableSpaceUsage.class);
	MonitoringAnchorPane<ASMDiskUsage> asmDiskUsageMAP = new MonitoringAnchorPane<>(ASMDiskUsage.class);
	MonitoringAnchorPane<OSDiskUsage> osDiskUsageMAP = new MonitoringAnchorPane<>(OSDiskUsage.class);
	Map<String, AlertLog> alertLogMonitoringResultMap = new HashMap<>();
	
	/* Common Data */
	String lastUseConnInfoFilePath = null;
	String lastUseMonitoringPresetName = null;
	String[] dbNames = null;
	String[] serverNames = null; 
	String[] connInfoFiles = null;
	List<String> presetList = null;
	
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
		connInfoFiles = propertyRepository.getConnectionInfoFileNames();
		if(connInfoFiles != null && connInfoFiles.length != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();			
			// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
			lastUseConnInfoFilePath = propertyRepository.getLastUseConnInfoFilePath();
			if(propertyRepository.isFileExist(lastUseConnInfoFilePath)) {
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
		archiveUsageTCM.put("Archive Name", new TypeAndFieldName(String.class, "archiveName"));
		archiveUsageTCM.put("Number Of Files", new TypeAndFieldName(Integer.class, "numberOfFiles"));
		archiveUsageTCM.put("Total Space(G)", new TypeAndFieldName(Double.class, "totalSpace"));
		archiveUsageTCM.put("Reclaimable Space(G)", new TypeAndFieldName(Double.class, "reclaimableSpace"));
		archiveUsageTCM.put("Used Space(G)", new TypeAndFieldName(Double.class, "usedSpace"));
		archiveUsageTCM.put("Used Percent(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		archiveUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, "dnt"));
		initAndAddMonitoringAnchorPane("ArchiveUsage", archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems, archiveUsageTCM);

		// TableSpace Usage TableView Setting
		Map<String, TypeAndFieldName> tableSpaceUsageTCM = new LinkedHashMap<>();
		tableSpaceUsageTCM.put("Table Space Name", new TypeAndFieldName(String.class, "tableSpaceName"));
		tableSpaceUsageTCM.put("Total Space(G)", new TypeAndFieldName(Double.class, "totalSpace"));
		tableSpaceUsageTCM.put("Free Space(G)", new TypeAndFieldName(Double.class, "freeSpace"));
		tableSpaceUsageTCM.put("Used Space(G)", new TypeAndFieldName(Double.class, "usedSpace"));
		tableSpaceUsageTCM.put("Used Percent(G)", new TypeAndFieldName(Double.class, "usedPercent"));
		tableSpaceUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, ""));
		initAndAddMonitoringAnchorPane("TableSpaceUsage", tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems, tableSpaceUsageTCM);

		// ASM Disk USage TableView Setting
		Map<String, TypeAndFieldName> asmDiskUsageTCM = new LinkedHashMap<>();
		asmDiskUsageTCM.put("Disk Group", new TypeAndFieldName(String.class, "asmDiskGroupName"));
		asmDiskUsageTCM.put("Disk Type", new TypeAndFieldName(String.class, "asmDiskGroupType"));
		asmDiskUsageTCM.put("Total Space(MB)", new TypeAndFieldName(Double.class, "totalRawSpace"));
		asmDiskUsageTCM.put("Total Usable(MB)", new TypeAndFieldName(Double.class, "totalFreeSpace"));
		asmDiskUsageTCM.put("Free Space(MB)", new TypeAndFieldName(Double.class, "freeSpace"));
		asmDiskUsageTCM.put("Used Space(MB)", new TypeAndFieldName(Double.class, "usedSpace"));
		asmDiskUsageTCM.put("Used Percent(MB)", new TypeAndFieldName(Double.class, "usedPercent"));
		asmDiskUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, ""));
		initAndAddMonitoringAnchorPane("ASMDiskUsage", asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems, asmDiskUsageTCM);

		// OS Disk Usage TableView Setting
		Map<String, TypeAndFieldName> osDiskUsageTCM = new LinkedHashMap<>();
		osDiskUsageTCM.put("File System", new TypeAndFieldName(String.class, "fileSystem"));
		osDiskUsageTCM.put("Mounted On", new TypeAndFieldName(String.class, "mountedOn"));
		osDiskUsageTCM.put("Total Space", new TypeAndFieldName(Double.class, "totalSpace"));
		osDiskUsageTCM.put("Available Space", new TypeAndFieldName(Double.class, "freeSpace"));
		osDiskUsageTCM.put("Used Space", new TypeAndFieldName(Double.class, "usedSpace"));
		osDiskUsageTCM.put("Used Percent", new TypeAndFieldName(Double.class, "usedPercent"));
		osDiskUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, ""));
		initAndAddMonitoringAnchorPane("OSDiskUsage", osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems, osDiskUsageTCM);

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
	private <T> void initAndAddMonitoringAnchorPane(String name,
			MonitoringAnchorPane<T> monitoringAP, 
			AnchorPane parentAP, String labelText, String[] comboBoxItems, 
			Map<String, TypeAndFieldName> tableColumns) {
		
		// Report file path setting
		// TODO [����]�޴����� report file path�� ������ �� �ֵ��� �ϱ�
		monitoringAP.setReportFilePath("./report/" + name + "/");
		
		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint ����
		monitoringAP.getLabel().setText(labelText); // ComboBox ���� Lebel Text ����
		monitoringAP.getComboBox().getItems().addAll(comboBoxItems); // ComboBox Items ����
		monitoringAP.getComboBox().getSelectionModel().selectFirst();
		for(String key : tableColumns.keySet()) { // TableView�� ����� Column ����
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key), key);
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
		propertyRepository.loadConnectionInfoConfig(connInfoConfigFilePath);
		// ����͸����� ���� Preset
		presetList = propertyRepository.getMonitoringPresetNameList();
		lastUseMonitoringPresetName = propertyRepository.getLastUseMonitoringPresetName();
		// DB/Server Names
		dbNames = propertyRepository.getMonitoringDBNames();
		serverNames = propertyRepository.getMonitoringServerNames();
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
		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("�� [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			JdbcDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, reportRepository);
			archiveUsageMAP.addTableDataSet(jdbc.getJdbcDBName(), usecase.getCurrentArchiveUsage());
			tableSpaceUsageMAP.addTableDataSet(jdbc.getJdbcDBName(), usecase.getCurrentTableSpaceUsage());
			asmDiskUsageMAP.addTableDataSet(jdbc.getJdbcDBName(), usecase.getCurrentASMDiskUsage());
			db.uninit();
		} 
		
		String alertLogStartDay = alertLogStartDayDP.getValue().toString();
		String alertLogEndDay = alertLogEndDayDP.getValue().toString();
		List<JschConnectionInfo> jschConnectionList = PropertiesUtils.getJschConnectionMap();
		for(JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("�� [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschServer server = new JschServer(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
			
			String alertLogFilePath = PropertiesUtils.connInfoConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
			String alertLogReadLine = PropertiesUtils.connInfoConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
			String alertLogDateFormat = PropertiesUtils.connInfoConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
			String alertLogDateFormatRegex = PropertiesUtils.connInfoConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, alertLogStartDay, alertLogEndDay);

			osDiskUsageMAP.addTableDataSet(server.getServerName(), usecase.getCurrentOSDiskUsage());
			alertLogMonitoringResultMap.put(server.getServerName(), usecase.getAlertLogDuringPeriod(alcp));
		}
		
		archiveUsageMAP.syncTableData(archiveUsageMAP.getComboBox().getSelectionModel().getSelectedItem());
		tableSpaceUsageMAP.syncTableData(tableSpaceUsageMAP.getComboBox().getSelectionModel().getSelectedItem());
		asmDiskUsageMAP.syncTableData(asmDiskUsageMAP.getComboBox().getSelectionModel().getSelectedItem());
		osDiskUsageMAP.syncTableData(osDiskUsageMAP.getComboBox().getSelectionModel().getSelectedItem());
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
