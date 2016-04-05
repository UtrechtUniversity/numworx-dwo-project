/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class AccountSchoolsRolesProperties {

    private static final Logger LOG = Logger.getLogger(AccountSchoolsRolesProperties.class.getName());
    private DomSchoolRoleAndClass selectedSrc;
    private DomSchoolsRolesAndClasses srcs;

    public void init() throws Dwo2Exception {
        try {
            srcs = SecureUserAccountLoginsManager.getSchoolLogins();
            selectedSrc = srcs.getActiveSchoolRoleAndClass();
        }
        catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, ex.getMessage());
            srcs = new DomSchoolsRolesAndClasses();
            selectedSrc = null;
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
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void setActiveSchoolRoleAndClass() throws Dwo2Exception {
            DomSchoolRoleAndClass src = SecureUserAccountLoginsManager.switchToSchoolLogin(getSelectedSchoolRoleAndClass());
            srcs.setActiveSchoolRoleAndClass(src);
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

    public void RemoveSchoolRoleAndClass(DomSchoolRoleAndClass selectedSrac) throws Dwo2Exception {
        boolean result;
        result = SecureUserAccountLoginsManager.removeASchoolLogin(selectedSrac);
        init();
    }

}
