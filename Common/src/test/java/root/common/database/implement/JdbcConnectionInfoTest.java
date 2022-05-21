package root.common.database.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JdbcConnectionInfoTest {

	public static String jdbcDBName = "Test";
	public static String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
	public static String jdbcUrl = "jdbc:oracle:thin:@111.111.111.111:1521/DBSID";
	public static String jdbcId = "dbid";
	public static String jdbcPw = "dbpw";
	public static String jdbcValidation = "select 1 from dual";
	public static int jdbcConnections = 10;

	public static String jdbcSID = "SID";
	public static String jdbcHost = "111.111.111.111";
	public static String jdbcPort = "1521";
	public static String jdbcOracleDriver = "oracle.jdbc.driver.OracleDriver";

	@Test
	public void testConstructor1() {
		new JdbcConnectionInfo();
	}

	@Test
	public void testConstructor2() {
		new JdbcConnectionInfo(jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation, jdbcConnections);
	}

	@Test
	public void testConstructor3() {
		new JdbcConnectionInfo(jdbcDBName, jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation, jdbcConnections);
	}

	@Test
	public void testGetterSetter() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo();
		jdbc.setJdbcDBName(jdbcDBName);
		jdbc.setJdbcDriver(jdbcDriver);
		jdbc.setJdbcUrl(jdbcUrl);
		jdbc.setJdbcId(jdbcId);
		jdbc.setJdbcPw(jdbcPw);
		jdbc.setJdbcValidation(jdbcValidation);
		jdbc.setJdbcConnections(jdbcConnections);
		jdbc.setJdbcSID(jdbcSID);
		jdbc.setJdbcHost(jdbcHost);
		jdbc.setJdbcPort(jdbcPort);
		jdbc.setJdbcOracleDriver(jdbcOracleDriver);

		assertEquals(jdbcDBName, jdbc.getJdbcDBName());
		assertEquals(jdbcDriver, jdbc.getJdbcDriver());
		assertEquals(jdbcUrl, jdbc.getJdbcUrl());
		assertEquals(jdbcId, jdbc.getJdbcId());
		assertEquals(jdbcPw, jdbc.getJdbcPw());
		assertEquals(jdbcValidation, jdbc.getJdbcValidation());
		assertEquals(jdbcConnections, jdbc.getJdbcConnections());
		assertEquals(jdbcSID, jdbc.getJdbcSID());
		assertEquals(jdbcHost, jdbc.getJdbcHost());
		assertEquals(jdbcPort, jdbc.getJdbcPort());
		assertEquals(jdbcOracleDriver, jdbc.getJdbcOracleDriver());
	}

	@Test
	public void testToString() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo(jdbcDBName, jdbcDriver, jdbcUrl, jdbcId, jdbcPw,
				jdbcValidation, jdbcConnections);
		String expected = "JdbcConnectionInfo(jdbcDBName=" + jdbc.getJdbcDBName() + ", jdbcDriver="
				+ jdbc.getJdbcDriver() + ", jdbcUrl=" + jdbc.getJdbcUrl() + ", jdbcId=" + jdbc.getJdbcId() + ", jdbcPw="
				+ jdbc.getJdbcPw() + ", jdbcValidation=" + jdbc.getJdbcValidation() + ", jdbcConnections="
				+ jdbc.getJdbcConnections() + ", jdbcSID=" + jdbc.getJdbcSID() + ", jdbcHost=" + jdbc.getJdbcHost()
				+ ", jdbcPort=" + jdbc.getJdbcPort() + ", jdbcOracleDriver=" + jdbc.getJdbcOracleDriver() + ")";
		assertEquals(expected, jdbc.toString());
	}

	@Test
	public void testEquals() {
		JdbcConnectionInfo jdbc1 = new JdbcConnectionInfo(jdbcDBName, jdbcDriver, jdbcUrl, jdbcId, jdbcPw,
				jdbcValidation, jdbcConnections);
		JdbcConnectionInfo jdbc2 = new JdbcConnectionInfo(jdbcDBName, jdbcDriver, jdbcUrl, jdbcId, jdbcPw,
				jdbcValidation, jdbcConnections);
		assertTrue(jdbc1.equals(jdbc1));
		assertTrue(jdbc1.equals(jdbc2));
		assertFalse(jdbc1.equals(new Object()));
		
		jdbc1 = null;
		assertFalse(jdbc2.equals(jdbc1));
		assertFalse(jdbc2.equals(new Object()));
	}
	
	@Test
	public void testHashCode() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo(jdbcDBName, jdbcDriver, jdbcUrl, jdbcId, jdbcPw,
				jdbcValidation, jdbcConnections);
		jdbc.hashCode();
	}
}
