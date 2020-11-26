package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * StudentModel manager for the student. Basic operations.
 *
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@RolesAllowed("STUDENT")
@Path("/secure/student/studentmodel")
public class SecuredStudentStudentModelManager {

    private static final Logger LOG = Logger.getLogger(SecuredStudentStudentModelManager.class.getName());

    /**
     * Returns the list of student models in the school.
     *
     * @param sc
     * @param context
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomStudentModelContext> getMergedStudentModels(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getMergedStudentModelContextList();        
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestSchoolClass context) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     return StudentModelContextUtilManager.reduce(state.getStudentModelContextList());        
    }

    /**
     * Returns the list of student models in the school.
     *
     * @param sc
     * @param context
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getScore")
    public DomStudentModelDataScore getStudentModelDataScore(@Context SecurityContext sc, RestStudentModelContextId restModelId) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(restModelId.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getStudentModelDataScore(restModelId.getDomStudentModelContext());        
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomStudentModelContext get(@Context SecurityContext sc, RestStudentModelContextId restModelId) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(restModelId.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getStudentModel(restModelId.getDomStudentModelContext());        
    }

    @PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context SecurityContext sc, @Context UriInfo info, RestContext rest) throws Dwo2Exception {
      StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole())
          .buildStudent();
      return state.getLRS(info);
    }

}
