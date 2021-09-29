package Root.Database;


import java.sql.*;

import Root.Model.JdbcConnectionInfo;

public class DatabaseUtil implements AbstractDatabase {
	public String driver = "";
	public String jdbc_url = "";
	public String id = "";
	public String pw = "";
	public String validationQuery = "";
	public int connCount = 1;
	public DatabaseConnectionPool connPool = null;
	public boolean driverLoaded = false;
	
	public DatabaseUtil(JdbcConnectionInfo jdbcConnectionInfo){
		this.driver = jdbcConnectionInfo.getJdbcDriver();
		this.jdbc_url = jdbcConnectionInfo.getJdbcUrl();
		this.id = jdbcConnectionInfo.getJdbcId();
		this.pw = jdbcConnectionInfo.getJdbcPw();
		this.validationQuery = jdbcConnectionInfo.getJdbcValidation();
		this.connCount = Integer.parseInt(jdbcConnectionInfo.getJdbcConnections());
	}
	
	public DatabaseUtil(String driver, String jdbc_url, String id, String pw, String validationQuery) {
		this.driver = driver;
		this.jdbc_url = jdbc_url;
		this.id = id;
		this.pw = pw;
		this.validationQuery = validationQuery;
		this.connCount = 1;
	}
	
	public DatabaseUtil(String driver, String jdbc_url, String id, String pw, String validationQuery, int connCount) {
		this.driver = driver;
		this.jdbc_url = jdbc_url;
		this.id = id;
		this.pw = pw;
		this.validationQuery = validationQuery;
		this.connCount = connCount;
	}

	@Override
	public void init() {
		if(driver == null || driver.length()==0) {
			return;
		}
		this.connPool = new DatabaseConnectionPool(connCount, driver, jdbc_url, id, pw, validationQuery);
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
