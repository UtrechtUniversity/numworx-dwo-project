/*
 * Created on Nov 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpc;

import com.jamonapi.proxy.MonProxyFactory;

import fi.beans.jdbc.DbConnectIF;
import fi.dwo.client.persistence.DbAccessIF;

/**
 * Servlet met interface naar de LDAP variant van DbAccess
 * @author Wim
 * @web.servlet
 *   name="DbAccessLdap"
 *   description="Db Access met LDAP extensies"
 * @web.servlet-mapping
 *   url-pattern=/dbaccessldap
 */
public class DbAccessLdapServlet extends DbAccessServlet
{

	static class Wrapper extends DbAccessProxy {

		protected DbAccessIF getDelegate() {
			return wrap;
		}

		DbAccessIF wrap;
		DbConnectIF connector;
		
		Wrapper( Object object) {
			wrap = (DbAccessIF) object;
			connector = (DbConnectIF) object;
		}

		public void close() {
			connector.close();
		}
	}
	
	static class MonitoringProxy extends DbAccessProxy {

		protected DbAccessIF createDelegate() {

			return new DbAccessLdap() {
        		
        		private Connection mine, his;
				public void close() {
					if(mine != null)
					{
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
					} 
					return mine;
				} };
		}
	}
	
	
	
	
    private static final long serialVersionUID = 1L;
    /**
     * Null constructor. Attach DbAccessLdap aan de DbAccessServlet.
     * @see DbAccessLdap
     * @see DbAccessServlet
     */
    public DbAccessLdapServlet() {
        super(new DbAccessProxy());
        unLock();
    }
    
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
        if(!"false".equals(getInitParameter("monitor")))
        {
        	setHandler(/*new Wrapper*/(MonProxyFactory.monitor(new MonitoringProxy())));
        	log("monitoring");
        } else {
        	log("no monitoring");
        }
        int maxthreads = 200;
        String param = getInitParameter("xmlrpc.maxthreads");
        if(param != null )
        	maxthreads = Integer.parseInt(param);
        XmlRpc.setMaxThreads(maxthreads);
    }

	public void destroy() {
		//super.destroy();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try { 
			super.service(req, resp);
		} catch (RuntimeException re) {
			log("runtime exception " + re, re);
			throw re;
		} finally { 
			((DbConnectIF) getHandler()).close();
		}
	}

	
	
}
