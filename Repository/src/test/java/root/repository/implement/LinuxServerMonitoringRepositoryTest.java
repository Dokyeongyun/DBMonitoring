package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

public class LinuxServerMonitoringRepositoryTest {

	public static JschServer jschServer;
	public static ServerMonitoringRepository repo;

	public static String alertLogString = "";
	public static String[] alertLogLines;

	@BeforeAll
	public static void setup() {
		// Load alert log test file
		try {
			alertLogString = Files.readString(Paths.get("src/test/resources/alertlog_sample.txt"));
		} catch (IOException e) {
		}
		alertLogLines = alertLogString.split("\n");

		String serverName = "testServer";
		ServerOS serverOS = ServerOS.LINUX;
		String host = "192.168.109.129";
		int port = 22;
		String id = "dky";
		String password = "ehruddbs1!";

		jschServer = new JschServer(new JschConnectionInfo(serverName, serverOS, host, port, id, password));
		jschServer.init();
		repo = new LinuxServerMonitoringRepository(jschServer);

		// Send test alert log file to remote server
		try {
			Session session = jschServer.getSession();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;
			InputStream is = new ByteArrayInputStream(alertLogString.getBytes());
			channelSftp.put(is, "/home/dky/test.txt");
			channelSftp.disconnect();
			channel.disconnect();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@AfterAll
	public static void teardown() {
		try {
			jschServer.executeCommand("rm -rf /home/dky/test.txt");
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
		AlertLogCommand alc = new AlertLogCommand(10, "/home/dky/test.txt");

		// Act
		int lineCount = repo.getAlertLogFileLineCount(alc);

		// Assert
		assertEquals(alertLogLines.length, lineCount);
	}

	@Test
	public void testCheckAlertLog() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand(alertLogLines.length, "/home/dky/test.txt");

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		assertEquals(alertLogString, result);
	}

	@Test
	public void testCheckAlertLog_Last10Lines() throws Exception {
		// Arrange
		AlertLogCommand alc = new AlertLogCommand(10, "/home/dky/test.txt");

		// Act
		String result = repo.checkAlertLog(alc);

		// Assert
		StringBuffer expected = new StringBuffer();
		for (int i = alertLogLines.length - 10; i < alertLogLines.length; i++) {
			expected.append(alertLogLines[i]);
			if(i != alertLogLines.length -1) {
				expected.append("\n");
			}
		}
		assertEquals(expected.toString(), result);
	}

	@Test
	public void testCheckOSDiskUsage() {
		// Arrange
		// Act
		 List<OSDiskUsage> result = repo.checkOSDiskUsage();

		// Assert
		 assertTrue(result.size() != 0);
	}
}
