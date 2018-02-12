/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import java.util.List;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Actions an authenticated user may do. The basic use cases.
 * 
 * @author Gert van der Plas
 */
public interface TeacherActions extends SchoolAdminTeacherActions {
    public List<DomStudentModelContext> getStudentModels(TeacherDomainAuthorizer.TeacherPersistentContext context) throws Dwo2Exception;
    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.TeacherPersistentContext context, PersistentStudentModelContext model) throws Dwo2Exception;
}
