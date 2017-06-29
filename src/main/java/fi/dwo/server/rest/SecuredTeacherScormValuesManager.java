package fi.dwo.server.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;

@PermitAll
@Path("/secure/teacher/scormValues")
public class SecuredTeacherScormValuesManager {

    @PUT
    @Produces({"application/json"})
    @Path("/get")
    DomTeacherScormValues get(@Context SecurityContext sc, RestTeacherScormValues rest) {
    	return rest.getDomTeacherScormValues();
    }

    @PUT
    @Produces({"application/json"})
    @Path("/set")
    Boolean set(@Context SecurityContext sc, RestTeacherScormValues rest) {
    	return Boolean.FALSE;
    }

	
}
