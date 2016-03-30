package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClassFull;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import static fi.dwo.commons.persistence.DomFactory.*;
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
import fi.dwo.commons.rest.entities.RestNewSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.commons.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClassFull;
import fi.dwo.commons.rest.entities.RestSubmitStudentToSchoolClass;
import fi.dwo.commons.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.commons.util.DwoDateUtilities;
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
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/schooladmin/schoolclass")
public class SecuredSchoolAdminSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredSchoolAdminSchoolClassManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchoolClass> getSchoolClasses(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        List<PersistentSchoolClass> schoolClasses = null;
        List<DomSchoolClass> restSchoolClasses;
        try {
            schoolClasses = SchoolClassManager.findEntities(school);
            LOG.log(Level.FINER, "Fetched all {0} schoolClasses. ", new Object[]{schoolClasses.size()});
            restSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            for (PersistentSchoolClass s : schoolClasses) {
                restSchoolClasses.add(new DomSchoolClass(s));
            }
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
        }
        return restSchoolClasses;
    }

    /**
     * Returns a list of the teachers in a school.
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            domTeachers = new ArrayList<DomTeacher>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                DomTeacher t = newDomTeacher((PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID()));
                domTeachers.add(t);
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        return domTeachers;
    }

    /**
     * Returns a list of the teachers in a school.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getStudentsInSchoolList")
    public List<DomStudent> getStudentsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.STUDENT);
            domStudents = new ArrayList<>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                DomStudent t = new DomStudent((PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID()));
                domStudents.add(t);
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        return domStudents;
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            //Fetch TeacherOfClass
            List<PersistentTeacherOfClass> teachersOfClass = TeacherOfClassManager.findEntities(schoolClass);
            if (teachersOfClass == null) {
                teachersOfClass = new ArrayList<PersistentTeacherOfClass>();
            }
            LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{teachersOfClass.size()});
            List<DomTeacher> domTeachers;
            try {
                domTeachers = new ArrayList<DomTeacher>(teachersOfClass.size());
                for (PersistentTeacherOfClass t : teachersOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentTeacherOfClassPK().getUserID());
                    domTeachers.add(newDomTeacher(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the teachers.");
            }
            return domTeachers;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access a school class from a different school or the schoolClass {1} is null.", new Object[]{sc.getUserPrincipal().getName(), restSchoolClass.getDomSchoolClass().getId()});
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            //Fetch TeacherOfClass
            List<PersistentStudentOfClass> studentsList = StudentOfClassManager.findEntities(schoolClass);
            if (studentsList == null) {
                studentsList = new ArrayList<PersistentStudentOfClass>();
            }
            LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{studentsList.size()});
            List<DomStudent> domStudents;
            try {
                domStudents = new ArrayList<DomStudent>(studentsList.size());
                for (PersistentStudentOfClass t : studentsList) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentStudentOfClassPK().getUserID());
                    domStudents.add(new DomStudent(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the students.");
            }
            return domStudents;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access a school class from a different school or the schoolClass {1} is null.", new Object[]{sc.getUserPrincipal().getName(), restSchoolClass.getDomSchoolClass().getId()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Registers an existing user into a new <school,hasRole> tuple.
     *
     * @param sc
     * @param restData
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitTeacher")
    public Boolean SubmitTeacherToSchoolClass(@Context SecurityContext sc, RestSubmitTeacherToSchoolClass restData) {
        DomTeacher domTeacher = restData.getDomSubmitTeacherToSchoolClass().getTeacher();
        DomSchoolClass domSchoolClass = restData.getDomSubmitTeacherToSchoolClass().getSchoolClass();

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
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
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            toc.setPersistentTeacherOfClassPK(new PersistentTeacherOfClassPK(teacher.getId(), schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
            toc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            TeacherOfClassManager.create(toc);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to add a teacher to a school class in a different school or is a schoolClass with id {1} that does not exist.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to add the school class.");
        }
        return true;
    }

    /**
     * Removes a teacher from a school class and returns true.
     *
     * @param sc
     * @param restData
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeTeacher")
    public Boolean removeTeacherFromSchoolClass(@Context SecurityContext sc, RestRemoveTeacherFromSchoolClass restData) {
        DomTeacher domTeacher = restData.getDomRemoveTeacherFromSchoolClass().getTeacher();
        DomSchoolClass domSchoolClass = restData.getDomRemoveTeacherFromSchoolClass().getSchoolClass();

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
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
     * Registers a new user.
     *
     * @param sc
     * @param nssStudent
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitSingleSchoolStudent")
    public Boolean SubmitSingleSchoolStudent(@Context SecurityContext sc, RestNewSingleSchoolStudent nssStudent) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (sg != null) {
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
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school);
                //add to schoolClass
                PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()));
                PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
                toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(user.getId(), schoolClass.getClassID(), user.getSchoolGroupId()));
                java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
                toSoc.setRegisterDate(d);
                StudentOfClassManager.create(toSoc);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Registers an existing user into a new <school,hasRole> tuple.
     *
     * @param sc
     * @param restData
     * @return true, throws an exception otherwise.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitStudent")
    public Boolean SubmitStudentToSchoolClass(@Context SecurityContext sc, RestSubmitStudentToSchoolClass restData) {
        DomStudent domStudent = restData.getDomSubmitStudentToSchoolClass().getStudent();
        DomSchoolClass domSchoolClass = restData.getDomSubmitStudentToSchoolClass().getSchoolClassTo();

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(domStudent.getId()));
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            PersistentStudentOfClass toc = new PersistentStudentOfClass();
            toc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getId(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
            toc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            StudentOfClassManager.create(toc);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to add a student to a school class in a different school or is a schoolClass with id {1} that does not exist.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to add the school class.");
        }
        return true;
    }

    /**
     * Removes a teacher from a school class and returns true.
     *
     * @param sc
     * @param restData
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeStudent")
    public Boolean removeStudentFromSchoolClass(@Context SecurityContext sc, RestRemoveStudentFromSchoolClass restData) {
        DomStudent domStudent = restData.getDomRemoveStudentFromSchoolClass().getStudent();
        DomSchoolClass domSchoolClass = restData.getDomRemoveStudentFromSchoolClass().getSchoolClass();

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(domStudent.getId()));
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));

        if (student != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                PersistentStudentOfClassPK tocId = new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID());
                StudentOfClassManager.destroy(tocId);
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
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getFull")
    public DomSchoolClassFull getFullSchoolClass(@Context SecurityContext sc, RestSchoolClass schoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
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
                LOG.log(Level.FINER, "Fetched full schoolClass {0} for schooladmin {1]. ", new Object[]{key, phr.getPersistentHasRolePK().getUserID()});

            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return new DomSchoolClassFull(persistentSchoolClass);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
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
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schoolamdin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass.getSchoolID().equals(school.getSchoolID())) {
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
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && restSchoolClass != null) {
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
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        return true;
    }
}
