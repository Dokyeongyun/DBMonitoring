package root.javafx.CustomView.dateCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.control.DateCell;

public class MonitoringHistoryDateCell extends DateCell {

	private List<String> monitoringDayList;

	public MonitoringHistoryDateCell(List<String> monitoringDayList) {
		this.monitoringDayList = monitoringDayList;
	}

	@Override
	public void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);
		LocalDate today = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedString = item.format(formatter);
		setDisable(empty || item.compareTo(today) > 0 || !monitoringDayList.contains(formattedString));
	}

}
