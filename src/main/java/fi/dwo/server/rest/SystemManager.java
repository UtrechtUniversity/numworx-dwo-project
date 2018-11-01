/**
 * 
 */
package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * @author wim
 *
 */
@Path("/system")
public class SystemManager {
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
  
  
}
