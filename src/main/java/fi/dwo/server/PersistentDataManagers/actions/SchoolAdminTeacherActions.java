/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import java.util.List;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.Context;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Actions an authenticated user may do. The basic use cases.
 * 
 * @author Gert van der Plas
 */
public interface SchoolAdminTeacherActions extends UserActions {

  int countStudents(PersistentHasRole hasRole, PersistentScoContext scoCtx);

  List<PersistentStudentModelContext> getReducedStudentModels(Context context) throws Dwo2Exception;

  PersistentStudentModelContext getStudentModel(Context context, DomStudentModelContextId id) throws Dwo2Exception;

}
