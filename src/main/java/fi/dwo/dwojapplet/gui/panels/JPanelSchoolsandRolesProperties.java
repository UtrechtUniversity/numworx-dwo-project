/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class JPanelSchoolsandRolesProperties {

    private static final Logger LOG = Logger.getLogger(JPanelSchoolsandRolesProperties.class.getName());
    private SchoolRoleAndClass selectedSrc;
    private SchoolsRolesAndClasses srcs;

    public void init() throws Dwo2Exception {
        try {
            srcs = SecureUserAccountLoginsManager.getSchoolLogins();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        } catch (Dwo2Exception ex) {
            
            LOG.log(Level.SEVERE, ex.getMessage());
            srcs = new SchoolsRolesAndClasses();
            selectedSrc=null;
            throw ex;
        }
    }

    /**
     * @return the user
     */
    public SchoolsRolesAndClasses getSchoolsRolesAndClasses() {
        return srcs;
    }

    /**
     * Sets the selected SchoolRoleAndClassCombination in the PeristentStore.
     */
    public void setActiveSchoolRoleAndClass() {
        try {
            SchoolRoleAndClass src = SecureUserAccountLoginsManager.switchToSchoolLogin(getSelectedSchoolRoleAndClass());
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
