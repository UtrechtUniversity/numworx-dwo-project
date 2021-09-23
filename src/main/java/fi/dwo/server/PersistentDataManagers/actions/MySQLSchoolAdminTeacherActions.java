/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import java.util.List;
import java.util.logging.Level;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

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

  public List<PersistentStudentModelContext> getReducedStudentModels(Context context) throws Dwo2Exception {
      List<PersistentStudentModelContext> pModels = StudentModelContextManager.findReducedEntities(context.getUserCtx().getSchool());
      return pModels;
  }

@Override
public PersistentStudentModelContext getStudentModel(Context context, DomStudentModelContextId model)
		throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(model);
    PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
    if ( pModel == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
    }
    //verify if studentModel is in school
    long uSchoolID = context.getUserCtx().school.getSchoolID().longValue();
	long mSchoolID = pModel.getSchoolID().longValue();
	if ( mSchoolID != 0L && mSchoolID != uSchoolID) {
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + context.getUserCtx().getUser().getUsername() + ".");
    }
    StudentModelContextUtilManager.merge(pModel);
    return pModel;
}

}
