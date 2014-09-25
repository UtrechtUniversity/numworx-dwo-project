// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\server\\persistence\\DbAccessServlet.java

package fi.dwo.server.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpc;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.proxy.MonProxyFactory;

import fi.beans.jdbc.DbConnect;
import fi.beans.jdbc.DbConnectIF;
import fi.beans.xmlrpc.Servlet;
import fi.dwo.VERSION;
import fi.dwo.client.persistence.DbAccessIF;

/**
 * Servlet voor XML-RPC access op de DWO database.
 * Zorgt tevens voor access van de jar files.
 * 
 * @author Peter Boon
 * @version $Rev$
 * @web.servlet
 *   name="DbAccess"
 *   description="Servlet voor database access van DWO via XML-RPC"
 * @web.servlet-init-param
 *   name="local"
 *   value="true"
 * @web.servlet-mapping
 *   url-pattern="/dbaccess"
 */
public class DbAccessServlet extends Servlet {
    
	static class MonitorDbAccess extends DbAccess {
	
		static private int count;
		
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

	
	static class MyProxy extends DbAccessProxy {

		protected DbAccessIF createDelegate() {
			return new DbAccess();
		}
		
	}

	static class MonitoringProxy extends DbAccessProxy {

		protected DbAccessIF createDelegate() {
			return new MonitorDbAccess();
		}
		
	}
	
	
    private static DbAccessIF dbAccess;
    
   // private static final String JAR_FOLDER = "file:/space/WWW/InfoGroups/dwo/jars/";
    /**

     */
    public DbAccessServlet() {
        super(dbAccess = new MyProxy());
        unLock();

    }

    protected DbAccessServlet(DbAccessIF myDbAccess)
    {
        super(dbAccess = myDbAccess);
    }
    
    public void init(ServletConfig arg0) throws ServletException {
        super.init(arg0);
        log("Initializatie r" + VERSION.REVISION);
        if(!"false".equals(getInitParameter("monitor")))
        {
        	setHandler(dbAccess = (DbAccessIF) (MonProxyFactory.monitor(new MonitoringProxy())));
        	log("monitoring");
        } else {
        	log("no monitoring");
        }

        unLock(); log("UNLOCK");
        int maxthreads = 200;
        String param = getInitParameter("xmlrpc.maxthreads");
        if(param != null )
        	maxthreads = Integer.parseInt(param);
        XmlRpc.setMaxThreads(maxthreads);

        if("true".equals(getInitParameter("local")))
        {
        	setLock(this);
        	dbAccess = new DbAccessLocal() {
        		
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
				
        	setHandler(MonProxyFactory.monitor(dbAccess));
        }
        
    }
    
      // End Of Life
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//        try {
//	        String key = (String) req.getParameter("key");
//	        log("we've got a get!");
//	        
//	        String jar = null;
//	        jar = dbAccess.getJar(key);
//	        if((jar != null) && (! jar.equals(""))) {
//		        resp.setContentType("application/java-archive");
//		        resp.setHeader("Content-disposition","attachment; filename=" + jar);
//		        ServletOutputStream os = resp.getOutputStream();
//		        URL url = new URL(JAR_FOLDER + jar);
//		  		URLConnection connection = url.openConnection();
//			
//				InputStream is = connection.getInputStream();
//		        
//		        
//		        byte b[] = new byte[16384];
//		        int numBytes;
//		        
//		        while((numBytes=is.read(b))!=-1){
//		                os.write(b,0,numBytes);
//		        }
//		        
//		        os.flush();
//		        is.close();
//		        os.close();
//	        }
//	
//	
//	        dbAccess.close();
//        } catch(Exception e) {
//            log("---------------------------------");
//            log("DbAccessServlet get");
//            log("Datetime " + System.currentTimeMillis());
//            log("key: " + ((String) req.getParameter("key")));
//            log("type exception: " + e.getClass().getName());
//            log("exception message: " + e.getMessage());
//            log("exception stackstrace: ");
//            for(int i = 0; i < e.getStackTrace().length; i++) {
//                log(e.getStackTrace()[i].toString());
//            }
//            log("---------------------------------");
//            
//        }
//    }
    
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

    public void destroy() {
        log("En weg ben ik...");
        ((DbConnectIF) dbAccess).close();
        super.destroy();
    }
    
//	/** Variant met monitoring from www.jamon.com, kan ook buiten om, via de jamon servlet-filter.
//	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
//	 */
//	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
//			throws ServletException, IOException {
//		Monitor x = MonitorFactory.startPrimary("DWO service");
//		
//		try { 
//			super.service(arg0, arg1);
//		} finally {
//			x.stop();
//		}
//		
//	}

}