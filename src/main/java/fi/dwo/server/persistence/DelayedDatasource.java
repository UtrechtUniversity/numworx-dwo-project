package fi.dwo.server.persistence;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

public class DelayedDatasource implements DataSource {

	private volatile DataSource delegate;
	private volatile PrintWriter logWriter;
	private volatile Integer loginTimeout;
	final protected String resource;
	
	
	public DelayedDatasource(String resource) {
		this.resource = resource;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	@Override
	public synchronized void setLogWriter(PrintWriter out) throws SQLException {
		if(delegate != null) 
			delegate.setLogWriter(out);
		this.logWriter = out;
	}

	@Override
	public synchronized void setLoginTimeout(int seconds) throws SQLException {
		if(delegate != null)
			delegate.setLoginTimeout(seconds);
		this.loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		if(loginTimeout == null && delegate != null)
			loginTimeout = delegate.getLoginTimeout();
		if(loginTimeout == null)
			throw new UnsupportedOperationException("unsupported");
		return loginTimeout;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(delegate == null)
			throw new SQLException("not found");
		return delegate.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(delegate == null)
			return false;
		return delegate.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return getDelegate().getConnection();
		} catch (SQLException e) {
			delegate = null;
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getConnection failed", e);
			throw e;
		} catch (RuntimeException e) {
			delegate = null;
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "getConnection failed", e);
			throw e;
		}
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		try {
			return getDelegate().getConnection(username, password);
		} catch (SQLException e) {
			synchronized(this) { delegate = null; }
			throw e;
		}
	}

	synchronized DataSource getDelegate() throws SQLException {
		if(delegate == null)
			initializeDelegate();
		return delegate;
	}

	protected void initializeDelegate() throws SQLException {
		Context initContext;
		try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            try {
        	   initContext = new InitialContext();
            }
            finally {
               Thread.currentThread().setContextClassLoader(cl);
            }
			DataSource ds = lookup(resource, initContext);
			if( ds  == null) {
			// voor TOMCAT: java:/comp/env/...
				ds = lookup("java:/comp/env/"+resource, initContext);
			}
			if( ds  == null) {
				// voor OSGI,   osgi:service/
				ds = lookup("osgi:service/"+resource, initContext);
			}
			initContext.close();
			if (ds == null) 
				throw new SQLException("Datasource " + resource + " not found");
			setDelegate(ds);
		} catch (NamingException e) {
			throw new SQLException(e.getMessage(), e);
		}		
	}

	private void setDelegate(DataSource delegate) throws SQLException {
		this.delegate = null;
		if (new DataSourceAccess(delegate).checkVersion() )
			throw new SQLException("Datasource invalid version, restart tomcat once fixed");
		this.delegate = delegate;
	}

	private DataSource lookup(String source, Context initContext)
	{
		try {
			return (DataSource)initContext.lookup(source);
		} catch (NamingException e) {
			return null;
		}
	}

	/**
	 * Stub method. Appears in java 1.7
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 * @since 1.7
	 */
	public Logger getParentLogger() {
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	}

}
