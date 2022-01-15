package root.core.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AlertLog {
	private String fullLogString;
	private List<Log> alertLogs;
	
	public AlertLog() {
		this.alertLogs = new ArrayList<>();
	}

	public void addLog(Log log) {
		this.alertLogs.add(log);
	}
	
	public int getTotalLineCount() {
		int result = 0;
		for(Log l : alertLogs) {
			result += l.getTotalLineCount();
		}
		return result;
	}
}