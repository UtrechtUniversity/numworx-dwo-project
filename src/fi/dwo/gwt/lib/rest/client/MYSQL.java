package fi.dwo.gwt.lib.rest.client;

import fi.dwo.rest.persistence.PersistenceClassType;
import fi.dwo.rest.persistence.PersistenceId;

class MYSQL implements DatabaseHelper {
	
	@Override
	public Object idOf(PersistenceId id, PersistenceClassType type) {
		if(id == null) return null;
		assert type == id.getType();	
		String key = id.getIdString();
		String[] split = key.split(";");
		assert "MYSQL".equals(split[0]);
		assert type.name().equals(split[1]);
		return new Integer(split[2]);
	}
	
}
