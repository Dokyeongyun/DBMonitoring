package root.core.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.utils.ConsoleUtils;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Log {
	private int index;
	private String logTimeStamp;
	private List<String> logContents;

	public int getTotalLineCount() {
		return logContents.size();
	}
	
	public String getFullLogString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < logContents.size(); i++) {
			result.append(logContents.get(i));
			if (i != logContents.size()) {
				result.append(System.lineSeparator());
			}
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
