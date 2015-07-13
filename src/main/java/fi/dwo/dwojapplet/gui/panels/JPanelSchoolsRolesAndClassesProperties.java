/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.domain.rest.SchoolsRolesAndClassesManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class JPanelSchoolsRolesAndClassesProperties {

    private static final Logger LOG = Logger.getLogger(JPanelSchoolsRolesAndClassesProperties.class.getName());
    private SchoolRoleAndClass selectedSrc;
    private SchoolsRolesAndClasses srcs;

    public void init() {
        try {
            srcs = SchoolsRolesAndClassesManager.getCurrentEnlistements();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
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
            srcs.setActiveSchoolRoleAndClass(src);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
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
