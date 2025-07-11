/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.Context;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Actions an authenticated user may do. The basic use cases.
 * 
 * @author Gert van der Plas
 */
public interface TeacherActions  {
    public List<PersistentStudentModelContext> getStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public List<PersistentStudentModelContext> getReducedStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.Context context, PersistentStudentModelContext model) throws Dwo2Exception;
	public PersistentStudentModelContext getStudentModel(Context context, DomStudentModelContextId id) throws Dwo2Exception;
    public List<PersistentSchoolClass> getSchoolClasses(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public List<PersistentUser> getTeachersStudents(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public void addStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolClass schoolClass, PersistentHasRole student) throws Dwo2Exception;
    public List<PersistenceId> getSharedTeacherClasses(TeacherDomainAuthorizer.Context context, PersistentUser user) throws Dwo2Exception;
    public List<PersistenceId> getTeachersClassesOfStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolGroup studentSg, PersistentUser user) throws Dwo2Exception;
    public Boolean addCourseToClass(TeacherDomainAuthorizer.Context context, CourseType courseType, Date from, Date to, String accessKey, ViewState state) throws Dwo2Exception;
    public Boolean attachCourseToClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public Boolean detachCourseFromClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception;
    public DomLRS getLRS(TeacherDomainAuthorizer.Context context, UriInfo info);

}
