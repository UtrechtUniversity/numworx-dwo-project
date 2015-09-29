/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.rest.util;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.SchoolGroupManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks if a user is in a certain role context.
 *
 * @author G.A.J. van der Plas
 */
public class JoinDataManager {

    private static final Logger LOG = Logger.getLogger(JoinDataManager.class.getName());

    /**
     * Checks if the user is in the given role. Returns the hasRolefor the
     * SchoolGroup registered in the {@Link PersistentUser} having the usercode.
     * It returns null if false.
     *
     * @param usercode Mostly the principal username from the REST-interface.
     * @param r
     * @return
     */
    public static PersistentHasRole getCurrentHasRole(String usercode, RoleType r) throws Dwo2Exception {
        PersistentUser u = UserManager.findByUserName(usercode);
        if (u == null || u.getSchoolGroupID() == null) {
            LOG.log(Level.SEVERE, "Given user or schoolGroup for userlogin {0} could not be found.", new Object[]{usercode});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "User or SchoolGroup for userlogin could not be found.");
        }

        PersistentHasRolePK hrKey = new PersistentHasRolePK(u.getUserID(), u.getSchoolGroupID());
        PersistentHasRole hr = HasRoleManager.findEntity(hrKey);
        if (hr == null) {
            LOG.log(Level.SEVERE, "Current HasRole of user for userlogin {0} could not be found.", new Object[]{usercode});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "HasRole could not be found.");
        }
        int roleId = 0;
        try {
            roleId = hr.getSchoolGroup().getRole().getGroupID();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.", new Object[]{usercode, hr.getPersistentHasRolePK()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
        }
        if (roleId != r.ordinal()) {
            return null;
        }
        return hr;
    }

    /**
     * Checks if the user is in the given role. Returns the hasRole, null if
     * false.
     *
     * @param usercode
     * @param r
     * @return
     */
    public static PersistentSchool getSchoolforHasRole(PersistentHasRole hr) throws Dwo2Exception {
        if (hr == null || hr.getPersistentHasRolePK() == null) {
            LOG.log(Level.SEVERE, "Given hasRole {0} could not be found.", new Object[]{hr.getPersistentHasRolePK()});
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

        PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(user.getUserID(), sg.getSchoolGroupID()));
        if (hr == null) {
            LOG.log(Level.SEVERE, "hasRole of userId {0} and schoolGroupId {1} could not be found.", new Object[]{user.getUserID(), sg.getSchoolGroupID()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "HasRole could not be found.");
        }
        return hr;
    }
}
