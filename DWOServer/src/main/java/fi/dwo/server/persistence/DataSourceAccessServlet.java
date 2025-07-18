package fi.dwo.server.persistence;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.jamonapi.proxy.MonProxyFactory;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.BUILD;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import java.util.List;
import java.util.Map;

/**
 * Supplies doGet for database status info and database-operations via doPost
 * using an XML-RPC handler.
 *
 */
@SuppressWarnings("serial")
public class DataSourceAccessServlet extends HttpServlet {
	
    private static final Logger LOG = Logger.getLogger(DataSourceAccessServlet.class.getName());


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
        String buildnumber = BUILD.buildNumber;
        String projectVersion = BUILD.version;
        LOG.log(Level.INFO, "Software version, buildnumber: {0}, {1}", new Object[]{projectVersion, buildnumber});

//        int maxthreads = 200;
//        String param = getInitParameter("xmlrpc.maxthreads");
//        if (param != null) {
//            maxthreads = Integer.parseInt(param);
//        }
//		  XmlRpc.setMaxThreads(maxthreads);
					
	
        if (DbAccess.checkPersistentVersion())
		  throw new ServletException("old sofware trying to use new database.");

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

        String buildNumber = BUILD.buildNumber;
        String softwareVersion = BUILD.version;
        String timeStamp = BUILD.timeStamp;
        out.println();
        out.println("Software version, buildnumber: " + softwareVersion + ", " + buildNumber + ", timestamp "+timeStamp);
        LOG.log(Level.INFO,"Software version {0}, buildnumber {1}, timestamp {2}", new Object[]{softwareVersion, buildNumber, timeStamp});
        out.println();

        //check for proper DB version
        try {
          List<PersistentDwoSystemParameters> list = DwoSystemParametersManager.findEntities();
          Map<String,String> hashMap = list.stream().collect(Collectors.toMap(PersistentDwoSystemParameters::getName, PersistentDwoSystemParameters::getValue));
            
            out.println();
            out.printf("We are compatible with the database version: " + (String) hashMap.get("DBVersion Major")
                    + "." + (String) hashMap.get("DBVersion Minor")
                    + "." + (String) hashMap.get("DBVersion Revision"));
            out.println();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Eror retrieving database version information from the database", e);
        }
    }


 
 
}
