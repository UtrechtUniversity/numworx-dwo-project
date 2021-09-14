package fi.dwo.server.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"ADMIN"})
@Path("/secure/dwoadmin/studentmodel")
public class SecuredDwoAdminStudentModelManager {

	
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
        DwoAdminState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())
                .buildDwoAdmin();
    	List<DomStudentModelContext> list = build.getReducedStudentModels();
    	return StudentModelContextUtilManager.reduce(list);
    }

	
}
