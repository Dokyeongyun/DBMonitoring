package Root.Database;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseConnectionPool {
	/**
	 * Check if {@code conn} is valid with {@code validationQuery}
	 * 
	 * @param conn            the connection instance to check
	 * @param validationQuery the query to be executed with the connection
	 * @return 1 if valid, else return -1
	 */
	public static int ValidateConn(Connection conn, String validationQuery) {
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

	public static final short CONN_STATUS_NULL = -1;
	public static final short CONN_STATUS_FREE = 0;
	public static final short CONN_STATUS_BUSY = 1;

	// synchronized connection pool
	private ConcurrentHashMap<ConnMap, Short> pool = null;
	private ConcurrentHashMap<Connection, ConnMap> busyConn = null;

	private synchronized void addBusyConn(Connection conn, ConnMap cm) {
		if (busyConn != null)
			busyConn.put(conn, cm);
	}

	private int connCount = 1;
	private String driver = "";
	private String jdbc_url = "";
	private String id = "";
	private String pw = "";
	private String validation_query = "";
	public boolean driverLoaded = false;

	public DatabaseConnectionPool(int connCount, String driver, String jdbc_url, String id, String pw, String validation_query) {
			this.connCount = connCount;
			this.driver = driver;
			this.jdbc_url = jdbc_url;
			this.id = id;
			this.pw = pw;
			this.validation_query = validation_query;
			this.driverLoaded = false;
		}

	private void loadDriver() {
		try {
			if (driverLoaded)
				return;
			Class.forName(this.driver);
			driverLoaded = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

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
		for (int i = 0; i < this.connCount; i++) {
			try {
				conn = DriverManager.getConnection(this.jdbc_url, this.id, this.pw);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cm = new ConnMap(conn);
			if (conn != null)
				pool.put(cm, DatabaseConnectionPool.CONN_STATUS_FREE);
			else
				pool.put(cm, DatabaseConnectionPool.CONN_STATUS_NULL);
		}
	}

	/**
	 * CLoses the connections within the pool
	 */
	public void destroyPool() {
		Iterator<ConnMap> it = pool.keySet().iterator();
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				pool.put(cm, DatabaseConnectionPool.CONN_STATUS_NULL);
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

	public synchronized Connection getConn() {
		Iterator<ConnMap> it = pool.keySet().iterator();
		Connection conn = null;
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				if (pool.get(cm) == DatabaseConnectionPool.CONN_STATUS_FREE) {
					if (DatabaseConnectionPool.ValidateConn(cm.conn, validation_query) != 1) {
						try {
							cm.conn = DriverManager.getConnection(this.jdbc_url, this.id, this.pw);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (cm.conn != null) {
						pool.put(cm, DatabaseConnectionPool.CONN_STATUS_BUSY);
						conn = cm.conn;
						addBusyConn(conn, cm);
						break;
					} else {
						pool.put(cm, DatabaseConnectionPool.CONN_STATUS_NULL);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	public synchronized void freeConn(Connection conn) {

		try {
			ConnMap cm = this.busyConn.remove(conn);
			if (cm == null) {
			} else {
				this.pool.put(cm, DatabaseConnectionPool.CONN_STATUS_FREE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNoOfFreeConn() {
		int ret = 0;
		Iterator<ConnMap> it = pool.keySet().iterator();
		ConnMap cm = null;
		while (it.hasNext()) {
			try {
				cm = it.next();
				if (this.pool.get(cm) == DatabaseConnectionPool.CONN_STATUS_FREE) {
					ret++;
				}
			} catch (Exception e) {

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
