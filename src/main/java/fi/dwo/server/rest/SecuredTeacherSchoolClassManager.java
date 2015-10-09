package fi.dwo.server.rest;

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
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass4Teacher;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
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
    public List<RestSchoolClass> getTeachersSchoolClasses(@Context SecurityContext sc) {
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
            List<PersistentSchoolClass> schoolClasses = null;
            List<RestSchoolClass> restSchoolClasses;
            try {
                List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr);
                restSchoolClasses = new ArrayList<RestSchoolClass>(tocList.size());
                for (PersistentTeacherOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentTeacherOfClassPK().getClassID());
                    restSchoolClasses.add(new RestSchoolClass(s));
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1]. ", new Object[]{schoolClasses.size(), phr.getPersistentHasRolePK().getUserID()});
                restSchoolClasses = new ArrayList<RestSchoolClass>(schoolClasses.size());
                for (PersistentSchoolClass s : schoolClasses) {
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return restSchoolClasses;
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
    public List<RestTeacher> getTeachersInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<RestTeacher> restTeachers = null;

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
            for (PersistentHasRole hr : hrList) {
                restTeachers.add(new RestTeacher(UserManager.findEntity(hr.getPersistentHasRolePK().getUserID())));
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        return restTeachers;
    }

    /**
     * Adds a schoolClass
     *
     * @param sc
     * @param schoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean SubmitSchoolClass(@Context SecurityContext sc, PersistentSchoolClass schoolClass) {
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

        if (phr != null && schoolClass != null && schoolClass.getSchoolID() == school.getSchoolID()) {
            SchoolClassManager.create(schoolClass);
            schoolClass = SchoolClassManager.findEntity(schoolClass.getClass1(), school);

            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK(phr.getUser().getUserID(), schoolClass.getClassID(), phr.getSchoolGroup().getSchoolGroupID());
            toc.setPersistentTeacherOfClassPK(key);
            java.util.Date d = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
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
    @GET
    @Produces({"application/json"})
    @Path("/getTeacherList")
    public List<RestTeacher> GetTeachersInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
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

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), phr.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (school != null && schoolClass.getSchoolID() == school.getSchoolID() && toc.getPersistentTeacherOfClassPK().getClassID() == schoolClass.getClassID()) {
            //Fetch TeacherOfClass
            List<PersistentTeacherOfClass> teachersOfClass = TeacherOfClassManager.findEntities(schoolClass);
            LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{teachersOfClass.size()});
            List<RestTeacher> restTeachers;
            try {
                restTeachers = new ArrayList<RestTeacher>(teachersOfClass.size());
                for (PersistentTeacherOfClass t : teachersOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentTeacherOfClassPK().getUserID());
                    restTeachers.add(new RestTeacher(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the teachers.");
            }
            return restTeachers;
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
    @GET
    @Produces({"application/json"})
    @Path("/getStudentList")
    public List<RestStudent> GetStudentsInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
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

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), phr.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (school != null && schoolClass.getSchoolID() == school.getSchoolID() && toc.getPersistentTeacherOfClassPK().getClassID() == schoolClass.getClassID()) {
            //Fetch TeacherOfClass
            List<PersistentStudentOfClass> studentsOfClass = StudentOfClassManager.findEntities(schoolClass);
            LOG.log(Level.FINER, "Fetched all {0} students. ", new Object[]{studentsOfClass.size()});
            List<RestStudent> restStudents;
            try {
                restStudents = new ArrayList<RestStudent>(studentsOfClass.size());
                for (PersistentStudentOfClass s : studentsOfClass) {
                    PersistentUser u = UserManager.findEntity(s.getPersistentStudentOfClassPK().getUserID());
                    restStudents.add(new RestStudent(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the students.");
            }
            return restStudents;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherSchoolClassManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(phr.getPersistentHasRolePK().getUserID(), phr.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID()));
        if (schoolClass.getSchoolID() == school.getSchoolID() && toc.getPersistentTeacherOfClassPK().getClassID() == schoolClass.getClassID()) {
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
     * @param restTeacher
     * @param restSchoolClass
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitTeacher")
    public Boolean SubmitTeacherToSchoolClass(@Context SecurityContext sc, RestTeacher restTeacher, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(restTeacher.getId()));
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

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (schoolClass.getSchoolID() == school.getSchoolID()) {
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            toc.setPersistentTeacherOfClassPK(new PersistentTeacherOfClassPK(teacher.getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
            java.util.Date d = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
            toc.setRegisterDate(d);
            TeacherOfClassManager.create(toc);
        }
        return true;
    }

//    /**
//     * Add a single school student to the school class.
//     *
//     * @param sc
//     * @param restStudent
//     * @param restSchoolClass
//     * @return true, throws an exception otherwise.
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/submitStudent")
//    public Boolean SubmitSingleSchoolStudentToSchoolClass(@Context SecurityContext sc, RestStudent restStudent, RestSchoolClass restSchoolClass) {
//        PersistentHasRole phr;
//        PersistentSchool school = null;
//        PersistentUser student = null;
//        PersistentHasRole thr = null;
//        try {
//            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
//            school = HasRoleUtilManager.getSchoolforHasRole(phr);
//            student = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(restStudent.getId()));
//            if (student == null) {
//                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find student to add.");
//            }
//            thr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
//        }
//        catch (Dwo2Exception ex) {
//            Logger.getLogger(SecuredTeacherSchoolClassManager.class
//                    .getName()).log(Level.SEVERE, null, ex);
//            throw new Dwo2RestException(ex);
//        }
//
//        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
//        if (schoolClass.getSchoolID() == school.getSchoolID() && student.isSingleSchoolAccount()) {
//            PersistentStudentOfClass toc = new PersistentStudentOfClass();
//            toc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getUserID(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
//            java.util.Date d = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
//            toc.setRegisterDate(d);
//            StudentOfClassManager.create(toc);
//        }
//        return true;
//    }

    /**
     * Removes a teacher from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restSchoolClass
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeTeacher")
    public Boolean removeTeacherFromSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass, RestTeacher restTeacher) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(restTeacher.getId()));
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));

        if (teacher != null && schoolClass != null && schoolClass.getSchoolID() == school.getSchoolID()) {
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
     * @param restSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeStudent")
    public Boolean removeStudentFromSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass, RestStudent restStudent) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(restStudent.getId()));
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));

        if (teacher != null && schoolClass != null && schoolClass.getSchoolID() == school.getSchoolID()) {
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

        schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));

        if (schoolClass != null && schoolClass.getSchoolID() == school.getSchoolID()) {
            try {
                schoolClass.setIconizer(1==restSchoolClass.getIconizer());
                schoolClass.setRegistrationKey(restSchoolClass.getRegistrationKey());
                schoolClass.setClass1(restSchoolClass.getSchoolClassName());
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
    @Path("/submitSingleSchoolStudent")
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
            PersistentUser user = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(nssStudent.getId()));
            if (user == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getUsername() + ".");
            }
            if (!user.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }

            user.setEmail(nssStudent.getEmail());
            user.setFirstname(nssStudent.getGivenName());
            user.setMiddlename(nssStudent.getInsertion());
            user.setLastname(nssStudent.getFamilyName());
            user.setPasswd(nssStudent.getPassword());
            try {
                UserManager.edit(user);
            }
            catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getUsername()});
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

}
