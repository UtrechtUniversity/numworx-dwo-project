package fi.dwo.server.db;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DBInstaller implements ServletContextListener {

  Logger LOG = Logger.getLogger(getClass().getName());

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
  }

}
