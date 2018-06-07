/** Copyrighted Feb 12, 2018 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.PersistentDataManagers.util.TeacherSchoolClassUtilManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
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
            pModel.setPublishState(PublishState.published);
            return StudentModelContextManager.create(pModel);
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
    public Boolean attachCourseToClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        //Loop up the course tree and find the tree path
        Stack<PersistentCourse> treePath = new Stack<>();
        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
        treePath.add(curCourse);
        while (curCourse.getParentID() != 0) {
            curCourse = CourseManager.findEntity(curCourse.getParentID());
            //if no classCourse addPrincipalUser to stack
            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse()).isEmpty()) {
                treePath.push(curCourse);
            } else {
                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
            }
        }// stop when added course with parentid = 0;

        //Walk the treepath list from top to bottom and add classCourses idempotently (ignore if it already exists).   
        while (!treePath.empty()) {
            curCourse = treePath.pop();
            List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse());
            if (pcc.isEmpty()) { //create new 
                PersistentClassCourse cc = new PersistentClassCourse();
                cc.setClassID(context.getTeacherCtx().getSchoolClass().getClassID());
                cc.setCourseID(curCourse.getCourseID());
                cc.setNotAfter(null);
                cc.setNotBefore(null);
                cc.setType(CourseType.normal.ordinal());
                cc.setViewState(ViewState.studentsAndTeachers);
                try {
                    cc = ClassCourseManager.create(cc);
                    LOG.log(Level.FINE, "User {3} adds a ClassCourse {0} for Course {1} and Class {2}", new Object[]{cc.getClassCourseID(), cc.getCourseID(), cc.getClassID(), context.getUserCtx().getUser().getUsername()});
                } catch (PersistenceException e) {
                    // ignore as it might already been created.
                }
            } else {//switch to visible.
                ClassCourseManager.editViewState(pcc.get(0).getClassCourseID(), ViewState.studentsAndTeachers);
            }
        }
        return true;
    }
//
//    public Boolean detachCourseFromClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        //Loop up the course tree and find the tree path
//        Deque<PersistentCourse> treePath = new LinkedList<>();
//        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
//        treePath.add(curCourse);
//        while (curCourse.getParentID() != 0) {
//            curCourse = CourseManager.findEntity(curCourse.getParentID());
//            //if no classCourse addPrincipalUser to stack
//            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse()).isEmpty()) {
//                treePath.push(curCourse);
//            } else {
//                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
//            }
//        }// stop when added course with parentid = 0;
//
//        //take leaf course
//        curCourse = treePath.removeLast();
//        List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse);
//        for (PersistentClassCourse cc : ccResult) {
//            try {
//                //destroy if no results
//                if (ClassCourseUtilManager.hasResults(cc)) {
//                    
//                    ClassCourseManager.destroy(cc.getClassCourseID());
//                } else {
//                    ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.invisible);
//                }
//
//            } catch (PersistenceException e) {
//                // ignore as it might be destroyed already;
//            }
//        }
//
//        //Walk the treepath list from bottom to top and remove classCourses idempotently (ignore if it already exists).   
//        while (!treePath.isEmpty()) {
//            curCourse = treePath.removeLast();
//            List<PersistentClassCourse> children = ClassCourseManager.findChildEntities(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse());
//            ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse);
//            if (children.isEmpty()) {
//                //erase ClassCourses
//                for (PersistentClassCourse cc : ccResult) {
//                    try {
//                        //destroy if no results
//                        if (1 != 1) {
//                            //if and only if no children
//                            ClassCourseManager.destroy(cc.getClassCourseID());
//                        } else {
//                            ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.invisible);
//                        }
//
//                    } catch (PersistenceException e) {
//                        // ignore as it might be destroyed already;
//                    }
//                }
//            } else {
//                //test if there is one visible
//                boolean visible = false;
//                for (PersistentClassCourse cc : children) {
//                    if (cc.getViewState() == ViewState.studentsAndTeachers) {
//                        visible = true;
//                        break;
//                    }
//                }
//                if (visible) {
//                    for (PersistentClassCourse cc : ccResult) {
//                        ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.studentsAndTeachers);
//                    }
//                } else {
//                    for (PersistentClassCourse cc : ccResult) {
//                        ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.invisible);
//                    }
//                }
//            }
//
//        }
//        return true;
//    }

}
