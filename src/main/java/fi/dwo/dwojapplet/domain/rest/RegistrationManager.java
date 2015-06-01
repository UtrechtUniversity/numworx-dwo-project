package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.rest.entities.*;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 * 
 * @author G.A.J. van der Plas
 */
public class RegistrationManager {
    private static final Logger LOG = Logger.getLogger(RegistrationManager.class.getName());
    
    public static boolean RegisterNewUser(NewUserRegistration newUserReg) throws RestException{
        boolean r;
             r = StoredRestManager.getInstance().put("/rest/secure/user/schoolsrolesandclasses/update/json", SchoolRoleAndClass.class, src);
       r = StoredRestManager.getInstance().get("/rest/secure/user/schoolsrolesandclasses/get/json", Boolean.class);
        return r;
    }   
    public static boolean RegisterExistingUser(NewUserRegistration newUserReg) throws RestException{
        boolean r;
        f
             r = StoredRestManager.getInstance().put("/rest/secure/user/schoolsrolesandclasses/update/json", SchoolRoleAndClass.class, src);
       r = StoredRestManager.getInstance().get("/rest/secure/user/schoolsrolesandclasses/get/json", Boolean.class);
        return r;
    }   
    
}
