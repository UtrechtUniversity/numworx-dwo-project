package fi.dwo.dwojapplet.persistence.cache;

import fi.dwo.commons.exceptions.PersistenceException;

public class ReadOnly extends NoCache {
	
	public static boolean hasSuspendData; // if true, access is STUDENT

    public ReadOnly() {
        super();
    }

    @Override
    public String setValue(int uid, int scoid, int sgid, int clsid, String key, String value) throws PersistenceException {
        if (hasSuspendData) sgid = Integer.MAX_VALUE;
 		if(hasSuspendData && allowWrite(key)) // verzegelen.
			return super.setValue(uid, scoid, sgid, clsid, key, value);
		return "true";
	}

	private boolean allowWrite(String key) {
		return "cmi.completion_status".equals(key)||"cmi.comments_from_lms.0.comment".equals(key) || "score".equals(key);
	}
	
    @Override
    public String getValue(int uid, int scoid, int sgid, int clsid, String key) throws PersistenceException {
      if (hasSuspendData) sgid = Integer.MAX_VALUE;
        if ("suspendData".equals(key) && !hasSuspendData) {
            return "";
        }
        return super.getValue(uid, scoid, sgid, clsid, key);
    }

}
