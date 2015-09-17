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
 * Checks if a user is in a certain role context
 * @author G.A.J. van der Plas
 */
public class RoleChecker {
    
    /**
     * Checks if the role is correct.
     * 
     * @param name 
     */
    public static boolean isInRole(String usercode,RoleType r){
        PersistentUser u = UserManager.findByUserName(usercode);
        PersistentHasRolePK hrKey = new PersistentHasRolePK(u.getUserID(), u.getSchoolGroupID());
        PersistentHasRole hr=HasRoleManager.findEntity(hrKey);
        int roleId = hr.getSchoolGroup().getRole().getGroupID();
        
        return roleId==r.ordinal();
    }
    
}
