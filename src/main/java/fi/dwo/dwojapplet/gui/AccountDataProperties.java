/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountManager;
import java.util.logging.Logger;

/**
 * Panel property and state class.
 * 
 * @author G.A.J. van der Plas
 */
public class AccountDataProperties {
    private static final Logger LOG = Logger.getLogger(AccountDataProperties.class.getName());
    
    private DomFullUser user;

    public AccountDataProperties() {
        
    }
    
    public void init() throws Dwo2Exception{
            user = SecureUserAccountManager.getAccountData();
    }
    
    /**
     * @return the user
     */
    public DomFullUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomFullUser user) {
        this.user = user;
    }    
    
    /**
     * Update the user.
     * 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public void Update() throws Dwo2Exception{
            user = SecureUserAccountManager.updateAccountData(user);
            // update local Global storage.
            DwoHelper.updateCurrentUser(user);
            //TODO above method currently updates the login date, this should not occur for this function.
            
    }
}
