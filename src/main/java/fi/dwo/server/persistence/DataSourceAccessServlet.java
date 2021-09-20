package fi.dwo.server.persistence;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.xmlrpc.XmlRpc;

//import com.jamonapi.proxy.MonProxyFactory;

import fi.beans.jdbc.DbConnectIF;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.HashMap;

/**
 * Supplies doGet for database status info and database-operations via doPost
 * using an XML-RPC handler.
 *
 */
public class DataSourceAccessServlet extends Servlet {
	private DelayedDatasource ds;
	private final boolean monitor = false;
	private boolean threading;
	private ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
	private ThreadLocal<PersistentUser> user = new ThreadLocal<>();
	
    private static final Logger LOG = Logger.getLogger(DataSourceAccessServlet.class.getName());

    class DataSourceProxy extends DbAccessProxy {

        DataSource ds;

        @Override
        protected DbAccessIF createDelegate() {
            return new DataSourceAccess(ds) {

              @Override
              protected PersistentUser getUser() {
                return user.get();
              }
           };
        }

        DataSourceProxy(DataSource ds) {
            this.ds = ds;
        }

    }

    /**
     * Initializes the xmlrpc servlet.
     *
     * Retrieves a database interface,
     * {@link fi.dwo.commons.persistence.DBAccessIF} via the context.xml
     * parameter. Encapsulates the connector via monitoring and/or threading of
     * servlets.
     *
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LOG.log(Level.SEVERE,"rds: {0} port: {1}", new Object[]{System.getProperty("RDS_HOSTNAME","onbekend"),System.getProperty("RDS_PORT","onbekend")});
        String buildnumber = getInitParameter("buildnumber");
        String projectVersion = getInitParameter("projectVersion");
        LOG.log(Level.INFO, "Software version, buildnumber: {0}, {1}", new Object[]{projectVersion, buildnumber});

        int maxthreads = 200;
        String param = getInitParameter("xmlrpc.maxthreads");
        if (param != null) {
            maxthreads = Integer.parseInt(param);
        }
        XmlRpc.setMaxThreads(maxthreads);
		
		String source = getInitParameter("datasource");
		threading = "true".equals(getInitParameter("threading"));
		
        LOG.log(Level.INFO, "monitoring = {0}, threading = {1}", new Object[]{monitor, threading});
			ds = new DelayedDatasource(source);
		
			// try immediately
			try { 
				ds.getDelegate();
			} catch (Exception e) {
				LOG.info("getDelegate at init " + e);
			}
			
			
			if (threading)
			{
				
				new DataSourceProxy(ds);
				unLock();
			}
			else 
			{
			
				new DataSourceAccess(ds);
			}
			

	}

	/**
     * Returns public servlet status information in a plain text webpage.
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
		out.println("OKAY");
		if(! "all" .equals( req.getParameter("status"))) 
		{
			return;
		}

		out.println(this);
        out.println(getHandler());
        out.println(ds);

        String buildNumber = getInitParameter("buildnumber");
        String softwareVersion = getInitParameter("projectVersion");
        String timeStamp = getInitParameter("timestamp");
        out.println();
        out.println("Software version, buildnumber: " + softwareVersion + ", " + buildNumber + ", timestamp "+timeStamp);
        LOG.log(Level.INFO,"Software version {0}, buildnumber {1}, timestamp {2}", new Object[]{softwareVersion, buildNumber, timeStamp});
        out.println();
        out.println("monitor = " + monitor);
        out.println("threading = " + threading);
        out.println();

        //check for proper DB version
        Connection c = null;
        try {
            c = ds.getConnection();
            PreparedStatement ps = c.prepareStatement("select * from tblDWOSystemParameters where name like 'DBVersion%'");
            ResultSet rs = ps.executeQuery();
            HashMap<String, String> hashMap = new HashMap<String, String>(5);
            while (rs.next()) {
                hashMap.put(rs.getString("name"), rs.getString("value"));
            }
            out.println();
            out.printf("We are compatible with the database version: " + (String) hashMap.get("DBVersion Major")
                    + "." + (String) hashMap.get("DBVersion Minor")
                    + "." + (String) hashMap.get("DBVersion Revision"));
            out.println();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Eror retrieving database version information from the database", e);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "Eror closing the connection", ex);
                }
            }
        }

    }

    @Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try { 
			ds.getDelegate();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "service", e);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}  

		try { 
			HttpSession s = req.getSession(true);
			session.set(s);
			user.set(null);
			s.setAttribute("ip", req.getRemoteAddr());
			String authorization = req.getHeader("Authorization");
			if(authorization != null) {
			  authorization(authorization,req);
			}
			super.service(req, resp);
		} catch (RuntimeException re) {
			LOG.log(Level.SEVERE, "service", re);
			throw re;
		} finally { 
			try {
				((DbConnectIF) getHandler()).close();
			} catch (Exception e) {
				LOG.log(Level.FINE, "finally close failed", e);
			}
			session.remove();
		}
	}

    private boolean authorization(String authHeader, HttpServletRequest req) {
      LOG.log(Level.FINE,"Authorization: " + authHeader);
      if (authHeader.startsWith("Basic ")) {
        authHeader = authHeader.substring(6);
        byte[] header = Base64.getDecoder().decode(authHeader);
        authHeader = new String(header, StandardCharsets.UTF_8);
        String authFields[] = authHeader.trim().split(":");
        PersistentUser u = UserManager.findByUserName(authFields[0]);
        if (u != null && u.getPassword().equals(authFields[1])) {
            user.set(u);
            LOG.log(Level.INFO,"Authenticated user: " + u.getUsername());
            req.setAttribute("username", u.getUsername());
            return true;
        } else {
          LOG.log(Level.SEVERE, "Unauthenticated user " +  authHeader);
          return false;
        }
      }
// TODO ....   
      return true; // okay for now.
    }

    @Override
    public void destroy() {
        LOG.log(Level.FINE, "Closing xmlrpc handler.");
        if (getHandler() instanceof DbConnectIF)
          ((DbConnectIF) getHandler()).close();
        super.destroy();
        ds = null;
    }

}
