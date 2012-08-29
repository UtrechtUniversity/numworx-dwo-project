package fi.dwo.server.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

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
		//c = DriverManager.getConnection("jdbc:mysql://localhost/dwo_tst", "root", "_dwo");
		c = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
		return c;
	}

	private void setupPool() {
		//
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        //
        ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory("jdbc:mysql://localhost/dwo_tst", "root", "_dwo");

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool =
            new GenericObjectPool();
        //
        // Next, we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);


        //
        // Finally, we create the PoolingDriver itself...
        //
        try {
			Class.forName("org.apache.commons.dbcp.PoolingDriver");
	        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

	        //
	        // ...and register our pool with it.
	        //
	        driver.registerPool("example",connectionPool);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //
        // Now we can just use the connect string "jdbc:apache:commons:dbcp:example"
        // to access our pool of Connections.
        //

	}
	/**
	 * 
	 */
	public DbAccessLocal() {
		super();
		System.err.println("Using local database!");
		
		setupPool();
		
	}

}
