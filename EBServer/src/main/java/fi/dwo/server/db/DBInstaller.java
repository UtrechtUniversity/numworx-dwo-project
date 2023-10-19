package fi.dwo.server.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

public class DBInstaller implements ServletContextListener {

  Logger LOG = Logger.getLogger(getClass().getName());
  RestAuthenticator authenticator = StoredRestManager.getInstance().getAuthenticator(); // XXX Singleton.

  public void contextDestroyed(ServletContextEvent e) {
    LOG.log(Level.INFO, "destroyed");
  }

  public void contextInitialized(ServletContextEvent e) {
    Driver driver;
    try {
      driver = ServiceLoader.load(java.sql.Driver.class).iterator().next();
      LOG.log(Level.INFO, "initialized " + driver);
    } catch (Exception e1) {
      LOG.log(Level.SEVERE, "contextInitialized exception", e1);
    }
	String dbrest_url = e.getServletContext().getInitParameter("dbrest.url");
    try {
		authenticator.setServerUrlPath(new URL(dbrest_url));
	} catch (MalformedURLException e2) {
		LOG.log(Level.WARNING, "rest parameter incorrect", e2);
	}

  }

}
