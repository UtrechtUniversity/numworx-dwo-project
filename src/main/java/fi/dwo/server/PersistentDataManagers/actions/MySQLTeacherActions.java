/**
 * Copyrighted Feb 12, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelItemManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.XapiManager;
import fi.dwo.server.PersistentDataManagers.util.CourseInClassManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import fi.dwo.server.PersistentDataManagers.util.TeacherSchoolClassUtilManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 *
 * @author plas0006
 */
public class MySQLTeacherActions implements TeacherActions {

    private static final Logger LOG = Logger.getLogger(MySQLTeacherActions.class.getName());

    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.Context context, PersistentStudentModelContext model) throws Dwo2Exception {
        try {
            PersistentStudentModelContext pModel = new PersistentStudentModelContext();
            pModel.setModelStructure(model.getModelStructure());
            pModel.setSchoolID(model.getSchoolID());
            pModel.setPublishState(model.getPublishState());
            pModel.setDwoProfileID(model.getDwoProfileID());
            return StudentModelContextUtilManager.create(pModel);
        } catch (Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{context.getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    public List<PersistentStudentModelContext> getStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        List<PersistentStudentModelContext> pModels = StudentModelContextManager.findEntities(context.getUserCtx().getSchool());
        return pModels;
    }

    public List<PersistentStudentModelContext> getReducedStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
    	List<PersistentStudentModelContext> pModels = StudentModelContextManager.findReducedEntities(context.getUserCtx().getSchool(), context.getTeacherCtx().getProfile());
        return pModels;
    }

    public PersistentStudentModelContext getStudentModel(TeacherDomainAuthorizer.Context context, DomStudentModelContextId model) throws Dwo2Exception {
        Long id = MySQLPersistenceId.getNativeId(model);
        PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
        if ( pModel == null) {
          throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
        }
        //verify if studentModel is in school
        long uSchoolID = context.getUserCtx().school.getSchoolID().longValue();
		long mSchoolID = pModel.getSchoolID().longValue();
		if ( mSchoolID != 0L && mSchoolID != uSchoolID) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{context.getUserCtx().getUser().getUsername(), context.getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + context.getUserCtx().getUser().getUsername() + ".");
        }
        StudentModelContextUtilManager.merge(pModel);
        return pModel;
    }
    
    
    
    @Override
    public List<PersistentSchoolClass> getSchoolClasses(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(context.getUserCtx().getHasRole());
        return schoolClasses;
    }

    @Override
    public List<PersistentUser> getTeachersStudents(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        Map<String, PersistentUser> students = new HashMap<>();
        List<PersistentSchoolClass> schoolClasses = getSchoolClasses(context);
        for (PersistentSchoolClass sc : schoolClasses) {
            StudentInClassManager.findEntities(sc).forEach((k -> students.putIfAbsent(k.getUser().buildPersistenceId().getIdString(), k.getUser())));
        }
        List<PersistentUser> results = new ArrayList<>(students.size());
        students.forEach((k, v) -> results.add(v));
        return results;
    }

    @Override
    public void addStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolClass sc, PersistentHasRole shr) throws Dwo2Exception {
        try {
            PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), sc.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID());
            PersistentStudentOfClass soc = new PersistentStudentOfClass();
            soc.setPersistentStudentOfClassPK(socId);
            soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            StudentOfClassManager.create(soc);

            if (shr.getClassID() == null) {
                shr.setClassID(sc.getClassID());
                HasRoleManager.edit(shr); // TODO met try/catch?
            }

        } catch (PersistenceException e) {
            String msg = MessageFormat.format("Can not add student to class for Username {0}, error: {1}", new Object[]{context.getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_CanNotAddStudentToClass, msg);
        }
    }

    @Override
    public List<PersistenceId> getSharedTeacherClasses(TeacherDomainAuthorizer.Context context, PersistentUser otherTeacher) throws Dwo2Exception {
        return TeacherSchoolClassUtilManager.getSharedTeacherClasses(context.getUserCtx().getUser(), context.getUserCtx().getSchoolGroup(), otherTeacher);
    }

    @Override
    public List<PersistenceId> getTeachersClassesOfStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolGroup studentSg, PersistentUser student) throws Dwo2Exception {
        return TeacherSchoolClassUtilManager.getTeachersStudentClasses(context.getUserCtx().getUser(), context.getUserCtx().getSchoolGroup(), studentSg, student);
    }
    
    @Override
    public Boolean addCourseToClass(TeacherDomainAuthorizer.Context context, CourseType courseType, Date from, Date to, String accessKey) throws Dwo2Exception {
        //Loop up the course tree and find the tree path
        Deque<PersistentCourse> treePath = new LinkedList<>();
        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
        treePath.add(curCourse);
        while (curCourse.getParentID() != 0) {
            curCourse = CourseManager.findEntity(curCourse.getParentID());
            //if no classCourse addPrincipalUser to stack
//            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse).isEmpty()) {
                treePath.push(curCourse);
//            } else {
//                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
//            }
        }// stop when added course with parentid = 0;

        //Walk the treepath list from bottom to top of tree and add classCourses idempotently (ignore if it already exists).   
        while (treePath.size() > 0) {
            curCourse = treePath.pop();
            List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(),curCourse);
            //if below is for the future case classcourse are not unique any more.
            if (ccResult.isEmpty()) { //create new 
                PersistentClassCourse cc = new PersistentClassCourse();
                cc.setClassID(context.getTeacherCtx().getSchoolClass().getClassID());
                cc.setCourseID(curCourse.getCourseID());
                cc.setDwoProfileID(curCourse.getDwoProfileID());
                cc.setNotAfter(to);
                cc.setNotBefore(from);
                cc.setAccessKey(accessKey);
                cc.setType(courseType.ordinal());
                cc.setViewState(ViewState.studentsAndTeachers);
                ClassCourseManager.insertOrUpdateViewState(cc);
//                    LOG.log(Level.INFO, "created cc of "+ccResult);
            } else {
                for (PersistentClassCourse cc : ccResult) {
//                    LOG.log(Level.INFO, "setting visibility of "+cc.getClassCourseID());
                    ClassCourseManager.editViewState(cc.getClassCourseID(),ViewState.studentsAndTeachers);
                }
            }
        }
        return true;    
            }
    
    @Override
    public Boolean attachCourseToClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        //Loop up the course tree and find the tree path
        Deque<PersistentCourse> treePath = new LinkedList<>();
        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
        treePath.add(curCourse);
        while (curCourse.getParentID() != 0) {
            curCourse = CourseManager.findEntity(curCourse.getParentID());
            //if no classCourse addPrincipalUser to stack
//            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse).isEmpty()) {
                treePath.push(curCourse);
//            } else {
//                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
//            }
        }// stop when added course with parentid = 0;

        //Walk the treepath list from bottom to top of tree and add classCourses idempotently (ignore if it already exists).   
        while (treePath.size() > 0) {
            curCourse = treePath.pop();
            List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(),curCourse);
            //if below is for the future case classcourse are not unique any more.
            if (ccResult.isEmpty()) { //create new 
                PersistentClassCourse cc = new PersistentClassCourse();
                cc.setClassID(context.getTeacherCtx().getSchoolClass().getClassID());
                cc.setCourseID(curCourse.getCourseID());
                cc.setDwoProfileID(curCourse.getDwoProfileID());
                cc.setNotAfter(null);
                cc.setNotBefore(null);
                cc.setType(CourseType.normal.ordinal());
                cc.setViewState(ViewState.studentsAndTeachers);
                ClassCourseManager.insertOrUpdateViewState(cc);
//                    LOG.log(Level.INFO, "created cc of "+ccResult);
            } else {
                for (PersistentClassCourse cc : ccResult) {
//                    LOG.log(Level.INFO, "setting visibility of "+cc.getClassCourseID());
                    ClassCourseManager.editViewState(cc.getClassCourseID(),ViewState.studentsAndTeachers);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean detachCourseFromClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        CourseInClassManager.detachLeaveAndUpdateTree(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse());
        return true;
    }
    
    @Override
    public DomLRS getLRS(TeacherDomainAuthorizer.Context context, UriInfo info) {
      PersistentUser user = context.getUserCtx().user;
      PersistentSchool school = context.getUserCtx().school;
      return XapiManager.getLRS(user, school, info);
    }

    
}
