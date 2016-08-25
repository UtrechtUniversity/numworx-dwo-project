package fi.dwo.server.rest;

import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.rest.entities.RestSchoolClass;
import fi.dwo.rest.entities.RestSchoolClassFull;
import fi.dwo.rest.entities.RestSingleSchoolStudent;
import fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

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
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<DomSchoolClass>(tocList.size());
                for (PersistentTeacherOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentTeacherOfClassPK().getClassID());
                    domSchoolClasses.add(s.createDomSchoolClass());
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1]. ", new Object[]{domSchoolClasses.size(), phr.getPersistentHasRolePK().getUserID()});
            }
            catch (Exception e) {
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
        if(schoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            PersistentSchoolClass persistentSchoolClass;
            Long key = (Long) MySQLPersistenceId.getId(schoolClass.getDomSchoolClass().getId());
            try {
                persistentSchoolClass = SchoolClassManager.findEntity(key);
                LOG.log(Level.FINER, "Fetched full schoolClass {0} for teacher {1]. ", new Object[]{key, phr.getPersistentHasRolePK().getUserID()});

            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return persistentSchoolClass.createDomSchoolClassFull();
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
        }
        catch (Dwo2Exception ex) {
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
        }
        catch (Dwo2Exception ex) {
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
    @Path("/getSingleSchoolStudentsInSchoolList")
    public List<DomStudent> getSingleSchoolStudentsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.STUDENT);
            domStudents = new ArrayList<DomStudent>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                PersistentUser u = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                if (u.isSingleSchoolAccount()) {
                    domStudents.add(new DomStudent());
                }
            }
        }
        catch (Dwo2Exception ex) {
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {

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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
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
            }
            catch (Exception e) {
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
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
            }
            catch (Exception e) {
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
//                restSchoolClasses.add(new RestSchoolClass(s));
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
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
					PersistentHasRole shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
					removeStudentFromSchoolHelper(sc, school, student, shr, schoolClass);
                }

                //Loop teachers in class
                List<PersistentTeacherOfClass> teacherList = TeacherOfClassManager.findEntities(schoolClass);
                for (PersistentTeacherOfClass t : teacherList) {
                    //remove teachers
                    TeacherOfClassManager.destroy(t.getPersistentTeacherOfClassPK());
                }
                SchoolClassManager.destroy(schoolClass.getClassID());
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(ex);
            }
            catch (PersistenceException e) {
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
        if(restSubmitTeacherToSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomTeacher restTeacher = restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().getTeacher();
        DomSchoolClass restSchoolClass = restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().getSchoolClass();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((Long) MySQLPersistenceId.getId(restTeacher.getId()));
            if (teacher == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find teacher to add.");
            }
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getId()));
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
     * Add a teacher to the school class.
     *
     * @param sc
     * @param restSubmitStudentToSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitStudent")
    public Boolean SubmitStudentToSchoolClass(@Context SecurityContext sc, RestSubmitStudentToSchoolClass restSubmitStudentToSchoolClass) {
        if(restSubmitStudentToSchoolClass==null){
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
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(domStudent.getId()));
            if (student == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find teacher to add.");
            }
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        fromClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domFromSchoolClass.getId()));
        toClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domToSchoolClass.getId()));
        if (fromClass == null || toClass == null) {
            LOG.log(Level.WARNING, "Username {0}: Submitted classes do not exist.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "One or both submitted schoolclasses do not exist.");
        }

        toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), fromClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && fromClass.getSchoolID().equals(school.getSchoolID()) && toClass.getSchoolID().equals(school.getSchoolID())) {
            PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
            toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getId(), toClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
            java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
            toSoc.setRegisterDate(d);
            StudentOfClassManager.create(toSoc);
            return true;
        } else {
            return false;
        }
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
        if(restRemoveTeacherFromSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomSchoolClass domSchoolClass = restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().getSchoolClass();
        DomTeacher domTeacher = restRemoveTeacherFromSchoolClass.getDomRemoveTeacherFromSchoolClass().getTeacher();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((Long) MySQLPersistenceId.getId(domTeacher.getId()));
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));
        PersistentTeacherOfClass ptoc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(thr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));

        if (toc != null && ptoc != null && teacher != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                TeacherOfClassManager.destroy(toc.getPersistentTeacherOfClassPK());
            }
            catch (PersistenceException e) {
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
        if(restRemoveStudentFromSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomSchoolClass domSchoolClass = restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().getSchoolClass();
        DomStudent domStudent = restRemoveStudentFromSchoolClass.getDomRemoveStudentFromSchoolClass().getStudent();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(domStudent.getId()));
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        PersistentStudentOfClass soc = StudentOfClassManager.findEntity(new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));

        return 	removeStudentFromSchoolHelper(sc, school, student, shr, schoolClass);

        
//        if (toc != null && student != null && soc != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
//            try {
//                if (shr.getClassID()!=null && shr.getClassID().equals(soc.getPersistentStudentOfClassPK().getClassID())) {
//                    shr.setClassID(null);
//                    HasRoleManager.edit(shr);
//                }
//                StudentOfClassManager.destroy(soc.getPersistentStudentOfClassPK());
//            }
//            catch (PersistenceException e) {
//                return false;
//            }
//        } else {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a student from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
//        }
//
//        return true;
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClassFull().getId()));

        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                schoolClass.setIconizer(restSchoolClass.getDomSchoolClassFull().getIconizer());
                schoolClass.setRegistrationKey(restSchoolClass.getDomSchoolClassFull().getRegistrationKey());
                schoolClass.setClass1(restSchoolClass.getDomSchoolClassFull().getSchoolClassName());
                SchoolClassManager.edit(schoolClass);
            }
            catch (PersistenceException e) {
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
        if(submit==null){
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
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        student = UserManager.findEntity(MySQLPersistenceId.getId(submit.getDomGetSingleSchoolStudent().getDomStudent().getId()));
        schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(submit.getDomGetSingleSchoolStudent().getDomSchoolClass().getId()));

        try {
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
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
            LOG.log(Level.SEVERE, "User {0} tried to access full userdata of user {1}.", new Object[]{phr.getPersistentHasRolePK().getId(), shr.getUser().getId()});
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
        if(nssStudent==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (sg != null) {
            PersistentUser user = UserManager.findEntity((Long) MySQLPersistenceId.getId(nssStudent.getDomSingleSchoolStudent().getId()));
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
            }
            catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getUserName()});
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
        if(nssStudent==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        if(!ValidUserFieldsChecker.isValidEmail(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getEmail())){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if(!ValidUserFieldsChecker.isValidUserName(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getUserName())){
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
            toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), MySQLPersistenceId.getId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()), phr.getPersistentHasRolePK().getSchoolGroupID()));
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
                PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()));
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school,schoolClass);
                //add to schoolClass
                PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
                toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(user.getId(), schoolClass.getClassID(), user.getSchoolGroupId()));
                java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
                toSoc.setRegisterDate(d);
                StudentOfClassManager.create(toSoc);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(ex);
            }
        } else {
            return false;
        }
        return true;
    }
}
