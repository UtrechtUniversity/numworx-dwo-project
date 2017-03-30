package fi.dwo.dwojapplet.persistence;

import java.net.URL;

import fi.dwo.commons.persistence.DbAccessIF;

/**
 * Utilityclass
 * @author wim
 *
 */
public class DbAccessBridge {

	private DbAccessBridge() {
	}

	public static void setInstance(DbAccessIF instance) {
		DbAccessCreator.setInstance(instance);
	}
	
	public static DbAccessIF createClient(URL u) {
		return new DbAccessClient(u);
	}
	
	public static DbAccessIF instance() {
		return DbAccessCreator.instance();
	}
}
