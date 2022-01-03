package root.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Data
public class AlertLogCommandPeriod extends AlertLogCommand {
	private String fromDate;
	private String toDate;
	
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
}
