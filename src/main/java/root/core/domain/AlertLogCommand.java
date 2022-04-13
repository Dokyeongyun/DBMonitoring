package root.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlertLogCommand {
	private int readLine;
	private String readFilePath;
	private String dateFormat;
	private String dateFormatRegex;
	private String[] catchErrorMsg;

	public AlertLogCommand(int readLine, String readFilePath) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
	}
	
	public AlertLogCommand(int readLine, String readFilePath, String dateFormat, String dateFormatRegex) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.dateFormat = dateFormat;
		this.dateFormatRegex = dateFormatRegex;
	}

	public AlertLogCommand(int readLine, String readFilePath, String... catchErrorMsg) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.catchErrorMsg = catchErrorMsg;
	}
}
