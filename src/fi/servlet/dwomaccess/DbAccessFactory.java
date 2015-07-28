package fi.servlet.dwomaccess;

import java.net.URL;

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
			return singleton = new DbAccessClient(new URL(param));
		} catch(Exception e) {
			return singleton = DbAccessCreator.instance();
		}

	}
	
}
