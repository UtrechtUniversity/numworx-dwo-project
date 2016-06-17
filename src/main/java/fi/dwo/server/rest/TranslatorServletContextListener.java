/**
 * Copyrighted Apr 18, 2016
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author G.A.J. van der Plas
 */
public class TranslatorServletContextListener implements ServletContextListener{
    private static final Logger LOG = Logger.getLogger(TranslatorServletContextListener.class.getName());
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.log(Level.FINE,"Destroying servlet context.");
	}

        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		LOG.log(Level.FINE,"Servlet context intialized.");
                Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
	}
}
