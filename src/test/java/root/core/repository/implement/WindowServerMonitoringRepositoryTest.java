package root.core.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import root.common.server.implement.JschServer;
import root.core.domain.AlertLogCommand;
import root.core.repository.constracts.ServerMonitoringRepository;

public class WindowServerMonitoringRepositoryTest {
	
	public JschServer jschServer;
	public ServerMonitoringRepository repo;
	public static String alertLogString = "";
	public static String[] alertLogLines; 

	@BeforeAll
	public static void before() {
		StringBuffer sb = new StringBuffer();
		sb.append("2022-03-23T13:38:35.065184+09:00").append("\n");
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
		repo = new WindowServerMonitoringRepository(jschServer);
	}

	@Test
	public void testGetServerName_ServerNameIsNull() {
		when(jschServer.getServerName()).thenReturn(null);
		String result = repo.getServerName();
		assertNull(result);
	}

	@Test
	public void testGetServerName_ServerNameIsNotNull() {
		when(jschServer.getServerName()).thenReturn("DKY SERVER");
		String result = repo.getServerName();
		assertNotNull(result);
	}

	@Test
	public void testGetAlertLogFileLineCount() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadFilePath("C:\\alert_DKYDB.log");
		
		String command = String.format("find /v /c \"\" %s", alc.getReadFilePath());
		when(jschServer.executeCommand(command)).thenReturn(String.valueOf(alertLogLines.length));

		// Act
		int lineCount = repo.getAlertLogFileLineCount(alc);

		// Assert
		assertEquals(lineCount, alertLogLines.length);
	}
	
	@Test
	public void testCheckAlertLog() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadLine(10);
		alc.setReadFilePath("C:\\alert_DKYDB.log");
		
		String command = String.format("tail %d %s", alc.getReadLine(), alc.getReadFilePath());
		when(jschServer.executeCommand(command)).thenReturn(alertLogString);

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		assertEquals(result, alertLogString);
	}

	/*
	@Test
	public void testCheckAlertLogDuringPeriod() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadLine(10);
		alc.setReadFilePath("C:\\alert_DKYDB.log");
		
		String command1 = String.format("find /v /c \"\" %s", alc.getReadFilePath());
		when(jschServer.executeCommand(command1)).thenReturn(String.valueOf(alertLogLines.length));
		
		String command2 = String.format("tail %d %s", alc.getReadLine(), alc.getReadFilePath());
		when(jschServer.executeCommand(command2)).thenReturn(alertLogString);

		// Act
		AlertLog alertLog = repo.checkAlertLogDuringPeriod(alc, "2022-03-24", "2022-03-29");

		// Assert
		assertEquals(alertLog.getTotalLineCount(), 12);
		assertEquals(alertLog.getAlertLogs().size(), 7);
	}
	
	@Test
	public void testCheckAlertLogDuringPeriod_ReadLineBiggerThenTotalLineCnt() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadLine(20);
		alc.setReadFilePath("C:\\alert_DKYDB.log");
		
		String command1 = String.format("find /v /c \"\" %s", alc.getReadFilePath());
		when(jschServer.executeCommand(command1)).thenReturn("26");
		
		String command2 = String.format("tail %d %s", alc.getReadLine(), alc.getReadFilePath());		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < Math.min(alertLogLines.length, alc.getReadLine()); i++) {
			builder.append(alertLogLines[i]).append("\n");
		}
		when(jschServer.executeCommand(command2)).thenReturn(builder.toString());

		String command3 = String.format("tail %d %s", alc.getReadLine() * 2, alc.getReadFilePath());
		builder = new StringBuilder();
		for (int i = 0; i < Math.min(alertLogLines.length, alc.getReadLine() * 2); i++) {
			builder.append(alertLogLines[i]).append("\n");
		}
		when(jschServer.executeCommand(command3)).thenReturn(builder.toString());

		// Act
		AlertLog alertLog = repo.checkAlertLogDuringPeriod(alc, "2022-03-23", "2022-03-24");

		// Assert
		assertEquals(alertLog.getTotalLineCount(), 3);
		assertEquals(alertLog.getAlertLogs().size(), 2);
	}
	*/
}
