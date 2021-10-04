package Root.Database;


import java.sql.*;

import Root.Model.JdbcConnectionInfo;

public class DatabaseUtil implements AbstractDatabase {
	private JdbcConnectionInfo jdbcConnectionInfo;
	private DatabaseConnectionPool connPool = null;
	private boolean driverLoaded = false;
	
	/**
	 * JdbcConnectionInfo 객체를 이용한 생성자
	 * @param jdbcConnectionInfo
	 */
	public DatabaseUtil(JdbcConnectionInfo jdbcConnectionInfo){
		this.jdbcConnectionInfo = jdbcConnectionInfo;
	}
	
	public DatabaseUtil(String dbName, String driver, String jdbc_url, String id, String pw, String validationQuery) {
		jdbcConnectionInfo.setJdbcDBName(dbName);
		jdbcConnectionInfo.setJdbcDriver(driver);
		jdbcConnectionInfo.setJdbcUrl(jdbc_url);
		jdbcConnectionInfo.setJdbcId(id);
		jdbcConnectionInfo.setJdbcPw(pw);
		jdbcConnectionInfo.setJdbcValidation(validationQuery);
		jdbcConnectionInfo.setJdbcConnections(1);
	}
	
	public DatabaseUtil(String dbName, String driver, String jdbc_url, String id, String pw, String validationQuery, int connCount) {
		jdbcConnectionInfo.setJdbcDBName(dbName);
		jdbcConnectionInfo.setJdbcDriver(driver);
		jdbcConnectionInfo.setJdbcUrl(jdbc_url);
		jdbcConnectionInfo.setJdbcId(id);
		jdbcConnectionInfo.setJdbcPw(pw);
		jdbcConnectionInfo.setJdbcValidation(validationQuery);
		jdbcConnectionInfo.setJdbcConnections(connCount);
	}
	
	public JdbcConnectionInfo getJdbcConnectionInfo() {
		return jdbcConnectionInfo;
	}

	public void setJdbcConnectionInfo(JdbcConnectionInfo jdbcConnectionInfo) {
		this.jdbcConnectionInfo = jdbcConnectionInfo;
	}

	public DatabaseConnectionPool getConnPool() {
		return connPool;
	}

	public void setConnPool(DatabaseConnectionPool connPool) {
		this.connPool = connPool;
	}

	public boolean isDriverLoaded() {
		return driverLoaded;
	}

	public void setDriverLoaded(boolean driverLoaded) {
		this.driverLoaded = driverLoaded;
	}

	@Override
	public void init() {
		if(jdbcConnectionInfo.getJdbcDriver() == null || jdbcConnectionInfo.getJdbcDriver().length()==0) {
			return;
		}
		this.connPool = new DatabaseConnectionPool(jdbcConnectionInfo);
		this.connPool.createPool();
	}
	
	@Override
	public void uninit() {
		if(this.connPool != null) {
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
			conn.setAutoCommit( val );
			return true;
		}
		catch( SQLException se ) {
			
			return false;
		}
	}

	@Override
	public boolean beginTransaction(Connection conn) {
		try {
			conn.setAutoCommit( false );
			return true;
		}
		catch( SQLException se ) {
			se.printStackTrace();	
			return false;
		}
	}

	@Override
	public boolean commitTransaction(Connection conn) {
		try {
			conn.commit();
			conn.setAutoCommit( false );
			return true;
		}
		catch( SQLException se ) {
			se.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean rollback(Connection conn) {
		try {
			conn.rollback();
			conn.setAutoCommit( true );	
			return true;
		}
		catch( SQLException se ) {
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
	public static int ValidateConn(Connection conn, String validationQuery){
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
