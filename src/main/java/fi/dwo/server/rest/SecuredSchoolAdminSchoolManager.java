package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestGetSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestSchoolAdmin;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
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
@Path("/secure/schooladmin/school")
public class SecuredSchoolAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredSchoolAdminSchoolManager.class.getName());

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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
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
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getStudentsInSchoolList")
    public static List<DomStudent> getStudentsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.STUDENT);
            domStudents = new ArrayList<DomStudent>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                domStudents.add(new DomStudent((PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID())));
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
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getSchoolAdminList")
    public static List<DomSchoolAdmin> getSchoolAdminsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomSchoolAdmin> domSchoolAdmin = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.SCHOOLADMIN);
            domSchoolAdmin = new ArrayList<DomSchoolAdmin>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                domSchoolAdmin.add(new DomSchoolAdmin((PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID())));
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        return domSchoolAdmin;
    }

    /**
     * Removes a student from a school and returns true.
     *
     * @param sc
     * @param msg
     * @param restStudent
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeSingleSchoolStudentFromSchool")
    public Boolean removeSingleSchoolStudentFromSchool(@Context SecurityContext sc, RestStudent restStudent) {

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(restStudent.getDomStudent().getId()));
            if (student == null) {
                return false;
            }
            if (!student.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), student.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            UserManager.destroy(student.getId());
        }
        catch (PersistenceException e) {
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
    public Boolean SubmitSingleSchoolStudent(@Context SecurityContext sc, RestSingleSchoolStudent nssStudent) {
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
            user.setEmail(nssStudent.getDomSingleSchoolStudent().getEmail());
            user.setGivenName(nssStudent.getDomSingleSchoolStudent().getGivenName());
            user.setInsertion(nssStudent.getDomSingleSchoolStudent().getInsertion());
            user.setLastname(nssStudent.getDomSingleSchoolStudent().getFamilyName());
            user.setPassword(nssStudent.getDomSingleSchoolStudent().getPassword());
            user.setRegisterDate(now);
            user.setUsername(nssStudent.getDomSingleSchoolStudent().getUserName());
            user.setSchoolGroupId(sg.getSchoolGroupID());
            user.setSingleSchoolAccount(true);

            try {
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school);
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
            PersistentUser user = UserManager.findEntity((Long) MySQLPersistenceId.getId(nssStudent.getDomSingleSchoolStudent().getId()));
            if (user == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getDomSingleSchoolStudent().getUserName() + ".");
            }
            if (!user.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
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
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getSchoolClassList")
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
        List<DomSchoolClass> domSchoolClasses;
        try {
            schoolClasses = SchoolClassManager.findEntities(school);
            LOG.log(Level.FINER, "Fetched all {0} schoolClasses. ", new Object[]{schoolClasses.size()});
            domSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            for (PersistentSchoolClass s : schoolClasses) {
                domSchoolClasses.add(new DomSchoolClass(s));
            }
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
        }
        return domSchoolClasses;
    }

    /**
     * Removes all the teachers data and hasRole in the current school and
     * returns true.
     *
     * @param sc
     * @param restTeacher
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeTeacher")
    public Boolean removeTeacherFromSchool(@Context SecurityContext sc, RestTeacher restTeacher) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity((Long) MySQLPersistenceId.getId(restTeacher.getDomTeacher().getId()));
            if (teacher == null) {
                return false;
            }
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(thr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        return true;
    }

    /**
     * Removes all the students data and hasRole in the current school and
     * returns true.
     *
     * @param sc
     * @param restStudent
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeStudent")
    public Boolean removeStudentFromSchool(@Context SecurityContext sc, RestStudent restStudent) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getId(restStudent.getDomStudent().getId()));
            if (student == null) {
                return false;
            }
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (student.isSingleSchoolAccount()) {
            return false;
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

//        if (!student.isSingleSchoolAccount()) {
//            return true;
//        }
//
//        try {
//            UserManager.destroy(student.getId());
//        }
//        catch (PersistenceException e) {
//            return false;
//        }
        return true;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/removeSchoolAdmin")
    public Boolean removeSchoolAdminFromSchool(@Context SecurityContext sc, RestSchoolAdmin restSchoolAdmin) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser schoolAdmin = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolAdmin = UserManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolAdmin.getDomSchoolAdmin().getId()));
            if (schoolAdmin == null) {
                return false;
            }
            shr = HasRoleUtilManager.getHasRoleInSchool(schoolAdmin, school, RoleType.SCHOOLADMIN);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
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

        PersistentHasRole phr = null;
        PersistentHasRole shr = null;
        PersistentUser student = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        student = UserManager.findEntity(MySQLPersistenceId.getId(submit.getDomGetSingleSchoolStudent().getDomStudent().getId()));

        try {
            shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (student.isSingleSchoolAccount()) {
            return new DomSingleSchoolStudent(student);
        } else {
            LOG.log(Level.SEVERE, "User {0} tried to access full userdata of user {1}.", new Object[]{phr.getPersistentHasRolePK().getId(), shr.getUser().getId()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("/getTeachersSchoolClassList")
    public List<DomSchoolClass> getTeachersSchoolClasses(@Context SecurityContext sc, RestTeacher restTeacher) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentHasRole thr = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            PersistentUser teacher = UserManager.findByUserName(restTeacher.getDomTeacher().getUserName());
            thr = HasRoleUtilManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (thr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<>(tocList.size());
                for (PersistentTeacherOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentTeacherOfClassPK().getClassID());
                    domSchoolClasses.add(new DomSchoolClass(s));
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1} for user {2}. ", new Object[]{domSchoolClasses.size(), thr.getPersistentHasRolePK().getUserID(), phr.getPersistentHasRolePK().getUserID()});
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("/getStudentsSchoolClassList")
    public List<DomSchoolClass> getStudentsSchoolClasses(@Context SecurityContext sc, RestStudent restStudent) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentHasRole thr = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            PersistentUser student = UserManager.findByUserName(restStudent.getDomStudent().getUserName());
            thr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (thr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentStudentOfClass> tocList = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<>(tocList.size());
                for (PersistentStudentOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentStudentOfClassPK().getClassID());
                    domSchoolClasses.add(new DomSchoolClass(s));
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of student {1} for user {2}. ", new Object[]{domSchoolClasses.size(), thr.getPersistentHasRolePK().getUserID(), phr.getPersistentHasRolePK().getUserID()});
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
}
