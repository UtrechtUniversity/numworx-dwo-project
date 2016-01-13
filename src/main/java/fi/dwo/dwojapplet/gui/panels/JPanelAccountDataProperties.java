/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel property and state class.
 * 
 * @author G.A.J. van der Plas
 */
public class JPanelAccountDataProperties {
    private static final Logger LOG = Logger.getLogger(JPanelAccountDataProperties.class.getName());
    
    private DomFullUser user;

    public JPanelAccountDataProperties() {
        
    }
    
    public void init(){
        try {
            user = SecureUserAccountManager.getAccountData();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean update = false;

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
     * @return the update
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * @param update the update to set
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }
    
    /**
     * Update the user.
     */
    public void Update(){
        try {
            user = SecureUserAccountManager.updateAccountData(user);
            // update local Global storage.
            DwoHelper.updateCurrentUser(user);
            //TODO above method currently updates the login date, this should not occur for this function.
            
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
