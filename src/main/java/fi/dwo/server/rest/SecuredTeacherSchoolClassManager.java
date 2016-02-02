package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass4Teacher;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.commons.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass4Teacher;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestSubmitStudentToSchoolClass;
import fi.dwo.commons.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.ArrayList;
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
public class SecuredTeacherSchoolClassManager {

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
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<DomSchoolClass>(tocList.size());
                for (PersistentTeacherOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentTeacherOfClassPK().getClassID());
                    domSchoolClasses.add(new DomSchoolClass(s));
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
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getFull")
    public DomSchoolClass4Teacher getFullSchoolClass(@Context SecurityContext sc,RestSchoolClass schoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            return new DomSchoolClass4Teacher(persistentSchoolClass);
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
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            domTeachers = new ArrayList<DomTeacher>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                domTeachers.add(new DomTeacher((PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID())));
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        return domTeachers;
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
    public Boolean SubmitSchoolClass(@Context SecurityContext sc, RestSchoolClass4Teacher restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && restSchoolClass != null) {
            PersistentSchoolClass schoolClass = new PersistentSchoolClass();
            schoolClass.setSchoolID(school.getSchoolID());
            schoolClass.setClass1(restSchoolClass.getDomSchoolClass4Teacher().getSchoolClassName());
            schoolClass.setIconizer(restSchoolClass.getDomSchoolClass4Teacher().getIconizer());
            schoolClass.setRegistrationKey(restSchoolClass.getDomSchoolClass4Teacher().getRegistrationKey());
            SchoolClassManager.create(schoolClass);
            schoolClass = SchoolClassManager.findEntity(schoolClass.getClass1(), school);
            if(schoolClass==null) return false;
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK(phr.getUser().getUserID(), schoolClass.getClassID(), phr.getSchoolGroup().getSchoolGroupID());
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
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
                    domTeachers.add(new DomTeacher(u));
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
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
                    domStudents.add(new DomStudent(u));
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
//            LOG.log(Level.SEVERE, null, ex);
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
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (schoolClass.getSchoolID().equals(school.getSchoolID()) && toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
            try {
                //Loop students in class
                List<PersistentStudentOfClass> studentList = StudentOfClassManager.findEntities(schoolClass);
                for (PersistentStudentOfClass t : studentList) {
                    //remove students
                    StudentOfClassManager.destroy(t.getPersistentStudentOfClassPK());
                }

                //Loop teachers in class
                List<PersistentTeacherOfClass> teacherList = TeacherOfClassManager.findEntities(schoolClass);
                for (PersistentTeacherOfClass t : teacherList) {
                    //remove teachers
                    TeacherOfClassManager.destroy(t.getPersistentTeacherOfClassPK());
                }
                SchoolClassManager.destroy(schoolClass.getClassID());
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
    public Boolean SubmitTeacherToSchoolClass(@Context SecurityContext sc, RestSubmitTeacherToSchoolClass restSubmitTeacherToSchoolClass){
        DomTeacher restTeacher =restSubmitTeacherToSchoolClass.getDomSubmitTeacherToSchoolClass().getTeacher();
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
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (schoolClass.getSchoolID().equals(school.getSchoolID())) {
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            toc.setPersistentTeacherOfClassPK(new PersistentTeacherOfClassPK(teacher.getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
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
        DomStudent domStudent = restSubmitStudentToSchoolClass.getStudent();
        DomSchoolClass domToSchoolClass = restSubmitStudentToSchoolClass.getSchoolToClass();
        DomSchoolClass domFromSchoolClass = restSubmitStudentToSchoolClass.getSchoolFromClass();
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
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getUserID(), toClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
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
    public Boolean removeTeacherFromSchoolClass(@Context SecurityContext sc, RestRemoveTeacherFromSchoolClass  restRemoveTeacherFromSchoolClass){
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
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));

        if (teacher != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                PersistentTeacherOfClassPK tocId = new PersistentTeacherOfClassPK(thr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID());
                TeacherOfClassManager.destroy(tocId);
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
     * @param restSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeStudent")
    public Boolean removeStudentFromSchoolClass(@Context SecurityContext sc, RestRemoveStudentFromSchoolClass restRemoveStudentFromSchoolClass){
        DomSchoolClass domSchoolClass = restRemoveStudentFromSchoolClass.getSchoolClass();
        DomStudent domStudent = restRemoveStudentFromSchoolClass.getStudent();
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((Long) MySQLPersistenceId.getId(domStudent.getId()));
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));

        if (teacher != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(thr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID());
                StudentOfClassManager.destroy(socId);
            }
            catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a student from a schoolclass id {1} one or both do not exists or are not in the school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove the school class.");
        }

        return true;
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
    public Boolean UpdateSchoolClass(@Context SecurityContext sc, RestSchoolClass4Teacher restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass4Teacher().getId()));

        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                schoolClass.setIconizer(restSchoolClass.getDomSchoolClass4Teacher().getIconizer());
                schoolClass.setRegistrationKey(restSchoolClass.getDomSchoolClass4Teacher().getRegistrationKey());
                schoolClass.setClass1(restSchoolClass.getDomSchoolClass4Teacher().getSchoolClassName());
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
     * Edits a singleSchoolStudent.
     *
     * @param sc
     * @param nssStudent
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/updateSingleSchoolStudent")
    public Boolean updateSingleSchoolStudent(@Context SecurityContext sc, RestSingleSchoolStudent nssStudent) {
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
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (sg != null) {
            PersistentUser user = UserManager.findEntity((Long) MySQLPersistenceId.getId(nssStudent.getDomSingleSchoolStudent().getId()));
            if (user == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getDomSingleSchoolStudent().getUsername() + ".");
            }
            if (!user.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by teacher {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            user.setUsername(nssStudent.getDomSingleSchoolStudent().getUsername());
            user.setEmail(nssStudent.getDomSingleSchoolStudent().getEmail());
            user.setFirstname(nssStudent.getDomSingleSchoolStudent().getGivenName());
            user.setMiddlename(nssStudent.getDomSingleSchoolStudent().getInsertion());
            user.setLastname(nssStudent.getDomSingleSchoolStudent().getFamilyName());
            user.setPasswd(nssStudent.getDomSingleSchoolStudent().getPassword());
            try {
                UserManager.edit(user);
            }
            catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getUsername()});
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

}
