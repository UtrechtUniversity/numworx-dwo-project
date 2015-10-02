/**
 * Copyrighted Oct 2, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.List;
import java.util.logging.Logger;

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

}
