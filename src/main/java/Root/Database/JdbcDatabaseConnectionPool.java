package Root.Database;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import Root.Model.JdbcConnectionInfo;
import lombok.extern.slf4j.Slf4j;

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
			Class.forName(this.jdbc.getJdbcDriver());
			driverLoaded = true;
		} catch (ClassNotFoundException e) {
			log.error("Database connection error caused by Jdbc Driver NOT FOUND");
		}
	}

	@Override
	public void createPool() {
		loadDriver();

		if (pool == null) {
			pool = new ConcurrentHashMap<ConnMap, Short>();
		} else {
			destroyPool();
		}
		if (this.busyConn != null) {
			this.busyConn.clear();
		} else {
			this.busyConn = new ConcurrentHashMap<Connection, ConnMap>();
		}

		ConnMap cm = null;
		Connection conn = null;
		for (int i = 0; i < this.jdbc.getJdbcConnections(); i++) {
			try {
				conn = DriverManager.getConnection(this.jdbc.getJdbcUrl(), this.jdbc.getJdbcId(),
						this.jdbc.getJdbcPw());
			} catch (Exception e) {
				e.printStackTrace();
			}
			cm = new ConnMap(conn);
			if (conn != null)
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_FREE);
			else
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
		}
	}

	@Override
	public void destroyPool() {
		Iterator<ConnMap> it = pool.keySet().iterator();
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
				if (cm.conn != null) {
					boolean ac = cm.conn.getAutoCommit();
					if (ac == false) {
						cm.conn.rollback();
					}
					cm.conn.close();
					cm.conn = null;
				}
			} catch (Exception e) {

			}
		}
		this.pool.clear();
	}

	@Override
	public synchronized Connection getConn() {
		Iterator<ConnMap> it = pool.keySet().iterator();
		Connection conn = null;
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				if (pool.get(cm) == JdbcDatabaseConnectionPool.CONN_STATUS_FREE) {
					if (JdbcDatabaseConnectionPool.validateConn(cm.conn, jdbc.getJdbcValidation()) != 1) {
						try {
							cm.conn = DriverManager.getConnection(this.jdbc.getJdbcUrl(), this.jdbc.getJdbcId(),
									this.jdbc.getJdbcPw());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (cm.conn != null) {
						pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_BUSY);
						conn = cm.conn;
						addBusyConn(conn, cm);
						break;
					} else {
						pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_NULL);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	@Override
	public synchronized void freeConn(Connection conn) {
		try {
			ConnMap cm = this.busyConn.remove(conn);
			if (cm == null) {
			} else {
				this.pool.put(cm, JdbcDatabaseConnectionPool.CONN_STATUS_FREE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getFreeConnectionCount() {
		int ret = 0;
		Iterator<ConnMap> it = pool.keySet().iterator();
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				if (this.pool.get(cm) == JdbcDatabaseConnectionPool.CONN_STATUS_FREE) {
					ret++;
				}
			} catch (Exception e) {

			}
		}
		return ret;
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
	public static int validateConn(Connection conn, String validationQuery) {
		int ret = -1;
		if (conn == null)
			return ret;

		Statement stmt = null;
		try {
			if (conn.isClosed() == true) {
				return ret;
			}
			if (conn.isValid(3) == false) {
				return ret;
			}
			stmt = conn.createStatement();
			stmt.execute(validationQuery);
			ret = 1;
		} catch (Exception e) {
			ret = -1;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return ret;
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
