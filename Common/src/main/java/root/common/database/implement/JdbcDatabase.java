package root.common.database.implement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Data;
import root.common.database.contracts.AbstractDatabase;

// TODO Builder Pattern �����ϱ�
/**
 * Jdbc Driver�� �̿��� DBMS�� ������ �����ϴ� Class
 * 
 * @author DKY
 *
 */
@Data
public class JdbcDatabase implements AbstractDatabase {
	private JdbcConnectionInfo jdbc;
	private JdbcDatabaseConnectionPool connPool = null;

	public JdbcDatabase(JdbcConnectionInfo jdbcConnectionInfo) {
		this.jdbc = jdbcConnectionInfo;
	}

	@Override
	public String getName() {
		if (jdbc == null) {
			return "";
		}
		return this.jdbc.getJdbcDBName();
	}

	@Override
	public void init() {
		if (jdbc.getJdbcDriver() == null || jdbc.getJdbcDriver().length() == 0) {
			return;
		}
		this.connPool = new JdbcDatabaseConnectionPool(jdbc);
		this.connPool.createPool();
	}

	@Override
	public void uninit() {
		if (this.connPool != null) {
			this.connPool.destroyPool();
		}
	}

	@Override
	public Connection getConn() {
		return this.connPool.getConn();
	}

	@Override
	public void freeConn(Connection conn) {
		this.connPool.freeConn(conn);
	}

	@Override
	public boolean setAutoCommit(Connection conn, boolean val) {
		try {
			conn.setAutoCommit(val);
			return true;
		} catch (SQLException se) {
			return false;
		}
	}

	@Override
	public boolean beginTransaction(Connection conn) {
		try {
			conn.setAutoCommit(false);
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean commitTransaction(Connection conn) {
		try {
			conn.commit();
			conn.setAutoCommit(false);
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean rollback(Connection conn) {
		try {
			conn.rollback();
			conn.setAutoCommit(true);
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	/**
	 * Check if {@code conn} is valid with {@code validationQuery}
	 * 
	 * @param conn            the connection instance to check
	 * @param validationQuery the query to be executed with the connection
	 * @return 1 if valid, else return -1
	 */
	public static boolean validateConn(Connection conn, String validationQuery) {
		if (conn == null) {
			return false;
		}

		try (Statement statement = conn.createStatement()) {
			if (conn.isClosed() || !conn.isValid(3)) {
				return false;
			}
			statement.execute(validationQuery);
		} catch (SQLException e) {
			return false;
		}

		return true;
	}
}
