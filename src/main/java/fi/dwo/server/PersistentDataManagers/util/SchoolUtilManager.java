/**
 * Copyrighted Oct 2, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 *
 * @author Gert van der Plas
 */
public class SchoolUtilManager {

    private static final Logger LOG = Logger.getLogger(SchoolUtilManager.class.getName());

//Slow use code in UserUtilManager.    
//    /**
//     * Returns the students in a school to be displayed.
//     *
//     * @param school
//     * @param role
//     * @return
//     * @throws fi.dwo.rest.exceptions.Dwo2Exception
//     */
//    public static List<PersistentUser> getUsersInRoleInSchool(PersistentSchool school, RoleType role) throws Dwo2Exception {
//        List<PersistentUser> users = null;
//
//        List<PersistentHasRole> hrList;
//        hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, role);
//        for (PersistentHasRole hr : hrList) {
//            users.add(UserManager.findEntity(hr.getPersistentHasRolePK().getUserID()));
//        }
//        return users;
//    }
    /**
     * Adds the user to the database and places him in the school. User is
     * expected to be fully initialized.
     *
     * @param user
     * @param school
     * @param schoolClass the default school class in the hasRole
     * @return
     * @throws Dwo2Exception
     */
    public static Boolean addSingleSchoolStudentAccount(PersistentUser user, PersistentSchool school, PersistentSchoolClass schoolClass) throws Dwo2Exception {
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
        user.setSchoolGroupId(sg.getSchoolGroupID());
        user.setSingleSchoolAccount(true);

        try {
            UserManager.create(user);
        } catch (PersistenceException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        }
        try {
            user = UserManager.findByUserName(user.getUsername());
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        if (user == null) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User created was not found.");
        }

        //make key
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getId());

        PersistentHasRole hr = new PersistentHasRole();
        hr.setPersistentHasRolePK(pk);
        hr.setLastLogin(null);
        hr.setRegisterDate(now);
        if (schoolClass != null) {
            hr.setClassID(schoolClass.getClassID());
        }

        hr.setRights("_");
        hr.setUser(user);

        try {
            HasRoleManager.create(hr);
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        return true;
    }

