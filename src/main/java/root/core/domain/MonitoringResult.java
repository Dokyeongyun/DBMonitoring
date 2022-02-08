package root.core.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import root.utils.DateUtils;

@AllArgsConstructor
@Data
public class MonitoringResult<T> {
	private Date monitoringDate;
	private List<T> monitoringResults;
	
	public MonitoringResult(List<T> monitoringResults) {
		this.monitoringDate = new Date();
		this.monitoringResults = monitoringResults;
	}
	
	public String getMonitoringDay() {
		if(monitoringDate == null) {
			return null;
		}
		
		return DateUtils.format(monitoringDate, "yyyyMMdd");
	}
	
	public String getMonitoringTime() {
		if(monitoringDate == null) {
			return null;
		}

		return DateUtils.format(monitoringDate, "HHmmss");
	}
}
