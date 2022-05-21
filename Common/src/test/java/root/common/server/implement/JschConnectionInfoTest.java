package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JschConnectionInfoTest {

	public static String serverName = "testServer";
	public static ServerOS serverOS = ServerOS.LINUX;
	public static String host = "111.111.111.111";
	public static int port = 22;
	public static String userName = "userName";
	public static String password = "password";
	public static AlertLogCommand alc = new AlertLogCommand();

	@Test
	public void testConstructor1() {
		JschConnectionInfo jsch = new JschConnectionInfo();
		assertNotNull(jsch.getAlc());
	}

	@Test
	public void testConstructor2() {
		new JschConnectionInfo(serverName, serverOS, host, "22", userName, password, alc);
	}

	@Test
	public void testConstructor3() {
		new JschConnectionInfo(host, "21", userName, password);
	}

	@Test
	public void testGetterSetter() {
		JschConnectionInfo jsch = new JschConnectionInfo();
		jsch.setServerName(serverName);
		jsch.setServerOS(serverOS);
		jsch.setHost(host);
		jsch.setPort(port);
		jsch.setUserName(userName);
		jsch.setPassword(password);
		jsch.setAlc(alc);

		assertEquals(serverName, jsch.getServerName());
		assertEquals(serverOS, jsch.getServerOS());
		assertEquals(host, jsch.getHost());
		assertEquals(port, jsch.getPort());
		assertEquals(userName, jsch.getUserName());
		assertEquals(password, jsch.getPassword());
		assertEquals(alc, jsch.getAlc());
	}

	@Test
	public void testToString() {
		JschConnectionInfo jsch = new JschConnectionInfo(serverName, serverOS, host, "22", userName, password, alc);
		String expected = "JschConnectionInfo(serverName=" + jsch.getServerName() + ", serverOS=" + jsch.getServerOS()
				+ ", host=" + jsch.getHost() + ", port=" + jsch.getPort() + ", userName=" + jsch.getUserName()
				+ ", password=" + jsch.getPassword() + ", alc=" + jsch.getAlc() + ")";
		
		assertEquals(expected, jsch.toString());
	}
	
	@Test
	public void testEquals() {
		JschConnectionInfo jsch1 = new JschConnectionInfo(serverName, serverOS, host, "22", userName, password, alc);
		JschConnectionInfo jsch2 = new JschConnectionInfo(serverName, serverOS, host, "22", userName, password, alc);

		assertTrue(jsch1.equals(jsch2));
		assertTrue(jsch1.equals(jsch2));
		assertFalse(jsch1.equals(new Object()));
		
		jsch1 = null;
		assertFalse(jsch2.equals(jsch1));
		assertFalse(jsch2.equals(new Object()));
	}
	
	@Test
	public void testHashCode() {
		JschConnectionInfo jsch = new JschConnectionInfo(serverName, serverOS, host, "22", userName, password, alc);
		jsch.hashCode();
	}
}
