package Root.Model;

public class AlertLogCommandPeriod extends AlertLogCommand {
	private String fromDate;
	private String toDate;
	
	public AlertLogCommandPeriod() {
		super();
	}
	
	public AlertLogCommandPeriod(AlertLogCommand alc, String fromDate, String toDate) {
		super(alc.getReadCommand(), alc.getReadLine(), alc.getReadFilePath(), alc.getDateFormat(), alc.getDateFormatRegex());
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public AlertLogCommandPeriod(String readCommand, String readLine, String readFilePath, String dateFormat, String dateFormatRegex) {
		super(readCommand, readLine, readFilePath, dateFormat, dateFormatRegex);
	}

	public AlertLogCommandPeriod(String readCommand, String readLine, String readFilePath, String... catchErrorMsg) {
		super(readCommand, readLine, readFilePath, catchErrorMsg);
	}

	public AlertLogCommandPeriod(String readCommand, String readLine, String readFilePath) {
		super(readCommand, readLine, readFilePath);
	}

	public String getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	
	public String getToDate() {
		return toDate;
	}
	
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "AlertLogCommandPeriod [fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
}
