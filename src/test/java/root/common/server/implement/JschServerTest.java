package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import root.core.domain.JschConnectionInfo;

public class JschServerTest {

	public static JschServer jsch;

	@BeforeAll
	public static void setup() {
		String serverName = "DKY SERVER";
		String host = "192.168.154.1";
		String userName = "dky";
		String port = "22";
		String password = "ehruddbs1!";
		jsch = new JschServer(new JschConnectionInfo(serverName, host, port, userName, password));
	}

	@Test
	public void testInit_ValidConnInfo() {
		jsch.init();
		Session session = jsch.getSession();
		assertNotNull(session);
	}

	@Test
	public void testInit_Fail_InvalidConnInfo() {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		Session session = jsch.getSession();
		assertNull(session);
	}

	@Test
	public void testConnect_Success() throws JSchException {
		jsch.init();
		Session session = jsch.getSession();
		session = jsch.connect(session);
		assertTrue(session.isConnected());
	}

	@Test
	public void testConnect_Success_AlreadyConnection() throws JSchException {
		jsch.init();
		Session session = jsch.getSession();
		jsch.connect(session);
		jsch.connect(session);

		assertTrue(session.isConnected());
	}

	@Test
	public void testConnect_Fail_SessionIsNull() {
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> jsch.connect(null));
		assertEquals("Session is null", thrown.getMessage());
	}

	@Test
	public void testDisConnect_DisConnectionSuccess() throws JSchException {
		jsch.init();
		Session session = jsch.getSession();

		session = jsch.connect(session);
		assertTrue(session.isConnected());

		jsch.disConnect(session);
		assertFalse(session.isConnected());
	}

	@Test
	public void testDisConnect_DisConnectionFail_SessionIsNull() {
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> jsch.disConnect(null));
		assertEquals("Session is null", thrown.getMessage());
	}

	@Test
	public void testOpenExecChannel_Success() throws JSchException {
		jsch.init();
		Session session = jsch.getSession();
		jsch.connect(session);

		Channel channel = jsch.openExecChannel(session, "echo 1");
		assertNotNull(channel);
	}

	@Test
	public void testOpenExecChannel_Success_WhenSessionIsNull() throws JSchException {
		Session session = null;
		Channel channel = jsch.openExecChannel(session, "echo 1");
		assertNotNull(channel);
	}

	@Test
	public void testConnectChannel_Success() throws JSchException, IOException {
		jsch.init();
		Session session = jsch.getSession();
		jsch.connect(session);

		Channel channel = jsch.openExecChannel(session, "echo 1");
		InputStream in = jsch.connectChannel(channel);

		assertNotNull(in);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String echoLine = br.readLine();
			assertEquals(echoLine, "1");
		}
	}

	@Test
	public void testConnectChannel_Success_WhenSessionIsNull() throws JSchException, IOException {
		Session session = null;
		Channel channel = jsch.openExecChannel(session, "echo 1");
		InputStream in = jsch.connectChannel(channel);

		assertNotNull(in);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String echoLine = br.readLine();
			assertEquals(echoLine, "1");
		}
	}

	@Test
	public void testDisConnectChannel_Success() throws JSchException {
		jsch.init();
		Session session = jsch.getSession();
		jsch.connect(session);

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
	public void testExecuteCommand() throws JSchException, IOException {
		String result = jsch.executeCommand("echo 1");
		assertEquals(result, "1");
	}

	@Test
	public void testValidateConn_Valid() {
		jsch.init();
		assertTrue(JschServer.validateConn(jsch.getSession()));
	}

	@Test
	public void testValidateConn_InValid() {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		assertFalse(JschServer.validateConn(jsch.getSession()));
	}
}
