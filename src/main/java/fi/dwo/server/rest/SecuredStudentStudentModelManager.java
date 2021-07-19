package fi.dwo.server.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getMergedStudentModelContextList();        
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getReducedList")
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestSchoolClass context) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     return StudentModelContextUtilManager.reduce(state.getStudentModelContextList());        
    }
    @PUT
    @Produces({"application/json"})
    @Path("/getReducedListForClass")
    public List<DomStudentModelContext4Student> getReducedStudentModelsforClass(@Context SecurityContext sc, RestSchoolClass context) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())//
                .buildStudent();
     if (context.getDomSchoolClass() == null) return Collections.emptyList();
     state = state.setSchoolClass(context.getDomSchoolClass());
     return StudentModelContextUtilManager.reduce4s(state.getStudentModelContextListForClass());        
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
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(restModelId.getRestContext().getDomHasRole())//
                .buildStudent();
     return state.getStudentModelDataScore(restModelId.getDomStudentModelContext());        
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomStudentModelContext get(@Context SecurityContext sc, RestStudentModelContextId restModelId) throws Dwo2Exception {
    StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(restModelId.getRestContext().getDomHasRole())//
                .buildStudent();
     return reduce(state.getStudentModel(restModelId.getDomStudentModelContext()));        
    }

    static DomStudentModelContext reduce(DomStudentModelContext studentModel) {
		reduce(studentModel.getModelStructure());
		return studentModel;
	}

    final static private Map<String, String> EMPTY = Collections.emptyMap();
	static void reduce(DomStudentModelStructure modelStructure) {
		modelStructure.getInfo().setDescription(EMPTY);
		List<DomStudentModelCategory> list = modelStructure.getCategories();
		list.forEach(SecuredStudentStudentModelManager::reduce);
	}
	private static void reduce(DomStudentModelCategory cat) {
		cat.getInfo().setDescription(EMPTY);
		List<DomStudentModelObj> list = cat.getObjectives();
		list.forEach(SecuredStudentStudentModelManager::reduce);
	}
	private static void reduce(DomStudentModelObj obj) {
		obj.getInfo().setDescription(EMPTY);
		List<DomStudentModelObj> list = obj.getObjectives();
		if (list != null) list.forEach(SecuredStudentStudentModelManager::reduce);
	}
	

	@PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context SecurityContext sc, @Context UriInfo info, RestContext rest) throws Dwo2Exception {
      StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole())
          .buildStudent();
      return state.getLRS(info);
    }

	@GET
	@Produces ("application/json") 
	@Path ("/getDescription")
	public Response getDescription(@Context SecurityContext sc, 
			@QueryParam("id") String uuid, @QueryParam("modelId") String modelid,
			@QueryParam("hasRoleId") String sgid, @QueryParam("locale") String locale
		) {
		DomHasRole hr = new DomHasRole();
		hr.setId(new PersistenceId(sgid));
		DomStudentModelContextId smc = new DomStudentModelContextId(new PersistenceId(modelid));
	      try {
			StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
			          .setHasRole(hr)
			          .buildStudent();
			DomStudentModelContext result = state.getStudentModel(smc);
			DomStudentModelStructure struct = result.getModelStructure();
			String obj = getStruct(struct, uuid, locale);
			
			return Response.ok(obj, MediaType.APPLICATION_JSON_TYPE).build();
		} catch (Dwo2Exception e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	static String getStruct(DomStudentModelStructure struct, String uuid, String locale) throws Dwo2Exception {
		if (uuid.equals(struct.getInfo().getId())) {
			return description(struct.getInfo(), locale);
		}
		for( DomStudentModelCategory cat: struct.getCategories()) {
			if (uuid.equals(cat.getInfo().getId())) {
				return description(cat.getInfo(), locale);
			}
			for (DomStudentModelObj obj : cat.getObjectives()) {
				Optional<String> result = getObj(obj, uuid, locale); 
				if (result.isPresent()) return result.get();
			}
		}
		throw new Dwo2Exception(Dwo2ExceptionCode.Rest_FormatError, "not found");
	}

	private static Optional<String> getObj(DomStudentModelObj obj, String uuid, String locale) {
		if (uuid.equals(obj.getInfo().getId()))
			return Optional.ofNullable(description(obj.getInfo(), locale));
		List<DomStudentModelObj> list = obj.getObjectives();
		if(list != null) for (DomStudentModelObj o: list) {
			Optional<String> result = getObj(o, uuid, locale);
			if (result.isPresent()) return result;
 		}
		return Optional.empty();
	}

	private static String description(DomStudentModelContextInfo info, String locale) {
		String json = info.getDescription().get(locale + "@JSON");
		if (json == null || json.isEmpty())
			return info.getDescription().getOrDefault(locale, "");
		return json;
	}
	
	
	
}
