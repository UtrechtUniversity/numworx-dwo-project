package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * StoredRestManager ensures there is only one copy or each rest-fetched persistent object.
 * It checks whether or not the class name of an object starts with "fi.dwo.commons.persistence.entities".
 * Currently storing is disabled!
 *
 * @author G.A.J. van der Plas
 */
@Singleton
public class StoredRestManager extends RestManager {
	
	@Inject public StoredRestManager(RestAuthenticator authenticator) {
		super(authenticator);
	}

	private static final Logger LOG = Logger.getLogger(StoredRestManager.class.getName());
 
    private static final StoredRestManager storedInstance = new StoredRestManager(RestAuthenticator.getInstance());

    /**
     * Singleton pattern.
     * @return the instance
     */
    public static StoredRestManager getInstance() {
        return storedInstance;
    }
    
}
