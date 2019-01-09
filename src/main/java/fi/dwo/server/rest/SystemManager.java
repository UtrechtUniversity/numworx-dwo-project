/**
 * 
 */
package fi.dwo.server.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestSchool;
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
      return buildDomSchoolFull(school);  
  }

  private DomSchoolFull buildDomSchoolFull(PersistentSchool school) {
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
  @Path("/schoolclasses/getList")
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
    ThreadLocalRandom secureRandom = ThreadLocalRandom.current();
    LOG.log(Level.FINE, "Creating authToken.");
    short authToken = (short) secureRandom.nextInt();
    samlUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
    samlUser.setAuthToken(Short.toString(authToken));
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
    Set<String> names = list.stream().map(PersistentUser::getUsername).collect(Collectors.toSet());
    while ( names.contains(input) && cntr < 100) {
      input = base + (++cntr);      
    }
    return input;
  }
  
}
