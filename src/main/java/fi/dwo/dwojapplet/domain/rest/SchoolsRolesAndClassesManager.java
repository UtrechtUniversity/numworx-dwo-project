package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 * 
 * @author G.A.J. van der Plas
 */
public class SchoolsRolesAndClassesManager {
    private static final Logger LOG = Logger.getLogger(SchoolsRolesAndClassesManager.class.getName());
    
    /**
    * Returns the current user 'logged in'. The information is extracted from the 
    * security context which depends on the credentials used for accessing the rest
    * interface. Technically it should be equal to the data in the DwoHelper.
    * 
    * @return 
    */
    public static SchoolsRolesAndClasses getCurrentEnlistements() throws Dwo2Exception{
        SchoolsRolesAndClasses src;
        src= StoredRestManager.getInstance().get("/rest/secure/user/schoolsrolesandclasses/get/json", SchoolsRolesAndClasses.class);
        return src;
    }   
    
    /**
     * Updates the user profile of a user. 
     * 
     * Fields updated are email, password and the full name of the user. The full
     * name exists out of the first, insertion and family name.
     * 
     * @param user
     * @return 
     */

    public static SchoolRoleAndClass setActiveSchoolRoleAndClass(SchoolRoleAndClass src) throws Dwo2Exception {
            src = StoredRestManager.getInstance().put("/rest/secure/user/schoolsrolesandclasses/update/json", SchoolRoleAndClass.class, src);
            //LOG.log(Level.FINE, "Updated active role  of username {0}.",new Object[]{src.getRoleId()});
        return src;
    }
}
