package fi.dwo.server.db;

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
		LOG.log(Level.INFO, "initialized");	
	}

}
