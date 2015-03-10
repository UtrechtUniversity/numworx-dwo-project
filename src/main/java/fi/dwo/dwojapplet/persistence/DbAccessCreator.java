// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\DbAccessCreator.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.DwoHelper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

class DbAccessCreator {
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
            if(DwoHelper.getServletConnectString()!=null){
            try {
                        server = new URL(DwoHelper.getServletConnectString());
                        dbAccess = new DbAccessClient(server);
                    } catch (MalformedURLException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        //TODO FIX put the URL in the Manifest and pick it up from there
                        //THIS Allows it to be configured from the pom.
                        server = new URL(new URL("http://ws.fisme.science.uu.nl/"), SERVLET);
                        dbAccess = new DbAccessClient(server);
                    } catch (MalformedURLException e) {
                        log.log(Level.SEVERE,null,e);
                    }
                }
            } 
        return dbAccess;
    }
}
