// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\server\\persistence\\DbAccessServlet.java

package fi.dwo.server.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.xmlrpc.Servlet;
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
    
    private static DbAccess dbAccess;
    
   // private static final String JAR_FOLDER = "file:/space/WWW/InfoGroups/dwo/jars/";
    /**

     */
    public DbAccessServlet() {
        super(dbAccess = new DbAccess());

    }
    protected DbAccessServlet(DbAccess myDbAccess)
    {
        super(dbAccess = myDbAccess);
    }
    
    public void init(ServletConfig arg0) throws ServletException {
        super.init(arg0);
        log("Initializatie");
        if("true".equals(getInitParameter("local")))
        	setHandler(dbAccess = new DbAccessLocal());
        
    }
    
      // End Of Life
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//        try {
//	        String key = (String) req.getParameter("key");
////	        log("we've got a get!");
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
    
    
    public void destroy() {
        log("En weg ben ik...");
        dbAccess.close();
        super.destroy();
    }
}