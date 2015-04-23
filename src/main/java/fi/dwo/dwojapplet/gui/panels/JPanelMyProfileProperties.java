/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.domain.rest.RestException;
import fi.dwo.dwojapplet.domain.rest.UserProfileManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class JPanelMyProfileProperties {
    private PersistentUser user;

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
    
    public void Update(){
        try {
            UserProfileManager.updateCurrentUser(user);
        } catch (RestException ex) {
            Logger.getLogger(JPanelMyProfileProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
