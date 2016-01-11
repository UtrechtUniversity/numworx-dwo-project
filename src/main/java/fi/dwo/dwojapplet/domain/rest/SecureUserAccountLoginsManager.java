package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomNewSchoolLogin;
import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountLoginsManager {

    private static final Logger LOG = Logger.getLogger(SecureUserAccountLoginsManager.class.getName());

    /**
     * Returns the current user 'logged in'. The information is extracted from
     * the security context which depends on the credentials used for accessing
     * the rest interface. Technically it should be equal to the data in the
     * DwoHelper.
     *
     * @return
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static DomSchoolsRolesAndClasses getSchoolLogins() throws Dwo2Exception {
        DomSchoolsRolesAndClasses src;
        src = StoredRestManager.getInstance().get("/rest/secure/user/account/logins/getList", DomSchoolsRolesAndClasses.class);
        //update local copy
        DwoHelper.setSchoolLogins(src);        
        return src;
    }

    /**
     * Switches to the schoollogin requested.
     *
     * @param src
     * @return
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static DomSchoolRoleAndClass switchToSchoolLogin(DomSchoolRoleAndClass src) throws Dwo2Exception {
        RestSchoolRoleAndClass rsrc = new RestSchoolRoleAndClass();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(src);
        DomSchoolRoleAndClass result = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/select", DomSchoolRoleAndClass.class, rsrc);
        //update local copy
        DwoHelper.getSchoolLogins().setActiveSchoolRoleAndClass(src);
        return result;
    }

    /**
     *
     * @param newSchoolLogin
     * @return
     */
    public static boolean addASchoolLogin(DomNewSchoolLogin newSchoolLogin) throws Dwo2Exception {
        boolean r;
        RestNewSchoolLogin rnl = new RestNewSchoolLogin();
        rnl.setRestContext(new DomContext());
        rnl.setDomNewSchoolLogin(newSchoolLogin);
        r = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/submit", Boolean.class, rnl);
        DwoHelper.setSchoolLogins(getSchoolLogins());        
        return r;
    }

    /**
     *
     * @param toRemoveSchoolLogin
     * @return
     */
    public static boolean removeASchoolLogin(DomNewSchoolLogin toRemoveSchoolLogin) throws Dwo2Exception {
        RestNewSchoolLogin rsrc = new RestNewSchoolLogin();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomNewSchoolLogin(toRemoveSchoolLogin);
        boolean r;
        r = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/delete", Boolean.class, rsrc);
        DwoHelper.setSchoolLogins(getSchoolLogins());        
        return r;

    }

}
