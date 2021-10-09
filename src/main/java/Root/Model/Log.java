package Root.Model;

import java.util.List;

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

	public List<String> getlogContents() {
		return logContents;
	}

	public void setlogContents(List<String> logContents) {
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

	@Override
	public String toString() {
		return "Log [logTimeStamp=" + logTimeStamp + ", logContents=" + logContents + "]";
	}
}
