package fi.dwo.server.rest;

import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.server.PersistentDataManagers.util.DwoSystemParametersUtilManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.OrderType;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentInClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.system.MD5;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.entities.RestUserFullv2;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.cache.SchoolCache;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import fi.dwo.server.rest.util.MailUtilManager;
import fi.dwo.server.rest.util.Realm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.persistence.internal.identitymaps.WeakCacheKey;

import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

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

    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersInSchoolList")
    public List<DomTeacher> getTeachersInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
       UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
      .setRealm(rest.getRestContext().getRealm())
      .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
      PersistentSchool school = state.getSchool();
      List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.TEACHER);
      ArrayList<DomTeacher> domTeachers = new ArrayList<>(userList.size());
      String realm = state.getRealm();
      for (PersistentUser u : userList) {
          domTeachers.add(u.buildDomTeacher(realm));
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
    @Path("/getTeachersInSchoolList")
    public List<DomTeacher> getTeachersInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomTeacher> domTeachers = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        String realm = Realm.of(sc.getUserPrincipal());
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.TEACHER);
            domTeachers = new ArrayList<>(userList.size());
            for (PersistentUser u : userList) {
                domTeachers.add(u.buildDomTeacher(realm));
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        return domTeachers;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersAndHasRoleInSchool")
    public List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
    	final UserState_U state0 = AnonDomainAuthorizer.build().submitUser(sc)
    			.setRealm(rest.getRestContext().getRealm());
		final DomHasRole hasrole = rest.getRestContext().getDomHasRole();
		UserState_HR_R_S_SG_U state = 
				hasrole == null ? state0.setDefaultHasRole() :
				state0.setHasRoleIfType(hasrole, RoleType.SCHOOLADMIN);
        PersistentSchool school = state.getSchool();
        List<PersistentHasRole> hrList;
        List<DomTeacherAndHasRole> resultList = null;
        hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
        resultList = new ArrayList<>(hrList.size());
        for (PersistentHasRole hr : hrList) {
            PersistentUser user = hr.getUser();
            if (hr.getLastLogin() == null) hr.setLastLogin(user.getLastLogin());
            DomTeacherAndHasRole domTAHR = new DomTeacherAndHasRole();
            domTAHR.setTeacher(user.buildDomTeacher(state.getRealm()));
            domTAHR.setHasRole(hr.buildDomHasRole());
            resultList.add(domTAHR);
        }
        return resultList;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getSchoolAdminsAndHasRoleInSchool")
    public List<DomSchoolAdminAndHasRole> getSchoolAdminsAndHasRoleInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
    	final UserState_U state0 = AnonDomainAuthorizer.build().submitUser(sc)
    			.setRealm(rest.getRestContext().getRealm());
		final DomHasRole hasrole = rest.getRestContext().getDomHasRole();
		UserState_HR_R_S_SG_U state = 
				hasrole == null ? state0.setDefaultHasRole() :
				state0.setHasRoleIfType(hasrole, RoleType.SCHOOLADMIN);
        PersistentSchool school = state.getSchool();
        List<PersistentHasRole> hrList;
        List<DomSchoolAdminAndHasRole> resultList = null;
        hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.SCHOOLADMIN);
        resultList = new ArrayList<>(hrList.size());
        for (PersistentHasRole hr : hrList) {
            PersistentUser user = hr.getUser();
            if (hr.getLastLogin() == null) hr.setLastLogin(user.getLastLogin());
            DomSchoolAdminAndHasRole domTAHR = new DomSchoolAdminAndHasRole();
            domTAHR.setSchoolAdmin(user.buildDomSchoolAdmin(state.getRealm()));
            domTAHR.setHasRole(hr.buildDomHasRole());
            resultList.add(domTAHR);
        }
        return resultList;
    }
   
    
    @PUT
    @Produces({"application/json"})
    @Path("/getStudentsInSchoolList")
    public List<DomStudent> getStudentsInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
     .setRealm(rest.getRestContext().getRealm())
     .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
     PersistentSchool school = state.getSchool();
     List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
     ArrayList<DomStudent>domStudents = new ArrayList<DomStudent>(userList.size());
     String realm = state.getRealm();
     for (PersistentUser u : userList) {
         domStudents.add(u.buildDomStudent(realm));
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
    @Path("/getStudentsInSchoolList")
    public List<DomStudent> getStudentsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomStudent> domStudents = null;
        String realm = Realm.of(sc.getUserPrincipal());
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
//        List<PersistentHasRole> hrList;
        try {
            List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
            domStudents = new ArrayList<DomStudent>(userList.size());
            for (PersistentUser u : userList) {
                domStudents.add(u.buildDomStudent(realm));
            }
//            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.STUDENT);
//            domStudents = new ArrayList<DomStudent>(hrList.size());
//            for (PersistentHasRole hr : hrList) {
//                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
//                domStudents.add(user.buildDomStudent());
//            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
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
    public List<DomSchoolAdmin> getSchoolAdminsInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomSchoolAdmin> domSchoolAdminList = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
//        List<PersistentHasRole> hrList;
        try {
          String realm = Realm.of(sc.getUserPrincipal());
           List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.SCHOOLADMIN);
            domSchoolAdminList = new ArrayList<>(userList.size());
            for (PersistentUser u : userList) {
                domSchoolAdminList.add(u.buildDomSchoolAdmin(realm));
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        return domSchoolAdminList;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getSchoolAdminList")
    public List<DomSchoolAdmin> getSchoolAdminsInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
	    UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
	    	      .setRealm(rest.getRestContext().getRealm())
	    	      .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
	      state.buildSchoolAdminTeacher();
	      PersistentSchool school = state.getSchool();
	      List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.SCHOOLADMIN);
	      ArrayList<DomSchoolAdmin> domSchoolAdminList = new ArrayList<>(userList.size());
	      String realm = state.getRealm();
	      for (PersistentUser u : userList) {
	          domSchoolAdminList.add(u.buildDomSchoolAdmin(realm));
	      }
	      return domSchoolAdminList;
    }

    /**
     * Removes a student from a school and returns true.
     *
     * @param sc
     * @param restStudent
     * @return true if success, false if the teacher does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/removeSingleSchoolStudentFromSchool")
    public Boolean removeSingleSchoolStudentFromSchool(@Context SecurityContext sc, RestStudent restStudent) {
        if (restStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity((Long) MySQLPersistenceId.getNativeId(restStudent.getDomStudent()));
            if (student == null) {
                return false;
            }
            if (!student.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), student.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        
        try {
            UserUtilManager.deleteUser(student);
        } catch (PersistenceException e) {
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
    public Boolean submitSingleSchoolStudent(@Context SecurityContext sc, RestNewSingleSchoolStudent nssStudent) {
        if (nssStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
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
            String userName = nssStudent.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getUserName();
            String realm = Realm.of(nssStudent.getRestContext());
            if (realm != null) 
              userName = userName + realm;
            user.setUsername(userName);
            user.setSchoolGroupId(sg.getSchoolGroupID());
            user.setSingleSchoolAccount(true);
            try {
                PersistentSchoolClass schoolClass = null;
                if (nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass() != null) {
                    SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(nssStudent.getDomNewSingleSchoolStudent().getDomSchoolClass()));
                }
                //add student with default schoolclass with schoolclass possible for null
                SchoolUtilManager.addSingleSchoolStudentAccount(user, school, schoolClass);

                if (schoolClass != null) {
                    //add to schoolClass
                    PersistentStudentOfClass toSoc = new PersistentStudentOfClass();
                    toSoc.setPersistentStudentOfClassPK(new PersistentStudentOfClassPK(user.getId(), schoolClass.getClassID(), user.getSchoolGroupId()));
                    java.util.Date d = DwoDateUtilities.getCurrentDwoDateAsCalendarDate().getTime();
                    toSoc.setRegisterDate(d);
                    StudentOfClassManager.create(toSoc);
                }

            } catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } else {
            return false;
        }
        return true;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/submitTeacherv2")
    public Boolean submitTeacher2(@Context SecurityContext sc, RestUserFullv2 rest, @Context ServletContext context) throws Dwo2Exception, IOException, AddressException, MessagingException {
    	UserState_HR_R_S_SG_U s0 = AnonDomainAuthorizer.build().submitUser(sc)
    			.setRealm(rest.getRestContext().getRealm())
    			.setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
    	PersistentSchool school = s0.getSchool();
    	String realm = s0.getRealm();
    	//SchoolAdminTeacherState_HR_R_S_SG_U s1 = s0.buildSchoolAdminTeacher();
    	//SchoolAdminState_HR_P_R_S_SG_U s2 = s1.setSchoolAdmin().addProfile(rest.getDwoProfile());
    	PersistentUser user = new PersistentUser();
        Date now = DwoDateUtilities.getCurrentDwoDate();
        user.setEmail(rest.getDomUserFull().getEmail());
        user.setGivenName(rest.getDomUserFull().getGivenName());
        user.setInsertion(rest.getDomUserFull().getInsertion());
        user.setLastname(rest.getDomUserFull().getFamilyName());
        
        String password = rest.getDomUserFull().getPassword();
        password = DomLoginCheck.crypt(password);
        user.setPassword(MD5.getHashString(password));
        user.setRegisterDate(now);
        String userName = rest.getDomUserFull().getUserName();
        if (realm != null) userName = userName + realm;
        user.setUsername(userName);
        user.setSingleSchoolAccount(false);
        SchoolUtilManager.addAccountAsTeacherInSchool(user, school);
        user.setPassword(password);
        Map<String,Object> properties = new TreeMap<>();
        Long pid = MySQLPersistenceId.getNativeId(rest.getDwoProfile());
        DomDwoProfileFull profile = PublicProfileCache.get(pid);
// cache can fail... (sure in testing)
        if (profile == null) {
        	profile = DwoProfileManager.findEntity(pid).buildDomDwoProfileFull();
        }
        properties.put("school", school);
        properties.put("sender", s0.getUser());
        properties.put("profile", profile);
        properties.put("user", user);
        MailUtilManager.send(user.getEmail(), properties, getClass().getPackage().getName() + ".submitTeacherMessage", profile.getLanguage(), context);
        
    	return Boolean.TRUE;
    }
    
    
    /**
     * Registers a new user and make him/her a teacher in the school.
     *
     * @param sc
     * @param teacher
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitTeacher")
    public Boolean submitTeacher(@Context SecurityContext sc, RestUserFull teacher) {
        if (teacher == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
//        PersistentSchoolGroup sg = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
//            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.TEACHER);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        if ( !school.licenseIsValid()) {
          throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "licence expired");
        }
//        if (sg != null) {
        Date now = DwoDateUtilities.getCurrentDwoDate();
        PersistentUser user = new PersistentUser();
        user.setEmail(teacher.getDomUserFull().getEmail());
        user.setGivenName(teacher.getDomUserFull().getGivenName());
        user.setInsertion(teacher.getDomUserFull().getInsertion());
        user.setLastname(teacher.getDomUserFull().getFamilyName());
        user.setPassword(teacher.getDomUserFull().getPassword());
        user.setRegisterDate(now);
        String userName = teacher.getDomUserFull().getUserName();
        String realm = Realm.of(teacher.getRestContext());
        if (realm != null) userName = userName + realm;
        //          user.setSchoolGroupId(sg.getSchoolGroupID());
        user.setUsername(userName);
      user.setSingleSchoolAccount(false);
        try {
            SchoolUtilManager.addAccountAsTeacherInSchool(user, school);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
//        } else {
//            return false;
//        }
        return Boolean.TRUE;
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
        if (nssStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolGroup sg = null;
        PersistentUser user = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            sg = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
            user = UserManager.findEntity(MySQLPersistenceId.getNativeId(nssStudent.getDomSingleSchoolStudent()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (sg != null) {
            if (user == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getDomSingleSchoolStudent().getUserName() + ".");
            }
            if (!user.isSingleSchoolAccount()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
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
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        List<PersistentSchoolClass> schoolClasses = null;
        List<DomSchoolClass> domSchoolClasses;
        try {
            schoolClasses = SchoolClassManager.findEntities(school);
            LOG.log(Level.FINER, "Fetched all {0} schoolClasses. ", new Object[]{schoolClasses.size()});
            domSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            for (PersistentSchoolClass s : schoolClasses) {
                domSchoolClasses.add(s.buildDomSchoolClass());
            }
        } catch (Exception e) {
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
        if (restTeacher == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser teacher = null;
        PersistentHasRole thr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            teacher = UserManager.findEntity(MySQLPersistenceId.getNativeId(restTeacher.getDomTeacher()));
            if (teacher == null) {
                return false;
            }
            thr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(teacher, school, RoleType.TEACHER);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(thr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
        if (restStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser student = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(restStudent.getDomStudent()));
            if (student == null) {
                return false;
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (student.isSingleSchoolAccount()) {
            return false;
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
        if (restSchoolAdmin == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentUser schoolAdmin = null;
        PersistentHasRole shr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            schoolAdmin = UserManager.findEntity((Long) MySQLPersistenceId.getNativeId(restSchoolAdmin.getDomSchoolAdmin()));
            if (schoolAdmin == null) {
                return false;
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(schoolAdmin, school, RoleType.SCHOOLADMIN);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            HasRoleUtilManager.removeHasRoleAndItsData(shr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
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
        if (submit == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }

        PersistentHasRole phr = null;
        PersistentHasRole shr = null;
        PersistentUser student = null;
        PersistentSchool school = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            student = UserManager.findEntity(MySQLPersistenceId.getNativeId(submit.getDomGetSingleSchoolStudent().getDomStudent()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (student.isSingleSchoolAccount()) {
            return student.buildDomSingleSchoolStudent(Realm.of(sc.getUserPrincipal()));
        } else {
            LOG.log(Level.SEVERE, "User {0} tried to access full userdata of user {1}.", new Object[]{submit.getDomGetSingleSchoolStudent().getDomStudent().getId(), shr.getUser()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersSchoolClassList")
    public List<DomSchoolClass> getTeachersSchoolClasses(@Context SecurityContext sc, RestTeacher restTeacher) {
        if (restTeacher == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentHasRole thr = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            PersistentUser teacher = UserManager.findEntity(MySQLPersistenceId.getNativeId(restTeacher.getDomTeacher()));
            thr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(teacher, school, RoleType.TEACHER);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (thr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(thr);
                domSchoolClasses = new ArrayList<>(schoolClasses.size());
                schoolClasses.stream().forEach((s) -> {
                    domSchoolClasses.add(s.buildDomSchoolClass());
                });
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1] for user {2}. ", new Object[]{domSchoolClasses.size(), thr.getPersistentHasRolePK().getUserID(), sc.getUserPrincipal().getName()});
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getStudentsSchoolClassList")
    public List<DomSchoolClass> getStudentsSchoolClasses(@Context SecurityContext sc, RestStudent restStudent) {
        if (restStudent == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentHasRole thr = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.SCHOOLADMIN);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
            PersistentUser student = UserManager.findEntity(MySQLPersistenceId.getNativeId(restStudent.getDomStudent()));
            thr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (thr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentStudentOfClass> tocList = StudentOfClassManager.findEntities(thr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<>(tocList.size());
                for (PersistentStudentOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentStudentOfClassPK().getClassID());
                    domSchoolClasses.add(s.buildDomSchoolClass());
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of student {1} for user {2}. ", new Object[]{domSchoolClasses.size(), thr.getPersistentHasRolePK().getUserID(), phr.getPersistentHasRolePK().getUserID()});
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateSchool(@Context SecurityContext sc, RestSchoolFull rest) throws Dwo2Exception {
       UserState_HR_R_S_SG_U role = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
      PersistentSchool ps = role.getSchool();
      SchoolAdminTeacherState_HR_R_S_SG_U state = role.buildSchoolAdminTeacher();

      DomSchoolFull school = rest.getDomSchoolFull();
      Long id = MySQLPersistenceId.getNativeId(school);
      if( ! id.equals(ps.getSchoolID()))
        throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "Illegal user action");
      if(school.getSchoolRights() != null) {
    	boolean kiosk = ps.hasKiosk();
        ps.setSchoolRights(school.getSchoolRights());
        if (!ps.hasKiosk() && kiosk) {
        	ps.setSchoolRights(ps.getSchoolRights() + "k");
        }
      }
      if (school.getExport() != null)
        ps.setExport(school.getExport());
      try {
        ps = SchoolManager.edit(ps);
// new school
        SchoolCache.putIfPresent(ps);
        HasRoleCache.remove(ps); // school has changed.
      } catch (OptimisticLockException ole) {
    	  LOG.log(Level.SEVERE, "updateSchool", ole);
    	  HasRoleCache.remove(ps);
    	  SchoolCache.remove(ps);
    	  throw new WebApplicationException(409);
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "update school for schooladmin", e);
        throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
      }
      return Boolean.TRUE;
    }
    
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/inviteStudent")
    public Boolean inviteStudent(@Context SecurityContext sc, RestTeacher rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U role = AnonDomainAuthorizer.build().submitUser(sc)
    	.setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
		SchoolAdminTeacherState_HR_R_S_SG_U state = role.buildSchoolAdminTeacher();

		PersistentSchool school = role.getSchool();
    	Long userID = MySQLPersistenceId.getNativeId(rest.getDomTeacher());
    	PersistentUser user;
    	PersistentHasRole hr = HasRoleUtilManager.getHasRole(userID, RoleType.TEACHER, school);
    	user = hr.getUser();
    	HasRoleUtilManager.getOrCreateHasRoleInSchool(user, school, RoleType.STUDENT);
    	return Boolean.TRUE;
    }
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/inviteTeacher")
    public Boolean inviteTeacher(@Context SecurityContext sc, RestStudent rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U role = AnonDomainAuthorizer.build().submitUser(sc)
    	.setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
		SchoolAdminTeacherState_HR_R_S_SG_U state = role.buildSchoolAdminTeacher();

		PersistentSchool school = role.getSchool();
    	Long userID = MySQLPersistenceId.getNativeId(rest.getDomStudent());
    	PersistentUser user;
    	PersistentHasRole hr = HasRoleUtilManager.getHasRole(userID, RoleType.STUDENT, school);
    	user = hr.getUser();
    	if (user.isSingleSchoolAccount()) {
    		if (!Boolean.FALSE.equals(rest.getDomStudent().getSingleSchool()))
    				return Boolean.FALSE; // FIXME..... upgrade user to non-single-school-account first
            PersistentSchool nullSchool = SchoolUtilManager.findBySchoolLogin(DwoSystemParametersUtilManager.findByName("NullSchoolLogin").getValue());
        	HasRoleUtilManager.getOrCreateHasRoleInSchool(user, nullSchool, RoleType.STUDENT);
        	user.setSingleSchoolAccount(Boolean.FALSE);
        	UserManager.edit(user);
    	}
    	HasRoleUtilManager.getOrCreateHasRoleInSchool(user, school, RoleType.TEACHER);
    	return Boolean.TRUE;
    	
    }
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getStudentsInSchool")
    @RolesAllowed({"SCHOOLADMIN"})
    public DomSchoolOrganisation getStudentsInSchool(@Context SecurityContext sc, RestSchoolOrganisation rest) throws Dwo2Exception {
     UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
   	     .setRealm(rest.getRestContext().getRealm())
         .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.SCHOOLADMIN);
 	 DomSchoolOrganisation org = rest.getDomSchoolOrganisation();
     PersistentSchool school = state.getSchool();
//     List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.STUDENT);
//     Collections.sort(userList, new Comparator<PersistentUser>() {
//
//		@Override
//		public int compare(PersistentUser o1, PersistentUser o2) {
//			String a; String b;
//			int result;
//			switch(org.getSort()) {
//			case familyName:
//			default: 
//				a = o1.getLastname(); b = o2.getLastname();
//				break;
//			}
//			result = a.compareToIgnoreCase(b);
//			if (org.getOrder() == OrderType.desc) result = -result;
//			return result;
//		}
//     	}
//     );
     
     RoleType role = org.getRole();
     if (role == null) {
    	 role = RoleType.STUDENT;
     }
	 List<PersistentHasRole> userList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, role);
	 
	 userList = userList.stream().filter(t -> t.getUser() != null).collect(Collectors.toList());
	 
     Collections.sort(userList, new Comparator<PersistentHasRole>() {

		@Override
		public int compare(PersistentHasRole o1, PersistentHasRole o2) {
			String a=""; String b="";
			int result;
			switch (org.getSort()) {
			case lastLogin:
				Date da = o1.getLastLogin(); if (da == null) da = o1.getUser().getLastLogin(); if (da == null) da = new Date(0L);
				Date db = o2.getLastLogin(); if (db == null) db = o2.getUser().getLastLogin(); if (db == null) db = new Date(0L);
				result = da.compareTo(db);
				if (org.getOrder() == OrderType.desc) result = -result;
				return result;
			case familyName:
			default:
				try {
					a = o1.getUser().getLastname(); b = o2.getUser().getLastname();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			result = a.compareToIgnoreCase(b);
			if (org.getOrder() == OrderType.desc) result = -result;
			return result;
		}}); 
     
     if (org.getSkip() != null) {
    	 userList = userList.subList(org.getSkip().intValue(), userList.size());
     } else 
    	 org.setSkip(0L);
     
     if (org.getLimit() != null && org.getLimit().intValue()< userList.size()) {
    	 userList = userList.subList(0, org.getLimit().intValue());
     }
     Stream<PersistentUser> stream = userList.stream().map(PersistentHasRole::getUser);
     String realm = state.getRealm();
     org.setRole(role);
     org.setUsers(stream.map(s -> s.buildDomStudent(realm)).collect(Collectors.toList()));
     stream = userList.stream().map(PersistentHasRole::getUser);
     Set<Long> ids = stream.map(PersistentUser::getId).collect(Collectors.toSet());
     List<PersistentSchoolClass> scl;
     if (org.getSchoolClasses() == null) {
     scl = SchoolClassManager.findEntities(school);
	 org.setSchoolClasses( 
    		 scl
			     .stream()
			     .map(PersistentSchoolClass::buildDomSchoolClass)
			     .collect(Collectors.toList())
	 );
     } else {
    	 scl = org.getSchoolClasses().stream().map(item -> {
			try {
				return new PersistentSchoolClass(MySQLPersistenceId.getNativeId(item));
			} catch (Dwo2Exception e) {
				throw new Dwo2RestException(e);
			}
		}).collect(Collectors.toList());
     }
	 if ( org.getUsersOfClasses() == null) {
	          List<PersistentStudentOfClass> studentOfClassList = scl.stream()
	        		  .flatMap(
	        			item -> 
	        			  StudentOfClassManager.findEntities(item).stream().filter(
	        					  i -> ids.contains(i.getPersistentStudentOfClassPK().getUserID()))
	          ).collect(Collectors.toList());
	        			  
	        			  
	          org.setUsersOfClasses(studentOfClassList.stream().map(PersistentStudentOfClass::buildDomStudentOfClass).collect(Collectors.toList()));
	 }
     org.setHasRoles(userList.stream().map(PersistentHasRole::buildDomHasRole).collect(Collectors.toList()));
	 
	 org.setSkip(org.getSkip() + userList.size());
     return org;
    }
}
