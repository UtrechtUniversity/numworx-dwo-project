package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.OrderType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.cache.SchoolCache;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/school")
public class SecuredTeacherSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolManager.class.getName());

    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateSchool(@Context SecurityContext sc, RestSchoolFull rest) throws Dwo2Exception {
        UserState_U user = AnonDomainAuthorizer.build().submitUser(sc);
        UserState_HR_R_S_SG_U role = user.setHasRole(rest.getRestContext().getDomHasRole());
        PersistentSchool ps = role.getSchool();
        TeacherState_HR_R_S_SG_U state = role.buildSchoolAdminTeacher().setTeacher();
      DomSchoolFull school = rest.getDomSchoolFull();
      Long id = MySQLPersistenceId.getNativeId(school);
      if( ! id.equals(ps.getSchoolID()))
        throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "Illegal user action");
      if (school.getExport() != null)
        ps.setExport(school.getExport());
      try {
    	SchoolCache.remove(ps);
    	HasRoleCache.remove(ps);
        SchoolManager.edit(ps);
      } catch (Exception e) {
          LOG.log(Level.SEVERE, "update school for teacher", e);
          throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
      }
      return Boolean.TRUE;
    }
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getStudentsInSchool")
    @RolesAllowed({"TEACHER"})
    public DomSchoolOrganisation getStudentsInSchool(@Context SecurityContext sc, RestSchoolOrganisation rest) throws Dwo2Exception {
     UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
   	     .setRealm(rest.getRestContext().getRealm())
         .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER);
 	 DomSchoolOrganisation org = rest.getDomSchoolOrganisation();
     PersistentSchool school = state.getSchool();
     
     RoleType role = org.getRole();
     if (role == null) {
    	 role = RoleType.STUDENT;
     }
	 List<PersistentHasRole> userList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, role);
	 List<? extends DomUser> users;
	 if (org.getUsers() == null)
		 users = state.buildSchoolAdminTeacher().setTeacher().getTeachersStudents();
	 else 
		 users = org.getUsers();
	 
	 Set<Long> uids = users.stream().map(u -> {
		try {
			return MySQLPersistenceId.getNativeId(u);
		} catch (Dwo2Exception e) {
			return null;
		}
	 })
			 .filter( t -> t != null)
			 .collect(Collectors.toSet());
	 
	 
	 userList = userList.stream().filter(t -> t.getUser() != null && uids.contains(t.getUser().getId())).collect(Collectors.toList());
	 
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
     List<DomSchoolClass> scl;
     if (org.getSchoolClasses() == null) {
     scl = state.buildSchoolAdminTeacher().setTeacher().getSchoolClasses();
	 org.setSchoolClasses( 
    		 scl
	 );
     } else {
    	 scl = org.getSchoolClasses();
     }
	 if ( org.getUsersOfClasses() == null) {
	          List<PersistentStudentOfClass> studentOfClassList = scl.stream()
	        		  .map(item -> {
						try {
							return new PersistentSchoolClass(MySQLPersistenceId.getNativeId(item));
						} catch (Dwo2Exception e) {
							return null;
						}
					})
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
