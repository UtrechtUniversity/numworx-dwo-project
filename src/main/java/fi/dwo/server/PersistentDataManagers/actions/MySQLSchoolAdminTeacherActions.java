/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;

/**
 * Actions an authenticated user may do. The basic use cases.
 * 
 * @author Gert van der Plas
 */
public class MySQLSchoolAdminTeacherActions extends MySQLUserActions implements SchoolAdminTeacherActions {
  public int countStudents(PersistentHasRole hasRole, PersistentScoContext scoCtx) {
    PersistentHasRolePK role = hasRole.getPersistentHasRolePK();
    return (int) StudentScoContextManager.getEntityCount(scoCtx, role);
  }

}