    /**
     * Adds the user to the database and places him in the school. User is
     * expected to be fully initialized.
     *
     * @param user
     * @param school
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean addSingleSchoolStudentAccount(PersistentUser user, PersistentSchool school) throws Dwo2Exception {
    
        //check if user single school student user
        if (!user.isSingleSchoolAccount()) {
            LOG.log(Level.SEVERE, "User not a single school user.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        return addAccountAsStudentInSchool(user, school);
    }

    /**
     * Adds the user to the database and places him in the school. User is
     * expected to be fully initialized.
     *
     * @param user
     * @param school
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean addAccountAsTeacherInSchool(PersistentUser user, PersistentSchool school) throws Dwo2Exception {
        if (user == null || school == null) {
            LOG.log(Level.SEVERE, "User or school parameter is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.TEACHER);
        if (sg == null) {
            LOG.log(Level.SEVERE, "Missing schoolgroup in database.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        //check if user single school student user
        if (user.isSingleSchoolAccount()) {
            LOG.log(Level.SEVERE, "User is a single school user.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        Date now = DwoDateUtilities.getCurrentDwoDate();
        //rewrite some user data
        user.setRegisterDate(now);
        user.setSchoolGroupId(sg.getSchoolGroupID());
        user.setSingleSchoolAccount(false);

        try {
            UserManager.create(user);
        } catch (PersistenceException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        }
        try {
            user = UserManager.findByUserName(user.getUsername());
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        if (user == null) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User created was not found.");
        }

        //make key
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getId());

        PersistentHasRole hr = new PersistentHasRole();
        hr.setPersistentHasRolePK(pk);
        hr.setLastLogin(null);
        hr.setRegisterDate(now);
        hr.setRights("_");
        hr.setUser(user);

        try {
            HasRoleManager.create(hr);
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        //building hasRole for null school
        PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
        Long schoolGroupId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
        pk.setSchoolGroupID(schoolGroupId);
        pk.setUserID(user.getId());
        hr.setPersistentHasRolePK(pk);

        hr.setClassID(null);
        hr.setLastLogin(now); //considering an account creation a first login as there is a password
        hr.setRegisterDate(now);
        hr.setRights("_"); //TODO make a rights manager
        HasRoleManager.create(hr);

        LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2}  was added to the database.", new Object[]{hr.getPersistentHasRolePK().getUserID(), hr.getPersistentHasRolePK().getSchoolGroupID(), sg.getRole().getGroupname()});

        return true;
    }

    /**
     * Adds the user to the database and places him in the school. User is
     * expected to be fully initialized, including the singleschoolflag!
     *
     * @param user
     * @param school
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean addAccountAsStudentInSchool(PersistentUser user, PersistentSchool school) throws Dwo2Exception {
        if (user == null || school == null) {
            LOG.log(Level.SEVERE, "User or school parameter is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        if (sg == null) {
            LOG.log(Level.SEVERE, "Missing schoolgroup in database.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        Date now = DwoDateUtilities.getCurrentDwoDate();
        //rewrite some user data
        user.setRegisterDate(now);
        user.setSchoolGroupId(sg.getSchoolGroupID());

        try {
            UserManager.create(user);
        } catch (PersistenceException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        }
        try {
            user = UserManager.findByUserName(user.getUsername());
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        if (user == null) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User created was not found.");
        }

        //make key
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getId());

        PersistentHasRole hr = new PersistentHasRole();
        hr.setPersistentHasRolePK(pk);
        hr.setLastLogin(null);
        hr.setRegisterDate(now);
        hr.setRights("_");
        hr.setUser(user);

        try {
            HasRoleManager.create(hr);
            LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2}  was added to the database.", new Object[]{hr.getPersistentHasRolePK().getUserID(), hr.getPersistentHasRolePK().getSchoolGroupID(), sg.getRole().getGroupname()});
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        if (!user.isSingleSchoolAccount()) {
            //building hasRole for null school if not a single school student.
            PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
            Long schoolGroupId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
            pk.setSchoolGroupID(schoolGroupId);
            pk.setUserID(user.getId());
            hr.setPersistentHasRolePK(pk);

            hr.setClassID(null);
            hr.setLastLogin(now); //considering an account creation a first login as there is a password
            hr.setRegisterDate(now);
            hr.setRights("_"); //TODO make a rights manager
            HasRoleManager.create(hr);
            LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2}  was added to the database.", new Object[]{hr.getPersistentHasRolePK().getUserID(), hr.getPersistentHasRolePK().getSchoolGroupID(), sg.getRole().getGroupname()});
        }

        return true;
    }
    

    /**
     * Adds the user to the database and places him in the school by creating a HasRole entry. User is
     * expected to be fully initialized, including the singleschoolflag!
     *
     * @param user
     * @param school
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean addAccountInSchool(PersistentUser user, PersistentSchool school, RoleType role) throws Dwo2Exception {
        if (user == null || school == null) {
            LOG.log(Level.SEVERE, "User or school parameter is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, role);
        if (sg == null) {
            LOG.log(Level.SEVERE, "Missing schoolgroup in database.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        Date now = DwoDateUtilities.getCurrentDwoDate();
        //rewrite some user data
        user.setRegisterDate(now);
        user.setSchoolGroupId(sg.getSchoolGroupID());

        try {
            UserManager.create(user);
        } catch (PersistenceException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        }
        try {
            user = UserManager.findByUserName(user.getUsername());
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        if (user == null) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User created was not found.");
        }

        //make key
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getId());

        PersistentHasRole hr = new PersistentHasRole();
        hr.setPersistentHasRolePK(pk);
        hr.setLastLogin(null);
        hr.setRegisterDate(now);
        hr.setRights("_");
        hr.setUser(user);

        try {
            HasRoleManager.create(hr);
            LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2}  was added to the database.", new Object[]{hr.getPersistentHasRolePK().getUserID(), hr.getPersistentHasRolePK().getSchoolGroupID(), sg.getRole().getGroupname()});
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "User creation failed.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        if (!user.isSingleSchoolAccount()) {
            //building hasRole for null school if not a single school student.
            PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
            Long schoolGroupId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
            pk.setSchoolGroupID(schoolGroupId);
            pk.setUserID(user.getId());
            hr.setPersistentHasRolePK(pk);

            hr.setClassID(null);
            hr.setLastLogin(now); //considering an account creation a first login as there is a password
            hr.setRegisterDate(now);
            hr.setRights("_"); //TODO make a rights manager
            HasRoleManager.create(hr);
            LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2}  was added to the database.", new Object[]{hr.getPersistentHasRolePK().getUserID(), hr.getPersistentHasRolePK().getSchoolGroupID(), sg.getRole().getGroupname()});
        }

        return true;
    }    
}
