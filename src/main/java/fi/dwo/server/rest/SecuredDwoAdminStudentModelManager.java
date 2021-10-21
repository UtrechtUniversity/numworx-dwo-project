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
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextPatch;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@RolesAllowed({"ADMIN"})
@Path("/secure/dwoadmin/studentmodel")
public class SecuredDwoAdminStudentModelManager {

	
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestDwoProfile context) throws Dwo2Exception {
         DwoAdminState_HR_P_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())
                .buildDwoAdmin().addDwoProfile(context.getDomDwoProfile());
    	List<DomStudentModelContext> list = build.getReducedStudentModels();
    	return StudentModelContextUtilManager.reduce(list);
    }

    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomStudentModelContext updateStudentModel(@Context SecurityContext sc, RestStudentModelContext model) throws Dwo2Exception {
        	DwoAdminState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setHasRole(model.getRestContext().getDomHasRole())
                    .buildDwoAdmin();
            return build.updateStudentModel(model.getDomStudentModelContext());
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/patch")
    public DomStudentModelContext patchStudentModel(@Context SecurityContext sc, RestStudentModelContextPatch patch) throws Dwo2Exception {
    	DwoAdminState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(patch.getRestContext().getDomHasRole())
                .buildDwoAdmin();
        return build.patchStudentModel(patch.getDomPatch());
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("get")
    public DomStudentModelContext getStudentModel(@Context SecurityContext sc, RestStudentModelContext rest) throws Dwo2Exception {
    	DwoAdminState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
    			.setHasRole(rest.getRestContext().getDomHasRole())
                .buildDwoAdmin();
    	return build.getStudentModel(rest.getDomStudentModelContext());
    }

	
}
