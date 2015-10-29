package fi.servlet.dwomaccess;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import fi.dwo.client.persistence.DbAccessClient;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.DbAccessIF;

public class DbAccessFactory {

	static private DbAccessIF singleton;
	
	static public DbAccessIF getDbAccess(ServletContext context) {
		if(singleton != null) return singleton;
		try {
			String param = context.getInitParameter("dbaccess.url");
			Logger.getLogger(DbAccessFactory.class.getName()).info("dbaccess url = " + param);
			singleton = new DbAccessClient(new URL(param));
			DbAccessCreator.setInstance(singleton);
			return singleton;
		} catch(Exception e) {
			Logger.getLogger(DbAccessFactory.class.getName()).log(Level.SEVERE, "dbaccess url error", e);
					
			return singleton = DbAccessCreator.instance();
		}

	}
	
}
