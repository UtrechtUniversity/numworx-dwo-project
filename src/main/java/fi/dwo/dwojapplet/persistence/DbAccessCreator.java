// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\DbAccessCreator.java
package fi.dwo.dwojapplet.persistence;

import java.net.MalformedURLException;
import java.net.URL;

import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbAccessCreator {
    private static final Logger log = Logger.getLogger(DbAccessCreator.class.getName());

    private static DbAccessIF dbAccess;
    /**
     * URL van dbaccess servlet. Publiek, kan dus aangepast worden in DWO.main()
     * of Dwo.init()
     *
     * @see fi.dwo.client.domain.DWO#init()
     * @see fi.dwo.client.domain.DWO#main(String[])
     */

    public static String SERVLET = "/servlet/dwodsaccess";
    //public static String SERVLET = "/servlet/dwodsaccess";
    //public static String SERVLET = "/dwo/dbaccess";

	// Let op, bovenstaande switch is nodig voor de dwoserver (bij start.jar)
    /**
     *
     */
    public DbAccessCreator() {

    }

    public static void setInstance(DbAccessIF update) {
        dbAccess = update;
    }

    /**
     * @return fi.dwo.client.persistence.DbAccessIF
     *
     */
    public static DbAccessIF instance() {
        if (dbAccess == null) {
            URL server;
            if (DwoHelper.isApplication()) {
        	   //Bij testen van lokale dbAccess, TODO in comment bij productie!
                //if(true) dbAccess = new DbAccessLocal(); else
                //if(true) dbAccess = new DbAccess(); else
                //if(true) dbAccess = new DbAccessScience(); else
                //if(true) dbAccess = new DbAccessColorado(); else
                //if(true)try{dbAccess=new DbAccessClient(new URL("http://localhost:8888/dwoapp"));}catch(MalformedURLException e1){log.log(Level.SEVERE,null,e1);}else
                try {
//                    server = new URL(new URL("http://localhost:8080"), "DWOServer/xmlrpc");
                    server = new URL(new URL("http://ws.fisme.science.uu.nl/"), SERVLET);
        		   //server = new URL(new URL("http://dwo.fi.uu.nl/") , SERVLET);
                    // Let op, bovenstaande switch is nodig voor de dwoserver (bij start.jar)

                    dbAccess = new DbAccessClient(server);
                } catch (MalformedURLException e) {
                    log.log(Level.SEVERE,null,e);
                }
            } else {
// for local access, overrule SERVLET
                String servletParameter
                        = DwoHelper.getApplet().getParameter("SERVLET");
                if (null != servletParameter) {
                    SERVLET = servletParameter;
                }
//         	   
                try {
                    server = new URL(DwoHelper.getApplet().getCodeBase(), SERVLET);
	                //System.out.println(DwoHelper.getApplet().getCodeBase() + SERVLET);
                    //server = new URL("http://www.fi.uu.nl/servlet/fi.dwo.server.persistence.DbAccessServlet");
                    dbAccess = new DbAccessClient(server);
                } catch (MalformedURLException e) {
                    log.log(Level.SEVERE,null,e);
                }/**/

            }
        }

        return dbAccess;
    }
}
