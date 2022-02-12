package root.core.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.utils.DateUtils;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonitoringResult {
	
	private String monitoringDate;

	private String monitoringTime;

	public MonitoringResult(Date date) {
		this.monitoringDate = DateUtils.format(date, "yyyyMMdd");
		this.monitoringTime = DateUtils.format(date, "HHmmss");
	}
}