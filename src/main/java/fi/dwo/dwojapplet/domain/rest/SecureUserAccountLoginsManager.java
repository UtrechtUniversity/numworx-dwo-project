package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.entities.RestNewSchoolLogin;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import fi.dwo.rest.entities.RestSchoolRoleAndClassV2;
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
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomSchoolsRolesAndClassesV2 getSchoolLogins() throws Dwo2Exception {
        DomSchoolsRolesAndClassesV2 src;
        src = StoredRestManager.getInstance().get("rest/secure/user/account/loginsV2/getList", DomSchoolsRolesAndClassesV2.class);
        //update local copy
        DwoHelper.setSchoolLogins(src);        
        return src;
    }

    /**
     * Switches to the schoollogin requested.
     *
     * @param src
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomSchoolRoleAndClassV2 switchToSchoolLogin(DomSchoolRoleAndClassV2 src) throws Dwo2Exception {
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(src);
        DomSchoolRoleAndClassV2 result = StoredRestManager.getInstance().put("rest/secure/user/account/loginsV2/select", DomSchoolRoleAndClassV2.class, rsrc);
        //update local copy
        DwoHelper.getSchoolLogins().setActiveSchoolRoleAndClass(src);
        return result;
    }

    /**
     *
     * @param newSchoolLogin
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static boolean addASchoolLogin(DomNewSchoolLogin newSchoolLogin) throws Dwo2Exception {
        boolean r;
        RestNewSchoolLogin rnl = new RestNewSchoolLogin();
        rnl.setRestContext(new DomContext());
        rnl.setDomNewSchoolLogin(newSchoolLogin);
        r = StoredRestManager.getInstance().put("rest/secure/user/account/loginsV2/submit", Boolean.class, rnl);
        DwoHelper.setSchoolLogins(getSchoolLogins());        
        return r;
    }

    /**
     *
     * @param toRemoveSchoolLogin
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static boolean removeASchoolLogin(DomSchoolRoleAndClassV2 toRemoveSchoolLogin) throws Dwo2Exception {
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(toRemoveSchoolLogin);
        boolean r;
        r = StoredRestManager.getInstance().put("rest/secure/user/account/loginsV2/remove", Boolean.class, rsrc);
        DwoHelper.setSchoolLogins(getSchoolLogins());        
        return r;

    }

}
