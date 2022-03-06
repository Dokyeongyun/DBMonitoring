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
		connInfoFiles = propRepo.getConnectionInfoFileNames();
		if(connInfoFiles != null && connInfoFiles.length != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();			
			// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
			lastUseConnInfoFilePath = propRepo.getLastUseConnInfoFilePath();
			if(propRepo.isFileExist(lastUseConnInfoFilePath)) {
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
		archiveUsageTCM.put("Archive명", new TypeAndFieldName(String.class, "archiveName"));
		archiveUsageTCM.put("파일 개수", new TypeAndFieldName(Integer.class, "numberOfFiles"));
		archiveUsageTCM.put("전체 공간", new TypeAndFieldName(Double.class, "totalSpace"));
		archiveUsageTCM.put("가용 공간", new TypeAndFieldName(Double.class, "reclaimableSpace"));
		archiveUsageTCM.put("사용중인 공간", new TypeAndFieldName(Double.class, "usedSpace"));
		archiveUsageTCM.put("사용량(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		archiveUsageTCM.put("모니터링일시", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems, archiveUsageTCM);

		// TableSpace Usage TableView Setting
		Map<String, TypeAndFieldName> tableSpaceUsageTCM = new LinkedHashMap<>();
		tableSpaceUsageTCM.put("테이블스페이스명", new TypeAndFieldName(String.class, "tableSpaceName"));
		tableSpaceUsageTCM.put("전체 공간", new TypeAndFieldName(Double.class, "totalSpace"));
		tableSpaceUsageTCM.put("가용 공간", new TypeAndFieldName(Double.class, "freeSpace"));
		tableSpaceUsageTCM.put("사용중인 공간", new TypeAndFieldName(Double.class, "usedSpace"));
		tableSpaceUsageTCM.put("사용량(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		tableSpaceUsageTCM.put("모니터링일시", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems, tableSpaceUsageTCM);

		// ASM Disk USage TableView Setting
		Map<String, TypeAndFieldName> asmDiskUsageTCM = new LinkedHashMap<>();
		asmDiskUsageTCM.put("디스크 그룹", new TypeAndFieldName(String.class, "asmDiskGroupName"));
		asmDiskUsageTCM.put("디스크 타입", new TypeAndFieldName(String.class, "asmDiskGroupType"));
		asmDiskUsageTCM.put("전체 공간(Raw)", new TypeAndFieldName(Double.class, "totalRawSpace"));
		asmDiskUsageTCM.put("전체 공간(Actual)", new TypeAndFieldName(Double.class, "totalFreeSpace"));
		asmDiskUsageTCM.put("가용 공간", new TypeAndFieldName(Double.class, "freeSpace"));
		asmDiskUsageTCM.put("사용중인 공간", new TypeAndFieldName(Double.class, "usedSpace"));
		asmDiskUsageTCM.put("사용량(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		asmDiskUsageTCM.put("모니터링일시", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems, asmDiskUsageTCM);

		// OS Disk Usage TableView Setting
		Map<String, TypeAndFieldName> osDiskUsageTCM = new LinkedHashMap<>();
		osDiskUsageTCM.put("파일 시스템", new TypeAndFieldName(String.class, "fileSystem"));
		osDiskUsageTCM.put("마운트 위치", new TypeAndFieldName(String.class, "mountedOn"));
		osDiskUsageTCM.put("전체 공간", new TypeAndFieldName(Double.class, "totalSpace"));
		osDiskUsageTCM.put("가용 공간", new TypeAndFieldName(Double.class, "freeSpace"));
		osDiskUsageTCM.put("사용중인 공간", new TypeAndFieldName(Double.class, "usedSpace"));
		osDiskUsageTCM.put("사용량(%)", new TypeAndFieldName(Double.class, "usedPercent"));
		osDiskUsageTCM.put("모니터링일시", new TypeAndFieldName(String.class, "monitoringDateTime"));
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems, osDiskUsageTCM);

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
	private <T extends MonitoringResult> void initAndAddMonitoringAnchorPane(MonitoringAPController<T> monitoringAP,
			AnchorPane parentAP, String labelText, String[] comboBoxItems, Map<String, TypeAndFieldName> tableColumns) {

		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint 설정
		monitoringAP.setAliasComboBoxLabelText(labelText); // ComboBox 좌측 Lebel Text 설정
		monitoringAP.setAliasComboBoxItems(comboBoxItems); // ComboBox Items 설정
		for (String key : tableColumns.keySet()) { // TableView에 출력할 Column 설정
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key).getClazz(),
					tableColumns.get(key).getFieldName(), key);
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
		propRepo.loadConnectionInfoConfig(connInfoConfigFilePath);
		// 모니터링여부 설정 Preset
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
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		if(!validateInput()) return;

		// DB Usage Check
		List<JdbcConnectionInfo> jdbcConnectionList = propService
				.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
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
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
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
