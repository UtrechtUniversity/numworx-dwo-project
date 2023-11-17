/** Copyrighted Jan 15, 2018 */
package fi.dwo.server.rest.util;

import fi.dwo.server.BUILD;
import fi.dwo.server.rest.PublicServerStatus;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * HeartBeat class for the ServletContextListener.
 *
 * @author plas0006
 */
public class HeartBeat {
    private static String serverVersion;
    private static String javaClientVersion;
    private static String htmlClientVersion;
    private static boolean initialized=false;

    /**
     * Initialize heartBeat values, this occurs after context is initialized. 
     * For example it is called in a ServletContextListener.contextInitialized method.
     * 
     * @param ctx 
     */
    public static void initializeHeartBeat(ServletContext ctx) throws Dwo2Exception {
        serverVersion = BUILD.version;
		javaClientVersion = BUILD.javaClient;
		htmlClientVersion = BUILD.htmlClient;
		initialized = true;
    }        

    /**
     * @return the serverVersion
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * @return the javaClientVersion
     */
    public String getJavaClientVersion() {
        return javaClientVersion;
    }

    /**
     * @return the htmlClientVersion
     */
    public String getHtmlClientVersion() {
        return htmlClientVersion;
    }


    /**
     * Is called after initializing the context.
     * 
     * @return DomHeartBeat
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomHeartBeat buildDomHeartBeat() throws Dwo2Exception {
        if(initialized){
        DomHeartBeat heartBeat = new DomHeartBeat();
        heartBeat.setHtmlClientVersion(htmlClientVersion);
        heartBeat.setJavaClientVersion(javaClientVersion);
        heartBeat.setServerVersion(serverVersion);
        heartBeat.setServerTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
        heartBeat.setEnv(System.getProperty("DWO_ENV", "app"));
        return heartBeat;
        }else{
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "HeartBeat is called before being initialized.");
        }
    }
    
}
