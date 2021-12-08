package Root.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlertLogCommand {
	private String readCommand;
	private String readLine;
	private String readFilePath;
	private String dateFormat;
	private String dateFormatRegex;
	private String[] catchErrorMsg;

	public AlertLogCommand(String readCommand, String readLine, String readFilePath) {
		this.readCommand = readCommand;
		this.readLine = readLine;
		this.readFilePath = readFilePath;
	}
	
	public AlertLogCommand(String readCommand, String readLine, String readFilePath, String dateFormat, String dateFormatRegex) {
		this.readCommand = readCommand;
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.dateFormat = dateFormat;
		this.dateFormatRegex = dateFormatRegex;
	}

	public AlertLogCommand(String readCommand, String readLine, String readFilePath, String... catchErrorMsg) {
		this.readCommand = readCommand;
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.catchErrorMsg = catchErrorMsg;
	}

	public String getCommand() {
		return this.getReadCommand() + " -" + this.getReadLine() + " " + this.getReadFilePath();
	}
}
