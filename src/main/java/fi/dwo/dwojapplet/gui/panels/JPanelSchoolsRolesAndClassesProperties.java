/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.domain.rest.RestException;
import fi.dwo.dwojapplet.domain.rest.SchoolsRolesAndClassesManager;
import fi.dwo.dwojapplet.domain.rest.UserProfileManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class JPanelSchoolsRolesAndClassesProperties {

    private static final Logger log = Logger.getLogger(JPanelSchoolsRolesAndClassesProperties.class.getName());
    private PersistentUser user;
    private SchoolsRolesAndClasses sacs;

    public void init() {
        try {
            user = UserProfileManager.getCurrentUser();
            sacs = SchoolsRolesAndClassesManager.getCurrentEnlistements();
        } catch (RestException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the user
     */
    public SchoolsRolesAndClasses getSchoolsRolesAndClasses() {
        return sacs;
    }

    /**
     * @param src
     */
    public void setActiveSchoolRoleAndClass(SchoolRoleAndClass src) {
        try {
            SchoolsRolesAndClassesManager.setActiveSchoolRoleAndClass(src);
        } catch (RestException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

        /**
     * @param src
     */
    public SchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return sacs.getCurrentSchoolRoleAndClass();
    }

    
}
