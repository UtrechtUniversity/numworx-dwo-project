package fi.dwo.server.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * extends DbAccessLDAP als alles werkt.
 * @author wim
 *
 */
public class DbAccessScience extends DbAccess {

	public DbAccessScience() {
		System.out.println("mysql.science.uu.nl");
	}

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
		c = DriverManager.getConnection("jdbc:mysql://mysql.science.uu.nl/fisme_dwo", "fisme_dwo", "_fisme_dwo");
		return c;
	}

}
