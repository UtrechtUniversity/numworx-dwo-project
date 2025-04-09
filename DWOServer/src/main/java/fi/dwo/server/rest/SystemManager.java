/**
 * 
 */
package fi.dwo.server.rest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.actions.MySQLSchoolAdminTeacherActions;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolDataManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextId;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * @author wim
 *
 */
@Path("/system")
public class SystemManager {
  private static final Logger LOG = Logger.getLogger(SystemManager.class.getName());

  
  /** for use elsewhere 
   * 
   * @param targetStringLength length
   * @return randomstring
   */
  public static String randomAlphanumericString(int targetStringLength) {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    ThreadLocalRandom random = ThreadLocalRandom.current();
	 
	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
	    return(generatedString);
	}
  
  
  
  
  
  @PUT
  @Produces({"application/json"})
  @Path("/school/get")
  public DomSchoolFull getSchool(RestSchool rest) throws Dwo2Exception {
      DomSchool s = rest.getDomSchool();
      Long id = MySQLPersistenceId.getNativeId(s);
      PersistentSchool school = SchoolManager.findEntity(id);
      return buildDomSchoolFull(school);
  }
  
  @PUT
  @Produces({"application/json"})
  @Path("/school/getByName")
  public DomSchoolFull getSchoolByName(RestSchool rest) throws Dwo2Exception {
      String name  = rest.getDomSchool().getSchoolName();
      PersistentSchool school = SchoolManager.findBySchoolLogin(name);
      if (school == null) {
    	  List<? extends Number> result = SchoolDataManager.findByBRIN(name);
    	  if (result.size() == 1) {
    		  school = SchoolManager.findEntity(result.get(0).longValue());
    	  } else
    		  return null;
      }
      
      return buildDomSchoolFull(school);  
  }

  private DomSchoolFull buildDomSchoolFull(PersistentSchool school) {
	if (school == null) return null;
    DomSchoolFull dom = school.buildDomSchoolFull();
    List<PersistentSchoolGroup> list = SchoolGroupManager.findEntities(school);
    List<DomMapEntry<RoleType, String>> passwords = 
        list.stream()
          .map(item -> new DomMapEntry<RoleType, String>(RoleType.values()[item.getGroupID()], item.getPasswd()))
          .collect(Collectors.toList());
    dom.setPasswords(passwords);
    return dom;
  }

