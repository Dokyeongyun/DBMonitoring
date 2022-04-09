package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
	public void testConnect_ConnectionSuccess() {
		jsch.init();
		Session session = jsch.getSession();
		try {
			session = jsch.connect(session);
		} catch (JSchException e) {
			fail();
		}
		assertTrue(session.isConnected());
	}

	@Test
	public void testConnect_ConnectionFail_SessionIsNull() {
		JschServer jsch = new JschServer(new JschConnectionInfo());
		jsch.init();
		Session session = jsch.getSession();
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> jsch.connect(session));
		assertEquals("Session is null", thrown.getMessage());
	}

	@Test
	public void testConnect_ConnectionFail_JSchException() {
		jsch.init();
		Session session = jsch.getSession();
		try {
			jsch.connect(session);
		} catch (JSchException e) {
			fail();
		}

		JSchException thrown = assertThrows(JSchException.class, () -> jsch.connect(session));
		assertEquals("session is already connected", thrown.getMessage());
	}
}
