package root.core.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.utils.DateUtils;
import root.utils.UnitUtils.FileSize;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class MonitoringResult {
	
	private String monitoringDate;

	private String monitoringTime;

	public MonitoringResult(Date date) {
		this.monitoringDate = DateUtils.format(date, "yyyyMMdd");
		this.monitoringTime = DateUtils.format(date, "HHmmss");
	}

	public String getMonitoringDateTime() {
		return this.monitoringDate + this.monitoringTime;
	}
	
	public abstract void convertUnit(FileSize fromUnit, FileSize toUnit, int round);
}