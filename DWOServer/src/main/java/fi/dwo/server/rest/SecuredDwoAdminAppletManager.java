package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.AppletManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/dwoadmin/applet")
public class SecuredDwoAdminAppletManager {

  
  @PUT
  @Produces({"application/json"})
  @Path("/getList")
  public List<DomAppletFull> getApplets(RestContext rest, @Context SecurityContext sc) throws Dwo2Exception {
    AnonDomainAuthorizer.build()
    .submitUser(sc)
    .setHasRole(rest.getRestContext().getDomHasRole())
    .buildDwoAdmin();
    return AppletManager.findEntities().stream().map(PersistentApplet::buildDomAppletFull).collect(Collectors.toList());
  }

}
