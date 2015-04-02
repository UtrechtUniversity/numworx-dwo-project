package fi.dwo.server.persistence;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.exceptions.DwoXmlRpcException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.xmlrpc.XmlRpc;

import com.jamonapi.proxy.MonProxyFactory;

import fi.beans.jdbc.DbConnectIF;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.commons.persistence.DbAccessIF;
import java.sql.PreparedStatement;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Supplies doGet for database status info and database-operations via doPost
 * using an XML-RPC handler.
 *
 */
public class DataSourceAccessServlet extends Servlet {

    private static final Logger log = Logger.getLogger(DataSourceAccessServlet.class.getName());

    private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("DWO_MySQLDB");

    private DataSource ds;
    private boolean monitor;
    private boolean threading;
    static private ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();

    private static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    static class MonitorDataSourceAccess extends DataSourceAccess implements fi.beans.jdbc.DbConnectIF, DbAccessIF {

        static private int count;

        public MonitorDataSourceAccess(DataSource ds) {
            super(ds);
        }

        @Override
        protected String session() {
            return session.get().getAttribute("login")
                    + "," + session.get().getAttribute("ip")
                    + ":";
        }

        @Override
        public Hashtable login(String username, String password)
                throws SQLException, DwoXmlRpcException {
            Hashtable h = super.login(username, password);
            log.log(Level.INFO, "Session login for user {0}.", new Object[]{username});

            EntityManager em;
            //EntityManagerFactory emf;
            //emf = Persistence.createEntityManagerFactory("DWO_MySQLDB");
            em = emf.createEntityManager();

            PersistentUser user;
            PersistentHasRole hasRole;
            try {
                javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
                q.setParameter("username", username);
                user = (PersistentUser) q.getSingleResult();
                log.log(Level.FINE, "Retrieved user {0} with role {1}.", new Object[]{user});
                q = em.createQuery("SELECT p FROM PersistentHasRole p WHERE p.persistentHasRolePK.schoolGroupID = :schoolGroupID AND p.persistentHasRolePK.userID= :userID");
                q.setParameter("userID", user.getUserID()).setParameter("schoolGroupID", user.getSchoolGroupID());
                hasRole = (PersistentHasRole) q.getSingleResult();

            } finally {
                em.close();
            }
            DwoSessionData sessionData = new DwoSessionData();

            sessionData.setLoginUser(user);
            sessionData.setLoginRole(hasRole);
            
            log.log(Level.INFO, "Session {0} stored logged in user {1} with role {2}.", new Object[]{session.get().getId(),user.toString(), hasRole.toString()});

            //Set some session fixed variables. 
            session.get().setAttribute("DwoSessionData", sessionData);
            session.get().setAttribute("login", username);
            return h;
        }

        private Connection mine, his;

        @Override
        public void close() {
            if (mine != null) {
                --count;
                if (count > 10) {
                    log.log(Level.INFO, " dwo access close {0}", count);
                }
                try {
                    mine.close();
                } catch (SQLException e) {
                    log.log(Level.SEVERE, " close ", e);
                }
            }
            mine = null;
            his = null;
            super.close();
        }

        @Override
        public Connection getConnection() throws SQLException {
            Connection c = super.getConnection();
            if (c != his || mine == null) {
                his = c;
                mine = MonProxyFactory.monitor(c);
                ++count;
                if (count > 10) {
                    log.log(Level.INFO, " dwo access connect {0}", count);
                }
            }
            return mine;
        }
    };

    static class DataSourceProxy extends DbAccessProxy {

        DataSource ds;

        @Override
        protected DbAccessIF createDelegate() {
            return new DataSourceAccess(ds);
        }

        DataSourceProxy(DataSource ds) {
            this.ds = ds;
        }

    }

    static class MonitoringProxy extends DbAccessProxy {

        DataSource ds;

        @Override
        protected DbAccessIF createDelegate() {
            //return (DbAccessIF) MonProxyFactory.monitor(new MonitorDataSourceAccess(ds));
            return new MonitorDataSourceAccess(ds);
        }

        MonitoringProxy(DataSource ds) {
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

        String buildnumber = getInitParameter("buildnumber");
        String projectVersion = getInitParameter("projectVersion");
        log.log(Level.INFO, "Software version, buildnumber: {0}, {1}", new Object[]{projectVersion, buildnumber});

        int maxthreads = 200;
        String param = getInitParameter("xmlrpc.maxthreads");
        if (param != null) {
            maxthreads = Integer.parseInt(param);
        }
        XmlRpc.setMaxThreads(maxthreads);

        String source = getInitParameter("datasource");
        monitor = !"false".equals(getInitParameter("monitor"));
        threading = "true".equals(getInitParameter("threading"));

        log.log(Level.INFO, "monitoring = {0}, threading = {1}", new Object[]{monitor, threading});
        try {
// find datasource from tomcat
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup(source);
            if (ds == null) {
                throw new ServletException("Resource " + source + " is null");
            } else {
                log.log(Level.INFO, "Found datasource {0}", new Object[]{ds});
            }

            if (new DataSourceAccess(ds).checkVersion()) {
                throw new ServletException("Datasource invalid version");
            }

            DbAccessIF dbaccess;
            if (threading) {
                if (monitor) {
                    dbaccess = new MonitoringProxy(ds);
                } else {
                    dbaccess = new DataSourceProxy(ds);
                }
                unLock();
            } else {
                if (monitor) {
                    dbaccess = (DbAccessIF) MonProxyFactory.monitor(new MonitorDataSourceAccess(ds));
                } else {
                    dbaccess = new DataSourceAccess(ds);
                }
            }

            setHandler(dbaccess);

        } catch (NamingException e) {
            throw new ServletException("Datasource in error", e);
        }

    }

    private void printRS(ResultSet rs, PrintWriter out) throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        String colName;
        while (rs.next()) {
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                colName = rsMeta.getColumnName(i);
                out.print(" " + colName + ": " + rs.getObject(i));
            }
            out.println();
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
        out.println(this);
        out.println(getHandler());
        out.println(ds);

        String buildnumber = getInitParameter("buildnumber");
        String projectVersion = getInitParameter("projectVersion");
        out.println();
        out.println("Software version, buildnumber: " + projectVersion + ", " + buildnumber);
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
            log.log(Level.SEVERE, "Eror retrieving database version information from the database", e);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException ex) {
                    log.log(Level.SEVERE, "Eror closing the connection", ex);
                }
            }
        }

        //Disabled code for security issues.
//        try {
//            c = ds.getConnection();
//            Statement s = c.createStatement();
//            ResultSet rs = s.executeQuery("SHOW TABLES");
//            printRS(rs, out);
//            rs.close();
//            s.close();
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace(out);
//        } finally {
//            try {
//                c.close();
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace(out);
//            }
//        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession s = req.getSession(true);
            session.set(s);
            s.setAttribute("ip", req.getRemoteAddr());
            super.service(req, resp);
        } catch (RuntimeException re) {
            log.log(Level.SEVERE, "service", re);
            throw re;
        } finally {
            try {
                ((DbConnectIF) getHandler()).close();
            } catch (Exception e) {
                log.log(Level.FINE, "Closing handler in finally failed:", e);
            }
        }
    }

    @Override
    public void destroy() {
        log.log(Level.FINE, "Closing xmlrpc handler.");
        ((DbConnectIF) getHandler()).close();
        super.destroy();
    }

}
