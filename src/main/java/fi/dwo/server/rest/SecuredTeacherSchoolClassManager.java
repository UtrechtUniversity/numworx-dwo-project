package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Operations for the GUI Component that manages the school classes.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/schoolclass")
public class SecuredTeacherSchoolClassManager extends AbstractSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolClassManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchoolClass> getTeachersSchoolClasses(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
                domSchoolClasses = new ArrayList<>(schoolClasses.size());
                schoolClasses.stream().forEach((s) -> {
                    domSchoolClasses.add(s.buildDomSchoolClass());
                });
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1]. ", new Object[]{domSchoolClasses.size(), phr.getPersistentHasRolePK().getUserID()});
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param schoolClass
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getFull")
    public DomSchoolClassFull getFullSchoolClass(@Context SecurityContext sc, RestSchoolClass schoolClass) {
        if (schoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            PersistentSchoolClass persistentSchoolClass;
            Long key = null;
            try {
                key = (Long) MySQLPersistenceId.getNativeId(schoolClass.getDomSchoolClass());
                persistentSchoolClass = SchoolClassManager.findEntity(key);
                LOG.log(Level.FINER, "Fetched full schoolClass {0} for teacher {1]. ", new Object[]{key, phr.getPersistentHasRolePK().getUserID()});

            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return persistentSchoolClass.buildDomSchoolClassFull();
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getTeachersInSchoolList")
    public List<DomTeacher> getTeachersInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomTeacher> domTeachers = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            domTeachers = new ArrayList<DomTeacher>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                domTeachers.add(user.buildDomTeacher());
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        return domTeachers;
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getTeachersStudents")
    public List<DomStudent> getTeachersStudents(@Context SecurityContext sc) {
        List<DomStudent> domStudents = null;
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setDefaultHasRole()
                    //.setDefaultHasRole()
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.getTeachersStudents();

        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getSingleSchoolStudentsInSchoolList")
    public List<DomStudent> getSingleSchoolStudentsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
            domStudents = new ArrayList<>(userList.size());
            for (PersistentUser u : userList) {
                if (u.isSingleSchoolAccount()) {
                    domStudents.add(u.buildDomStudent());
                }
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        return domStudents;
    }

    /**
     * Adds a schoolClass
     *
     * @param sc
     * @param restSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean SubmitSchoolClass(@Context SecurityContext sc, RestSchoolClassFull restSchoolClass) {
        if (restSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        } catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && restSchoolClass != null) {
            PersistentSchoolClass existingClass = SchoolClassManager.findEntity(restSchoolClass.getDomSchoolClassFull().getSchoolClassName(), school);
            if (existingClass != null) {
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Submitted_SchoolClass_exists, "A schoolclass with that name already exists within the school.");
            }
            PersistentSchoolClass schoolClass = new PersistentSchoolClass();
            schoolClass.setSchoolID(school.getSchoolID());
            schoolClass.setClass1(restSchoolClass.getDomSchoolClassFull().getSchoolClassName());
            schoolClass.setIconizer(restSchoolClass.getDomSchoolClassFull().getIconizer());
            schoolClass.setRegistrationKey(restSchoolClass.getDomSchoolClassFull().getRegistrationKey());
            SchoolClassManager.create(schoolClass);
            schoolClass = SchoolClassManager.findEntity(schoolClass.getClass1(), school);
            if (schoolClass == null) {
                return false;
            }
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK(phr.getUser().getId(), schoolClass.getClassID(), phr.getSchoolGroup().getSchoolGroupID());
            toc.setPersistentTeacherOfClassPK(key);
            java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
            toc.setRegisterDate(d);
            TeacherOfClassManager.create(toc);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        return true;
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchoolClass
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeacherList")
    public List<DomTeacher> GetTeachersInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        if (restSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(restSchoolClass.getDomSchoolClass()));
        } catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (school != null && schoolClass.getSchoolID().equals(school.getSchoolID()) && toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
            //Fetch TeacherOfClass
            List<PersistentTeacherOfClass> teachersOfClass = TeacherOfClassManager.findEntities(schoolClass);
            LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{teachersOfClass.size()});
            List<DomTeacher> domTeachers;
            try {
                domTeachers = new ArrayList<DomTeacher>(teachersOfClass.size());
                for (PersistentTeacherOfClass t : teachersOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentTeacherOfClassPK().getUserID());
                    domTeachers.add(u.buildDomTeacher());
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the teachers.");
            }
            return domTeachers;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchoolClass
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getStudentList")
    public List<DomStudent> GetStudentsInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        if (restSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(restSchoolClass.getDomSchoolClass()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (school != null && schoolClass.getSchoolID().equals(school.getSchoolID()) && toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
            //Fetch TeacherOfClass
            List<PersistentStudentOfClass> studentsOfClass = StudentOfClassManager.findEntities(schoolClass);
            LOG.log(Level.FINER, "Fetched all {0} students. ", new Object[]{studentsOfClass.size()});
            List<DomStudent> domStudents;
            try {
                domStudents = new ArrayList<DomStudent>(studentsOfClass.size());
                for (PersistentStudentOfClass s : studentsOfClass) {
                    PersistentUser u = UserManager.findEntity(s.getPersistentStudentOfClassPK().getUserID());
                    domStudents.add(u.buildDomStudent());
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the students.");
            }
            return domStudents;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

//    /**
//     * Returns the school data to be displayed.
//     *
//     * @param sc
//     * @return
//     */
//    @GET
//    @Produces({"application/json"})
//    @Path("/getList")
//    public List<RestSchoolClass> getSchoolClasses(@Context SecurityContext sc) {
//        PersistentHasRole phr = null;
//        PersistentSchool school = null;
//
//        try {
//            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
//            school = HasRoleUtilManager.getSchoolforHasRole(phr);
//        }
//        catch (Dwo2Exception ex) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
//            LOG.log(Level.SEVERE, "", ex);
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//        }
//
//        List<PersistentSchoolClass> schoolClasses = null;
//        List<RestSchoolClass> restSchoolClasses;
//        try {
//            schoolClasses = SchoolClassManager.findEntities(school);
//            LOG.log(Level.FINER, "Fetched all {0} schoolClasses. ", new Object[]{schoolClasses.size()});
//            restSchoolClasses = new ArrayList<RestSchoolClass>(schoolClasses.size());
//            for (PersistentSchoolClass s : schoolClasses) {
//                restSchoolClasses.addPrincipalUser(new RestSchoolClass(s));
//            }
//        }
//        catch (Exception e) {
//            LOG.log(Level.WARNING, "Unexpected exception", e);
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
//        }
//        return restSchoolClasses;
//    }
    /**
     * Removes all the school data of the current school and returns true.
     *
     * @param sc
     * @param restSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        if (restSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(restSchoolClass.getDomSchoolClass()));

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (schoolClass.getSchoolID().equals(school.getSchoolID())
                && toc != null // XXX if findEntity cannot find it
                && toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
            try {
                //Loop students in class
                List<PersistentStudentOfClass> studentList = StudentOfClassManager.findEntities(schoolClass);
                for (PersistentStudentOfClass t : studentList) {
                    //remove students
                    //StudentOfClassManager.destroy(t.getPersistentStudentOfClassPK());
                    Long id = t.getPersistentStudentOfClassPK().getUserID();
                    PersistentUser student = UserManager.findEntity(id);
                    PersistentHasRole shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
                    removeStudentFromSchoolClass(sc, school, student, shr, schoolClass);
                }

                //Loop teachers in class
                List<PersistentTeacherOfClass> teacherList = TeacherOfClassManager.findEntities(schoolClass);
                for (PersistentTeacherOfClass t : teacherList) {
                    //remove teachers
                    TeacherOfClassManager.destroy(t.getPersistentTeacherOfClassPK());
                }
                SchoolClassManager.destroy(schoolClass.getClassID());
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(ex);
            } catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a schoolClass with id {1}.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school class.");
        }
        return true;
    }

    /**
     * Add a teacher to the school class.
     *
     * @param sc
     * @param restSubmitTeacherToSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitTeacher")
    public Boolean SubmitTeacherToSchoolClass(@Context SecurityContext sc, RestSubmitTeacherToSchoolClass restSubmitTeacherToSchoolClass) {
        if (restSubmitTeacherToSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomTeacher restTeacher = restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().getTeacher();
        DomSchoolClass restSchoolClass = restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().getSchoolClass();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity(MySQLPersistenceId.getNativeId(restTeacher));
            if (teacher == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find teacher to add.");
            }
            thr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(teacher, school, RoleType.TEACHER);
            schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(restSchoolClass));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (schoolClass.getSchoolID().equals(school.getSchoolID())) {
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            toc.setPersistentTeacherOfClassPK(new PersistentTeacherOfClassPK(teacher.getId(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
            java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
            toc.setRegisterDate(d);
            TeacherOfClassManager.create(toc);
        }
        return true;
    }

    /**
     * Add a student to the school class.
     *
     * @param sc
     * @param restSubmitStudentToSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitStudent")
    public Boolean SubmitStudentToSchoolClass(@Context SecurityContext sc, RestSubmitStudentToSchoolClass restSubmitStudentToSchoolClass) {
        if (restSubmitStudentToSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomStudent domStudent = restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().getStudent();
        DomSchoolClass domToSchoolClass = restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().getSchoolClassTo();
        DomSchoolClass domFromSchoolClass = restSubmitStudentToSchoolClass.getDomSubmitStudentToSchoolClass().getSchoolClassFrom();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass fromClass = null;
        PersistentSchoolClass toClass = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        PersistentTeacherOfClass toc = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(domStudent));
            if (student == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find teacher to add.");
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
            fromClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domFromSchoolClass));
            toClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domToSchoolClass));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (fromClass == null || toClass == null) {
            LOG.log(Level.WARNING, "Username {0}: Submitted classes do not exist.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "One or both submitted schoolclasses do not exist.");
        }

        toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), fromClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && fromClass.getSchoolID().equals(school.getSchoolID()) && toClass.getSchoolID().equals(school.getSchoolID())) {
//            PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
//            toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getId(), toClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
//            java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
//            toSoc.setRegisterDate(d);
//            StudentOfClassManager.create(toSoc);
//            return true;
            return SchoolClassUtilManager.registerStudentForSchoolClass(shr, toClass);
        } else {
            return false;
        }
    }

    /**
     * Move a student to a different school class.
     *
     * @param sc
     * @param restSubmitStudentToSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/moveStudent")
    public Boolean MoveStudentToSchoolClass(@Context SecurityContext sc, RestMoveStudentToSchoolClass restMoveStudentToSchoolClass) {
        if (restMoveStudentToSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomStudent domStudent = restMoveStudentToSchoolClass.getDomMoveStudentToSchoolClass().getStudent();
        DomSchoolClass domToSchoolClass = restMoveStudentToSchoolClass.getDomMoveStudentToSchoolClass().getSchoolClassTo();
        DomSchoolClass domFromSchoolClass = restMoveStudentToSchoolClass.getDomMoveStudentToSchoolClass().getSchoolClassFrom();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass fromClass = null;
        PersistentSchoolClass toClass = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        PersistentTeacherOfClass toc = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(domStudent));
            if (student == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find student to add.");
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
            fromClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domFromSchoolClass));
            toClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domToSchoolClass));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (fromClass == null || toClass == null) {
            LOG.log(Level.WARNING, "Username {0}: Submitted classes do not exist.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "One or both submitted schoolclasses do not exist.");
        }

        toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), fromClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && fromClass.getSchoolID().equals(school.getSchoolID()) && toClass.getSchoolID().equals(school.getSchoolID())) {
//            PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
//            toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getId(), toClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
//            java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
//            toSoc.setRegisterDate(d);
//            StudentOfClassManager.create(toSoc);
//            return true;
            if (SchoolClassUtilManager.registerStudentForSchoolClass(shr, toClass)) {
                return SchoolClassUtilManager.removeStudentFromSchoolClass(shr, fromClass);
            } else {
                Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.Rest_CanNotAddStudentToClass, "Can not add student to class as requested.");
                LOG.log(Level.SEVERE, "", e);
                throw e;
            }
        } else {
            return false;
        }
    }


    /**
     * Move a student to a different school class.
     *
     * @param sc
     * @param restSubmitStudentToSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/moveStudent2")
    public Boolean MoveStudentToSchoolClass2(@Context SecurityContext sc, RestMoveStudentToSchoolClass rest) {
//        //secure builder
//        try {
//            TeacherDomainAuthorizer.TeacherState_HR_R_S_SC_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
//                    .setHasRole(rest.getRestContext().getDomHasRole())
//                    .buildSchoolAdminTeacher()
//                    .setTeacher();
////                    .addSchoolClass(rest.getDomMoveStudentToSchoolClass().getSchoolClassFrom());
//                    
//                    
//        } catch (Dwo2Exception e) {
//            throw new Dwo2RestException(e);
//        }
        return true;
    }    
    
    /**
     * Removes a teacher from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restRemoveTeacherFromSchoolClass
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeTeacher")
    public Boolean removeTeacherFromSchoolClass(@Context SecurityContext sc, RestRemoveTeacherFromSchoolClass restRemoveTeacherFromSchoolClass) {
        if (restRemoveTeacherFromSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomSchoolClass domSchoolClass = restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().getSchoolClass();
        DomTeacher domTeacher = restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().getTeacher();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity(MySQLPersistenceId.getNativeId(domTeacher));
            thr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(teacher, school, RoleType.TEACHER);
            schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domSchoolClass));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentTeacherOfClass ptoc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(thr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && ptoc != null && teacher != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                TeacherOfClassManager.destroy(toc.getPersistentTeacherOfClassPK());
            } catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a teacher from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
        }

        return true;
    }

    /**
     * Removes a student from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restRemoveStudentFromSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeStudent")
    public Boolean removeStudentFromSchoolClass(@Context SecurityContext sc, RestRemoveStudentFromSchoolClass restRemoveStudentFromSchoolClass) {
        if (restRemoveStudentFromSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomSchoolClass domSchoolClass = restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().getSchoolClass();
        DomStudent domStudent = restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().getStudent();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(domStudent));
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
            schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getNativeId(domSchoolClass));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        PersistentStudentOfClass soc = StudentOfClassManager.findEntity(new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && student != null && soc != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            return removeStudentFromSchoolClass(sc, school, student, shr, schoolClass);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a student from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
        }
    }

    /**
     * Removes a student from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean UpdateSchoolClass(@Context SecurityContext sc, RestSchoolClassFull restSchoolClass) {
        if (restSchoolClass == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(restSchoolClass.getDomSchoolClassFull()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                schoolClass.setIconizer(restSchoolClass.getDomSchoolClassFull().getIconizer());
                schoolClass.setRegistrationKey(restSchoolClass.getDomSchoolClassFull().getRegistrationKey());
                schoolClass.setClass1(restSchoolClass.getDomSchoolClassFull().getSchoolClassName());
                SchoolClassManager.edit(schoolClass);
            } catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update a schoolclass with id {1} and one or both do not exists or are not in the same school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the school class.");
        }

        return true;
    }

    /**
     * Returns a singleSchoolStudent.
     *
     * @param sc
     * @param submit
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getSingleSchoolStudent")
    public DomSingleSchoolStudent getSingleSchoolStudent(@Context SecurityContext sc, RestGetSingleSchoolStudent submit) {
        if (submit == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }

        PersistentHasRole phr = null;
        PersistentHasRole shr = null;
        PersistentStudentOfClass studentInSchoolClass = null;
        PersistentTeacherOfClass teacherInSchoolClass = null;
        PersistentUser student = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(submit.getDomGetSingleSchoolStudent().getDomStudent()));
            schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(submit.getDomGetSingleSchoolStudent().getDomSchoolClass()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        teacherInSchoolClass = TeacherOfClassManager.findEntity(
                new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(),
                        schoolClass.getClassID(),
                        phr.getPersistentHasRolePK().getSchoolGroupID()));
        studentInSchoolClass = StudentOfClassManager.findEntity(
                new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(),
                        schoolClass.getClassID(),
                        shr.getPersistentHasRolePK().getSchoolGroupID()));

        if (student.isSingleSchoolAccount() && teacherInSchoolClass.getPersistentTeacherOfClassPK().getClassID().equals(studentInSchoolClass.getPersistentStudentOfClassPK().getClassID())) {
            return student.buildDomSingleSchoolStudent();
        } else {
            LOG.log(Level.SEVERE, "User {0} tried to access full userdata of user {1}.", new Object[]{phr.getPersistentHasRolePK().toString(), shr.getUser().getId()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Edits a singleSchoolStudent.
     *
     * @param sc
     * @param nssStudent
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/updateSingleSchoolStudent")
    public Boolean updateSingleSchoolStudent(@Context SecurityContext sc, RestSingleSchoolStudent nssStudent
    ) {
        if (nssStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (sg != null) {
            PersistentUser user;
            try {
                user = UserManager.findEntity(MySQLPersistenceId.getNativeId(nssStudent.getDomSingleSchoolStudent()));
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            if (user == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getDomSingleSchoolStudent().getUserName() + ".");
            }
            if (!user.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by teacher {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            user.setUsername(nssStudent.getDomSingleSchoolStudent().getUserName());
            user.setEmail(nssStudent.getDomSingleSchoolStudent().getEmail());
            user.setGivenName(nssStudent.getDomSingleSchoolStudent().getGivenName());
            user.setInsertion(nssStudent.getDomSingleSchoolStudent().getInsertion());
            user.setLastname(nssStudent.getDomSingleSchoolStudent().getFamilyName());
            user.setPassword(nssStudent.getDomSingleSchoolStudent().getPassword());
            try {
                UserManager.edit(user);
            } catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getUserName()});
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Registers a new user.
     *
     * @param sc
     * @param nssStudent
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitSingleSchoolStudent")
    public Boolean SubmitSingleSchoolStudent(@Context SecurityContext sc, RestNewSingleSchoolStudent nssStudent
    ) {
        if (nssStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        if (!ValidUserFieldsChecker.isValidEmail(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if (!ValidUserFieldsChecker.isValidUserName(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getUserName())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
        }

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        PersistentTeacherOfClass toc = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
            toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), MySQLPersistenceId.getNativeId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass()), phr.getPersistentHasRolePK().getSchoolGroupID()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.WARNING, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        //teacher should be a member of the school class
        if (toc != null && sg != null) {
            Date now = DwoDateUtilities.getCurrentDwoDate();
            PersistentUser user = new PersistentUser();
            user.setEmail(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getEmail());
            user.setGivenName(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getGivenName());
            user.setInsertion(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getInsertion());
            user.setLastname(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getFamilyName());
            user.setPassword(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getPassword());
            user.setRegisterDate(now);
            user.setUsername(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getUserName());
            user.setSchoolGroupId(sg.getSchoolGroupID());
            user.setSingleSchoolAccount(true);
            try {
                PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass()));
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school, schoolClass);
                //add to schoolClass
                PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
                toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(user.getId(), schoolClass.getClassID(), user.getSchoolGroupId()));
                java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
                toSoc.setRegisterDate(d);
                StudentOfClassManager.create(toSoc);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
                LOG.log(Level.WARNING, "Reason:", ex);
                throw new Dwo2RestException(ex);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Fetches all the course and classcourse information that a teacher should
     * see from within a school.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getModules")
    public DomCoursesOfSchoolClass4Teacher getModules(@Context SecurityContext sc, RestSchoolClassAndProfile rest) throws Dwo2Exception {
        //init
        PersistentHasRole phr = null;
        PersistentHasRolePK phrPK = MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        DomDwoProfile domProfile = rest.getDomSchoolClassAndProfile().getDomDwoProfile();
        final PersistentDwoProfile profile;
        //check if user has matching hasRole
        try {
            PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
            if (!u.getId().equals(phrPK.getUserID())) {
                throw new Dwo2Exception();
            }
            phr = HasRoleManager.findEntity(phrPK);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
            if (profile == null) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        } catch (Exception e) {
            //in case use disappeared and such
            LOG.log(Level.WARNING, "Username {0}: Internal error.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error.");
        }

        //fetch schoolclass from parameter
        Long classID = MySQLPersistenceId.getNativeId(rest.getDomSchoolClass());
        schoolClass = SchoolClassManager.findEntity(classID);
        if (schoolClass == null) {
            String msg = MessageFormat.format("Username {0}: Given schoolclass with id {1} can not be found.", new Object[]{sc.getUserPrincipal().getName(), classID});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_SchoolclassDoesNotExist, msg);
        }
        //verify if user is in class
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setClassID(schoolClass.getClassID());
        key.setSchoolGroupID(phr.getPersistentHasRolePK().getSchoolGroupID());
        key.setUserID(phr.getPersistentHasRolePK().getUserID());
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(key);
        if (toc == null) {
            String msg = MessageFormat.format("Username {0} is not a teacher of schoolclass {1}.", new Object[]{sc.getUserPrincipal().getName(), classID});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //verify if schoolClass is in school
        if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} from a different school that registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }
// end verification		
        DomCoursesOfSchoolClass4Teacher result = new DomCoursesOfSchoolClass4Teacher();

        Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());

        //fetch all courses in the school and profile
        List<PersistentCourse> listCourse = CourseManager.findEntities(profile.getDwoProfileID(), school.getSchoolID());
        result.setCourses(listCourse.stream().map((e) -> new DomMapEntry<PersistenceId, DomCourse>(e.buildPersistenceId(), e.buildDomCourse()))
                .collect(Collectors.toList()));

        List<PersistentClassCourse> listClassCourse = ClassCourseManager.findEntities(schoolClass);

        Map<PersistenceId, DomClassCourse4Teacher> classCourseMap = new HashMap<>();
        Map<PersistenceId, DomCourse> courseMap = new HashMap<>();

        listClassCourse.stream().forEach(
                (scc) -> {
                    Long courseID = scc.getCourseID();
                    PersistentCourse course = CourseManager.findEntity(courseID);
                    if (course == null) {
                        LOG.log(Level.SEVERE, "course null for courseid = " + courseID + " sccid = " + scc.getClassCourseID());
                    } else if (profileID.equals(course.getDwoProfileID())) {
                        DomClassCourse4Teacher dcc = scc.buildDomClassCourse4Teacher();
                        classCourseMap.put(dcc.getId(), dcc);
                        DomCourse dcs = course.buildDomCourse();
                        courseMap.put(dcs.getId(), dcs);
                    }
                });

        result.setSchoolClass(schoolClass.buildDomSchoolClass());
        result.setClassCourses(classCourseMap.entrySet()
                .stream()
                .map((e) -> new DomMapEntry<PersistenceId, DomClassCourse4Teacher>(e))
                .collect(Collectors.toList()));
//        result.setCourses(courseMap.entrySet()
//                .stream()
//                .map((e) -> new DomMapEntry<PersistenceId, DomCourse>(e))
//                .collect(Collectors.toList()));
        result.setFetchTimeStamp(Long.valueOf(System.currentTimeMillis()));
        return result;

    }

    /**
     * Attaches a leaf course that a class in a school can see.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/attachCourseToClass")
    public Boolean attachCourseToClass(@Context SecurityContext sc, RestSchoolClassCourseAndProfile rest) throws Dwo2Exception {
        //secure builder to detach course by setting it invisible.
        try {
            //fail if invalid parameters, fill build if valid
//            CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
            TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    //.setDefaultHasRole()
                    .buildSchoolAdminTeacher()
                    .setTeacher()
                    .addProfile(rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile())
                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass())
                    .addCourse(rest.getDomSchoolClassCourseAndProfile().getCourse());

            //Loop up the course tree and find the tree path
            Stack<PersistentCourse> treePath = new Stack<>();
            PersistentCourse curCourse = build.getCourse();
            treePath.add(curCourse);
            while (curCourse.getParentID() != 0) {
                curCourse = CourseManager.findEntity(curCourse.getParentID());
                //if no classCourse addPrincipalUser to stack
                if (ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse()).isEmpty()) {
                    treePath.push(curCourse);
                } else {
                    break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
                }
            }// stop when added course with parentid = 0;

            //Walk the treepath list from top to bottom and add classCourses idempotently (ignore if it already exists).   
            while (!treePath.empty()) {
                curCourse = treePath.pop();
                List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
                if (pcc.isEmpty()) { //create new 
                    PersistentClassCourse cc = new PersistentClassCourse();
                    cc.setClassID(build.getSchoolClass().getClassID());
                    cc.setCourseID(curCourse.getCourseID());
                    cc.setNotAfter(null);
                    cc.setNotBefore(null);
                    cc.setType(CourseType.normal.ordinal());
                    cc.setViewState(ViewState.studentsAndTeachers);
                    try {
                        cc = ClassCourseManager.create(cc);
                        LOG.log(Level.FINE, "User {3} adds a ClassCourse {0} for Course {1} and Class {2}", new Object[]{cc.getClassCourseID(), cc.getCourseID(), cc.getClassID(), sc.getUserPrincipal().getName()});
                    } catch (PersistenceException e) {
                        // ignore as it might already exist.
                    }
                } else {//switch to visible.
                    ClassCourseManager.editViewState(pcc.get(0).getClassCourseID(), ViewState.studentsAndTeachers);
                }
            }
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
        return true;
    }

    /**
     * Detaches a leaf course that a class in a school can see.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/detachCourseFromClass")
    public Boolean detachCourseFromClass(@Context SecurityContext sc, RestSchoolClassCourseAndProfile rest) throws Dwo2Exception {
//secure builder to detach course by setting it invisible.
        try {
            CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
                    .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass())
                    .addProfile(rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile())
                    .addCourse(rest.getDomSchoolClassCourseAndProfile().getCourse());
            List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
            if (pcc.size() > 0) {
                //update type.
                ClassCourseManager.editViewState(pcc.get(0).getClassCourseID(), ViewState.invisible);
                return true;
            }
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
        return false;

// old code that destroys the classcourse entry, keep as example or for revert.    
//init
//    PersistentHasRole phr = null;
//    PersistentHasRolePK phrPK = MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
//    PersistentSchool school = null;
//    PersistentSchoolClass schoolClass = null;
//    PersistentCourse course = null;
//    DomDwoProfile domProfile = rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile();
//    final PersistentDwoProfile profile;
//    //check if user has matching hasRole
//
//    
//        try {
//            PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
//        if (!u.getId().equals(phrPK.getUserID())) {
//            throw new Dwo2Exception();
//        }
//        phr = HasRoleManager.findEntity(phrPK);
//        school = HasRoleUtilManager.getSchoolforHasRole(phr);
//        profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
//        if (profile == null) {
//            LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
//            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//        }
//    }
//    catch (Dwo2Exception ex
//
//    
//        ) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
//        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//    }
//    catch (Exception e
//
//    
//        ) {
//            //in case use disappeared and such
//            LOG.log(Level.WARNING, "Username {0}: Internal error.", new Object[]{sc.getUserPrincipal().getName()});
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error.");
//    }
//
//    //fetch schoolclass from parameter
//    Long classID = MySQLPersistenceId.getNativeId(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass());
//    schoolClass  = SchoolClassManager.findEntity(classID);
//    if (schoolClass
//
//    
//        == null) {
//            String msg = MessageFormat.format("Username {0}: Given schoolclass with id {1} can not be found.", new Object[]{sc.getUserPrincipal().getName(), classID});
//        LOG.log(Level.WARNING, msg);
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_SchoolclassDoesNotExist, msg);
//    }
//    //verify if user is in class
//    PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
//
//    key.setClassID (schoolClass.getClassID
//
//    ());
//    key.setSchoolGroupID (phr.getPersistentHasRolePK
//
//    ().getSchoolGroupID());
//    key.setUserID (phr.getPersistentHasRolePK
//    ().getUserID());
//        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(key);
//    if (toc
//
//    
//        == null) {
//            String msg = MessageFormat.format("Username {0} is not a teacher of schoolclass {1}.", new Object[]{sc.getUserPrincipal().getName(), classID});
//        LOG.log(Level.WARNING, msg);
//        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
//    }
//    //verify if schoolClass is in school
//    if (schoolClass
//
//    == null || !schoolClass.getSchoolID () 
//        .equals(school.getSchoolID())) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
//    }
//
//    Long courseId = MySQLPersistenceId.getNativeId(rest.getDomSchoolClassCourseAndProfile().getCourse());
//    course  = CourseManager.findEntity(courseId);
//    //verify if course is in school
//    if (course
//
//    == null || (course.getSchoolID () 
//        != null && !course.getSchoolID().equals(school.getSchoolID()))) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (course != null) ? course.getCourseID() : null});
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
//    }
//
//    if (course.isWithChildren () 
//        ) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not a leaf in the course tree of school {1} for usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), (course != null) ? course.getCourseID() : null});
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error using usercode " + sc.getUserPrincipal().getName() + ".");
//    }
//
//    if (course.getDwoProfileID () 
//        == null || !course.getDwoProfileID().equals(profile.getDwoProfileID())) {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {1} is from a different profile than requested with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), (course != null) ? course.getCourseID() : null});
//        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
//    }
//    // end verification		
//
//    //detach leaf course
//    List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(schoolClass, course);
//    for (PersistentClassCourse cc : ccResult
//
//    
//        ) {
//            try {
//            ClassCourseManager.destroy(cc.getClassCourseID());
//        } catch (PersistenceException e) {
//            // ignore as it might be destroyed already;
//        }
//    }
//    //Loop up the course tree and detach required maps
//    LinkedList<PersistentCourse> treePath = new LinkedList<>();
//    PersistentCourse curCourse = course;
//
//    while (curCourse.getParentID () 
//        != 0) {
//            curCourse = CourseManager.findEntity(curCourse.getParentID());
//        //if no classCourse addPrincipalUser to stack
//        treePath.addLast(curCourse);
//    }// stop when added course with parentid = 0;
//
//    //Loop the treepath list  from top  to down and addPrincipalUser classCourses, ignore if it already exists.   
//    while (!treePath.isEmpty () 
//        ) {
//            curCourse = treePath.pollFirst();
//        //check if a class course exists for current course 
//        ccResult = ClassCourseManager.findEntities(schoolClass, curCourse);
//        int cSize = ccResult.size();
//        if (cSize != 0 && curCourse.getCourseID() != 0) {// not empty, asynchroneous may allow for more than one classcourse
//            List<PersistentCourse> kids = CourseManager.findChildrenOf(curCourse);
//            int count = 0;
//            // count the siblings of curCourse that own one or more class courses.
//            for (PersistentCourse pc : kids) {
//                if (ClassCourseManager.findEntities(schoolClass, pc).size() > 0) {
//                    count++;
//                }
//            }
//            if (count > 0) {
//                break;
//            }
//            for (PersistentClassCourse pcc : ccResult) {
//                try {
//                    ClassCourseManager.destroy(pcc.getClassCourseID());
//                    LOG.log(Level.FINE, "User {3} deletes a ClassCourse {0} for Course {1} and Class {2}", new Object[]{pcc.getClassCourseID(), pcc.getCourseID(), pcc.getClassID(), sc.getUserPrincipal().getName()});
//                } catch (PersistenceException e) {
//                    // ignore as it might be destroyed already;
//                }
//            }
//        }
//    }
        //commit
//        return true;
    }

    /**
     * Updates the from time of a class-course of a class in a school.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/setFromDateClassCourse")
    public Boolean setFromDateClassCourse(@Context SecurityContext sc, RestSchoolClassCourseProfilewFrom rest) throws Dwo2Exception {
        //secure builder
        CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
                .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                .addSchoolClass(rest.getDomSchoolClassCourseProfilewFrom().getDomSchoolClass())
                .addProfile(rest.getDomSchoolClassCourseProfilewFrom().getDomDwoProfile())
                .addCourse(rest.getDomSchoolClassCourseProfilewFrom().getCourse());
        List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
        if (pcc.size() > 0) {
            //update type.
            ClassCourseManager.editFrom(pcc.get(0).getClassCourseID(), rest.getDomSchoolClassCourseProfilewFrom().getFrom());
            return true;
        }
        return false;
    }

    /**
     * Updates the to time of a class-course of a class in a school.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/setToDateClassCourse")
    public Boolean setToDateClassCourse(@Context SecurityContext sc, RestSchoolClassCourseProfilewTo rest) throws Dwo2Exception {
        //secure builder
        CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
                .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                .addSchoolClass(rest.getDomSchoolClassCourseProfilewTo().getDomSchoolClass())
                .addProfile(rest.getDomSchoolClassCourseProfilewTo().getDomDwoProfile())
                .addCourse(rest.getDomSchoolClassCourseProfilewTo().getCourse());
        List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
        if (pcc.size() > 0) {
            //update type.
            ClassCourseManager.editTo(pcc.get(0).getClassCourseID(), rest.getDomSchoolClassCourseProfilewTo().getTo());
            return true;
        }
        return false;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/setAccessKeyClassCourse")
    public Boolean setAccessKeyClassCourse(@Context SecurityContext sc, RestSchoolClassCourseProfilewAccessKey rest) throws Dwo2Exception {
        State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder
                .user(sc.getUserPrincipal().getName())
                .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                .addSchoolClass(rest.getDomSchoolClassCourseProfilewAccessKey().getDomSchoolClass())
                .addProfile(rest.getDomSchoolClassCourseProfilewAccessKey().getDomDwoProfile())
                .addCourse(rest.getDomSchoolClassCourseProfilewAccessKey().getCourse());
        List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
        if (pcc.size() > 0) {
            //update type.
            ClassCourseManager.editAccessKey(pcc.get(0).getClassCourseID(), rest.getDomSchoolClassCourseProfilewAccessKey().getAccessKey());
            return true;
        }
        return false;

    }

    /**
     * Updates the type of a class-course of a class in a school.
     *
     * @param sc
     * @param rest
     * @return
     * @throws Dwo2Exception
     */
    @PUT
    @Produces({"application/json"})
    @Path("/setClassCourseType")
    public Boolean setClassCourseType(@Context SecurityContext sc, RestSchoolClassCourseProfilewType rest) throws Dwo2RestException {
        //secure builder
        try {
            CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
                    .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                    .addSchoolClass(rest.getDomSchoolClassCourseProfilewType().getDomSchoolClass())
                    .addProfile(rest.getDomSchoolClassCourseProfilewType().getDomDwoProfile())
                    .addCourse(rest.getDomSchoolClassCourseProfilewType().getCourse());
            List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
            if (pcc.size() > 0) {
                //update type.
                ClassCourseManager.editType(pcc.get(0).getClassCourseID(), rest.getDomSchoolClassCourseProfilewType().getType());
                return true;
            }
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
        return false;
    }
}
