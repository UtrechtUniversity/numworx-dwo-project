package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"SCHOOLADMIN"})
@Path("/secure/schooladmin/method")
public class SecuredSchoolAdminMethodManager {
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomMethod> getMethods(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(context.getRestContext().getDomHasRole());
    	hasRole.buildSchoolAdminTeacher();
    	
    	PersistentSchool school = hasRole.getSchool();
    	List<PersistentMethod> methods = MethodManager.findEntities(school);
    	return methods.stream().map(MethodManager::toDom).collect(Collectors.toList());
    }

}
