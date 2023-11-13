package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"ADMIN"})
@Path("/secure/dwoadmin/method")
public class SecuredDwoAdminMethodManager {
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomMethod> getMethods(@Context SecurityContext sc, RestDwoProfile context) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(context.getRestContext().getDomHasRole());
    	DwoAdminState_HR_P_R_S_SG_U state = hasRole.buildDwoAdmin().addDwoProfile(context.getDomDwoProfile());
    	return state.getMethods();
    }
    @PUT
    @Produces({"application/json"})
    @Path("/addProfile")
    public Boolean addProfile(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	DwoAdminState_HR_P_R_S_SG_U state = hasRole.buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());
    	return state.addProfile(rest.getDomMethod());
    }
    @PUT
    @Produces({"application/json"})
    @Path("/removeProfile")
    public Boolean removedProfile(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	DwoAdminState_HR_P_R_S_SG_U state = hasRole.buildDwoAdmin().addDwoProfile(rest.getDomDwoProfile());
    	return state.removeProfile(rest.getDomMethod());
    }

}
