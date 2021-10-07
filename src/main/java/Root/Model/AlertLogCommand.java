package Root.Model;

import java.util.Arrays;

public class AlertLogCommand {
	private String readCommand;
	private String readLine;
	private String readFilePath;
	private String dateFormat;
	private String dateFormatRegex;
	private String[] catchErrorMsg;

	public AlertLogCommand() {
	}

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

	public String getReadCommand() {
		return readCommand;
	}

	public void setReadCommand(String readCommand) {
		this.readCommand = readCommand;
	}

	public String getReadLine() {
		return readLine;
	}

	public void setReadLine(String readLine) {
		this.readLine = readLine;
	}

	public String getReadFilePath() {
		return readFilePath;
	}

	public void setReadFilePath(String readFilePath) {
		this.readFilePath = readFilePath;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateFormatRegex() {
		return dateFormatRegex;
	}

	public void setDateFormatRegex(String dateFormatRegex) {
		this.dateFormatRegex = dateFormatRegex;
	}

	public String[] getCatchErrorMsg() {
		return catchErrorMsg;
	}

	public void setCatchErrorMsg(String... catchErrorMsg) {
		this.catchErrorMsg = catchErrorMsg;
	}

	@Override
	public String toString() {
		return "AlertLogCommand [readCommand=" + readCommand + ", readLine=" + readLine + ", readFilePath="
				+ readFilePath + ", dateFormat=" + dateFormat + ", dateFormatRegex=" + dateFormatRegex
				+ ", catchErrorMsg=" + Arrays.toString(catchErrorMsg) + "]";
	}

	public String getCommand() {
		return this.getReadCommand() + " -" + this.getReadLine() + " " + this.getReadFilePath();
	}
}
