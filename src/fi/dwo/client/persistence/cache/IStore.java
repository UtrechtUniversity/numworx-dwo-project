package fi.dwo.client.persistence.cache;

import fi.dwo.client.system.PersistenceException;

public interface IStore {
	String getValue(int uid, int scoid, String key) throws PersistenceException;
	String setValue(int uid, int scoid, String key, String value) throws PersistenceException;
	String commit(int uid, int scoid, String param) throws PersistenceException;
	void destroy();
}
