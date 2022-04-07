package root.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlertLogCommand {
	private String readLine;
	private String readFilePath;
	private String dateFormat;
	private String dateFormatRegex;
	private String[] catchErrorMsg;

	public AlertLogCommand(String readLine, String readFilePath) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
	}
	
	public AlertLogCommand(String readLine, String readFilePath, String dateFormat, String dateFormatRegex) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.dateFormat = dateFormat;
		this.dateFormatRegex = dateFormatRegex;
	}

	public AlertLogCommand(String readLine, String readFilePath, String... catchErrorMsg) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.catchErrorMsg = catchErrorMsg;
	}

	// TODO OS∫∞ command ¿€º∫
	public String getCommand() {
		return "tail -" + this.getReadLine() + " " + this.getReadFilePath();
	}
}
