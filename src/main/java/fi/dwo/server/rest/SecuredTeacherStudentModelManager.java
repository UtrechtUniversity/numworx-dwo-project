package fi.dwo.server.rest;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextPatch;
import nl.uu.fi.dwo.rest.entities.RestStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 * StudentModel manager for the teacher. Basic operations.
 *
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/studentmodel")
public class SecuredTeacherStudentModelManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherStudentModelManager.class.getName());

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
    public List<DomStudentModelContext> getMergedStudentModels(@Context SecurityContext sc, RestContext context) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(context.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.getMergedStudentModels();
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }

    List<DomStudentModelContext> getStudentModels(@Context SecurityContext sc, RestContext context) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(context.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.getStudentModels();
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }
   
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestContext context) {
    	List<DomStudentModelContext> list = getStudentModels(sc, context);
    	return StudentModelContextUtilManager.reduce(list);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("get")
    public DomStudentModelContext getStudentModel(@Context SecurityContext sc, RestStudentModelContext rest) throws Dwo2Exception {
    	TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
    			.setHasRole(rest.getRestContext().getDomHasRole())
    			.buildSchoolAdminTeacher()
    			.setTeacher();
    	return build.getStudentModel(rest.getDomStudentModelContext());
    }
    
    
    
    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param mode The created model.
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/add")
    public DomStudentModelContext addStudentModel(@Context SecurityContext sc, RestStudentModelContext model) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(model.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.addStudentModel(model.getDomStudentModelContext());
            
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomStudentModelContext updateStudentModel(@Context SecurityContext sc, RestStudentModelContext model) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(model.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.updateStudentModel(model.getDomStudentModelContext());
            
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/patch")
    public DomStudentModelContext patchStudentModel(@Context SecurityContext sc, RestStudentModelContextPatch patch) throws Dwo2Exception {
        TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                .setHasRole(patch.getRestContext().getDomHasRole())
                .buildSchoolAdminTeacher()
                .setTeacher();
        return build.patchStudentModel(patch.getDomPatch());
    }

    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeStudentModel(@Context SecurityContext sc, RestStudentModelContext model) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(model.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.removeStudentModel(model.getDomStudentModelContext());
            
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("getScores")
    public DomStudentModelScorePerTeacher getScores(@Context SecurityContext sc, RestStudentModelScorePerTeacher rest) {
    	try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            return build.getScores(rest.getDomStudentModelScorePerTeacher());
    	} catch (Dwo2Exception e) {
    		throw new Dwo2RestException(e);
    	}
    	
    	
    }
    
    @PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context SecurityContext sc, @Context UriInfo info, RestContext rest) throws Dwo2Exception {
       TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole())
          .buildSchoolAdminTeacher().setTeacher();
      return state.getLRS(info);
    }

}
