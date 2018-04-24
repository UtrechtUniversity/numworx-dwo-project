package fi.dwo.server.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull4DwoAdmin;

@PermitAll
@Path("/secure/dwoadmin/scoContext")
public class SecuredDwoAdminScoContextManager {
 
	@PUT
    @Path("update")
    @Produces({"application/json"})
    public DomScoContextFull update(@Context SecurityContext sc, RestScoContextFull4DwoAdmin rest) {
    	return null;
    }
}
