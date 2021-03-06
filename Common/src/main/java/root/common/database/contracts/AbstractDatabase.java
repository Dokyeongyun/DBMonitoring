package root.common.database.contracts;

import java.sql.Connection;

/**
 * A Class about Database Connection And Transaction
 * 
 * @Date	2021.09.29.
 * @author	aservmz
 *
 */
public interface AbstractDatabase {
	
	String getName();

	public void init();
	
	public void uninit();
	
	public Connection getConn();
	
	public void freeConn(Connection conn);
	
	/**
	 * Sets autocommit status of the connection
	 * @param val	value to set
	 * @return true when succeeded, false when failed
	 */
	public boolean setAutoCommit(Connection conn, boolean val);

	/**
	 * Start transaction
	 * @return	true when succeeded, false when failed
	 */
	public boolean beginTransaction(Connection conn);
	
	/**
	 * Commit transaction
	 * @return	true when succeeded, false when failed
	 */
	public boolean commitTransaction(Connection conn);

	/**
	 * Rollback transaction
	 * @return	true when succeeded, false when failed
	 */
	public boolean rollbackTransaction(Connection conn);
	
}
