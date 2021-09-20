package fi.dwo.server.rest;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethodPK;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolMethodManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolMethod;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext4Student;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextPatch;
import nl.uu.fi.dwo.rest.entities.RestStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * StudentModel manager for the teacher. Basic operations.
 *
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@RolesAllowed({"TEACHER"})
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
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
    public List<DomStudentModelContext> getReducedStudentModels(@Context SecurityContext sc, RestContext context) throws Dwo2Exception {
        TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                .setHasRole(context.getRestContext().getDomHasRole())
                .buildSchoolAdminTeacher()
                .setTeacher();
    	List<DomStudentModelContext> list = build.getReducedStudentModels();
    	return StudentModelContextUtilManager.reduce(list);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("get")
    public DomStudentModelContext getStudentModel(@Context SecurityContext sc, RestStudentModelContext rest) throws Dwo2Exception {
    	TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
    			.setHasRole(rest.getRestContext().getDomHasRole())
    			.buildSchoolAdminTeacher()
    			.setTeacher();
    	return build.getStudentModel(rest.getDomStudentModelContext());
    }
    
    
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("getReduced")
    public DomStudentModelContext getReduced(@Context SecurityContext sc, RestStudentModelContext rest) throws Dwo2Exception {
    	return SecuredStudentStudentModelManager.reduce(getStudentModel(sc, rest));
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
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
        TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
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
    public DomStudentModelScorePerTeacher getScores(@Context SecurityContext sc, @Context UriInfo info, RestStudentModelScorePerTeacher rest) {
    	try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().submitUser(sc)
                    .setHasRole(rest.getRestContext().getDomHasRole())
                    .buildSchoolAdminTeacher()
                    .setTeacher();
            DomStudentModelScorePerTeacher scores = build.getScores(rest.getDomStudentModelScorePerTeacher(), info);
            scores.getStudentModelContexts().forEach(t -> SecuredStudentStudentModelManager.reduce(t.getValue()));
            return scores;
    	} catch (Dwo2Exception e) {
    		throw new Dwo2RestException(e);
    	}
    	
    	
    }
    
    @PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context SecurityContext sc, @Context UriInfo info, RestContext rest) throws Dwo2Exception {
       TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole())
          .buildSchoolAdminTeacher().setTeacher();
      return state.getLRS(info);
    }

    
    @PUT
    @Produces("application/json")
    @Path("/updateForClass")
    public Boolean updateForClass(@Context SecurityContext sc, RestStudentModelContext4Student rest) throws Dwo2Exception {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
		UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
		TeacherState_HR_R_S_SG_U state = hrstate.buildSchoolAdminTeacher().setTeacher();
    	DomStudentModelContext4Student dom = rest.getDomStudentModelContext();
    	DomSchoolClassId schoolClass = dom.getSchoolClass();
    	// verify schoolclass belongs to teacher
    	PersistentUser teacher = hrstate.getUser();
    	PersistentSchool school = hrstate.getSchool();
    	PersistentSchoolClass psc = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(schoolClass));
    	PersistentStudentModelContext model = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(dom));
    	if (school.getSchoolID().longValue() != psc.getSchoolID().longValue())
    		throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong school");
    	PersistentTeacherOfClass ptoc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(teacher.getId(), psc.getClassID(), hrstate.getSchoolGroup().getSchoolGroupID()));
    	if (ptoc == null) {
    		throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "not a member");
    	}
    	PersistentStudentModelOfClassPK pk = new PersistentStudentModelOfClassPK(psc.getClassID(), model.getModelID(), school.getSchoolID());
    	
    	Map<String, Map<String, Set<Integer>>> filter = dom.getFilter();
    	String value = JSONObject.toJSONString(filter);
    	
    	PersistentStudentModelOfClass entity = StudentModelOfClassManager.findEntity(pk);
    	if (entity == null && filter != null) {
    		entity = new PersistentStudentModelOfClass();
    		entity.setId(pk);
    		entity.setValue(value);
    		StudentModelOfClassManager.create(entity);
    		return Boolean.TRUE;
    	} else if (filter != null) {
    		entity.setValue(value);
    		StudentModelOfClassManager.edit(entity);
    	} else {
    		StudentModelOfClassManager.destroy(entity.getId());
    	}
    	return Boolean.FALSE;
    }

    
	private String key(Map<String, Map<String, Set<Integer>>> filter) {
		return filter.keySet().stream().filter(key -> key != null && !key.isEmpty()).findAny().orElse(null);
	}

    @PUT
    @Produces("application/json")
    @Path("/getForClass")
    public DomStudentModelContext4Student getForClass(@Context SecurityContext sc, RestStudentModelContextId rest) throws Dwo2Exception, ParseException {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
		UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
		TeacherState_HR_R_S_SG_U state = hrstate.buildSchoolAdminTeacher().setTeacher();
		DomStudentModelContextId dom = rest.getDomStudentModelContext();
    	DomSchoolClassId schoolClass = rest.getDomSchoolClass();
    	// verify schoolclass belongs to teacher
    	PersistentUser teacher = hrstate.getUser();
    	PersistentSchool school = hrstate.getSchool();
    	PersistentSchoolClass psc = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(schoolClass));
    	PersistentStudentModelContext model = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(dom));
    	if (school.getSchoolID().longValue() != psc.getSchoolID().longValue())
    		throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "wrong school");
    	PersistentTeacherOfClass ptoc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(teacher.getId(), psc.getClassID(), hrstate.getSchoolGroup().getSchoolGroupID()));
    	if (ptoc == null) {
    		throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "not a member");
    	}
    	PersistentStudentModelOfClassPK pk = new PersistentStudentModelOfClassPK(psc.getClassID(), model.getModelID(), school.getSchoolID());
    	PersistentStudentModelOfClass of = StudentModelOfClassManager.findEntity(pk);
    	if(of == null) return null;
    	DomStudentModelContext context = state.getStudentModel(dom);
    	DomStudentModelContext4Student result = new DomStudentModelContext4Student(context.getId());
    	result.setFilter(toFilter(of));
    	result.setSchoolClass(schoolClass);
		SecuredStudentStudentModelManager.reduce(context.getModelStructure());
    	result.setModelStructure(context.getModelStructure());

    	String filterKey = key(result.getFilter());
		String methodKey = DomMethod.key(result.getModelStructure().getActiveMethod());
		if (filterKey != null && !Objects.equals(filterKey, methodKey)) {
			try {
				List<PersistentMethod> ms = MethodManager.findEntities(school);
				for(PersistentMethod m: ms) {
					String mKey = DomMethod.key(m.buildPersistenceId());
					if (Objects.equals(mKey, filterKey)) {
						result.getModelStructure().setActiveMethod(m.buildPersistenceId());
						break;
					}
				}
			} catch(Exception oops) {
				LOG.log(Level.WARNING, "extract filter method");
			}
		}
    	return result;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public
	static Map<String,Map<String,Set<Integer>>> toFilter(PersistentStudentModelOfClass of) throws ParseException {
		HashMap<String,Map<String,Set<Integer>>> result = new HashMap<>();
		Map<String,?> map = (Map) new JSONParser().parse(of.getValue());
		Set<String> keys = map.keySet();
		for (String k : keys) {
			Map<String, ?> m = (Map) map.get(k);
			Map r = result.computeIfAbsent(k, q -> new HashMap<>());
			for (String l: m.keySet()) {
				Collection<Number> set = (Collection<Number>) m.get(l);
				r.put(l, set.stream().map(Number::intValue).collect(Collectors.toSet()));
			}
		}
		return result;
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
			TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
			          .setHasRole(hr)
			          .buildSchoolAdminTeacher().setTeacher();
			DomStudentModelContext result = state.getStudentModel(smc);
			DomStudentModelStructure struct = result.getModelStructure();
			String obj = SecuredStudentStudentModelManager.getStruct(struct, uuid, locale);
			
			return Response.ok(obj, MediaType.APPLICATION_JSON_TYPE).build();
		} catch (Dwo2Exception e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

    @PUT
    @Produces("application/json")
    @Path("/updateMethod")
    public DomSchoolMethod updateActiveMethod(@Context SecurityContext sc, RestSchoolMethod rest) throws Dwo2Exception {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
		UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
		TeacherState_HR_R_S_SG_U state = hrstate.buildSchoolAdminTeacher().setTeacher();
    	DomSchoolMethod dom = rest.getDomSchoolMethod();
		Long smID = MySQLPersistenceId.getNativeId(dom);
		Long optLock = dom.getOptLock();
    	PersistentStudentModelContext context = StudentModelContextManager.findEntity(smID);
    	PersistentSchool school = hrstate.getSchool();
    	
    	PersistentSchoolMethodPK pk = new PersistentSchoolMethodPK(school.getSchoolID(), context.getModelID());
    	PersistentSchoolMethod   sm = SchoolMethodManager.findEntity(pk);
    	if (sm == null) {
    		sm = new PersistentSchoolMethod(pk);
    		sm.setMethodID(dom.getActiveMethod());
    		SchoolMethodManager.create(sm);
    	} else {
    		sm.setMethodID(dom.getActiveMethod());
    		sm.setOptlock(optLock);
    		sm = SchoolMethodManager.edit(sm);   	
    	}
    	return sm.buildDomSchoolMethod();
    }
    @PUT
    @Produces("application/json")
    @Path("/getMethod")
    public DomSchoolMethod updateActiveMethod(@Context SecurityContext sc, RestStudentModelContextId rest) throws Dwo2Exception {
    	UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
		UserState_HR_R_S_SG_U hrstate = ustate.setHasRole(rest.getRestContext().getDomHasRole());
		TeacherState_HR_R_S_SG_U state = hrstate.buildSchoolAdminTeacher().setTeacher();
    	DomStudentModelContextId dom = rest.getDomStudentModelContext();
		Long smID = MySQLPersistenceId.getNativeId(dom);
    	PersistentStudentModelContext context = StudentModelContextManager.findEntity(smID);
    	if (context == null) return null;
    	Long schoolID = context.getSchoolID();
    	PersistentSchool school = hrstate.getSchool();
    	if (schoolID.longValue() != 0L && !schoolID.equals(school.getSchoolID()))
    		return null; // illegal....
    	
    	PersistentSchoolMethodPK pk = new PersistentSchoolMethodPK(school.getSchoolID(), context.getModelID());
    	PersistentSchoolMethod   sm = SchoolMethodManager.findEntity(pk);
    	if (sm != null) {
    		return sm.buildDomSchoolMethod();
    	} else {
    		DomSchoolMethod dsm = new DomSchoolMethod(context.buildPersistenceId());
    		dsm.setActiveMethod(context.getModelStructure().getActiveMethod());
    		dsm.setOptLock(null); // fake, geen optlock
    		return dsm;
    	}
    }
}
