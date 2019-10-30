package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.AppletConfigManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/teacher/config")
public class SecuredTeacherConfigManager {
  @PUT
  @Produces({"application/json"})
  @Path("/getList/{language}")
  public List<DomAppletConfig> getConfigurations(@Context SecurityContext sc,
      @PathParam("language") String language, RestContext rest) throws Dwo2Exception {

    AnonDomainAuthorizer.build()
      .submitUser(sc.getUserPrincipal().getName())
      .setHasRole(rest.getRestContext().getDomHasRole())
      .buildSchoolAdminTeacher();
    List<PersistentAppletConfig> config = AppletConfigManager.findEntities();
    List<DomAppletConfig> list = 
    config.stream()
    .filter(t -> t.getLanguage().equals(language))
    .map(PersistentAppletConfig::buildDomAppletConfig)
    .collect(Collectors.toList());
    return list;
  }

}
