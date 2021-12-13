package JavaFx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
	
	/* Custom View */
	MonitoringAnchorPane<ArchiveUsage> archiveUsageMAP = new MonitoringAnchorPane<>();
	MonitoringAnchorPane<TableSpaceUsage> tableSpaceUsageMAP = new MonitoringAnchorPane<>();
	MonitoringAnchorPane<ASMDiskUsage> asmDiskUsageMAP = new MonitoringAnchorPane<>();
	MonitoringAnchorPane<OSDiskUsage> osDiskUsageMAP = new MonitoringAnchorPane<>();

	Map<String, AlertLog> alertLogMonitoringResultMap = new HashMap<>();
	
	/* Common Data */
	String[] dbNames = new String[]{};
	String[] serverNames = new String[]{}; 
	
	/**
	 * 모니터링 AnchorPane 추가하고 요소를 초기화한다.
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
		monitoringAP.setAnchor(0, 0, 0, 0); // Anchor Constraint 설정
		monitoringAP.getLabel().setText(labelText); // ComboBox 좌측 Lebel Text 설정
		monitoringAP.getComboBox().getItems().addAll(comboBoxItems); // ComboBox Items 설정
		for(String key : tableColumns.keySet()) { // TableView에 출력할 Column 설정
			monitoringAP.addAndSetPropertyTableColumn(tableColumns.get(key), key);
		}
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane을 부모 Node에 추가
	}
	
	/**
	 * 실행메뉴 화면 진입시 초기화를 수행한다.
	 */
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = propertyService.getLastUseConnInfoFileName();
		if(lastUsePropertiesFile != null) {
			loadConnectionInfoProperties(lastUsePropertiesFile);
		}
		
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
		
		// TODO TableColumn 속성을 설정하는 메서드를 따로 구분해보자. 객체를 생성해서 전달하는 방법도 고려하기
		// ex) TableColumnHeaderText, Width, Align
		
		// AlertLog 화면의 UI 요소를 초기화한다.
		initAlertLogMonitoringElements();
	}
	
	/**
	 * AlertLog AnchorPane의 UI 요소들의 값을 초기화한다.
	 */
	private void initAlertLogMonitoringElements() {
		// ComboBox 변경 이벤트
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
			// alertLogTA.setText(alertLogMonitoringResultMap.get(newValue).getFullLogString());
		});

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
	}

	/**
	 * [실행] - 접속정보 설정파일을 읽고, 모니터링설정 Preset을 읽는다.
	 * @param connInfoConfigFilePath
	 */
	private void loadConnectionInfoProperties(String connInfoConfigFilePath) {
		// 접속정보 프로퍼티 파일 Load
		propertyService.loadConnectionInfoConfig(connInfoConfigFilePath);
		// TODO 파일시스템 내에 존재하는 모든 접속정보 프로퍼티 파일을 ComboBox에 추가해야 한다.
		
		// 모니터링여부 설정 Preset ComboBox
		List<String> presetList = propertyService.getMonitoringPresetNameList();
		runMonitoringPresetComboBox.getItems().addAll(presetList);
		
		// DB/Server Names ComboBox
		dbNames = propertyService.getMonitoringDBNames();
		serverNames = propertyService.getMonitoringServerNames();
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
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
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
	
	/**
	 * [실행] - 모니터링 DB/Server TableView를 변경한다.
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
