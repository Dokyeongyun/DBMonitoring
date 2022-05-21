package root.common.database.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JdbcDatabaseTest {

	public static String jdbcDriver;
	public static String jdbcUrl;
	public static String jdbcId;
	public static String jdbcPw;
	public static String jdbcValidation;
	public static int jdbcConnections;

	public static JdbcConnectionInfo jdbc;
	public static JdbcDatabase db;

	@BeforeAll
	public static void setup() {

		// Load db config
		try {
			String str = Files.readString(Paths.get("src/test/resources/dbconfig.txt"));
			String[] split = str.split(",");
			Map<String, String> configMap = new HashMap<>();
			for (String s : split) {
				String[] split2 = s.split("=");
				configMap.put(split2[0], split2[1]);
			}

			jdbcDriver = configMap.get("jdbcDriver");
			jdbcUrl = configMap.get("jdbcUrl");
			jdbcId = configMap.get("jdbcId");
			jdbcPw = configMap.get("jdbcPw");
			jdbcValidation = configMap.get("jdbcValidation");
			jdbcConnections = Integer.parseInt(configMap.get("jdbcConnections"));

		} catch (IOException e) {
		}

		jdbc = new JdbcConnectionInfo(jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation, jdbcConnections);
		db = new JdbcDatabase(jdbc);
		db.init();
	}

	@Test
	public void testGetName_NameIsNull() {
		jdbc.setJdbcDBName(null);
		String result = db.getName();
		assertNull(result);
	}

	@Test
	public void testGetName_JdbcIsNull() {
		JdbcDatabase db = new JdbcDatabase(null);
		String result = db.getName();
		assertEquals("", result);
	}

	@Test
	public void testInit_DriverIsNull() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo();
		JdbcDatabase db = new JdbcDatabase(jdbc);
		db.init();
		assertThrows(NullPointerException.class, () -> db.getConn());
	}

	@Test
	public void testUnInit() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo(jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation,
				jdbcConnections);
		JdbcDatabase db = new JdbcDatabase(jdbc);
		db.init();
		assertNotNull(db.getConn());

		db.uninit();
		assertNull(db.getConn());
	}

	@Test
	public void testSetAutoCommit() throws SQLException {
		Connection conn = db.getConn();
		assertTrue(db.setAutoCommit(conn, true));
		assertTrue(conn.getAutoCommit());
		assertTrue(db.setAutoCommit(conn, false));
		assertFalse(conn.getAutoCommit());
		db.freeConn(conn);
	}

	@Test
	public void testSetAutoCommit_ConnIsNull() throws SQLException {
		Connection conn = null;
		assertFalse(db.setAutoCommit(conn, true));
	}

	@Test
	public void testBeginTransaction() throws SQLException {
		Connection conn = db.getConn();
		assertTrue(db.beginTransaction(conn));
		assertFalse(conn.getAutoCommit());
	}

	@Test
	public void testBeginTransaction_ConnIsNull() {
		Connection conn = null;
		assertFalse(db.beginTransaction(conn));
	}

	@Test
	public void testCommitTransaction() throws SQLException {
		Connection conn = db.getConn();
		assertTrue(db.commitTransaction(conn));
		assertFalse(conn.getAutoCommit());
	}

	@Test
	public void testCommitTransaction_ConnIsNull() {
		Connection conn = null;
		assertFalse(db.commitTransaction(conn));
	}

	@Test
	public void testRollbackTransaction() throws SQLException {
		Connection conn = db.getConn();
		assertTrue(db.rollbackTransaction(conn));
		assertTrue(conn.getAutoCommit());
	}

	@Test
	public void testRollbackTransaction_ConnIsNull() {
		Connection conn = null;
		assertFalse(db.rollbackTransaction(conn));
	}

	@Test
	public void testValidateConn() {
		Connection conn = db.getConn();
		assertTrue(JdbcDatabase.validateConn(conn, "select 1 from dual"));
	}

	@Test
	public void testValidateConn_ConnWasClosed() throws SQLException {
		Connection conn = db.getConn();
		conn.close();
		assertFalse(JdbcDatabase.validateConn(conn, "select 1 from dual"));
	}

	@Test
	public void testValidateConn_ConnIsNull() {
		Connection conn = null;
		assertFalse(JdbcDatabase.validateConn(conn, "select 1 from dual"));
	}
}
