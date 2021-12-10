package JavaFx.Controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.Configuration;

import com.jfoenix.controls.JFXComboBox;

import Root.Database.DatabaseUtil;
import Root.Model.ASMDiskUsage;
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
import Root.Utils.PropertiesUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	
	@FXML JFXComboBox<String> runConnInfoFileComboBox;
	@FXML JFXComboBox<String> runMonitoringPresetComboBox;

	@FXML JFXComboBox<String> auDbComboBox;
	@FXML JFXComboBox<String> tsuDbComboBox;
	@FXML JFXComboBox<String> asmDbComboBox;
	@FXML JFXComboBox<String> osServerComboBox;
	@FXML JFXComboBox<String> alertLogServerComboBox;
	
	private Map<String, List<ArchiveUsage>> archiveUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<TableSpaceUsage>> tableSpaceUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<ASMDiskUsage>> asmDiskUsageMonitoringResultMap = new HashMap<>();
	private Map<String, List<OSDiskUsage>> osDiskUsageMonitoringResultMap = new HashMap<>();
	
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
		
		// remember.properties 파일에서, 최근 사용된 설정파일 경로가 있다면 해당 설정파일을 불러온다.
		String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");

		try {
			if(lastUsePropertiesFile != null) {
				
				// 접속정보 프로퍼티 파일 ComboBox
				PropertiesUtils.loadAppConfiguration(lastUsePropertiesFile, "connInfoConfig");
				runConnInfoFileComboBox.getItems().add(lastUsePropertiesFile);
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
				
				// DB Names ComboBox
				auDbComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("dbnames"));
				auDbComboBox.getSelectionModel().select(0);
				auDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					changeArchiveUsageMonitoringResult(newValue);
				});
				
				tsuDbComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("dbnames"));
				tsuDbComboBox.getSelectionModel().select(0);
				tsuDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					changeTableSpaceUsageMonitoringResult(newValue);
				});
				
				asmDbComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("dbnames"));
				asmDbComboBox.getSelectionModel().select(0);
				asmDbComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					changeASMDiskUsageMonitoringResult(newValue);
				});
				
				// Server Names ComboBox
				osServerComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("servernames"));
				osServerComboBox.getSelectionModel().select(0);
				osServerComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					changeOSDiskUsageMonitoringResult(newValue);
				});
				
				alertLogServerComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("servernames"));
				alertLogServerComboBox.getSelectionModel().select(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		
		List<JschConnectionInfo> jschConnectionList = PropertiesUtils.getJschConnectionMap();
		for(JschConnectionInfo jsch : jschConnectionList) {
			System.out.println("■ [ " + jsch.getServerName() + " Monitoring Start ]\n");
			JschUtil server = new JschUtil(jsch);
			server.init();
			ServerCheckRepository repo = new ServerCheckRepositoryImpl(server);
			ServerCheckUsecase usecase = new ServerCheckUsecaseImpl(repo);
			
			osDiskUsageMonitoringResultMap.put(server.getServerName(), usecase.getCurrentOSDiskUsage("df -Ph"));

//			String alertLogFilePath = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.filepath");
//			String alertLogReadLine = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.readline");
//			String alertLogDateFormat = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformat");
//			String alertLogDateFormatRegex = PropertiesUtils.propConfig.getString(jsch.getServerName().toLowerCase() + ".server.alertlog.dateformatregex");
//			AlertLogCommand alc = new AlertLogCommand("tail", alertLogReadLine, alertLogFilePath, alertLogDateFormat, alertLogDateFormatRegex);
//			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(alc, DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1), DateUtils.getToday("yyyy-MM-dd"));
		} 
		
		changeArchiveUsageMonitoringResult(auDbComboBox.getSelectionModel().getSelectedItem());
		changeTableSpaceUsageMonitoringResult(tsuDbComboBox.getSelectionModel().getSelectedItem());
		changeASMDiskUsageMonitoringResult(asmDbComboBox.getSelectionModel().getSelectedItem());
		changeOSDiskUsageMonitoringResult(osServerComboBox.getSelectionModel().getSelectedItem());
	}
	
	private void changeArchiveUsageMonitoringResult(String dbName) {
		archiveUsageTV.setItems(FXCollections.observableArrayList(archiveUsageMonitoringResultMap.get(dbName)));
	}
	
	private void changeTableSpaceUsageMonitoringResult(String dbName) {
		tableSpaceUsageTV.setItems(FXCollections.observableArrayList(tableSpaceUsageMonitoringResultMap.get(dbName)));
	}
	
	private void changeASMDiskUsageMonitoringResult(String dbName) {
		asmDiskUsageTV.setItems(FXCollections.observableArrayList(asmDiskUsageMonitoringResultMap.get(dbName)));
	}
	
	private void changeOSDiskUsageMonitoringResult(String serverName) {
		osDiskUsageTV.setItems(FXCollections.observableArrayList(osDiskUsageMonitoringResultMap.get(serverName)));
	}
}
