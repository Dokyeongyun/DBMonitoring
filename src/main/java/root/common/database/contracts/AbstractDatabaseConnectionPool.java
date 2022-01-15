package root.common.database.contracts;

import java.sql.Connection;

public interface AbstractDatabaseConnectionPool {

	/**
	 * Load database driver.
	 */
	void loadDriver();
	
	/**
	 * Create a database pool.
	 */
	void createPool();
	
	/**
	 * Get a Connection object from the free database pool.
	 * At the same time, add connection in the busy database pool.
	 * 
	 * @return Database Connection Object
	 */
	Connection getConn();
	
	/**
	 * Change the state of the connection from the busy to free.
	 * 
	 * @param conn
	 */
	void freeConn(Connection conn);
	
	/**
	 * Closes the connections within the pool.
	 */
	void destroyPool();
}
