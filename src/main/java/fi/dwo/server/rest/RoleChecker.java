/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;

/**
 * Checks if a user is in a certain role context.
 *
 * @author G.A.J. van der Plas
 */
public class RoleChecker {

    /**
     * Checks if the user is in the given role. Returns the hasRole,  null if false.
     *
     * @param usercode
     * @param r
     * @return 
     */
    public static  PersistentHasRole getCurrentRole(String usercode, RoleType r) {
        PersistentUser u = UserManager.findByUserName(usercode);
        if (u == null) {
            return null;
        }

        PersistentHasRolePK hrKey = new PersistentHasRolePK(u.getUserID(), u.getSchoolGroupID());
        PersistentHasRole hr = HasRoleManager.findEntity(hrKey);
        int roleId = hr.getSchoolGroup().getRole().getGroupID();
        if(roleId != r.ordinal()) return null;
        return hr;
    }

}
