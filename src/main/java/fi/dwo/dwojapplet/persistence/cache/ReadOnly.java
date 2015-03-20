package fi.dwo.dwojapplet.persistence.cache;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.DbAccessIF;

public class ReadOnly extends NoCache {

    public ReadOnly(DbAccessIF dbAccess) {
        super(dbAccess);
    }

    @Override
    public String setValue(int uid, int scoid, String key, String value) {
        return "true";
    }

    @Override
    public String getValue(int uid, int scoid, String key) throws PersistenceException {
        if ("suspendData".equals(key)) {
            return "";
        }
        return super.getValue(uid, scoid, key);
    }

}
