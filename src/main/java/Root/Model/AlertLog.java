package Root.Model;

import java.util.ArrayList;
import java.util.List;

public class AlertLog {
	private String fullLogString;
	private List<Log> alertLogs;
	
	public AlertLog() {
		this.alertLogs = new ArrayList<>();
	}

	public String getFullLogString() {
		return fullLogString;
	}
	
	public void setFullLogString(String fullLogString) {
		this.fullLogString = fullLogString;
	}
	
	public List<Log> getAlertLogs() {
		return alertLogs;
	}
	
	public void setAlertLogs(List<Log> alertLogs) {
		this.alertLogs = alertLogs;
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

	@Override
	public String toString() {
		return "AlertLog [fullLogString=" + fullLogString + ", alertLogs=" + alertLogs + "]";
	}
}