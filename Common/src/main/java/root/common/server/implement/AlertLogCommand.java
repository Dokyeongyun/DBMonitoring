package root.common.server.implement;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlertLogCommand {
	private int readLine;
	private String readFilePath;
	private String[] catchErrorMsg;

	public AlertLogCommand(int readLine, String readFilePath) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
	}

	public AlertLogCommand(int readLine, String readFilePath, String... catchErrorMsg) {
		this.readLine = readLine;
		this.readFilePath = readFilePath;
		this.catchErrorMsg = catchErrorMsg;
	}
}
