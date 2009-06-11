package fi.dwo.server.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbAccessLocal extends DbAccess {
    
    private Connection c;

    /* (non-Javadoc)
	 * @see fi.beans.jdbc.DbConnect#close()
	 */
	public void close() {
		if(c != null)
			try {
				c.close();
			} catch (SQLException e) {
			} finally {
				c = null;
			}
	}

	public Connection getConnection() throws SQLException {
		if(c != null && !c.isClosed())
			return c;
		//c = DriverManager.getConnection("jdbc:mysql://localhost/dwo_tst", "root", "");
		c = DriverManager.getConnection("jdbc:mysql://localhost/dwo_tst", "root", "_dwo");
		return c;
	}

	/**
	 * 
	 */
	public DbAccessLocal() {
		super();
		System.err.println("Using local database!");
	}

}
