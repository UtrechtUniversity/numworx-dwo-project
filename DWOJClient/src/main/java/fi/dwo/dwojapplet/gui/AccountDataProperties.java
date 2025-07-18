/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.HttpAuthenticationType;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
//import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

/**
 * Panel property and state class.
 * 
 * @author G.A.J. van der Plas
 */
public class AccountDataProperties {
    //private static final Logger LOG = Logger.getLogger(AccountDataProperties.class.getName());
    
    private DomUserFull user;

    public AccountDataProperties() {
        
    }
    
    public void init() throws Dwo2Exception{
            user = SecureUserAccountManager.getAccountData();
    }
    
    /**
     * @return the user
     */
    public DomUserFull getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomUserFull user) {
        this.user = user;
    }    
    
    /**
     * Update the user.
     * 
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void Update() throws Dwo2Exception{
            user = SecureUserAccountManager.updateAccountData(user);
            DomLoginContext context = DwoHelper.getCurrentLoginContext();

            // update local Global storage.
            
 // XXX no information hiding here: only digest or basic. (digest is not supported at all)
            if (HttpAuthenticationType.BEARER != DwoHelper.getHttpAuthentication())
              StoredRestManager.getInstance().setBasicAuthString(user.getUserName(),user.getPassword(),context.getRealm());
 
            DwoHelper.updateCurrentUser(user);
            DwoHelper.setCurrentUser(user, context);
            //TODO above method currently updates the login date, this should not occur for this function.
            
    }
}
