package root.core.repository.implement;

import java.util.List;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.JschServer;
import root.core.domain.AlertLogCommand;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerMonitoringRepository;

@Slf4j
public class WindowServerMonitoringRepository implements ServerMonitoringRepository {
	private JschServer jsch;

	public WindowServerMonitoringRepository(JschServer jsch) {
		this.jsch = jsch;
	}

	@Override
	public String getServerName() {
		return jsch.getServerName();
	}

	@Override
	public int getAlertLogFileLineCount(AlertLogCommand alc) {
		int fileLineCnt = 0;

		try {
			String command = String.format("find /v /c \"\" %s", alc.getReadFilePath());
			String executeResult = jsch.executeCommand(command);
			StringTokenizer st = new StringTokenizer(executeResult);
			String lastToken = "0";
			while (st.hasMoreTokens()) {
				lastToken = st.nextToken();
			}

			fileLineCnt = Integer.parseInt(lastToken);
			log.debug(String.format("alert log file line count: %s, %d", alc.getReadFilePath(), fileLineCnt));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return fileLineCnt;
	}

	@Override
	public String checkAlertLog(AlertLogCommand alc) {
		String result = "";
		try {
			String command = String.format("tail -%d %s", alc.getReadLine(), alc.getReadFilePath());
			result = jsch.executeCommand(command);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.debug(alc.toString());
		return result;
	}

	@Override
	public List<OSDiskUsage> checkOSDiskUsage() {
		// TODO Auto-generated method stub
		return null;
	}
}
