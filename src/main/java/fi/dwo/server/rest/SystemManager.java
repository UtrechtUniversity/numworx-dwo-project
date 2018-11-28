/**
 * 
 */
package fi.dwo.server.rest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
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
  DomSchoolFull getSchool(RestSchool rest) throws Dwo2Exception {
      Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool());
      PersistentSchool school = SchoolManager.findEntity(id);
      return school.buildDomSchoolFull();
  }

  @PUT
  @Produces({"application/json"})
  @Path("/schoolclasses/getList")
  List<DomSchoolClass> getListSchoolClass(RestSchool rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool());
    PersistentSchool school = SchoolManager.findEntity(id);
    List<PersistentSchoolClass> list = SchoolClassManager.findEntities(school);
    return list.stream().map(PersistentSchoolClass::buildDomSchoolClass).collect(Collectors.toList());
  }
  
  @PUT
  @Produces({"application/json"})
  @Path("/user/requestSamlToken")
  DomSamlUser requestSamlToken(RestSamlUser rest) {
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
  
  
}
