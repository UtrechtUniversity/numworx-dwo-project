package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 * 
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountLoginsManager {
    private static final Logger LOG = Logger.getLogger(SecureUserAccountLoginsManager.class.getName());
    
    /**
    * Returns the current user 'logged in'. The information is extracted from the 
    * security context which depends on the credentials used for accessing the rest
    * interface. Technically it should be equal to the data in the DwoHelper.
    * 
    * @return 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception 
    */
    public static SchoolsRolesAndClasses getSchoolLogins() throws Dwo2Exception{
        SchoolsRolesAndClasses src;
        src= StoredRestManager.getInstance().get("/rest/secure/user/account/logins/getlist", SchoolsRolesAndClasses.class);
        return src;
    }   
    
    /**
     * Switches to the schoollogin requested. 
     * 
     * @param src
     * @return 
     * @throws fi.dwo.commons.exceptions.Dwo2Exception 
     */

    public static SchoolRoleAndClass switchToSchoolLogin(SchoolRoleAndClass src) throws Dwo2Exception {
            src = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/switch", SchoolRoleAndClass.class, src);
            //LOG.log(Level.FINE, "Updated active role  of username {0}.",new Object[]{src.getRoleId()});
        return src;
    }
    
    /**
     *
     * @param existingUserReg
     * @return
     */
    public static boolean addASchoolLogin(KnownUserRegistration existingUserReg) throws Dwo2Exception {
        boolean r;
        r = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/submit", Boolean.class, existingUserReg);

        return r;

    }    


        /**
     *
     * @param existingUserReg
     * @return
     */
    public static boolean removeASchoolLogin(KnownUserRegistration existingUserReg) throws Dwo2Exception {
        boolean r;
        r = StoredRestManager.getInstance().put("/rest/secure/user/account/logins/delete", Boolean.class, existingUserReg);

        return r;

    }    

}
