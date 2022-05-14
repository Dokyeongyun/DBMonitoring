package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

	public static JschServer jsch;

	@BeforeAll
	public static void setup() {
		String serverName = "DKY SERVER";
		ServerOS serverOS = ServerOS.WINDOW;
		String host = "192.168.154.1";
		String userName = "dky";
		String port = "22";
		String password = "ehruddbs1!";
		jsch = new JschServer(new JschConnectionInfo(serverName, serverOS, host, port, userName, password));
	}

	@Test
	public void testInit_ValidConnInfo() throws Exception {
		jsch.init();
		Session session = jsch.getSession();
		assertNotNull(session);
	}

	@Test
	public void testInit_Fail_InvalidConnInfo() throws Exception {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		Session session = jsch.getSession();
		assertNull(session);
	}

	@Test
	public void testConnect_Success() throws Exception {
		jsch.init();
		Session session = jsch.getSession();
		assertTrue(session.isConnected());
	}

	@Test
	public void testDisConnect_DisConnectionSuccess() throws Exception {
		jsch.init();
		Session session = jsch.getSession();

		assertTrue(session.isConnected());

		jsch.disConnect(session);
		assertFalse(session.isConnected());
	}

	@Test
	public void testDisConnect_DisConnectionFail_SessionIsNull() throws Exception {
		jsch.disConnect(null);
	}

	@Test
	public void testOpenExecChannel_Success() throws Exception {
		jsch.init();
		Session session = jsch.getSession();

		Channel channel = jsch.openExecChannel(session, "echo 1");
		assertNotNull(channel);
	}

	@Test
	public void testOpenExecChannel_Success_WhenSessionIsNull() throws JSchException {
		Session session = null;
		JSchException thrown = assertThrows(JSchException.class, () -> jsch.openExecChannel(session, "echo 1"));
		assertEquals("session is not valid", thrown.getMessage());
	}

	@Test
	public void testConnectChannel_Success() throws Exception {
		jsch.init();
		Session session = jsch.getSession();

		Channel channel = jsch.openExecChannel(session, "echo 1");
		InputStream in = jsch.connectChannel(channel);

		assertNotNull(in);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String echoLine = br.readLine();
			assertEquals(echoLine, "1");
		}
	}

	@Test
	public void testConnectChannel_Success_WhenSessionIsNull() throws Exception {
		Session session = null;
		assertThrows(JSchException.class, () -> jsch.openExecChannel(session, "echo 1"));
	}

	@Test
	public void testDisConnectChannel_Success() throws Exception {
		jsch.init();
		Session session = jsch.getSession();

		Channel channel = jsch.openExecChannel(session, "echo 1");
		jsch.connectChannel(channel);
		assertTrue(channel.isConnected());

		jsch.disConnectChannel(channel);
		assertFalse(channel.isConnected());
	}

	@Test
	public void testGetServerName() {
		String serverName = jsch.getServerName();
		assertEquals(serverName, "DKY SERVER");
	}

	@Test
	public void testExecuteCommand_EchoCommand() throws Exception {
		jsch.init();
		Session session = jsch.getSession();
		String result = jsch.executeCommand(session, "echo 1");
		assertEquals("1", result.trim());
	}

	@Test
	public void testExecuteCommand_TailCommand() throws Exception {
		jsch.init();
		Session session = jsch.getSession();
		String result = jsch.executeCommand(session, "tail -500 C://Users/aserv/Desktop/alert_DB.log");
		assertNotEquals(result, "");
	}

	@Test
	public void testValidateConn_Valid() throws Exception {
		jsch.init();
		assertTrue(jsch.validateConn(jsch.getSession()));
	}

	@Test
	public void testValidateConn_InValid() throws Exception {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		assertFalse(jsch.validateConn(null));
	}
}
