/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class AccountSchoolsRolesProperties {

    private static final Logger LOG = Logger.getLogger(AccountSchoolsRolesProperties.class.getName());
    private DomSchoolRoleAndClassV2 selectedSrc;
    private DomSchool nullSchool;
    private DomSchoolsRolesAndClassesV2 srcs;

    public void init() throws Dwo2Exception {
        try {
            srcs = SecureUserAccountLoginsManager.getSchoolLogins();            
            DwoHelper.setSchoolLogins(srcs);
            setNullSchool(srcs.getNullSchool());            
            selectedSrc = srcs.getActiveSchoolRoleAndClass();            
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            srcs = new DomSchoolsRolesAndClassesV2();
            selectedSrc = null;
            throw ex;
        }
    }

    /**
     * @return the user
     */
    public DomSchoolsRolesAndClassesV2 getSchoolsRolesAndClasses() {
        return srcs;
    }

    /**
     * Sets the selected SchoolRoleAndClassCombination in the PeristentStore.
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void setActiveSchoolRoleAndClass() throws Dwo2Exception {
            DomSchoolRoleAndClassV2 src = SecureUserAccountLoginsManager.switchToSchoolLogin(getSelectedSchoolRoleAndClass());
            srcs.setActiveSchoolRoleAndClass(src);
            DwoHelper.getSchoolLogins().setActiveSchoolRoleAndClass(src);
            DwoHelper.getCurrentLoginContext().setHasRoleId(src.getHasRole().getId());
            DwoHelper.getCurrentLoginContext().setSchoolGroupId(src.getHasRole().getSchoolGroupId());
    }

    /**
     * @return
     */
    public DomSchoolRoleAndClassV2 getActiveSchoolRoleAndClass() {
        return srcs.getActiveSchoolRoleAndClass();
    }

    /**
     * @return the selectedSrc
     */
    public DomSchoolRoleAndClassV2 getSelectedSchoolRoleAndClass() {
        return selectedSrc;
    }

    /**
     * @param selectedSrc the selectedSrc to set
     */
    public void setSelectedSchoolRoleAndClass(DomSchoolRoleAndClassV2 selectedSrc) {
        this.selectedSrc = selectedSrc;
    }

    public Boolean RemoveSchoolRoleAndClass(DomSchoolRoleAndClassV2 selectedSrac) throws Dwo2Exception {
        Boolean result;
        result = SecureUserAccountLoginsManager.removeASchoolLogin(selectedSrac);
//        DomSchoolsRolesAndClassesV2 srcs = getSchoolLogins(); //calls and sets DwoHelper
//        DwoHelper.setSchoolLogins(srcs);
        init();
        return result;
    }

    /**
     * @return the nullSchool
     */
    public DomSchool getNullSchool() {
        return nullSchool;
    }

    /**
     * @param nullSchool the nullSchool to set
     */
    public void setNullSchool(DomSchool nullSchool) {
        this.nullSchool = nullSchool;
    }

}
