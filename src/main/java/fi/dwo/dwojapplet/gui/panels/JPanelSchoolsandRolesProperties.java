/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.panels;

import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Gert van der Plas
 */
public class JPanelSchoolsandRolesProperties {

    private static final Logger LOG = Logger.getLogger(JPanelSchoolsandRolesProperties.class.getName());
    private DomSchoolRoleAndClass selectedSrc;
    private DomSchoolsRolesAndClasses srcs;

    public void init() throws Dwo2Exception {
        try {
            srcs = SecureUserAccountLoginsManager.getSchoolLogins();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        } catch (Dwo2Exception ex) {
            
            LOG.log(Level.SEVERE, ex.getMessage());
            srcs = new DomSchoolsRolesAndClasses();
            selectedSrc=null;
            throw ex;
        }
    }

    /**
     * @return the user
     */
    public DomSchoolsRolesAndClasses getSchoolsRolesAndClasses() {
        return srcs;
    }

    /**
     * Sets the selected SchoolRoleAndClassCombination in the PeristentStore.
     */
    public void setActiveSchoolRoleAndClass() {
        try {
            DomSchoolRoleAndClass src = SecureUserAccountLoginsManager.switchToSchoolLogin(getSelectedSchoolRoleAndClass());
            srcs.setActiveSchoolRoleAndClass(src);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

        /**
     * @return 
     */
    public DomSchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return srcs.getActiveSchoolRoleAndClass();
    }

    /**
     * @return the selectedSrc
     */
    public DomSchoolRoleAndClass getSelectedSchoolRoleAndClass() {
        return selectedSrc;
    }

    /**
     * @param selectedSrc the selectedSrc to set
     */
    public void setSelectedSchoolRoleAndClass(DomSchoolRoleAndClass selectedSrc) {
        this.selectedSrc = selectedSrc;
    }

    
}
