package fi.dwo.gwt.lib.rest.client;

import com.google.gwt.core.client.GWT;

import fi.dwo.rest.persistence.PersistenceClassType;
import fi.dwo.rest.persistence.PersistenceId;


// legacy conversion
public interface DatabaseHelper {
	DatabaseHelper instance = GWT.create(MYSQL.class);
	
	Object idOf(PersistenceId id, PersistenceClassType expected);
}
