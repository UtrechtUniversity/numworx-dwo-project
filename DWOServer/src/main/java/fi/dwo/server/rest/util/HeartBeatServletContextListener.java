/**
 * Copyrighted Apr 18, 2016
 */
package fi.dwo.server.rest.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author G.A.J. van der Plas
 */
public class HeartBeatServletContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(HeartBeatServletContextListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LOG.log(Level.FINE, "Destroying servlet context.");
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        try {
            HeartBeat.initializeHeartBeat(ctx);
        LOG.log(Level.FINE, "HeartBeat intialized.");
        } catch (Dwo2Exception ex) {
        LOG.log(Level.FINE, "HeartBeat intialization failed.");
            Logger.getLogger(HeartBeatServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
