/*
 * Created on Nov 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.jamonapi.proxy.MonProxyFactory;

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

	static class MonitoringProxy extends DbAccessProxy {

		protected DbAccessIF createDelegate() {

			return new DbAccessLdap() {
        		
        		private Connection mine, his;
				public void close() {
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
        	setHandler(MonProxyFactory.monitor(new MonitoringProxy()));
        }
        
    }

	public void destroy() {
		// TODO Auto-generated method stub
		//super.destroy();
	}

}
