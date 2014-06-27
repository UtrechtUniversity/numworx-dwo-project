package fi.dwo.client.persistence.cache;

import fi.dwo.client.persistence.DbAccessIF;

public class ReadOnly extends NoCache {

	public ReadOnly(DbAccessIF dbAccess) {
		super(dbAccess);
	}

	public String setValue(int uid, int scoid, String key, String value)
	{
		return "true";
	}

}
