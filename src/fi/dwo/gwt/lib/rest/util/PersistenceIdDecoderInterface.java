package fi.dwo.gwt.lib.rest.util;

import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;


// legacy conversion
public interface PersistenceIdDecoderInterface {
	PersistenceIdDecoderInterface instance = GWT.create(MySQLPersistenceIdDecoder.class);
	
	Object idOf(PersistenceId id, PersistenceClassType expected);
}
