/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks if a user is in a certain role context.
 *
 * @author G.A.J. van der Plas
 */
public class HasRoleUtilManager {

    private static final Logger LOG = Logger.getLogger(HasRoleUtilManager.class.getName());

    /**
     * Checks if the user is in the given role. Returns the hasRolefor the
     * SchoolGroup registered in the {@Link PersistentUser} having the usercode.
     * It returns null if false.
     *
     * @param usercode Mostly the principal username from the REST-interface.
     * @param r
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static PersistentHasRole getCurrentHasRole(String usercode, RoleType r) throws Dwo2Exception {
        PersistentUser u = (PersistentUser) UserManager.findByUserName(usercode);
        if (u == null || u.getSchoolGroupId() == null) {
            LOG.log(Level.SEVERE, "Given user or schoolGroup for userlogin {0} could not be found.", new Object[]{usercode});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User or SchoolGroup for userlogin could not be found.");
        }

        PersistentHasRolePK hrKey = new PersistentHasRolePK(u.getId(), u.getSchoolGroupId());
        PersistentHasRole hr = (PersistentHasRole) HasRoleManager.findEntity(hrKey);
        if (hr == null) {
            LOG.log(Level.SEVERE, "Current HasRole of user for userlogin {0} could not be found.", new Object[]{usercode});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "HasRole could not be found.");
        }
        Long roleId = 0L;
        try {
            roleId = hr.getSchoolGroup().getRole().getGroupID();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.", new Object[]{usercode, hr.getPersistentHasRolePK()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
        }
        if ( roleId.intValue() != r.ordinal()) {
            return null;
        }
        return hr;
    }

    /**
     * Checks if the user is in the given role. Returns the hasRole, null if
     * false.
     *
     * @param hr
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static PersistentSchool getSchoolforHasRole(PersistentHasRole hr) throws Dwo2Exception {
        if (hr == null || hr.getPersistentHasRolePK() == null) {
            LOG.log(Level.SEVERE, "Given hasRole {0} could not be found.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Given hasRole could not be found.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findEntity(hr.getPersistentHasRolePK().getSchoolGroupID());
        if (sg == null) {
            LOG.log(Level.SEVERE, "SchoolGroup of hasRole {0} could not be found.", new Object[]{hr.getPersistentHasRolePK()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "SchoolGroup could not be found.");
        }

        PersistentSchool s = sg.getSchool();
        if (s == null) {
            LOG.log(Level.SEVERE, "School of schoolGroupId {0} could not be found.", new Object[]{sg.getSchoolGroupID()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "School could not be found.");
        }
        return s;
    }

    public static PersistentHasRole getHasRoleInSchool(PersistentUser user, PersistentSchool school, RoleType roleType) throws Dwo2Exception {
        if (user == null || school == null || roleType == null) {
            LOG.log(Level.SEVERE, "School, user or  roleType parameters are invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, roleType);
        if (sg == null) {
            LOG.log(Level.SEVERE, "schoolGroup of schoolId {0} and roleType {1} could not be found.", new Object[]{school.getSchoolID(), roleType.name()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "SchoolGroup could not be found.");
        }

        PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(user.getId(), sg.getSchoolGroupID()));
        if (hr == null) {
            LOG.log(Level.SEVERE, "hasRole of userId {0} and schoolGroupId {1} could not be found.", new Object[]{user.getId(), sg.getSchoolGroupID()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "HasRole could not be found.");
        }
        return hr;
    }

    public static List<PersistentHasRole> getHasRolesInSchoolAndRole(PersistentSchool school, RoleType roleType) throws Dwo2Exception {
        if (school == null && roleType == null) {
            LOG.log(Level.SEVERE, "School or role parameter is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, roleType);
        if (sg == null) {
            LOG.log(Level.SEVERE, "schoolGroup of schoolId {0} and roleType {1} could not be found.", new Object[]{school.getSchoolID(), roleType.name()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "SchoolGroup could not be found.");
        }

        List<PersistentHasRole> hrList = HasRoleManager.findEntities(sg);
        return hrList;
    }

 public static Boolean removeHasRoleAndItsData(PersistentHasRole hr) throws Dwo2Exception{
        if(hr==null){
            LOG.log(Level.SEVERE, "HasRole parameter is NULL.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Operation could no longer be executed.");
        }

        List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentStudentScoContext ssc : sscList) {
            StudentScoDataManager.destroy(ssc.getStudentSco());
            StudentScoContextManager.destroy(ssc.getStudentSco());
        }
        //Remove StudentOf and TeacherOf
        List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentStudentOfClass so : soList) {
            StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
        }
        List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentTeacherOfClass to : toList) {
            TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
        }
        //Ready to remove hasRoles
        HasRoleManager.destroy(hr.getPersistentHasRolePK());
  

        PersistentUser user = UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
        if(user==null){
            LOG.log(Level.SEVERE, "User with id {0} disappeared.", new Object[]{hr.getPersistentHasRolePK().getUserID()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User could not be found.");
        }
        //Update the default hasRole to the null school if user is in the current role.
        if(user.getSchoolGroupId().equals(hr.getPersistentHasRolePK().getSchoolGroupID())) //userid's already match...
        {
            RoleType type = RoleType.STUDENT;
            PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(SchoolManager.findBySchoolLogin("null"), type);
            user.setSchoolGroupId(sg.getSchoolGroupID());
        }

        UserManager.edit(user);

        return true;
 }
}
