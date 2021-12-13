package JavaFx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.Configuration;

import com.jfoenix.controls.JFXComboBox;

import JavaFx.CustomView.DisableAfterTodayDateCell;
import JavaFx.CustomView.MonitoringAnchorPane;
import JavaFx.Model.TypeAndFieldName;
import JavaFx.Service.PropertyService;
import JavaFx.Service.PropertyServiceImpl;
import Root.Database.DatabaseUtil;
import Root.Model.ASMDiskUsage;
import Root.Model.AlertLog;
import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.ArchiveUsage;
import Root.Model.JdbcConnectionInfo;
import Root.Model.JschConnectionInfo;
import Root.Model.OSDiskUsage;
import Root.Model.TableSpaceUsage;
import Root.Model.UnitString;
import Root.RemoteServer.JschUtil;
import Root.Repository.DBCheckRepository;
import Root.Repository.DBCheckRepositoryImpl;
import Root.Repository.ServerCheckRepository;
import Root.Repository.ServerCheckRepositoryImpl;
import Root.Usecases.DBCheckUsecase;
import Root.Usecases.DBCheckUsecaseImpl;
import Root.Usecases.ServerCheckUsecase;
import Root.Usecases.ServerCheckUsecaseImpl;
import Root.Utils.AlertUtils;
import Root.Utils.PropertiesUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class RunMenuController implements Initializable {
	
	/* Dependency Injection */
	PropertyService propertyService = PropertyServiceImpl.getInstance();

	/* View */
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
	
	private MonitoringAnchorPane<ArchiveUsage> archiveUsageMAP = new MonitoringAnchorPane<>();
	private MonitoringAnchorPane<TableSpaceUsage> tableSpaceUsageMAP = new MonitoringAnchorPane<>();
	private MonitoringAnchorPane<ASMDiskUsage> asmDiskUsageMAP = new MonitoringAnchorPane<>();
	private MonitoringAnchorPane<OSDiskUsage> osDiskUsageMAP = new MonitoringAnchorPane<>();

	Map<String, AlertLog> alertLogMonitoringResultMap = new HashMap<>();
	String[] dbNames = new String[]{};
	String[] serverNames = new String[]{}; 
	
	/**
	 * ����͸� AnchorPane �߰��ϰ� ��Ҹ� �ʱ�ȭ�Ѵ�.
	 * @param <T>
	 * @param monitoringAP
	 * @param parentAP
	 * @param labelText
	 * @param comboBoxItems
	 * @param tableColumns
	 */
	private <T> void initAndAddMonitoringAnchorPane(MonitoringAnchorPane<T> monitoringAP, 
			AnchorPane parentAP, String labelText, String[] comboBoxItems, 
			Map<String, TypeAndFieldName> tableColumns) {
		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint ����
		monitoringAP.getLabel().setText(labelText); // ComboBox ���� Lebel Text ����
		monitoringAP.getComboBox().getItems().addAll(comboBoxItems); // ComboBox Items ����
		for(String key : tableColumns.keySet()) { // TableView�� ����� Column ����
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key), key);
		}
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane�� �θ� Node�� �߰�
	}
	
	/**
	 * ����޴� ȭ�� ���Խ� �ʱ�ȭ�� �����Ѵ�.
	 */
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
		String lastUsePropertiesFile = propertyService.getLastUseConnInfoFileName();
		if(lastUsePropertiesFile != null) {
			loadConnectionInfoProperties(lastUsePropertiesFile);
		}
		
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
		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems, archiveUsageTCM);

		// TableSpace Usage TableView Setting
		Map<String, TypeAndFieldName> tableSpaceUsageTCM = new LinkedHashMap<>();
		tableSpaceUsageTCM.put("Table Space Name", new TypeAndFieldName(String.class, "tableSpaceName"));
		tableSpaceUsageTCM.put("Total Space(G)", new TypeAndFieldName(Double.class, "totalSpace"));
		tableSpaceUsageTCM.put("Free Space(G)", new TypeAndFieldName(Double.class, "freeSpace"));
		tableSpaceUsageTCM.put("Used Space(G)", new TypeAndFieldName(Double.class, "usedSpace"));
		tableSpaceUsageTCM.put("Used Percent(G)", new TypeAndFieldName(Double.class, "usedPercent"));
		tableSpaceUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, ""));
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems, tableSpaceUsageTCM);

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
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems, asmDiskUsageTCM);

		// OS Disk Usage TableView Setting
		Map<String, TypeAndFieldName> osDiskUsageTCM = new LinkedHashMap<>();
		osDiskUsageTCM.put("File System", new TypeAndFieldName(String.class, "fileSystem"));
		osDiskUsageTCM.put("Mounted On", new TypeAndFieldName(String.class, "mountedOn"));
		osDiskUsageTCM.put("Total Space", new TypeAndFieldName(Double.class, "totalSpace"));
		osDiskUsageTCM.put("Available Space", new TypeAndFieldName(Double.class, "freeSpace"));
		osDiskUsageTCM.put("Used Space", new TypeAndFieldName(Double.class, "usedSpace"));
		osDiskUsageTCM.put("Used Percent", new TypeAndFieldName(Double.class, "usedPercent"));
		osDiskUsageTCM.put("Monitoring Date", new TypeAndFieldName(String.class, ""));
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems, osDiskUsageTCM);

		archiveUsageMAP.addTableData("1", new ArchiveUsage("1", 10, new UnitString(2, "")
				,new UnitString(2, ""), new UnitString(2, ""),new UnitString(2, ""), "1"));
		archiveUsageMAP.syncTableData("1");
		archiveUsageMAP.addTableData("2", new ArchiveUsage("1", 10, new UnitString(2, "")
				, new UnitString(2, ""), new UnitString(2125, ""),new UnitString(2, ""), "1"));
		archiveUsageMAP.syncTableData("2");
		
		// TODO TableColumn �Ӽ��� �����ϴ� �޼��带 ���� �����غ���. ��ü�� �����ؼ� �����ϴ� ����� ����ϱ�
		// ex) TableColumnHeaderText, Width, Align
		
		// AlertLog ȭ���� UI ��Ҹ� �ʱ�ȭ�Ѵ�.
		initAlertLogMonitoringElements();
	}
	
	/**
	 * AlertLog AnchorPane�� UI ��ҵ��� ���� �ʱ�ȭ�Ѵ�.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox ���� �̺�Ʈ
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
			// alertLogTA.setText(alertLogMonitoringResultMap.get(newValue).getFullLogString());
		});

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
	}

	/**
	 * [����] - �������� ���������� �а�, ����͸����� Preset�� �д´�.
	 * @param connInfoConfigFilePath
	 */
	private void loadConnectionInfoProperties(String connInfoConfigFilePath) {
		// �������� ������Ƽ ���� Load
		propertyService.loadConnectionInfoConfig(connInfoConfigFilePath);
		// TODO ���Ͻý��� ���� �����ϴ� ��� �������� ������Ƽ ������ ComboBox�� �߰��ؾ� �Ѵ�.
		
		// ����͸����� ���� Preset ComboBox
		List<String> presetList = propertyService.getMonitoringPresetList();
		runMonitoringPresetComboBox.getItems().addAll(presetList);
		
		// DB/Server Names ComboBox
		dbNames = propertyService.getMonitoringDBNames();
		serverNames = propertyService.getMonitoringServerNames();
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
			DatabaseUtil db = new DatabaseUtil(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
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
			JschUtil server = new JschUtil(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
			
			String alertLogFilePath = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
			String alertLogReadLine = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
			String alertLogDateFormat = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
			String alertLogDateFormatRegex = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, alertLogStartDay, alertLogEndDay);

			osDiskUsageMAP.addTableDataSet(server.getServerName(), usecase.getCurrentOSDiskUsage("df -Ph"));
			alertLogMonitoringResultMap.put(server.getServerName(), usecase.getAlertLogDuringPeriod(alcp));
		} 
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
	
	/**
	 * [����] - ����͸� DB/Server TableView�� �����Ѵ�.
	 * @param <T>
	 * @param dbOrServerName
	 * @param resultMap
	 * @param resultTV
	 */
	private <T> void changeMonitoringResultTV(String dbOrServerName, Map<String, List<T>> resultMap, TableView<T> resultTV) {
		if(dbOrServerName.isEmpty() || resultMap.get(dbOrServerName) == null) return;
		resultTV.setItems((ObservableList<T>) FXCollections.observableArrayList(resultMap.get(dbOrServerName)));
	}
}
