package fi.dwo.client.persistence.cache;

import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.system.PersistenceException;

public class ReadOnly extends NoCache {

	public ReadOnly(DbAccessIF dbAccess) {
		super(dbAccess);
	}

	public String setValue(int uid, int scoid, String key, String value)
	{
		return "true";
	}

	public String getValue(int uid, int scoid, String key) throws PersistenceException
	{
		if("suspendData".equals(key))
			return "";
		return super.getValue(uid, scoid, key);
	}

}
