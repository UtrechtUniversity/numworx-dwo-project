/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.rest.util;

import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.SchoolGroupManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;

/**
 * Checks if a user is in a certain role context.
 *
 * @author G.A.J. van der Plas
 */
public class JoinDataManager {

    /**
     * Checks if the user is in the given role. Returns the hasRolefor the SchoolGroup
     * registered in the {@Link PersistentUser} having the usercode. It returns 
     * null if false.
     *
     * @param usercode Mostly the principal username from the REST-interface.
     * @param r
     * @return 
     */
    public static  PersistentHasRole getCurrentHasRole(String usercode, RoleType r) {
        PersistentUser u = UserManager.findByUserName(usercode);
        if (u == null || u.getSchoolGroupID()==null) {
            return null;
        }

        PersistentHasRolePK hrKey = new PersistentHasRolePK(u.getUserID(), u.getSchoolGroupID());
        PersistentHasRole hr = HasRoleManager.findEntity(hrKey);
        int roleId = hr.getSchoolGroup().getRole().getGroupID();
        if(roleId != r.ordinal()) return null;
        return hr;
    }
    
    /**
     * Checks if the user is in the given role. Returns the hasRole,  null if false.
     *
     * @param usercode
     * @param r
     * @return 
     */
    public static  PersistentSchool getSchoolforHasRole(PersistentHasRole hr) {
        if (hr == null || hr.getPersistentHasRolePK()==null) {
            return null;
        }
        
        PersistentSchoolGroup sg = SchoolGroupManager.findEntity(hr.getPersistentHasRolePK().getSchoolGroupID());
        if(sg==null) return null;
        
        PersistentSchool s = sg.getSchool();
        return s;
    }

    public static PersistentHasRole getHasRoleInSchool(PersistentUser teacher, PersistentSchool school, RoleType roleType) {
        if ( teacher== null || school==null|| roleType ==null) {
            return null;
        }

        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, roleType);
        if(sg==null) return null;
        
        return HasRoleManager.findEntity(new PersistentHasRolePK(teacher.getUserID(),sg.getSchoolGroupID()));
    }
    
  
}
