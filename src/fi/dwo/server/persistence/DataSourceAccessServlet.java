package fi.dwo.server.persistence;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.xmlrpc.XmlRpc;

import com.jamonapi.proxy.MonProxyFactory;

import fi.beans.jdbc.DbConnectIF;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.VERSION;
import fi.dwo.client.persistence.DbAccessIF;

public class DataSourceAccessServlet extends Servlet {
	private DataSource ds;
	private boolean monitor;
	private boolean threading;
	
	
	static class MonitorDataSourceAccess extends DataSourceAccess {
		
		static private int count;
		
		public MonitorDataSourceAccess(DataSource ds) {
			super(ds);
		}

		private Connection mine, his;
		public void close() {
			if(mine != null)
			{   --count;
				if(count > 10) System.out.println(System.currentTimeMillis()+ " dwo access close " + count);
				try {
					mine.close();
				} catch (SQLException e) {
					log(this + " close " + e);
				}
			}
			mine = null; his = null;
			super.close();
		}

		public Connection getConnection() throws SQLException {
			Connection c = super.getConnection();
			if(c != his || mine == null) 
			{	
				his = c;
				mine = MonProxyFactory.monitor(c);
				++count;
				if(count > 10) System.out.println(System.currentTimeMillis()+ " dwo access connect " + count);
			} 
			return mine;
		}
	};

	static class DataSourceProxy extends DbAccessProxy {

		DataSource ds;
		
		protected DbAccessIF createDelegate() {
			return new DataSourceAccess(ds);
		}

		DataSourceProxy(DataSource ds) {
			this.ds = ds;
		}

		
	}

	static class MonitoringProxy extends DbAccessProxy {

		DataSource ds;
		protected DbAccessIF createDelegate() {
			return new MonitorDataSourceAccess(ds);
		}

		MonitoringProxy(DataSource ds) {
			this.ds = ds;
		}
		
		
	}

	
	/* (non-Javadoc)
	 * @see fi.dwo.server.persistence.DbAccessServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

        log("Initializatie r" + VERSION.REVISION);
        int maxthreads = 200;
        String param = getInitParameter("xmlrpc.maxthreads");
        if(param != null )
        	maxthreads = Integer.parseInt(param);
        XmlRpc.setMaxThreads(maxthreads);
		
		String source = getInitParameter("datasource");
		monitor = ! "false".equals (getInitParameter("monitor"));
		threading = "true".equals(getInitParameter("threading"));
		
		log( "monitoring = " + monitor + ", threading = " + threading);
		try {
// find datasource from tomcat
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup(source);
			if(ds == null) {
				throw new ServletException("Resource " + source + " is null");
			} else log("found datasource " + ds);

//			try {
//				new com.mysql.jdbc.Driver();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
// CHECK version here:
//			if (new DataSourceAccess(ds).checkVersion())
//				throw new ServletException("Datasource invalid version");
			
			DbAccessIF dbaccess;
			if (threading)
			{
				if(monitor)
					dbaccess = new MonitoringProxy(ds);
				else
					dbaccess = new DataSourceProxy(ds);
				unLock();
			}
			else 
			{
				if(monitor)
					dbaccess = (DbAccessIF) MonProxyFactory.monitor(new MonitorDataSourceAccess(ds));
				else
					dbaccess = new DataSourceAccess(ds);
			}
			
			setHandler(dbaccess);

		} catch (NamingException e) {
			throw new ServletException("Datasource in error",e);
		}

	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		out.println(this);
		out.println(getHandler());
		out.println(ds);
		out.println("monitor = " + monitor);
		out.println("threading = " + threading);
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try { 
			super.service(req, resp);
		} catch (RuntimeException re) {
			log("runtime exception " + re, re);
			throw re;
		} finally { 
			try {
				((DbConnectIF) getHandler()).close();
			} catch (Exception e) {
				log("finally close failed", e);
			}
		}
	}

    public void destroy() {
        log("En weg ben ik...");
        ((DbConnectIF) getHandler()).close();
        super.destroy();
    }

}
