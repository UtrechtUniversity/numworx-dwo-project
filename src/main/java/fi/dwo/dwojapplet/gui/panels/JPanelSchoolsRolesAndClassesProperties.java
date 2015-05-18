/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.domain.rest.RestException;
import fi.dwo.dwojapplet.domain.rest.SchoolsRolesAndClassesManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class JPanelSchoolsRolesAndClassesProperties {

    private static final Logger log = Logger.getLogger(JPanelSchoolsRolesAndClassesProperties.class.getName());
    private SchoolRoleAndClass selectedSrc;
    private SchoolsRolesAndClasses srcs;

    public void init() {
        try {
            srcs = SchoolsRolesAndClassesManager.getCurrentEnlistements();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        } catch (RestException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the user
     */
    public SchoolsRolesAndClasses getSchoolsRolesAndClasses() {
        return srcs;
    }

    /**
     * @param src
     */
    public void setActiveSchoolRoleAndClass() {
        try {
            SchoolRoleAndClass src = SchoolsRolesAndClassesManager.setActiveSchoolRoleAndClass(getSelectedSchoolRoleAndClass());
            srcs.setCurrentSchoolRoleAndClass(src);
        } catch (RestException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

        /**
     * @return 
     */
    public SchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return srcs.getActiveSchoolRoleAndClass();
    }

    /**
     * @return the selectedSrc
     */
    public SchoolRoleAndClass getSelectedSchoolRoleAndClass() {
        return selectedSrc;
    }

    /**
     * @param selectedSrc the selectedSrc to set
     */
    public void setSelectedSchoolRoleAndClass(SchoolRoleAndClass selectedSrc) {
        this.selectedSrc = selectedSrc;
    }

    
}
