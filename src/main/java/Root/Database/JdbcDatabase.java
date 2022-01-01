package Root.Database;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import Root.Model.JdbcConnectionInfo;
import lombok.Data;

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
	private boolean driverLoaded = false;
	
	public JdbcDatabase(JdbcConnectionInfo jdbcConnectionInfo){
		this.jdbc = jdbcConnectionInfo;
	}
	
	public JdbcDatabase(String dbName, String driver, String jdbcUrl, String id, String pw, String validationQuery) {
		this.jdbc = new JdbcConnectionInfo(dbName, driver, jdbcUrl, id, pw, validationQuery,1);
	}
	
	public JdbcDatabase(String dbName, String driver, String jdbcUrl, String id, String pw, String validationQuery, int connCount) {
		this.jdbc = new JdbcConnectionInfo(dbName, driver, jdbcUrl, id, pw, validationQuery, connCount);
	}

	@Override
	public void init() {
		if (jdbc.getJdbcDriver() == null || jdbc.getJdbcDriver().isBlank()) {
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
	 * @param conn	the connection instance to check
	 * @param validationQuery	the query to be executed with the connection
	 * @return 1 if valid, else return -1
	 */
	public static int validateConn(Connection conn, String validationQuery){
		int ret = -1;
		if(conn==null) return ret;
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			stmt.execute(validationQuery);
			ret = 1;
		}catch(Exception e){
			ret = -1;
		}finally{
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return ret;
	}
}