  @PUT
  @Produces({"application/json"})
  @Path("/schoolclass/getList")
  public List<DomSchoolClass> getListSchoolClass(RestSchool rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool());
    PersistentSchool school = SchoolManager.findEntity(id);
    if(school == null) return Collections.emptyList();
    List<PersistentSchoolClass> list = SchoolClassManager.findEntities(school);
    return list.stream().map(PersistentSchoolClass::buildDomSchoolClass).collect(Collectors.toList());
  }
  
  @PUT
  @Produces({"application/json"})
  @Path("/user/requestSamlToken")
  public DomSamlUser requestSamlToken(RestSamlUser rest) {
    DomSamlUser u = rest.getDomSamlUser();
    PersistentSamlUser samlUser = SamlUserManager.findEntity(u.getSamlUserId(), u.getSamlOrgId());
    if(samlUser == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "not found");
    }
    LOG.log(Level.FINE, "Creating authToken.");
    String authToken = randomAlphanumericString(16);
    samlUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
    samlUser.setAuthToken(authToken);
    try {
        SamlUserManager.edit(samlUser);
    } catch (Exception e) {
        LOG.log(Level.SEVERE, null, e);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error. Can't update samluser with id:" + samlUser.getId() + ".");
    }
    u.setAuthToken(samlUser.getAuthToken());
    return u;
  }
  
  @PUT
  @Produces({"text/plain", "application/json"})
  @Path("/user/suggestion")
  public String suggestion(String input) throws ParseException {
    if(input.startsWith("\"")) {
      JSONParser p = new JSONParser();
      input = p.parse(input).toString();
    }
    int cntr = 0;
    List<PersistentUser> list = UserManager.findUsersLike(input);
    if (list.isEmpty())
      return input;
    String base = input;
    Set<String> names = list.stream()
    		.map(PersistentUser::getUsername)
    		.map(String::toLowerCase)
    		.collect(Collectors.toSet());
    while ( names.contains(input.toLowerCase()) && cntr < 100) {
      input = base + (++cntr);      
    }
    return input;
  }
  @PUT
  @Path("/school/submit")
  public Boolean submitSchool(RestSchoolFull rest) {
	  DomSchoolFull school = rest.getDomSchoolFull();
      // allowed user role
      PersistentSchool s = new PersistentSchool();
      s.setExpire(school.getExpire());
      s.setExport(school.getExport());
      s.setImage(school.getImage());
      s.setSchoolLogin(school.getSchoolLogin());
      s.setSchoolName(school.getSchoolName());
      String schoolRights = school.getSchoolRights();
      if (schoolRights == null) schoolRights = "_"; // geen null
      s.setSchoolRights(schoolRights);
      s.setAboType(school.getAboType());
      try {
          SchoolManager.create(s);
          s = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
          LOG.log(Level.INFO, "created school with schoollogin {1} and id {2}.", new Object[]{null, s.getSchoolLogin(), s.getSchoolID()});
          //add user roles
      } catch (RollbackException e) {
          LOG.log(Level.INFO, "A Rollback exception occured while creating school with schoollogin "+ s.getSchoolLogin());
          s = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
          //if (s == null)
          	throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, e.getMessage());
      } catch (PersistenceException e) {
          //non-fatal for semi-idempotent operation
          LOG.log(Level.INFO, "A Persistence exception occured while creating school with schoollogin {0}.", s.getSchoolLogin());
          throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school " + school.getSchoolLogin() + ".");
      }
      for (DomMapEntry<RoleType, String> entry : school.getPasswords()) {
          PersistentSchoolGroup newSg = new PersistentSchoolGroup();
          newSg.setSchoolID(s.getSchoolID().intValue());
          newSg.setGroupID(entry.getKey().ordinal());
          newSg.setPasswd(entry.getValue());
          try {
              SchoolGroupManager.create(newSg);
          } catch (PersistenceException e) {
              //non-fatal for idempotent operation
              String msg = MessageFormat.format("A Persistence exception occured while creating schoolgroup for school "
                      + "with logincode {0} and RoleType {1} (with groupid {2}).",
                      new Object[]{s.getSchoolLogin(), entry.getKey().name(), newSg.getGroupID()});
              LOG.log(Level.INFO, msg);
              throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
          }
      }
      return Boolean.TRUE;
  }
  
  @PUT
  @Path("school/getTeachersInSchoolList")
  public List<DomTeacher> getTeachersInSchoolList(RestSchool rest) throws Dwo2Exception {
	  Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool());
	  PersistentSchool school = SchoolManager.findEntity(id);
	  if (school == null) return null;
	  DomContext context = rest.getRestContext();
	  final String realm = context != null ? context.getRealm(): null;
      List<PersistentUser> userList = UserUtilManager.getUsersInRoleInSchool(school, RoleType.TEACHER);
      return userList.stream().map(t -> t.buildDomTeacher(realm)).collect(Collectors.toList());
  }
  
  @PUT
  @Path("schoolclass/submitStudent")
  public Boolean submitStudentToSchoolClass(RestSubmitStudentToSchoolClass rest) throws Dwo2Exception {
	  Long uid = MySQLPersistenceId.getNativeId(rest.getDomSubmitStudentToSchoolClass().getStudent());
	  Long cid = MySQLPersistenceId.getNativeId(rest.getDomSubmitStudentToSchoolClass().getSchoolClassTo());
	  PersistentSchoolClass schoolclass = SchoolClassManager.findEntity(cid);
	  PersistentUser student = UserManager.findEntity(uid);
	  PersistentSchool school = SchoolManager.findEntity(schoolclass.getSchoolID());
      PersistentHasRole shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, school, RoleType.STUDENT);
      return SchoolClassUtilManager.registerStudentForSchoolClass(shr, schoolclass);
  }
  
  @PUT
  @Path("scoContext/getSchool")
  public DomSchoolId getSchool(RestScoContextId id) throws Dwo2Exception {
	  Long sid = MySQLPersistenceId.getNativeId(id.getDomScoContext());
	  PersistentScoContext sco = ScoContextManager.findEntity(sid);
	  sid = sco.getSchoolID();
	  if (sid == null) return null;
	  DomSchoolId school = new DomSchoolId();
	  school.setId(PersistentSchool.buildPersistenceId(sid));
	  return school;
  }
  
  @PUT
  @Path("course/getSchool")
  public DomSchoolId getSchool(RestCourse id) throws Dwo2Exception {
	  Long sid = MySQLPersistenceId.getNativeId(id.getDomCourse());
	  PersistentCourse sco = CourseManager.findEntity(sid);
	  sid = sco.getSchoolID();
	  if (sid == null) return null;
	  DomSchoolId school = new DomSchoolId();
	  school.setId(PersistentSchool.buildPersistenceId(sid));
	  return school;
  }

}
