package JavaFx.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.Configuration;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;

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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RunMenuController implements Initializable {
	
	/* Archive Usage TableView */
	@FXML TableView<ArchiveUsage> archiveUsageTV;
	@FXML TableColumn<ArchiveUsage, String> auNameTC;
	@FXML TableColumn<ArchiveUsage, Integer> auNOFTC;
	@FXML TableColumn<ArchiveUsage, Double> auLimitSpaceTC;
	@FXML TableColumn<ArchiveUsage, Double> auReclaimableSpaceTC;
	@FXML TableColumn<ArchiveUsage, Double> auUsedSpaceTC;
	@FXML TableColumn<ArchiveUsage, Double> auUsedPercentTC;
	@FXML TableColumn<ArchiveUsage, String> auDntTC;
	
	/* TableSpace Usage TableView */
	@FXML TableView<TableSpaceUsage> tableSpaceUsageTV;
	@FXML TableColumn<TableSpaceUsage, String> tsuNameTC;
	@FXML TableColumn<TableSpaceUsage, Double> tsuTotalSpaceTC;
	@FXML TableColumn<TableSpaceUsage, Double> tsuFreeSpaceTC;
	@FXML TableColumn<TableSpaceUsage, Double> tsuUsedSpaceTC;
	@FXML TableColumn<TableSpaceUsage, Double> tsuUsedPercentTC;
	
	/* ASM Disk Usage TableView */
	@FXML TableView<ASMDiskUsage> asmDiskUsageTV;
	@FXML TableColumn<ASMDiskUsage, String> asmNameTC;
	@FXML TableColumn<ASMDiskUsage, String> asmTypeTC;
	@FXML TableColumn<ASMDiskUsage, Double> asmTotalSpaceTC;
	@FXML TableColumn<ASMDiskUsage, Double> asmTotalUsableSpaceTC;
	@FXML TableColumn<ASMDiskUsage, Double> asmFreeSpaceTC;
	@FXML TableColumn<ASMDiskUsage, Double> asmUsedSpaceTC;
	@FXML TableColumn<ASMDiskUsage, Double> asmUsedPercentTC;
	
	/* OS Disk Usage TableView */
	@FXML TableView<OSDiskUsage> osDiskUsageTV;
	@FXML TableColumn<OSDiskUsage, String> osFileSystemTC;
	@FXML TableColumn<OSDiskUsage, String> osMountedOnTC;
	@FXML TableColumn<OSDiskUsage, Double> osTotalSpaceTC;
	@FXML TableColumn<OSDiskUsage, Double> osFreeSpaceTC;
	@FXML TableColumn<OSDiskUsage, Double> osUsedSpaceTC;
	@FXML TableColumn<OSDiskUsage, Double> osUsedPercentTC;
	
	/* AlertLog TextArea */
	@FXML JFXTextArea alertLogTA;
	
	@FXML JFXComboBox<String> runConnInfoFileComboBox;
	@FXML JFXComboBox<String> runMonitoringPresetComboBox;

	@FXML JFXComboBox<String> auDbComboBox;
	@FXML JFXComboBox<String> tsuDbComboBox;
	@FXML JFXComboBox<String> asmDbComboBox;
	@FXML JFXComboBox<String> osServerComboBox;
	@FXML JFXComboBox<String> alertLogServerComboBox;
	
	@FXML DatePicker alertLogStartDayDP;
	@FXML DatePicker alertLogEndDayDP;
	
	private Map<String, List<ArchiveUsage>> archiveUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<TableSpaceUsage>> tableSpaceUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<ASMDiskUsage>> asmDiskUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<OSDiskUsage>> osDiskUsageMonitoringResultMap = new HashMap<>();
	private Map<String, AlertLog> alertLogMonitoringResultMap = new HashMap<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		auNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArchiveName()));
		auNOFTC.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumberOfFiles()).asObject());
		auLimitSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalSpace().getValue()).asObject());
		auReclaimableSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getReclaimableSpace().getValue()).asObject());
		auUsedSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedSpace().getValue()).asObject());
		auUsedPercentTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedPercent().getValue()).asObject());
		auDntTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDnt()));
		
		tsuNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTableSpaceName()));
		tsuTotalSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalSpace().getValue()).asObject());
		tsuFreeSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFreeSpace().getValue()).asObject());
		tsuUsedSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedSpace().getValue()).asObject());
		tsuUsedPercentTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedPercent().getValue()).asObject());

		asmNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsmDiskGroupName()));
		asmTypeTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsmDiskGroupType()));
		asmTotalSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalRawSpace().getValue()).asObject());
		asmTotalUsableSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalFreeSpace().getValue()).asObject());
		asmFreeSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFreeSpace().getValue()).asObject());
		asmUsedSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedSpace().getValue()).asObject());
		asmUsedPercentTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedPercent().getValue()).asObject());
		
		osFileSystemTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileSystem()));
		osMountedOnTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMountedOn()));
		osTotalSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalSpace().getValue()).asObject());
		osFreeSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getFreeSpace().getValue()).asObject());
		osUsedSpaceTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedSpace().getValue()).asObject());
		osUsedPercentTC.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUsedPercent().getValue()).asObject());
		
		// ComboBox 변경 이벤트
		auDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeMonitoringResultTV(newValue, archiveUsageMonitoringResultMap, archiveUsageTV);
		});
		tsuDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeMonitoringResultTV(newValue, tableSpaceUsageMonitoringResultMap, tableSpaceUsageTV);
		});
		asmDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeMonitoringResultTV(newValue, asmDiskUsageMonitoringResultMap, asmDiskUsageTV);
		});
		osServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			changeMonitoringResultTV(newValue, osDiskUsageMonitoringResultMap, osDiskUsageTV);
		});
		alertLogServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVlaue, newValue) -> {
			alertLogTA.setText(alertLogMonitoringResultMap.get(newValue).getFullLogString());
		});

		// AlertLog 조회기간 기본값 설정
		alertLogStartDayDP.setValue(LocalDate.now().minusDays(1));
		alertLogEndDayDP.setValue(LocalDate.now());
		
		// AlertLog 조회기간 오늘 이후 날짜 선택 불가
		alertLogStartDayDP.setDayCellFactory(picker -> new DateCell() {
	        public void updateItem(LocalDate date, boolean empty) {
	            LocalDate today = LocalDate.now();
	            setDisable(empty || date.compareTo(today) > 0 );
	        }
		});
		alertLogEndDayDP.setDayCellFactory(picker -> new DateCell() {
	        public void updateItem(LocalDate date, boolean empty) {
	            LocalDate today = LocalDate.now();
	            setDisable(empty || date.compareTo(today) > 0 );
	        }
		});
		
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
		
		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
		if(lastUsePropertiesFile != null) {
			loadConnectionInfoProperties(lastUsePropertiesFile);
		}
	}

	/**
	 * [실행] - 접속정보 설정파일을 읽고, 모니터링설정 Preset을 읽는다.
	 * @param connInfoConfigFilePath
	 */
	private void loadConnectionInfoProperties(String connInfoConfigFilePath) {
		try {
			// 접속정보 프로퍼티 파일 ComboBox
			PropertiesUtils.loadAppConfiguration(connInfoConfigFilePath, "connInfoConfig");
			runConnInfoFileComboBox.getItems().add(connInfoConfigFilePath);
			runConnInfoFileComboBox.getSelectionModel().select(0);
			
			// 모니터링여부 설정 Preset ComboBox
			Configuration monitoringConfig = PropertiesUtils.connInfoConfig.subset("monitoring.setting.preset");
			Iterator<String> presetIt = monitoringConfig.getKeys();
			
			String lastUsePresetName = "";
			while(presetIt.hasNext()) {
				String key = presetIt.next();
				if(key.startsWith("lastuse")) {
					lastUsePresetName = monitoringConfig.getString(key);
				} else {
					runMonitoringPresetComboBox.getItems().add(key.substring(0, key.indexOf(".")));
				}
			}
			runMonitoringPresetComboBox.getSelectionModel().select(lastUsePresetName);
			
			// DB/Server Names ComboBox
			String[] dbNames = PropertiesUtils.connInfoConfig.getStringArray("dbnames");
			String[] serverNames = PropertiesUtils.connInfoConfig.getStringArray("servernames");
			setComboBoxItems(auDbComboBox, dbNames);
			setComboBoxItems(tsuDbComboBox, dbNames);
			setComboBoxItems(asmDbComboBox, dbNames);
			setComboBoxItems(osServerComboBox, serverNames);
			setComboBoxItems(alertLogServerComboBox, serverNames);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ComboBox Item을 세팅하고 첫번째 요소를 선택한다.
	 * @param comboBox
	 * @param items
	 */
	private void setComboBoxItems(JFXComboBox<String> comboBox, String[] items) {
		comboBox.getItems().addAll(items);
		comboBox.getSelectionModel().select(0);
	}
	
	/**
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		archiveUsageMonitoringResultMap.clear();
		tableSpaceUsageMonitoringResultMap.clear();
		asmDiskUsageMonitoringResultMap.clear();
		osDiskUsageMonitoringResultMap.clear();
		alertLogMonitoringResultMap.clear();

		if(!validateInput()) return;

		// DB Usage Check   		
		List<JdbcConnectionInfo> jdbcConnectionList = PropertiesUtils.getJdbcConnectionMap();
		for(JdbcConnectionInfo jdbc : jdbcConnectionList) {
			System.out.println("■ [ " + jdbc.getJdbcDBName() + " Monitoring Start ]\n");
			DatabaseUtil db = new DatabaseUtil(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo);
			archiveUsageMonitoringResultMap.put(jdbc.getJdbcDBName(), usecase.getCurrentArchiveUsage());
			tableSpaceUsageMonitoringResultMap.put(jdbc.getJdbcDBName(), usecase.getCurrentTableSpaceUsage());
			asmDiskUsageMonitoringResultMap.put(jdbc.getJdbcDBName(), usecase.getCurrentASMDiskUsage());
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

			osDiskUsageMonitoringResultMap.put(server.getServerName(), usecase.getCurrentOSDiskUsage("df -Ph"));
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
