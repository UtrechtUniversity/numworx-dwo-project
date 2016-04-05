package fi.restrpcgwt.client;

import com.google.gwt.core.client.GWT;

import fi.restrpcgwt.shared.entities.PersistenceClassType;
import fi.restrpcgwt.shared.entities.PersistenceId;


// legacy conversion
public interface DatabaseHelper {
	DatabaseHelper instance = GWT.create(MYSQL.class);
	
	Object idOf(PersistenceId id, PersistenceClassType expected);
}
