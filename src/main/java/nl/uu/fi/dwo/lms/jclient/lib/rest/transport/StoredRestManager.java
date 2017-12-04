package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.util.logging.Logger;

/**
 * StoredRestManager ensures there is only one copy or each rest-fetched persistent object.
 * It checks whether or not the class name of an object starts with "fi.dwo.commons.persistence.entities".
 * Currently storing is disabled!
 *
 * @author G.A.J. van der Plas
 */
public class StoredRestManager extends RestManager {
    private static final Logger LOG = Logger.getLogger(StoredRestManager.class.getName());
 
    private static final StoredRestManager storedInstance = new StoredRestManager();

    /**
     * @return the instance
     */
    public static StoredRestManager getInstance() {
        return storedInstance;
    }
    
}
