package fi.dwo.dwojapplet.REST;

import fi.dwo.dwojapplet.domain.rest.RestException;
import java.util.logging.Logger;

/**
 * StoredRestManager ensures there is only one copy or each rest-fetched persistent object.
 * It checks whether or not the class name of an object starts with "fi.dwo.commons.persistence.entities".
 * Currently storing is disabled!
 *
 * @author G.A.J. van der Plas
 */
public class StoredRestManager extends RestManager {
    private static final Logger log = Logger.getLogger(StoredRestManager.class.getName());

    @Override
    public <T> T get(String path, Class<T> c) throws RestException {
        T object = super.get(path,c);
        //only store persistent objects... 
        // if(object.getClass().getName().startsWith("fi.dwo.commons.persistence.entities")
        //StoreManager.insertOrUpdate(object);
        return object;
    }
}
