package root.javafx.CustomView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import root.core.domain.AlertLog;
import root.core.domain.Log;
import root.javafx.DI.DependencyInjection;

@Slf4j
public class AlertLogMonitoringSummaryAP extends AnchorPane implements Initializable {

	@FXML
	Label alertLogFileLB;

	@FXML
	private TableView<AlertLogSummary> summaryTV;

	@FXML
	private TableColumn<AlertLogSummary, Integer> totalCL;

	@FXML
	private TableColumn<AlertLogSummary, Integer> normalCL;

	@FXML
	private TableColumn<AlertLogSummary, Integer> errorCL;

	@FXML
	private TableColumn<AlertLogSummary, Double> errorRateCL;
	
	@FXML
	private ListView<Log> errorLogLV;
	
	private AlertLog alertLog;
	
	private List<Log> errorLogs = new ArrayList<>();

	public AlertLogMonitoringSummaryAP(AlertLog alertLog) {
		this.alertLog = alertLog;
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/AlertLogMonitoringSummary.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set cell value factory of summary table column
		totalCL.setCellValueFactory(cellData -> cellData.getValue().getTotalLogCount().asObject());
		normalCL.setCellValueFactory(cellData -> cellData.getValue().getNormalLogCount().asObject());
		errorCL.setCellValueFactory(cellData -> cellData.getValue().getErrorLogCount().asObject());
		errorRateCL.setCellValueFactory(cellData -> cellData.getValue().getErrorRate().asObject());

		// Set cell factory of error log listview
		errorLogLV.setCellFactory(categoryList -> new AlertLogListViewCell());

		render();
	}

	private void render() {
		// Set Alert log file path label text
		alertLogFileLB.setText(alertLog.getFilePath());

		// Set summary tableview value
		summaryTV.setItems(FXCollections.observableArrayList(List.of(getSummaryData())));
		
		errorLogLV.getItems().addAll(errorLogs);
	}

	private AlertLogSummary getSummaryData() {
		int total = 0;
		int normal = 0;
		int error = 0;

		for (Log l : alertLog.getAlertLogs()) {
			total++;
			boolean isErrorLog = false;
			for (String s : l.getLogContents()) {
				// TODO Remove hard-coding that identifying error log
				if (s.contains("ORA-")) {
					isErrorLog = true;
					break;
				}
			}
			if (isErrorLog) {
				error++;
				errorLogs.add(l);
			} else {
				normal++;
			}
		}

		return new AlertLogSummary(total, normal, error);
	}

	@Data
	private static class AlertLogSummary {
		private SimpleIntegerProperty totalLogCount;
		private SimpleIntegerProperty normalLogCount;
		private SimpleIntegerProperty errorLogCount;
		private SimpleDoubleProperty errorRate;

		public AlertLogSummary(int totalLogCount, int normalLogCount, int errorLogCount) {
			double rate = (double) Math.round((errorLogCount / (double) totalLogCount * 100) * 100) / 100;

			this.totalLogCount = new SimpleIntegerProperty(totalLogCount);
			this.normalLogCount = new SimpleIntegerProperty(normalLogCount);
			this.errorLogCount = new SimpleIntegerProperty(errorLogCount);
			this.errorRate = new SimpleDoubleProperty(rate);
		}
	}
}
