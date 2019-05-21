package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.omg.CORBA.UnknownUserException;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * StudentModel manager for the teacher. Basic operations.
 *
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
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
    public List<DomStudentModelContext> getStudentModels(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getStudentModelContextList();        
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
    
//
//    /**
//     * Returns the school data to be displayed.
//     *
//     * @param sc
//     * @param mode The created model.
//     * @return
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/add")
//    public DomStudentModelData updateStudentModel(@Context SecurityContext sc, RestStudentModelData data) {
//        try {
//            StudentDomainAuthorizer.StudentState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
//                    .setHasRole(data.getRestContext().getDomHasRole())
//                    
//                    //.setDefaultHasRole()
//                    
//            return build.addStudentModel(data.getDomStudentModelContext());
//            
//        } catch (Dwo2Exception e) {
//            throw new Dwo2RestException(e);
//        }
//    }    

    @PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context SecurityContext sc, @Context UriInfo info, RestContext rest) throws Dwo2Exception {
      StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole())
          .buildStudent();
      PersistentUser user = state.getContext().getUserCtx().user;
      Agent agent = new Agent();
      agent.name = user.getUsername();
      agent.account = new Account();
      agent.account.homePage = info.getBaseUri().toASCIIString();
      agent.account.name = "pid:" + user.buildPersistenceId().getIdString();    
      DomLRS lrs = new DomLRS();
      lrs.setAgent(agent);
      String endpoint = System.getProperty("XAPI_ENDPOINT", "/data/xAPI/");
      lrs.setEndpoint(endpoint);
      PersistentSchool school = state.getContext().getUserCtx().school;
      if (school.getAboType() != AboType.premium) return null;
      // school afhankelijk?
      String auth = System.getProperty("XAPI_AUTH", "MzdjZTEwMzE2NzQxN2NhODlmNDNkODA1ZDJhNGY3YjU1MzM3MzE3YjpjY2QzODMwYjc1NWJkY2E3ZDJlYzQ5NmQ0ZTkyZWQwYzJlNDljYjRh");
      lrs.setAuth("Basic " + auth);

      return lrs;
    }

}
