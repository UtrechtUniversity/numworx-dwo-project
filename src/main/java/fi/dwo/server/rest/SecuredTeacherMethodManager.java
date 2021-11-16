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
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@RolesAllowed({"TEACHER"})
@Path("/secure/teacher/method")
public class SecuredTeacherMethodManager {

    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomMethod> getMethods(@Context SecurityContext sc, RestDwoProfile context) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(context.getRestContext().getDomHasRole());
    	PersistentSchool school = hasRole.getSchool();
    	TeacherState_HR_P_R_S_SG_U state = hasRole.buildSchoolAdminTeacher().setTeacher().addProfile(context.getDomDwoProfile());
    	return state.getMethods();
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/add")
    public DomMethod add(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentSchool school = hasRole.getSchool();
    	TeacherState_HR_P_R_S_SG_U state = hasRole.buildSchoolAdminTeacher().setTeacher().addProfile(rest.getDomDwoProfile());
    	return state.addMethod(rest.getDomMethod());
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomMethod update(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
        TeacherState_HR_P_R_S_SG_U state = hasRole.buildSchoolAdminTeacher().setTeacher().addProfile(rest.getDomDwoProfile());
        PersistentSchool school = hasRole.getSchool();
 // cannot change profileid       
    	PersistentMethod p = MethodManager.toValue(rest.getDomMethod(), school, null);
    	PersistentMethod old = MethodManager.findEntity(p.getMethodID());
    	p.setDwoProfileID(old.getDwoProfileID());
    	p = MethodManager.edit(p);
    	return MethodManager.toDom(p);
    }

    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean remove(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentSchool school = hasRole.getSchool();
    	hasRole.buildSchoolAdminTeacher().setTeacher();
    	PersistentMethod p = MethodManager.toValue(rest.getDomMethod(), school, null);
    	MethodManager.destroy(p.getMethodID());
    	return Boolean.TRUE;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomMethod get(@Context SecurityContext sc, RestMethod rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U hasRole = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentSchool school = hasRole.getSchool();
    	hasRole.buildSchoolAdminTeacher().setTeacher().addProfile(rest.getDomDwoProfile());

    	PersistentMethod p = MethodManager.toValue(rest.getDomMethod(), school, null);
    	p = MethodManager.findEntity(p.getMethodID());
    	if (!p.buildPersistenceId().equals(rest.getDomDwoProfile().getId()))
    	  return null; // wrong profile
		long ms = p.getSchoolID().longValue();
		long ss = school.getSchoolID().longValue();
		if (ss == ms || ms == 0L)
			return MethodManager.toDom(p);
		return null;
    }
}
