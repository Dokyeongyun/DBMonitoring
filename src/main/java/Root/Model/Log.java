package Root.Model;

import java.util.List;

import Root.Utils.ConsoleUtils;

public class Log {
	private String logTimeStamp;
	private List<String> logContents;
	
	public Log() { }
	
	public Log(String logTimeStamp, List<String> logContents) {
		this.logTimeStamp = logTimeStamp;
		this.logContents = logContents;
	}

	public String getLogTimeStamp() {
		return logTimeStamp;
	}

	public void setLogTimeStamp(String logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	public List<String> getLogContents() {
		return logContents;
	}

	public void setLogContents(List<String> logContents) {
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
			if(line.indexOf("ORA") >= 0) {
				result.append(ConsoleUtils.FONT_RED + line + ConsoleUtils.RESET);
			} else {
				result.append(line);
			}
		}
		result.append("===========================================================================\n");
		return result.toString();
	}

	@Override
	public String toString() {
		return "Log [logTimeStamp=" + logTimeStamp + ", logContents=" + logContents + "]";
	}
}
