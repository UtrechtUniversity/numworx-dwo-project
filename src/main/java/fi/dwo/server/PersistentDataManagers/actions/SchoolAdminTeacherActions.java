/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentScoContext;

/**
 * Actions an authenticated user may do. The basic use cases.
 * 
 * @author Gert van der Plas
 */
public interface SchoolAdminTeacherActions extends UserActions{

  int countStudents(PersistentHasRole hasRole, PersistentScoContext scoCtx);

}
