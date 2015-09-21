/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.entities.PersistentUser;
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
    
    private PersistentUser user;

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
    public PersistentUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(PersistentUser user) {
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
            SecureUserAccountManager.updateAccountData(user);
            // update local Global storage.
            DwoHelper.setCurrentUser(user);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
