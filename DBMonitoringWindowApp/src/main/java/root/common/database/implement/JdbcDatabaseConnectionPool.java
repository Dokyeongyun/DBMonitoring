package root.common.database.implement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import root.common.database.contracts.AbstractDatabaseConnectionPool;
import root.core.domain.JdbcConnectionInfo;

@Slf4j
public class JdbcDatabaseConnectionPool implements AbstractDatabaseConnectionPool {

	public static final short CONN_STATUS_NULL = -1;
	public static final short CONN_STATUS_FREE = 0;
	public static final short CONN_STATUS_BUSY = 1;

	// synchronized connection pool
	private ConcurrentHashMap<ConnMap, Short> pool = null;
	private ConcurrentHashMap<Connection, ConnMap> busyConn = null;
	private JdbcConnectionInfo jdbc;
	private boolean driverLoaded = false;

	public JdbcDatabaseConnectionPool(JdbcConnectionInfo jdbc) {
		this.jdbc = jdbc;
		this.driverLoaded = false;
	}

	@Override
	public void loadDriver() {
		try {
			if (driverLoaded) {
				return;
			}
			Class.forName(jdbc.getJdbcDriver());
			driverLoaded = true;
		} catch (ClassNotFoundException e) {
			log.error("Database connection error caused by Jdbc Driver NOT FOUND");
		}
	}

	@Override
	public void createPool() {
		// Load database driver.
		loadDriver();

		// Use existing objects without creating new objects.
		if (pool == null) {
			pool = new ConcurrentHashMap<ConnMap, Short>();
		} else {
			destroyPool();
		}

		if (busyConn == null) {
			busyConn = new ConcurrentHashMap<Connection, ConnMap>();
		} else {
			busyConn.clear();
		}

		// Create database connections and Save in the database pool.
		ConnMap cm = null;
		Connection conn = null;
		for (int i = 0; i < jdbc.getJdbcConnections(); i++) {
			try {
				conn = DriverManager.getConnection(jdbc.getJdbcUrl(), jdbc.getJdbcId(), jdbc.getJdbcPw());
			} catch (SQLException e) {
				log.error("Database getConnection Failed");
			}
			cm = new ConnMap(conn);
			if (conn == null) {
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
			} else {
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_FREE);
			}
		}
	}

	@Override
	public void destroyPool() {
		for (ConnMap cm : pool.keySet()) {
			try {
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
				if (cm.conn != null) {
					if (!cm.conn.getAutoCommit()) {
						cm.conn.rollback();
					}
					cm.conn.close();
					cm.conn = null;
				}
			} catch (SQLException e) {
			}
		}
		pool.clear();
	}

	@Override
	public synchronized Connection getConn() {
		Connection conn = null;
		for (ConnMap cm : pool.keySet()) {
			if (pool.get(cm) == JdbcDatabaseConnectionPool.CONN_STATUS_FREE) {
				if (!validateConn(cm.conn, jdbc.getJdbcValidation())) {
					try {
						cm.conn = DriverManager.getConnection(jdbc.getJdbcUrl(), jdbc.getJdbcId(), jdbc.getJdbcPw());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (cm.conn == null) {
					pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
				} else {
					pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_BUSY);
					conn = cm.conn;
					addBusyConn(conn, cm);
					break;
				}

			}
		}
		return conn;
	}

	@Override
	public synchronized void freeConn(Connection conn) {
		ConnMap cm = busyConn.remove(conn);
		if (cm != null) {
			pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_FREE);
		}
	}

	public int getFreeConnectionCount() {
		int result = 0;
		for (ConnMap cm : pool.keySet()) {
			if (pool.get(cm) == JdbcDatabaseConnectionPool.CONN_STATUS_FREE) {
				result++;
			}
		}
		return result;
	}

	private synchronized void addBusyConn(Connection conn, ConnMap cm) {
		if (busyConn != null) {
			busyConn.put(conn, cm);
		}
	}

	/**
	 * Check if {@code conn} is valid with {@code validationQuery}
	 * 
	 * @param conn            the connection instance to check
	 * @param validationQuery the query to be executed with the connection
	 * @return 1 if valid, else return -1
	 */
	public boolean validateConn(Connection conn, String validationQuery) {
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

	/**
	 * A container to hold a connection and its status
	 * 
	 * @author wonk
	 *
	 */
	public static class ConnMap {
		public Connection conn;

		public ConnMap(Connection conn) {
			this.conn = conn;
		}
	}

}
