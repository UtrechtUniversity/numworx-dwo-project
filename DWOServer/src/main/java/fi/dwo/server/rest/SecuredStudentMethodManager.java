package fi.dwo.server.rest;

import java.util.List;

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
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.StudentState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"STUDENT"})
@Path("/secure/student/method")
public class SecuredStudentMethodManager {

	@PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomMethod get(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentSchool school = hasRole.getSchool();
    	DomDwoProfileId domDwoProfile = rest.getDomDwoProfile();
        StudentState_HR_R_S_SG_U state = hasRole.buildStudent().setDwoProfile(domDwoProfile);
 // state.getMethod(domMethod);
    	PersistentDwoProfile profile = null;
    	if (domDwoProfile != null)
    	{
    		profile = state.getContext().getStudentCtx().dwoProfile;
    	}
 
    	PersistentMethod p = MethodManager.toValue(rest.getDomMethod(), school, profile);
		p = MethodManager.findEntity(p.getMethodID());
		if (p == null) {
			if (rest.getDomMethod().getId().getIdString().startsWith("PROXY;")) {
				String key = ";"+DomMethod.key(rest.getDomMethod().getId());
				List<PersistentMethod> list = MethodManager.findEntities();
				p = list.stream().filter(t  -> t.getMethodID().endsWith(key)).findAny().orElseGet(null);
			}
		}
		long ms = p.getSchoolID().longValue();
		long ss = school.getSchoolID().longValue();
		if (ss == ms || ms == 0L)
			return MethodManager.toDom(p);
		return null;
    }

}
