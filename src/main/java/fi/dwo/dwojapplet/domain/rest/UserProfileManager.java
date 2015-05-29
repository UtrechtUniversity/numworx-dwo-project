package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 * 
 * @author G.A.J. van der Plas
 */
public class UserProfileManager {
    private static final Logger LOG = Logger.getLogger(UserProfileManager.class.getName());
    
    /**
    * Returns the current user 'logged in'. The information is extracted from the 
    * security context which depends on the credentials used for accessing the rest
    * interface. Technically it should be equal to the data in the DwoHelper.
    * 
    * @return 
    */
    public static PersistentUser getCurrentUser() throws RestException{
        PersistentUser user;
        user = StoredRestManager.getInstance().get("/rest/secure/user/userprofile/get/json", PersistentUser.class);
        return user;
    }   
    
    /**
     * Updates the user profile of a user. 
     * 
     * Fields updated are email, password and the full name of the user. The full
     * name exists out of the first, insertion and family name.
     * 
     * @param user
     * @return 
     */

    public static PersistentUser updateCurrentUser(PersistentUser user) throws RestException {
            user = StoredRestManager.getInstance().put("/rest/secure/user/userprofile/update/json", PersistentUser.class, user);
            LOG.log(Level.FINE, "Updated user profile of username {0}.",new Object[]{user.getUsername()});
        return user;
    }
}
