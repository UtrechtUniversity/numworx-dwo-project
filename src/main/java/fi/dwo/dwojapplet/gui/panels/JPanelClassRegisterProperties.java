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
public class JPanelClassRegisterProperties {

    private static final Logger LOG = Logger.getLogger(JPanelClassRegisterProperties.class.getName());
    private RestSchoolRoleAndClass selectedSrc;
    private RestSchoolsRolesAndClasses srcs;

    public void init() {
        try {
            srcs = SecureUserAccountLoginsManager.getSchoolLogins();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * @return the user
     */
    public RestSchoolsRolesAndClasses getSchoolsRolesAndClasses() {
        return srcs;
    }

    /**
     */
    public void setActiveSchoolRoleAndClass() {
        try {
            RestSchoolRoleAndClass src = SecureUserAccountLoginsManager.switchToSchoolLogin(getSelectedSchoolRoleAndClass());
            srcs.setActiveSchoolRoleAndClass(src);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

        /**
     * @return 
     */
    public RestSchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return srcs.getActiveSchoolRoleAndClass();
    }

    /**
     * @return the selectedSrc
     */
    public RestSchoolRoleAndClass getSelectedSchoolRoleAndClass() {
        return selectedSrc;
    }

    /**
     * @param selectedSrc the selectedSrc to set
     */
    public void setSelectedSchoolRoleAndClass(RestSchoolRoleAndClass selectedSrc) {
        this.selectedSrc = selectedSrc;
    }

    
}
