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
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DatatypeConverter;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import nl.uu.fi.dwo.rest.entities.RestContext;
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
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SC_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import fi.dwo.server.rest.util.Realm;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.digitalmolehill.crypto.SymmetricCryptor;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.security.TOTP;

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
    @Deprecated
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

    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchoolClass> getTeachersSchoolClasses(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
    	TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher().setTeacher();
    	return state.getSchoolClasses();
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
        	phr = getHasRole(sc, schoolClass.getRestContext());
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
    @Deprecated
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
            String realm = Realm.of(sc.getUserPrincipal());
            for (PersistentHasRole hr : hrList) {
                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                domTeachers.add(user.buildDomTeacher(realm));
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        return domTeachers;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersInSchoolList")
    public List<DomTeacher> getTeachersInSchool(@Context SecurityContext sc, RestContext rest) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomTeacher> domTeachers = null;

        try {
            phr = getHasRole(sc, rest.getRestContext());
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            domTeachers = new ArrayList<DomTeacher>(hrList.size());
            String realm = rest.getRestContext().getRealm();
            for (PersistentHasRole hr : hrList) {
                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                domTeachers.add(user.buildDomTeacher(realm));
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
    @Deprecated
    public List<DomStudent> getTeachersStudents(@Context SecurityContext sc) {
        List<DomStudent> domStudents = null;
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setDefaultHasRole()
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.getTeachersStudents();

        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersStudents")
    public List<DomStudent> getTeachersStudents(@Context SecurityContext sc, RestContext rest) {
        List<DomStudent> domStudents = null;
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
            	   .setRealm(rest.getRestContext().getRealm())
                   .setHasRole(rest.getRestContext().getDomHasRole())
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
    @Deprecated
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
            String realm = Realm.of(sc.getUserPrincipal());
            for (PersistentUser u : userList) {
                if (u.isSingleSchoolAccount()) {
                    domStudents.add(u.buildDomStudent(realm));
                }
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        return domStudents;
    }
    @PUT
    @Produces({"application/json"})
    @Path("/getSingleSchoolStudentsInSchoolList")
    public List<DomStudent> getSingleSchoolStudentsInSchool(@Context SecurityContext sc, RestContext rest) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;

        try {
            phr = getHasRole(sc, rest.getRestContext());
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
            domStudents = new ArrayList<>(userList.size());
            String realm = rest.getRestContext().getRealm();
            for (PersistentUser u : userList) {
                if (u.isSingleSchoolAccount()) {
                    domStudents.add(u.buildDomStudent(realm));
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
        	phr = getHasRole(sc, restSchoolClass.getRestContext());
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
        	phr = getHasRole(sc, restSchoolClass.getRestContext());
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
                String realm = Realm.of(restSchoolClass.getRestContext());
                for (PersistentTeacherOfClass t : teachersOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentTeacherOfClassPK().getUserID());
                    domTeachers.add(u.buildDomTeacher(realm));
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
        	phr = getHasRole(sc, restSchoolClass.getRestContext());
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
                String realm = Realm.of(restSchoolClass.getRestContext());
                for (PersistentStudentOfClass s : studentsOfClass) {
                    PersistentUser u = UserManager.findEntity(s.getPersistentStudentOfClassPK().getUserID());
                    if(u != null)
                      domStudents.add(u.buildDomStudent(realm));
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
        	phr = getHasRole(sc, restSchoolClass.getRestContext());
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
            	// loop student models in class
            	List<PersistentStudentModelOfClass> ofClass = StudentModelOfClassManager.findEntities(schoolClass);
                for( PersistentStudentModelOfClass item: ofClass) {
              	  try {
      				StudentModelOfClassManager.destroy(item.getId()); // not fatal
              	  } catch (PersistenceException e) {
              	  }
                }
           	
            	
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
                // Loop classcourses of class
        		List<PersistentClassCourse> cclist = ClassCourseManager.findEntities(schoolClass);
        		for (PersistentClassCourse cc : cclist) {
        			ClassCourseManager.destroy(cc.getClassCourseID());
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
        	phr = getHasRole(sc, restSubmitTeacherToSchoolClass.getRestContext());
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
        	phr = getHasRole(sc, restSubmitStudentToSchoolClass.getRestContext());
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

	private PersistentHasRole getHasRole(SecurityContext sc, DomContext context) throws Dwo2Exception {
		PersistentHasRole phr;
		if (context == null || context.getDomHasRole() == null)
			phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER);
		else 
		{
			phr = HasRoleManager.findEntity(MySQLPersistenceId.getNativeId(context.getDomHasRole()));
			if (! phr.getUser().getUsername().equals(sc.getUserPrincipal().getName())||
					phr.getSchoolGroup().getGroupID() != RoleType.TEACHER.ordinal())
				throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "No Permission");
		}
		return phr;
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
            phr = getHasRole(sc, restMoveStudentToSchoolClass.getRestContext());
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
            phr = getHasRole(sc, restRemoveTeacherFromSchoolClass.getRestContext());
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
            phr = getHasRole(sc, restRemoveStudentFromSchoolClass.getRestContext());
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
            phr = getHasRole(sc, restSchoolClass.getRestContext());
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
            phr = getHasRole(sc, submit.getRestContext());
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

        if (student.isSingleSchoolAccount() 
                && teacherInSchoolClass.getPersistentTeacherOfClassPK().getClassID()!=null
                && studentInSchoolClass.getPersistentStudentOfClassPK().getClassID()!=null
                && teacherInSchoolClass.getPersistentTeacherOfClassPK().getClassID().longValue()==studentInSchoolClass.getPersistentStudentOfClassPK().getClassID().longValue()) {
            String realm = Realm.of(submit.getRestContext());
            return student.buildDomSingleSchoolStudent(realm);
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
            phr = getHasRole(sc, nssStudent.getRestContext());
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
            //user.setUsername(nssStudent.getDomSingleSchoolStudent().getUserName());
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
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
    	String dwo_env = System.getProperty("DWO_ENV", "app");
    	if (dwo_env.contains("saml"))
    		throw new WebApplicationException(HttpServletResponse.SC_NOT_FOUND);

    	if (nssStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        if (!ValidUserFieldsChecker.isValidEmail(nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        String userName = nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getUserName();
        if (!ValidUserFieldsChecker.isValidUserName(userName)) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
        }

        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        PersistentTeacherOfClass toc = null;

        try {
            phr = getHasRole(sc, nssStudent.getRestContext());
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
            String realm = Realm.of(nssStudent.getRestContext());
            if (realm != null) userName += realm;
            user.setUsername(userName);
            user.setSchoolGroupId(sg.getSchoolGroupID());
            user.setSingleSchoolAccount(true);
            try {
                Long schoolclassID = MySQLPersistenceId.getNativeId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass());
                if (schoolclassID == null) throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set,"null schoolclas");
                PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(schoolclassID);
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
        UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
        UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
        TeacherState_HR_R_S_SG_U tstate = hrstate.buildSchoolAdminTeacher().setTeacher();
        TeacherState_HR_P_R_S_SC_SG_U psstate = tstate.addProfile(rest.getDomSchoolClassAndProfile().getDomDwoProfile()).addSchoolClass(rest.getDomSchoolClassAndProfile().getDomSchoolClass());
      //init
        PersistentHasRole phr = hrstate.getHasRole();
        PersistentSchool school = hrstate.getSchool();
        PersistentSchoolClass schoolClass = psstate.getSchoolClass();
        final PersistentDwoProfile profile = psstate.getDwoProfile();

        //verify if user is in class
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setClassID(schoolClass.getClassID());
        key.setSchoolGroupID(phr.getPersistentHasRolePK().getSchoolGroupID());
        key.setUserID(phr.getPersistentHasRolePK().getUserID());
        PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(key);
        if (toc == null) {
            String msg = MessageFormat.format("Username {0} is not a teacher of schoolclass {1}.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClass1()});
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

        Long profileID = profile.getDwoProfileID();

        //fetch all courses in the school and profile
        Collection<PersistentCourse> listCourse = CourseManager.findEntities(profileID, school.getSchoolID()); // XXX children of trashed folders in list
        Map<Long, PersistentCourse> courseMap = new TreeMap<>();
        listCourse.forEach(item -> courseMap.put(item.getCourseID(), item));
        listCourse = new LinkedList<>(listCourse); // implementatie met snelle Iterator.remove voor accesscontrol en trash
// Filter by schoolAccess       
        if (school.accessControl()) {
          List<PersistentACL> acls = ACLManager.findBySchool(school, profile);
          List<PersistentSchoolClass> classes = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
          Set<String> rights = classes.stream().map(c -> c.buildPersistenceId().getIdString()).collect(Collectors.toSet());
          rights.add(ustate.getUser().buildPersistenceId().getIdString());
          rights.add(school.buildPersistenceId().getIdString());
          Map<Long, List<PersistentACL>> aclmap = new TreeMap<>();
          acls.forEach(acl -> {
            List<PersistentACL> l = aclmap.get(acl.getCourseID());
            if (l == null) { l = new ArrayList<>(); aclmap.put(acl.getCourseID(), l); }
            l.add(acl);
          });
          //listCourse = courseMap.values();
          Iterator<PersistentCourse> iterator = listCourse.iterator();
          while (iterator.hasNext()) {
            PersistentCourse pc = iterator.next();
            if (pc.getSchoolID() == null) continue;
            final PersistentCourse pc0 = pc;
            List<PersistentACL> a = aclmap.get(pc.getCourseID());
            boolean parent = false;
            while ( (a == null || a.isEmpty()) && pc != null) {
              parent = true;
              pc = courseMap.get(pc.getParentID());
              if (pc != null) a = aclmap.get(pc.getCourseID());
              else a = null;
            }
            if (a ==null) {
              if (! school.teachersCanWrite()) {
            	  
            	  iterator.remove();
                  courseMap.remove(pc0.getCourseID(), pc0);
              }
            } else {
              ACL acl = a.stream()
                  .filter(item -> rights.contains(item.getEntity()))
                  .map(PersistentACL::getAccess)
                  .sorted( (ACL aa, ACL bb) -> - aa.compareTo(bb))
                  .findFirst()
                  .orElse(ACL.NONE);
              if ( acl == ACL.NONE|| (parent && acl == ACL.ACCESS))
              {  iterator.remove();
                 courseMap.remove(pc0.getCourseID(), pc0);
              }
            }
          }
          
        } else if (school.getAboType() != AboType.premium) {
          Iterator<PersistentCourse> iterator = listCourse.iterator();
          while (iterator.hasNext()) {
            PersistentCourse pc = iterator.next();
            if (!PublicCourseManager.visible(pc))
            {
              iterator.remove();
              courseMap.remove(pc.getCourseID(), pc);
            }
          }
        }
 // filter children/offspring of trash
        boolean trashed;
        do {
        	trashed = false;
        	Iterator<PersistentCourse> iterator = listCourse.iterator();
        	while(iterator.hasNext()) {
        		PersistentCourse pc = iterator.next();
        		long parent = pc.getParentID();
        		if (parent != 0 && !courseMap.containsKey(Long.valueOf(parent))) {
        			iterator.remove();
        			courseMap.remove(pc.getCourseID(), pc);
        			trashed = true;
        		}
        	}
        } while (trashed);
        result.setCourses(listCourse.stream().map((e) -> new DomMapEntry<PersistenceId, DomCourse>(e.buildPersistenceId(), e.buildDomCourse()))
                .collect(Collectors.toList()));

        List<PersistentClassCourse> listClassCourse = ClassCourseManager.findEntities(schoolClass, profileID);

        Map<PersistenceId, DomClassCourse4Teacher> classCourseMap = new HashMap<>();
 
        listClassCourse.forEach(
                (scc) -> {
                    Long courseID = scc.getCourseID();
                    PersistentCourse course = courseMap.get(courseID);
                    if (course == null) {
                        LOG.log(Level.INFO, "course null for courseid = " + courseID + " sccid = " + scc.getClassCourseID());
                    } else {
                        DomClassCourse4Teacher dcc = scc.buildDomClassCourse4Teacher();
                        classCourseMap.put(dcc.getId(), dcc);
                    }
                });

        result.setSchoolClass(schoolClass.buildDomSchoolClass());
        result.setClassCourses(classCourseMap.entrySet()
                .stream()
                .map((e) -> new DomMapEntry<PersistenceId, DomClassCourse4Teacher>(e))
                .collect(Collectors.toList()));
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
    @Path("/addCourseToClass")
    public Boolean addCourseToClass(@Context SecurityContext sc, RestSchoolClassCourseAndProfileNew rest) throws Dwo2Exception {
        //secure builder to detach course by setting it invisible.
        try {
            TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher()
                    .addProfile(rest.getDomSchoolClassCourseAndProfileNew().getDomDwoProfile())
                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfileNew().getDomSchoolClass())
                    .addCourse(rest.getDomSchoolClassCourseAndProfileNew().getCourse());
            return build.addCourseToClass(rest.getDomSchoolClassCourseAndProfileNew().getCourseType(), rest.getDomSchoolClassCourseAndProfileNew().getFrom(),
                    rest.getDomSchoolClassCourseAndProfileNew().getTo(),
                    rest.getDomSchoolClassCourseAndProfileNew().getAccessKey());
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
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
            TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher()
                    .addProfile(rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile())
                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass())
                    .addCourse(rest.getDomSchoolClassCourseAndProfile().getCourse());
            return build.attachCourseToClass();
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
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
        try {
            TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher()
                    .addProfile(rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile())
                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass())
                    .addCourse(rest.getDomSchoolClassCourseAndProfile().getCourse());
            return build.detachCourseFromClass();
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }

//    /**
//     * Detaches a leaf course that a class in a school can see.
//     *
//     * @param sc
//     * @param rest
//     * @return
//     * @throws Dwo2Exception
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/detachCourseFromClass")
//    public Boolean detachCourseFromClass(@Context SecurityContext sc, RestSchoolClassCourseAndProfile rest) throws Dwo2Exception {
////secure builder to detach course by setting it invisible.
//        try {
//            CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
//                    .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
//                    .addSchoolClass(rest.getDomSchoolClassCourseAndProfile().getDomSchoolClass())
//                    .addProfile(rest.getDomSchoolClassCourseAndProfile().getDomDwoProfile())
//                    .addCourse(rest.getDomSchoolClassCourseAndProfile().getCourse());
//            List<PersistentClassCourse> pcc = ClassCourseManager.findEntities(build.getSchoolClass(), build.getCourse());
//            if (pcc.size() > 0) {
//                //update type.
//                pcc.get(0).setViewState(ViewState.invisible);
//                ClassCourseManager.insertOrUpdateViewState(pcc.get(0));
//                return true;
//            }
//        } catch (Dwo2Exception e) {
//            throw new Dwo2RestException(e);
//        }
//        return false;
//    }
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

    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersClassesOfStudent")
    public List<DomSchoolClassId> getTeachersClassesOfStudent(@Context SecurityContext sc, RestStudent restStudent) throws Dwo2Exception {
        TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(restStudent.getRestContext().getDomHasRole())
                .buildSchoolAdminTeacher()
                .setTeacher();
        return build.getTeachersClassesOfStudent(restStudent.getDomStudent());
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getSharedTeacherClasses")
    public List<DomSchoolClassId> getSharedTeacherClasses(@Context SecurityContext sc, RestTeacher restTeacher) throws Dwo2Exception {
        TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(restTeacher.getRestContext().getDomHasRole())
                .buildSchoolAdminTeacher()
                .setTeacher();
        return build.getSharedTeacherClasses(restTeacher.getDomTeacher());
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getBearerToken")
    @RolesAllowed("TEACHER")
    public String getBearerToken(@Context SecurityContext sc, RestStudent rest) throws Exception {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
        TeacherState_HR_R_S_SG_U state = ustate.setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher().setTeacher();

        String teacher = sc.getUserPrincipal().getName();
    	String student = UserManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomStudent())).getUsername();
    	List<PersistentLoginContext> list = LoginContextManager.findEntities(ustate.getUser().getId());
    	String password = DatatypeConverter.printHexBinary(list.get(0).getSecretKey());
        student = new SymmetricCryptor().encrypt(password.toCharArray(), student);
        Long time = DwoDateUtilities.getCurrentDwoUnixTimeStamp() / TOTP.defaultPeriod;
        String timeString = time.toString();
        String result = TOTP.generateTOTP(password, timeString, "8");
        byte[] bytes = ("4\f" + teacher + "\f" + student + "\f" + result).getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

}
