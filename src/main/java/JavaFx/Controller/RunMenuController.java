package JavaFx.Controller;

import Root.Application.Application;
import Root.Model.ArchiveUsage;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RunMenuController {
	
	/* Archive Usage TableView */
	@FXML TableView<ArchiveUsage> archiveUsageTV;
	@FXML TableColumn<ArchiveUsage, String> auNameTC;
	@FXML TableColumn<ArchiveUsage, Integer> auNOFTC;
	@FXML TableColumn<ArchiveUsage, String> auLimitSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auReclaimableSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auUsedSpaceTC;
	@FXML TableColumn<ArchiveUsage, String> auUsedPercentTC;
	@FXML TableColumn<ArchiveUsage, String> auDntTC;
	
	/**
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		// Application.main(new String[] {});
		monitoringArchiveUsage();
	}
	
	public void monitoringArchiveUsage() {
		auNameTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getArchiveName()));
		auNOFTC.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumberOfFiles()).asObject());
		auLimitSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalSpaceString()));
		auReclaimableSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReclaimableSpaceString()));
		auUsedSpaceTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedSpaceString()));
		auUsedPercentTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsedPercentString()));
		auDntTC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDnt()));

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
	}
}
