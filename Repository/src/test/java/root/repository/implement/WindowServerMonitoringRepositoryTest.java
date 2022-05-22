package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.common.server.implement.ServerOS;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerMonitoringRepository;

public class WindowServerMonitoringRepositoryTest {

	public static JschServer jschServer;
	public static ServerMonitoringRepository repo;
	public static String alertLogString = "";
	public static String[] alertLogLines;

	@BeforeAll
	public static void before() {
		// Load alert log test file
		try {
			alertLogString = Files.readString(Paths.get("src/test/resources/alertlog_sample.txt"));
		} catch (IOException e) {
		}
		alertLogLines = alertLogString.split("\n");

		String serverName = "testServer";
		ServerOS serverOS = ServerOS.WINDOW;
		String host = "192.168.154.1";
		int port = 22;
		String id = "dky";
		String password = "ehruddbs1!";

		jschServer = new JschServer(new JschConnectionInfo(serverName, serverOS, host, port, id, password));
		jschServer.init();
		repo = new WindowServerMonitoringRepository(jschServer);

		// Send test alert log file to remote server
		try {
			Session session = jschServer.getSession();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;
			InputStream is = new ByteArrayInputStream(alertLogString.getBytes());
			channelSftp.put(is, "C:\\test.txt");
			channelSftp.disconnect();
			channel.disconnect();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@AfterAll
	public static void teardown() {
		try {
			// jschServer.executeCommand("rd /s /q C:\\test.txt");
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetServerName() {
		// Act
		String result = repo.getServerName();

		// Assert
		assertEquals("testServer", result);
	}

	@Test
	public void testGetAlertLogFileLineCount() {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand(10, "C:\\test.txt");

		// Act
		int lineCount = repo.getAlertLogFileLineCount(alc);

		// Assert
		assertEquals(alertLogLines.length, lineCount);
	}

	@Test
	public void testCheckAlertLog() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand(alertLogLines.length, "C:\\test.txt");

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		assertEquals(alertLogString, result);
	}

	@Test
	public void testCheckAlertLog_Last10Lines() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand(10, "C:\\test.txt");

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		StringBuffer expected = new StringBuffer();
		for (int i = alertLogLines.length - 10; i < alertLogLines.length; i++) {
			expected.append(alertLogLines[i]).append("\n");
		}
		assertEquals(expected.toString(), result);
	}

	@Test
	public void testCheckOSDiskUsage() {
		// Arrange
		// Act
		// List<OSDiskUsage> result = repo.checkOSDiskUsage();

		// Assert
		// assertTrue(result.size() != 0);
	}

	/*
	 * @Test
	 * public void testGetServerName_ServerNameIsNull() {
	 * when(jschServer.getServerName()).thenReturn(null);
	 * String result = repo.getServerName();
	 * assertNull(result);
	 * }
	 * 
	 * @Test
	 * public void testGetServerName_ServerNameIsNotNull() {
	 * when(jschServer.getServerName()).thenReturn("DKY SERVER");
	 * String result = repo.getServerName();
	 * assertNotNull(result);
	 * }
	 * 
	 * @Test
	 * public void testGetAlertLogFileLineCount() throws Exception {
	 * // Arrange
	 * AlertLogCommand alc = new AlertLogCommand();
	 * alc.setReadFilePath("C:\\alert_DKYDB.log");
	 * 
	 * String command = String.format("find /v /c \"\" %s", alc.getReadFilePath());
	 * when(jschServer.executeCommand(command)).thenReturn(String.valueOf(
	 * alertLogLines.length));
	 * 
	 * // Act
	 * int lineCount = repo.getAlertLogFileLineCount(alc);
	 * 
	 * // Assert
	 * assertEquals(lineCount, alertLogLines.length);
	 * }
	 * 
	 * @Test
	 * public void testCheckAlertLog() throws Exception {
	 * // Arrange
	 * AlertLogCommand alc = new AlertLogCommand();
	 * alc.setReadLine(10);
	 * alc.setReadFilePath("C:\\alert_DKYDB.log");
	 * 
	 * String command = String.format("tail %d %s", alc.getReadLine(),
	 * alc.getReadFilePath());
	 * when(jschServer.executeCommand(command)).thenReturn(alertLogString);
	 * 
	 * // Act
	 * String result = repo.checkAlertLog(alc);
	 * 
	 * // Assert
	 * assertEquals(result, alertLogString);
	 * }
	 */
	/*
	 * @Test
	 * public void testCheckAlertLogDuringPeriod() throws Exception {
	 * // Arrange
	 * AlertLogCommand alc = new AlertLogCommand();
	 * alc.setReadLine(10);
	 * alc.setReadFilePath("C:\\alert_DKYDB.log");
	 * 
	 * String command1 = String.format("find /v /c \"\" %s", alc.getReadFilePath());
	 * when(jschServer.executeCommand(command1)).thenReturn(String.valueOf(
	 * alertLogLines.length));
	 * 
	 * String command2 = String.format("tail %d %s", alc.getReadLine(),
	 * alc.getReadFilePath());
	 * when(jschServer.executeCommand(command2)).thenReturn(alertLogString);
	 * 
	 * // Act
	 * AlertLog alertLog = repo.checkAlertLogDuringPeriod(alc, "2022-03-24",
	 * "2022-03-29");
	 * 
	 * // Assert
	 * assertEquals(alertLog.getTotalLineCount(), 12);
	 * assertEquals(alertLog.getAlertLogs().size(), 7);
	 * }
	 * 
	 * @Test
	 * public void testCheckAlertLogDuringPeriod_ReadLineBiggerThenTotalLineCnt()
	 * throws Exception {
	 * // Arrange
	 * AlertLogCommand alc = new AlertLogCommand();
	 * alc.setReadLine(20);
	 * alc.setReadFilePath("C:\\alert_DKYDB.log");
	 * 
	 * String command1 = String.format("find /v /c \"\" %s", alc.getReadFilePath());
	 * when(jschServer.executeCommand(command1)).thenReturn("26");
	 * 
	 * String command2 = String.format("tail %d %s", alc.getReadLine(),
	 * alc.getReadFilePath());
	 * StringBuilder builder = new StringBuilder();
	 * for (int i = 0; i < Math.min(alertLogLines.length, alc.getReadLine()); i++) {
	 * builder.append(alertLogLines[i]).append("\n");
	 * }
	 * when(jschServer.executeCommand(command2)).thenReturn(builder.toString());
	 * 
	 * String command3 = String.format("tail %d %s", alc.getReadLine() * 2,
	 * alc.getReadFilePath());
	 * builder = new StringBuilder();
	 * for (int i = 0; i < Math.min(alertLogLines.length, alc.getReadLine() * 2);
	 * i++) {
	 * builder.append(alertLogLines[i]).append("\n");
	 * }
	 * when(jschServer.executeCommand(command3)).thenReturn(builder.toString());
	 * 
	 * // Act
	 * AlertLog alertLog = repo.checkAlertLogDuringPeriod(alc, "2022-03-23",
	 * "2022-03-24");
	 * 
	 * // Assert
	 * assertEquals(alertLog.getTotalLineCount(), 3);
	 * assertEquals(alertLog.getAlertLogs().size(), 2);
	 * }
	 */
}
