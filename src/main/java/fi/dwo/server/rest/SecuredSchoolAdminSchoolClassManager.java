package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.commons.util.DwoDateUtilities;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

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
public class SecuredSchoolAdminSchoolClassManager extends AbstractSchoolClassManager {

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
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        List<PersistentSchoolClass> schoolClasses = null;
        List<DomSchoolClass> restSchoolClasses;
        try {
            schoolClasses = SchoolClassManager.findEntities(school);
            LOG.log(Level.FINER, "Fetched all {0} schoolClasses. ", new Object[]{schoolClasses.size()});
            restSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            for (PersistentSchoolClass s : schoolClasses) {
                restSchoolClasses.add(s.createDomSchoolClass());
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
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.TEACHER);
            domTeachers = new ArrayList<>(userList.size());
            for (PersistentUser u : userList) {
                domTeachers.add(u.buildDomTeacher());
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
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
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
//        List<PersistentHasRole> hrList;
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
            domStudents = new ArrayList<DomStudent>(userList.size());
            for(PersistentUser u:userList){
                domStudents.add(u.buildDomStudent());
            }
//            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.STUDENT);
//            domStudents = new ArrayList<DomStudent>(hrList.size());
//            for (PersistentHasRole hr : hrList) {
//                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
//                domStudents.add(user.buildDomStudent());
//            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
                    domTeachers.add(u.buildDomTeacher());
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            //Fetch StudentOfClass
            List<PersistentStudentOfClass> studentsList = StudentOfClassManager.findEntities(schoolClass);
            if (studentsList == null) {
                studentsList = new ArrayList<PersistentStudentOfClass>();
            }
            LOG.log(Level.FINER, "Fetched all {0} students. ", new Object[]{studentsList.size()});
            List<DomStudent> domStudents;
            try {
                domStudents = new ArrayList<DomStudent>(studentsList.size());
                for (PersistentStudentOfClass t : studentsList) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentStudentOfClassPK().getUserID());
                    if (u != null) { // if not found,  no constraint of foreign key.
                    	domStudents.add(u.buildDomStudent());
                    }
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
        if(restData==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
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
            LOG.log(Level.SEVERE, "", ex);
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
        if(restData==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
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
            LOG.log(Level.SEVERE, "", ex);
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
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
                //add to schoolClass
                PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(MySQLPersistenceId.getId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()));
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school, schoolClass);
                PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
                toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(user.getId(), schoolClass.getClassID(), user.getSchoolGroupId()));
                java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
                toSoc.setRegisterDate(d);
                StudentOfClassManager.create(toSoc);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
                LOG.log(Level.SEVERE, "", ex);
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
        if(restData==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
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
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));
        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
//            PersistentStudentOfClass toc = new PersistentStudentOfClass();
//            toc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(student.getId(), schoolClass.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID()));
//            toc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
//            StudentOfClassManager.create(toc);
        	return SchoolClassUtilManager.registerStudentForSchoolClass(shr, schoolClass);
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to add a student to a school class in a different school or is a schoolClass with id {1} that does not exist.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to add the school class.");
        }
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
        if(restData==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
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
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(domSchoolClass.getId()));

        return removeStudentFromSchoolClass(sc, school, student, shr,
				schoolClass);
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
    @Path("/getFull")
    public DomSchoolClassFull getFullSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            PersistentSchoolClass persistentSchoolClass;
            Long key = (Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId());
            try {
                persistentSchoolClass = SchoolClassManager.findEntity(key);
                LOG.log(Level.FINER, "Fetched full schoolClass {0} for schooladmin {1]. ", new Object[]{key, phr.getPersistentHasRolePK().getUserID()});

            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return persistentSchoolClass.createDomSchoolClassFull();
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
        if(restSchoolClass==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schoolamdin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);

        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));
        if (schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                //Loop students in class
                List<PersistentStudentOfClass> studentList = StudentOfClassManager.findEntities(schoolClass);
                for (PersistentStudentOfClass t : studentList) {
                    //remove students
                    //StudentOfClassManager.destroy(t.getPersistentStudentOfClassPK());
                	
                	Long id = t.getPersistentStudentOfClassPK().getUserID();
					PersistentUser student = UserManager.findEntity(id);
					PersistentHasRole shr = HasRoleUtilManager.getHasRoleInSchool(student, school, RoleType.STUDENT);
					removeStudentFromSchoolClass(sc, school, student, shr, schoolClass);
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
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
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
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        return true;
    }
}
