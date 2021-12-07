package JavaFx.Controller;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.commons.configuration2.Configuration;

import com.jfoenix.controls.JFXComboBox;

import Root.Model.ASMDiskUsage;
import Root.Model.ArchiveUsage;
import Root.Model.OSDiskUsage;
import Root.Model.TableSpaceUsage;
import Root.Utils.PropertiesUtils;
import Root.Utils.UnitUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
				
				tsuDbComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("dbnames"));
				tsuDbComboBox.getSelectionModel().select(0);
				
				asmDbComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("dbnames"));
				asmDbComboBox.getSelectionModel().select(0);
				
				// Server Names ComboBox
				osServerComboBox.getItems().addAll(PropertiesUtils.connInfoConfig.getStringArray("servernames"));
				osServerComboBox.getSelectionModel().select(0);

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
		// Application.main(new String[] {});
		monitoringArchiveUsage();
	}
	
	public void monitoringArchiveUsage() {
		ObservableList<ArchiveUsage> archiveUsageList = FXCollections.observableArrayList(
				new ArchiveUsage("+RECO", 103, 
						UnitUtils.parseFileSizeString("1000G"),
						UnitUtils.parseFileSizeString("284G"),
						UnitUtils.parseFileSizeString("434"),
						UnitUtils.parseFileSizeString("43%"),
						"2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, 
						UnitUtils.parseFileSizeString("1000G"),
						UnitUtils.parseFileSizeString("284G"),
						UnitUtils.parseFileSizeString("434G"),
						UnitUtils.parseFileSizeString("45%"),
						"2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, 
						UnitUtils.parseFileSizeString("1000G"),
						UnitUtils.parseFileSizeString("284G"),
						UnitUtils.parseFileSizeString("434G"),
						UnitUtils.parseFileSizeString("48%"),
						"2021-10-13 00:06:34")
				);
		archiveUsageTV.setItems(archiveUsageList);
		
		ObservableList<TableSpaceUsage> tableSpaceUsageList = FXCollections.observableArrayList(
				new TableSpaceUsage("GGS_DATA",
						UnitUtils.parseFileSizeString("17.67G"),
						UnitUtils.parseFileSizeString(".84G"),
						UnitUtils.parseFileSizeString("16.83G"),
						UnitUtils.parseFileSizeString("95%")),
				new TableSpaceUsage("SYSTEM",
						UnitUtils.parseFileSizeString("3.2G"),
						UnitUtils.parseFileSizeString(".3G"),
						UnitUtils.parseFileSizeString("2.9G"),
						UnitUtils.parseFileSizeString("91%")),
				new TableSpaceUsage("DAISO_INDX",
						UnitUtils.parseFileSizeString("1080G"),
						UnitUtils.parseFileSizeString("119.91G"),
						UnitUtils.parseFileSizeString("960.09G"),
						UnitUtils.parseFileSizeString("89%")),
				new TableSpaceUsage("DAISO_TBS",
						UnitUtils.parseFileSizeString("2130G"),
						UnitUtils.parseFileSizeString("410.45G"),
						UnitUtils.parseFileSizeString("1719.55G"),
						UnitUtils.parseFileSizeString("81%"))
				);
		tableSpaceUsageTV.setItems(tableSpaceUsageList);
		
		ObservableList<ASMDiskUsage> asmDiskUsageList = FXCollections.observableArrayList(
				new ASMDiskUsage("DATA", "NORMAL", 
						UnitUtils.parseFileSizeString("12209920MB"),
						UnitUtils.parseFileSizeString("4883968MB"),
						UnitUtils.parseFileSizeString("339168MB"),
						UnitUtils.parseFileSizeString("4544800MB"),
						UnitUtils.parseFileSizeString("93.06%"),
						"WARNING"),
				new ASMDiskUsage("RECO", "NORMAL", 
						UnitUtils.parseFileSizeString("3051520MB"),
						UnitUtils.parseFileSizeString("1220608MB"),
						UnitUtils.parseFileSizeString("754898MB"),
						UnitUtils.parseFileSizeString("465710MB"),
						UnitUtils.parseFileSizeString("38.15%"),
						"GOOD"),
				new ASMDiskUsage("REDO", "HIGH", 
						UnitUtils.parseFileSizeString("3051520MB"),
						UnitUtils.parseFileSizeString("762880MB"),
						UnitUtils.parseFileSizeString("631480MB"),
						UnitUtils.parseFileSizeString("131400MB"),
						UnitUtils.parseFileSizeString("17.22%"),
						"GOOD")
				);
		asmDiskUsageTV.setItems(asmDiskUsageList);

		ObservableList<OSDiskUsage> osDiskUsageList = FXCollections.observableArrayList(
				new OSDiskUsage("/dev/mapper/VolGroupSys-LogVolRoot", "/",
						UnitUtils.parseFileSizeString("30G"),
						UnitUtils.parseFileSizeString("21G"),
						UnitUtils.parseFileSizeString("7.8G"),
						UnitUtils.parseFileSizeString("28%")),
				new OSDiskUsage("tmpfs", "/dev/shm",
						UnitUtils.parseFileSizeString("189G"),
						UnitUtils.parseFileSizeString("187G"),
						UnitUtils.parseFileSizeString("1.3G"),
						UnitUtils.parseFileSizeString("1%")),
				new OSDiskUsage("/dev/md0", "/boot",
						UnitUtils.parseFileSizeString("477M"),
						UnitUtils.parseFileSizeString("407M"),
						UnitUtils.parseFileSizeString("45M"),
						UnitUtils.parseFileSizeString("10%"))
				);
		osDiskUsageTV.setItems(osDiskUsageList);
	}
}
