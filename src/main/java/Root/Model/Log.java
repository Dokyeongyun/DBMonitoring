package Root.Model;

import java.util.List;

import Root.Utils.ConsoleUtils;
import lombok.Data;

@Data
public class Log {
	private String logTimeStamp;
	private List<String> logContents;
	
	public Log(String logTimeStamp, List<String> logContents) {
		this.logTimeStamp = logTimeStamp;
		this.logContents = logContents;
	}

	public int getTotalLineCount() {
		return logContents.size();
	}
	
	public String getFullLogString() {
		StringBuffer result = new StringBuffer();
		for(String s : logContents) {
			result.append(s);
		}
		return result.toString();
	}
	
	public String errorLogToString() {
		StringBuffer result = new StringBuffer();
		result.append("===========================================================================\n\n");
		result.append(this.getLogTimeStamp()).append("\n");
		for(String line : this.getLogContents()) {
			if(line.indexOf("ORA-") >= 0) {
				result.append(ConsoleUtils.FONT_RED + line + ConsoleUtils.RESET);
			} else {
				result.append(line);
			}
		}
		result.append("===========================================================================\n");
		return result.toString();
	}
}
