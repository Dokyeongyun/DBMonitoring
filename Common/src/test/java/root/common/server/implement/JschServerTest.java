package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschServerTest {

	public static JschServer windowJsch;
	public static JschServer linuxJsch;

	@BeforeAll
	public static void setup() {
		String windowServerName = "DKY WINDOW SERVER";
		ServerOS windowServerOS = ServerOS.WINDOW;
		String windowHost = "192.168.154.1";
		String windowUserName = "dky";
		int windowPort = 22;
		String windowPassword = "ehruddbs1!";
		windowJsch = new JschServer(new JschConnectionInfo(windowServerName, windowServerOS, windowHost, windowPort, windowUserName, windowPassword));
		windowJsch.init();
		
		String linuxServerName = "DKY LINUX SERVER";
		ServerOS linuxServerOS = ServerOS.LINUX;
		String linuxHost = "192.168.56.137";
		String linuxUserName = "dokyeongyun";
		int linuxPort = 22;
		String linuxPassword = "ehruddbs1!";
		linuxJsch = new JschServer(new JschConnectionInfo(linuxServerName, linuxServerOS, linuxHost, linuxPort, linuxUserName, linuxPassword));
		linuxJsch.init();
	}
	
	@Test
	public void testGetServerName_Window() {
		String serverName = windowJsch.getServerName();
		assertEquals(serverName, "DKY WINDOW SERVER");
	}
	
	@Test
	public void testGetServerName_Linux() {
		String serverName = linuxJsch.getServerName();
		assertEquals(serverName, "DKY LINUX SERVER");
	}

	@Test
	public void testInit_ValidConnInfo_Window() throws Exception {
		Session session = windowJsch.getSession();
		assertNotNull(session);
	}
	
	@Test
	public void testInit_ValidConnInfo_Linux() throws Exception {
		Session session = linuxJsch.getSession();
		assertNotNull(session);
	}

	@Test
	public void testInit_Fail_InvalidConnInfo() throws Exception {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		Session session = jsch.getSession();
		assertNull(session);
	}

	@Test
	public void testConnect_Success_Window() throws Exception {
		Session session = windowJsch.getSession();
		assertTrue(session.isConnected());
	}
	
	@Test
	public void testConnect_Success_Linux() throws Exception {
		Session session = linuxJsch.getSession();
		assertTrue(session.isConnected());
	}

	@Test
	public void testDisConnect_DisConnectionSuccess() throws Exception {
		Session session = windowJsch.getSession();
		assertTrue(session.isConnected());

		windowJsch.disConnect(session);
		assertFalse(session.isConnected());
	}

	@Test
	public void testDisConnect_DisConnectionFail_SessionIsNull() throws Exception {
		Session session = null;
		windowJsch.disConnect(session);
		assertNull(session);
	}

	@Test
	public void testOpenExecChannel_Success_Window() throws Exception {
		Session session = windowJsch.getSession();
		Channel channel = windowJsch.openExecChannel(session, "echo 1");
		assertNotNull(channel);
	}
	
	@Test
	public void testOpenExecChannel_Success_Linux() throws Exception {
		Session session = linuxJsch.getSession();
		Channel channel = linuxJsch.openExecChannel(session, "echo 1");
		assertNotNull(channel);
	}

	@Test
	public void testOpenExecChannel_Fail_WhenSessionIsNull() throws JSchException {
		Session session = null;
		JSchException thrown = assertThrows(JSchException.class, () -> windowJsch.openExecChannel(session, "echo 1"));
		assertEquals("session is not valid", thrown.getMessage());
	}

	@Test
	public void testConnectChannel_Success_Window() throws Exception {
		Session session = windowJsch.getSession();
		Channel channel = windowJsch.openExecChannel(session, "echo 1");
		InputStream in = windowJsch.connectChannel(channel);

		assertNotNull(in);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String echoLine = br.readLine();
			assertEquals(echoLine, "1");
		}
	}
	
	@Test
	public void testConnectChannel_Success_Linux() throws Exception {
		Session session = linuxJsch.getSession();
		Channel channel = linuxJsch.openExecChannel(session, "echo 1");
		InputStream in = linuxJsch.connectChannel(channel);

		assertNotNull(in);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String echoLine = br.readLine();
			assertEquals(echoLine, "1");
		}
	}

	@Test
	public void testConnectChannel_Fail_WhenSessionIsNull() throws Exception {
		Session session = null;
		assertThrows(JSchException.class, () -> windowJsch.openExecChannel(session, "echo 1"));
	}

	@Test
	public void testDisConnectChannel_Success() throws Exception {
		Session session = windowJsch.getSession();

		Channel channel = windowJsch.openExecChannel(session, "echo 1");
		windowJsch.connectChannel(channel);
		assertTrue(channel.isConnected());

		windowJsch.disConnectChannel(channel);
		assertFalse(channel.isConnected());
	}

	@Test
	public void testExecuteCommand_EchoCommand_Window() throws Exception {
		Session session = windowJsch.getSession();
		String result = windowJsch.executeCommand(session, "echo 1");
		assertEquals("1", result.trim());
		
		result = windowJsch.executeCommand("echo 1");
		assertEquals("1", result.trim());
	}
	
	@Test
	public void testExecuteCommand_EchoCommand_Linux() throws Exception {
		Session session = linuxJsch.getSession();
		String result = linuxJsch.executeCommand(session, "echo 1");
		assertEquals("1", result.trim());
	}

	@Test
	public void testValidateConn_Valid() throws Exception {
		windowJsch.init();
		assertTrue(windowJsch.validateConn(windowJsch.getSession()));
	}

	@Test
	public void testValidateConn_InValid() throws Exception {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		assertFalse(jsch.validateConn(null));
	}
}
