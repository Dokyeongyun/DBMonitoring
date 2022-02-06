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
	 * 실행메뉴 화면 진입시 초기화를 수행한다.
	 */
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		/*
		 * 1. 접속정보 프로퍼티 파일 ComboBox를 설정한다.
		 * 2. 접속정보 프로퍼티 파일 유무를 확인한다.
		 * 	2-1. 있으면 [3]으로 이동
		 * 	2-2. 한개도 없으면 설정 메뉴로 이동하여 접속정보를 설정하도록 한다. [END]
		 * 3. 최근 사용한 접속정보 프로퍼티 파일이 있는지 확인한다.
		 * 	3-1. 있으면 해당 파일을 Load한다. 
		 * 	3-2. 없으면 첫 번째 파일을 Load한다. 
		 */
		// 접속정보 설정 프로퍼티 파일 
		connInfoFiles = propertyRepository.getConnectionInfoFileNames();
		if(connInfoFiles != null && connInfoFiles.length != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();			
			// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
			lastUseConnInfoFilePath = propertyRepository.getLastUseConnInfoFilePath();
			if(propertyRepository.isFileExist(lastUseConnInfoFilePath)) {
				runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);			
				loadConnectionInfoProperties(lastUseConnInfoFilePath);
			}
		} else {
			AlertUtils.showAlert(AlertType.INFORMATION, "접속정보 설정", "설정된 DB/Server 접속정보가 없습니다.\n[설정]메뉴로 이동합니다.");
			return;
		}
		
		// ComboBox 변경 이벤트
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			loadConnectionInfoProperties(newValue);
		});

		String dbComboBoxLabel = "DB 선택";
		String[] dbComboBoxItems = dbNames;
		String serverComboBoxLabel = "Server 선택";
		String[] serverComboBoxItems = serverNames;
		
		// Archive Usage TableView Setting
		Map<String, TypeAndFieldName> archiveUsageTCM = new LinkedHashMap<>(); // LinkedHashMap은 순서가 보장된다.
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

		// TODO TableColumn 속성을 설정하는 메서드를 따로 구분해보자. 객체를 생성해서 전달하는 방법도 고려하기
		// ex) TableColumnHeaderText, Width, Align
		
		// AlertLog 화면의 UI 요소를 초기화한다.
		initAlertLogMonitoringElements();
	}
	
	/**
	 * 모니터링 AnchorPane 추가하고 요소를 초기화한다.
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
		// TODO [설정]메뉴에서 report file path를 설정할 수 있도록 하기
		monitoringAP.setReportFilePath("./report/" + name + "/");
		
		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint 설정
		monitoringAP.getLabel().setText(labelText); // ComboBox 좌측 Lebel Text 설정
		monitoringAP.getComboBox().getItems().addAll(comboBoxItems); // ComboBox Items 설정
		monitoringAP.getComboBox().getSelectionModel().selectFirst();
		for(String key : tableColumns.keySet()) { // TableView에 출력할 Column 설정
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key), key);
		}
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane을 부모 Node에 추가
	}
	
	private void changeAlertLogListViewData(String serverID) {
		alertLogLV.getItems().clear();
		AlertLog al = alertLogMonitoringResultMap.get(serverID);
		if(al != null) {
			alertLogLV.getItems().addAll(al.getAlertLogs());				
		}
	}
	
	/**
	 * AlertLog AnchorPane의 UI 요소들의 값을 초기화한다.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox 변경 이벤트
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeAlertLogListViewData(newValue);
		});
		alertLogServerComboBox.getItems().addAll(serverNames);
		alertLogServerComboBox.getSelectionModel().selectFirst();

		// AlertLog 조회기간 기본값 설정
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());
		
		// AlertLog 조회기간 오늘 이후 날짜 선택 불가
		alertLogStartDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		alertLogEndDayDP.setDayCellFactory(picker -> new DisableAfterTodayDateCell());
		
		// AlertLog 조회기간 변경 이벤트
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
	 * [실행] - 접속정보 설정파일을 읽고, 모니터링설정 Preset을 읽는다.
	 * @param connInfoConfigFilePath
	 */
	private void loadConnectionInfoProperties(String connInfoConfigFilePath) {
		// 접속정보 프로퍼티 파일 Load
		propertyRepository.loadConnectionInfoConfig(connInfoConfigFilePath);
		// 모니터링여부 설정 Preset
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
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		if(!validateInput()) return;

		// DB Usage Check   		
		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
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
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
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
	 * [실행] - 모니터링 실행 시, 입력값 검사
	 * @return
	 */
	private boolean validateInput() {
		String alertHeaderText = "";
		String alertContentText = "";

		// 1. AlertLog 조회기간
		alertHeaderText = "AlertLog 조회기간";
		
		LocalDate alertLogStartDay = alertLogStartDayDP.getValue();
		LocalDate alertLogEndDay = alertLogEndDayDP.getValue();
		if(alertLogStartDay == null || alertLogEndDay == null) {
			alertContentText = "조회기간을 입력해주세요.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}

		try {
			if(!alertLogStartDay.isBefore(alertLogEndDay) && !alertLogStartDay.isEqual(alertLogEndDay)) {
				alertContentText = "조회시작일은 조회종료일보다 이전 날짜여야 합니다.";
				AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
				return false;
			}				
		} catch (Exception e) {
			alertContentText = "조회기간이 올바르지 않습니다.";
			AlertUtils.showAlert(AlertType.ERROR, alertHeaderText, alertContentText);
			return false;
		}
		
		return true;
	}

}
