package JavaFx.Controller;

import java.net.URL;
import java.util.ResourceBundle;

import Root.Model.ASMDiskUsage;
import Root.Model.ArchiveUsage;
import Root.Model.OSDiskUsage;
import Root.Model.TableSpaceUsage;
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
	@FXML TableColumn<ArchiveUsage, String> auLimitSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auReclaimableSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auUsedSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auUsedPercentTC;
	@FXML TableColumn<ArchiveUsage, String> auDntTC;
	
	/* TableSpace Usage TableView */
	@FXML TableView<TableSpaceUsage> tableSpaceUsageTV;
	@FXML TableColumn<TableSpaceUsage, String> tsuNameTC;
	@FXML TableColumn<TableSpaceUsage, String> tsuTotalSpaceTC;
	@FXML TableColumn<TableSpaceUsage, String> tsuFreeSpaceTC;
	@FXML TableColumn<TableSpaceUsage, String> tsuUsedSpaceTC;
	@FXML TableColumn<TableSpaceUsage, String> tsuUsedPercentTC;
	
	/* ASM Disk Usage TableView */
	@FXML TableView<ASMDiskUsage> asmDiskUsageTV;
	@FXML TableColumn<ASMDiskUsage, String> asmNameTC;
	@FXML TableColumn<ASMDiskUsage, String> asmTypeTC;
	@FXML TableColumn<ASMDiskUsage, String> asmTotalSpaceTC;
	@FXML TableColumn<ASMDiskUsage, String> asmTotalUsableSpaceTC;
	@FXML TableColumn<ASMDiskUsage, String> asmFreeSpaceTC;
	@FXML TableColumn<ASMDiskUsage, String> asmUsedSpaceTC;
	@FXML TableColumn<ASMDiskUsage, String> asmUsedPercentTC;
	
	/* OS Disk Usage TableView */
	@FXML TableView<OSDiskUsage> osDiskUsageTV;
	@FXML TableColumn<OSDiskUsage, String> osFileSystemTC;
	@FXML TableColumn<OSDiskUsage, String> osMountedOnTC;
	@FXML TableColumn<OSDiskUsage, String> osTotalSpaceTC;
	@FXML TableColumn<OSDiskUsage, String> osFreeSpaceTC;
	@FXML TableColumn<OSDiskUsage, String> osUsedSpaceTC;
	@FXML TableColumn<OSDiskUsage, String> osUsedPercentTC;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		auNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArchiveName()));
		auNOFTC.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumberOfFiles()).asObject());
		auLimitSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalSpaceString()));
		auReclaimableSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReclaimableSpaceString()));
		auUsedSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedSpaceString()));
		auUsedPercentTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedPercentString()));
		auDntTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDnt()));
		
		tsuNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTableSpaceName()));
		tsuTotalSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalSpaceString()));
		tsuFreeSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailableSpaceString()));
		tsuUsedSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedSpaceString()));
		tsuUsedPercentTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedPercentString()));

		asmNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsmDiskGroupName()));
		asmTypeTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsmDiskGroupType()));
		asmTotalSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalRawSpaceString()));
		asmTotalUsableSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalAvailableSpaceString()));
		asmFreeSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailableSpaceString()));
		asmUsedSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedSpaceString()));
		asmUsedPercentTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedPercentString()));
		
		osFileSystemTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFileSystem()));
		osMountedOnTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMountedOn()));
		osTotalSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalSpaceString()));
		osFreeSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAvailableSpaceString()));
		osUsedSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedSpaceString()));
		osUsedPercentTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedPercentString()));
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
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "44%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "7%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "60%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "42%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "23%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "1%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "23%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "63%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "83%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "3%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "24%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "45%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "48%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "32%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34"),
				new ArchiveUsage("+RECO", 103, "284(G)", "434(G)", "43%", "1000(G)", "2021-10-13 00:06:34")
				);
		archiveUsageTV.setItems(archiveUsageList);
		
		ObservableList<TableSpaceUsage> tableSpaceUsageList = FXCollections.observableArrayList(
				new TableSpaceUsage("GGS_DATA", ".84G", "16.83G", "95%", "17.67G"),
				new TableSpaceUsage("SYSTEM", ".3G", "2.9G", "91%", "3.2G"),
				new TableSpaceUsage("DAISO_INDX", "119.91G", "960.09G", "89%", "1080G"),
				new TableSpaceUsage("DAISO_TBS", "410.45G", "1719.55G", "81%", "2130G"),
				new TableSpaceUsage("SYSAUX", "11.14G", "18.86G", "63%", "30G")
				);
		tableSpaceUsageTV.setItems(tableSpaceUsageList);
		
		ObservableList<ASMDiskUsage> asmDiskUsageList = FXCollections.observableArrayList(
				new ASMDiskUsage("DATA", "NORMAL", "12209920MB", "4883968MB", "339168MB", "4544800MB", "93.06", "WARNING"),
				new ASMDiskUsage("RECO", "NORMAL", "3051520MB", "1220608MB", "754898MB", "465710MB", "38.15", "GOOD"),
				new ASMDiskUsage("REDO", "HIGH", "3051520MB", "762880MB%", "631480MB", "131400MB", "17.22", "GOOD")
				);
		asmDiskUsageTV.setItems(asmDiskUsageList);

		ObservableList<OSDiskUsage> osDiskUsageList = FXCollections.observableArrayList(
				new OSDiskUsage("/dev/mapper/VolGroupSys-LogVolRoot", "/", "30G", "21G", "7.8G", "28%"),
				new OSDiskUsage("tmpfs", "/dev/shm", "189G", "187G", "1.3G", "1%"),
				new OSDiskUsage("/dev/md0", "/boot", "477M", "407M", "45M", "10%")
				);
		osDiskUsageTV.setItems(osDiskUsageList);
	}
}
