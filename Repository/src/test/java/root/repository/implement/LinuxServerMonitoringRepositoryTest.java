package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschServer;
import root.core.repository.constracts.ServerMonitoringRepository;

public class LinuxServerMonitoringRepositoryTest {

	public JschServer jschServer;

	public ServerMonitoringRepository repo;

	public static MockedStatic<IOUtils> ioUtilsMock;

	public static String alertLogString = "";

	public static String[] alertLogLines;

	@BeforeAll
	public static void before() {
		ioUtilsMock = mockStatic(IOUtils.class);
		StringBuffer sb = new StringBuffer();
		sb.append("2022-03-24T13:38:35.065184+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7606 (LGWR switch)").append("\n");
		sb.append("  Current log# 5 seq# 7606 mem# 0: +REDO/1003346093").append("\n");
		sb.append("2022-03-24T13:39:00.180206+09:00").append("\n");
		sb.append("Archived Log entry 13398 added for T-1.S-7605 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-25T14:24:57.291344+09:00").append("\n");
		sb.append("Session (223,56276): RECO logon successful: Inbound connection from client").append("\n");
		sb.append("Session (223,56276): RECO logon successful: DB Logon User: RECO, ").append("\n");
		sb.append("Session (223,56276): RECO logon successful: Client IP Address: -").append("\n");
		sb.append("2022-03-26T18:04:53.572965+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7607 (LGWR switch)").append("\n");
		sb.append("  Current log# 1 seq# 7607 mem# 0: +REDO/DBERP/ONLINELOG/group_1.257.966360593").append("\n");
		sb.append("2022-03-27T18:05:16.929231+09:00").append("\n");
		sb.append("Archived Log entry 13400 added for T-1.S-7606 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-28T00:02:21.629284+09:00").append("\n");
		sb.append("TABLE SYS.WRI$_OPTSTAT_HISTHEAD_HISTORY: ADDED INTERVAL PARTITION SYS_P38333)").append("\n");
		sb.append("TABLE SYS.WRI$_OPTSTAT_HISTGRM_HISTORY: ADDED INTERVAL PARTITION SYS_P38336)").append("\n");
		sb.append("2022-03-28T01:00:18.971650+09:00").append("\n");
		sb.append("ALTER SYSTEM ARCHIVE LOG").append("\n");
		sb.append("2022-03-29T01:00:18.980968+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7608 (LGWR switch)").append("\n");
		sb.append("  Current log# 6 seq# 7608 mem# 0: +REDO/1003346037").append("\n");
		sb.append("2022-03-30T01:00:38.903080+09:00").append("\n");
		sb.append("Archived Log entry 13401 added for T-1.S-7607 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-31T02:56:51.984291+09:00").append("\n");
		sb.append("Starting control autobackup").append("\n");
		alertLogString = sb.toString();

		alertLogLines = alertLogString.split("\n");
	}

	@BeforeEach
	public void setup() {
		jschServer = mock(JschServer.class);
		repo = new LinuxServerMonitoringRepository(jschServer);
	}

	@AfterAll
	public static void after() {
		ioUtilsMock.close();
	}

	@Test
	public void checkAlertLogTest() throws Exception {
		// Arrange
		AlertLogCommand alc = mock(AlertLogCommand.class);
		alc.setReadLine(10);
		alc.setReadFilePath("/test/alert_DB.log");

		String command = String.format("tail -%d %s", alc.getReadLine(), alc.getReadFilePath());
		when(jschServer.executeCommand(command)).thenReturn(alertLogString);

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		assertEquals(result, alertLogString);
	}

	@Test
	public void getAlertLogFileLineCountTest() throws Exception {
		// Arrange
		AlertLogCommand alc = mock(AlertLogCommand.class);
		alc.setReadLine(10);
		alc.setReadFilePath("/test/alert_DB.log");

		String command = String.format("cat %s | wc -l", alc.getReadFilePath());
		when(jschServer.executeCommand(command)).thenReturn(String.valueOf(alertLogLines.length));

		// Act
		int lineCount = repo.getAlertLogFileLineCount(alc);

		// Assert
		assertEquals(lineCount, alertLogLines.length);
	}
}
