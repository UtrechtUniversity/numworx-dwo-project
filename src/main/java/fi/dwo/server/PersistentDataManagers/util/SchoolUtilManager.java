/**
 * Copyrighted Oct 2, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolUtilManager {

    private static final Logger LOG = Logger.getLogger(SchoolUtilManager.class.getName());

    /**
     * Returns the students in a school to be displayed.
     *
     * @param school
     * @param role
     * @return
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static List<PersistentUser> getUsersInRoleInSchool(PersistentSchool school, RoleType role) throws Dwo2Exception {
        List<PersistentUser> users = null;

        List<PersistentHasRole> hrList;
        hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, role);
        for (PersistentHasRole hr : hrList) {
            users.add(UserManager.findEntity(hr.getPersistentHasRolePK().getUserID()));
        }
        return users;
    }

    /**
     * Adds the user to the database and places him in the school. User is
     * expected to be fully initialized.
     *
     * @param user
     * @param school
     * @return
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static Boolean addSingleSchoolStudentAccount(PersistentUser user, PersistentSchool school) throws Dwo2Exception {
        if (user == null || school == null) {
            LOG.log(Level.SEVERE, "User or school parameter is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        if (sg == null) {
            LOG.log(Level.SEVERE, "Missing schoolgroup in database.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        //check if user single school student user
        if (!user.isSingleSchoolAccount()) {
            LOG.log(Level.SEVERE, "User not a single school user.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        Date now = DwoDateUtilities.getCurrentDwoDate();
        //rewrite some user data
        user.setRegisterDate(now);
        user.setSchoolGroupID(sg.getSchoolGroupID());
        user.setSingleSchoolAccount(true);

        try {
            UserManager.create(user);
            user = UserManager.findByUserName(user.getUsername());
        }
        catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }  

        if(user==null){
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User created was not found.");
        }
        
        //make key
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getUserID());

        PersistentHasRole hr = new PersistentHasRole();
        hr.setPersistentHasRolePK(pk);
        hr.setLastLogin(null);
        hr.setRegisterDate(now);
        hr.setRights("_");
        hr.setUser(user);

        try {
            HasRoleManager.create(hr);
        }
        catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        
        return true;
    }
    
}
