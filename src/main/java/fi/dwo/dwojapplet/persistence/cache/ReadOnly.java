package fi.dwo.dwojapplet.persistence.cache;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.DbAccessIF;

public class ReadOnly extends NoCache {
	
	public static boolean hasSuspendData;

    public ReadOnly(DbAccessIF dbAccess) {
        super(dbAccess);
    }

    @Override
    public String setValue(int uid, int scoid, int sgid, String key, String value) throws PersistenceException {
 		if(hasSuspendData && allowWrite(key)) // verzegelen.
			return super.setValue(uid, scoid, sgid, key, value);
		return "true";
	}

	private boolean allowWrite(String key) {
		return "cmi.completion_status".equals(key)||"cmi.comments_from_lms.0.comment".equals(key) || "score".equals(key);
	}
	
    @Override
    public String getValue(int uid, int scoid, int sgid, String key) throws PersistenceException {
        if ("suspendData".equals(key) && !hasSuspendData) {
            return "";
        }
        return super.getValue(uid, scoid, sgid, key);
    }

}
