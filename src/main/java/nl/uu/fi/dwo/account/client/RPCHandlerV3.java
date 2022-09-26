package nl.uu.fi.dwo.account.client;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.fusesource.restygwt.client.Defaults;
import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.CourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.CoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicCoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicUserResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.ScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentCoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.StudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.UserResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.util.Base64;

@SuppressWarnings("deprecation")
public class RPCHandlerV3 extends RPCHandlerV2 {

	ScoContextManager scoManager;	
	CourseManager courseManager;
	PublicProfileManager profileManager;
	CoursesOfSchoolClassManager studentManager;
	UserResultsManager resultManager;
	StudentScoDataManager scormApi;
	SecuredStudentStudentModelManager studentModelManager;
	AccessManager accessManager;
	OAuthManager oauthManager;
	
	protected Promise<DomDwoProfileFull> profile;
	
	
	public RPCHandlerV3(String dummy, int profile, boolean secure) {
		super(profile);
		this.secure = secure;
		scoManager = new PublicScoContextManager();
		courseManager = new PublicCourseManager();
		profileManager = new PublicProfileManager();
		studentManager = new PublicCoursesOfSchoolClassManager();
		resultManager = new PublicUserResultsManager();
		scormApi = new PublicStudentScoDataManager();
		accessManager = new AccessManager();
		oauthManager = new OAuthManager();
		this.profile = getDwoProfile(profile);
	}

// What if fails?
	private Promise<DomDwoProfileFull> getDwoProfile(final int id) {
		return profileManager.get(id)
//				.recover(new Function<Promise<?>, DomDwoProfileFull>() {
//
//			@Override
//			public DomDwoProfileFull apply(Promise<?> t) {
//				DomDwoProfileFull recover = new DomDwoProfileFull();
//				recover.setDwoProfileDescription("");
//				recover.setDwoProfileName("");
//				recover.setDwoProfileRights("rl");
//				recover.setDwoProfileText("");
//				recover.setId(idOf(id, PersistenceClassType.PersistentDwoProfile));
//				return recover;
//			}
//		})
		;
	}

	public Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID) {
		final DomCourse course = toCourse(courseID);
		return profile.then(new Success<DomDwoProfile, DomResultsPerStudentCourse>() {

			@Override
			public Promise<DomResultsPerStudentCourse> call(Promise<DomDwoProfile> resolved) throws Exception {
				return resultManager.getCourseResults(getContext(), course, resolved.getValue());
			}
		});
	}

	public Promise<List<DomCourseStudent>> getCourses() {
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return courseManager.getCourses(p,context).then(accessManager);
			}
		});
	}

	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return courseManager.getCoursesSchool(p,context).then(accessManager);
			}
		});
	}

	public Promise<DomCoursesOfSchoolClass> getCoursesClass(
			final DomSchoolClass schoolclass) {
		return profile.then(new Success<DomDwoProfile, DomCoursesOfSchoolClass>() {

			@Override
			public Promise<DomCoursesOfSchoolClass> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return studentManager.getCoursesClass(getContext(), schoolclass, p);
			}
		});
	}

	public Promise<DomCoursesOfSchoolClass> getCourseClass(Object id, DomSchoolClass schoolclass) {
		DomCourse course = toCourse(id);
		return profile.then( p -> 
			studentManager.getCourseClass(getContext(), schoolclass, course, p.getValue())
		);
	}
	
	public Promise<DomCoursesOfSchoolClass> getClassCourse(Object id) {
	  DomClassCourse cc = toClassCourse(id);
	  return profile.then(p ->
	    studentManager.getClassCourse(getContext(), cc, p.getValue())
	  );
	}

	public Promise<DomCoursesOfSchoolClass> getScoContextClass(Object id, DomSchoolClass schoolclass) {
		DomScoContext sco = toScoContext(id);
		return profile.then( p -> 
			studentManager.getScoContextClass(getContext(), schoolclass, sco, p.getValue())
		);
	}
	
	public static final Promise<List<DomCourseStudent>> NO_ACCESS = Promises.resolved(Collections.emptyList());
	
	public Promise<List<DomCourseStudent>> getCourses(Object id) {

	  Promise<Boolean> start = AccessManager.TRUE;
	  if (id instanceof DomCourseStudent && ((DomCourseStudent) id).getSchoolId() != null) {
	    start = accessManager.access((DomCourseStudent) id);
	  }
	  final DomCourse parent = toCourse(id);
	  return start.then(p -> {
	    if (p.getValue().booleanValue())
	      return profile.then(resolved-> courseManager.getCourses(parent, resolved.getValue(),context)).then(accessManager);
	    return NO_ACCESS;
	  });}

	private DomClassCourse toClassCourse(Object id) {
	  if (id instanceof DomClassCourse) return (DomClassCourse) id;
	  DomClassCourse result = new DomClassCourse();
	  if (id instanceof PersistenceId) {
	    result.setId((PersistenceId) id);
	  } else {
	    result.setId(idOf(id, PersistenceClassType.PersistentClassCourse));
	  }
	  return result;
	}
	
	
	
	private DomCourse toCourse(Object id) {
		if(id instanceof DomCourse) return (DomCourse) id;
		DomCourse result = new DomCourse();
		if(id instanceof PersistenceId) {
			result.setId((PersistenceId) id);
		} else {
			result.setId(idOf(id, PersistenceClassType.PersistentCourse));
		}
		return result;
	}

	public Promise<DomCourseStudent> getCourse(Object courseID) {
		final DomCourse course = toCourse(courseID);	
		return profile.then(new Success<DomDwoProfile, DomCourseStudent>() {

			@Override
			public Promise<DomCourseStudent> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return courseManager.getCourse(course, resolved.getValue(), 
						getSchoolClass(),
						context).then(accessManager::single);
			}
		});
	}
	
	

	public Promise<DomDwoProfileFull> getDwoProfile() {
		return profile;
	}

	DomSchoolClass getSchoolClass() {
		return DwoGlobalVars.instance().getCurrentSchoolClass();
	}
	
	public Promise<List<DomScoContext>> getScos(Object id) {
		final DomCourse parent = toCourse(id);
		return profile.then(new Success<DomDwoProfile, List<DomScoContext>>(){

			@Override
			public Promise<List<DomScoContext>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return scoManager.getScos(parent, resolved.getValue(), getSchoolClass(), getContext());
			}
			});
	}

	DomContext getContext() {
		return context;
	}

	public Promise<DomScoContext> getSco(Object scoID) {
		final DomScoContext dummy = toScoContext(scoID);
		return profile.then(new Success<DomDwoProfile, DomScoContext>() {

			@Override
			public Promise<DomScoContext> call(Promise<DomDwoProfile> resolved)
					throws Exception {
				return scoManager.getSco(dummy, resolved.getValue(), getSchoolClass(), getContext());
			}
			
		}).then(accessManager::sco);
	}

	private DomScoContext toScoContext(Object scoID) {
		if(scoID instanceof DomScoContext) return (DomScoContext) scoID;
		DomScoContext sco = new DomScoContext();
		if(scoID instanceof PersistenceId) {
			sco.setId( (PersistenceId) scoID);
		} else {
			sco.setId(idOf(scoID, PersistenceClassType.PersistentScoContext));
		}
		return sco;
	}

	private boolean secure = false;
	
	
	@Override
	public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		return super.getSchoolLogins().then(new Success<DomSchoolsRolesAndClassesV2, DomSchoolsRolesAndClassesV2>() {

			@Override
			public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomSchoolsRolesAndClassesV2> resolved)
					throws Exception {
				
				DomSchoolRoleAndClassV2 active = resolved.getValue().getActiveSchoolRoleAndClass();
                DomHasRole hasRole = active.getHasRole();
				context.setDomHasRole(hasRole);
				scoManager = new SecuredUserScoContextManager(secure);
				courseManager = new SecuredUserCourseManager();
				studentManager = new SecuredStudentCoursesOfSchoolClassManager(secure);
				resultManager = new SecuredUserResultsManager();
				scormApi = new SecuredStudentScoDataManager(secure);
				studentModelManager = new SecuredStudentStudentModelManager();
				if (active.getSchool().accessControl() && active.getRole().getRoleName().equals(RoleType.TEACHER.name())) {
				  accessManager = new TeacherAccessManager(active, null, profile);
				}
				return resolved;
			}
		});
	}

	@Override
	public Promise<Void> logout() {
		Promise<Void> logout;
		logout = super.logout();
        return logout.then(new Success<Void,Void>(){

			@Override
			public Promise<Void> call(Promise<Void> resolved) throws Exception {
				GwtRestVars.instance().getCustomHeaders().clear();
				DwoGlobalVars.instance().clearCurrentUser();
				context.setDomHasRole(null);
				scoManager = new PublicScoContextManager();
				courseManager = new PublicCourseManager();
				studentManager = new PublicCoursesOfSchoolClassManager();
				resultManager = new PublicUserResultsManager();
				scormApi = new PublicStudentScoDataManager();
				accessManager = new AccessManager();
				studentModelManager = null;
				return null;
			}});

		
	}

	public Promise<Map<String, String>> getValues(Object scoID,
			Collection<String> keys) {
		DomScoContext sco = toScoContext(scoID);
		return scormApi.getValues(sco, getSchoolClass(), getContext(), keys);
	}

	public Promise<?> setValues(Object scoID, Map<String, String> values) {
		DomScoContext sco = toScoContext(scoID);
		return scormApi.setValues(sco, getSchoolClass(), getContext(), values);
	}

	public Promise<JSONValue> getJSONLaunchDataBytes(Object scoID) {
		final DomScoContext id = toScoContext(scoID);
		Function<DomDwoProfile, Promise<? extends JSONValue>> t;
		t = new Function<DomDwoProfile, Promise<? extends JSONValue>>() {

			@Override
			public Promise<JSONValue> apply(DomDwoProfile resolved)
		    {
				return scormApi.getJSONLaunchDataBytes(id, resolved, getSchoolClass(), getContext());
			}
			
		};
		
		return profile.flatMap(t);
	}

	// cachable!!!!
	
	private Promise<JSONValue> profileDescription;
	
	public Promise <JSONValue> getProfileDescription() {
	  if (profileDescription != null) return profileDescription;
	  return 
	      profileDescription = 
	      profile.flatMap(profile -> {
    	    RestDwoProfile rest = new RestDwoProfile();
    	    rest.setDomDwoProfile(profile);
    	    rest.setRestContext(context);
    	    return profileManager.getDescription(rest);
    	  });
	}
	
	
	
	
	
	public Promise<JSONValue> getCourseDescription(Object courseID) {
		final DomCourse id = toCourse(courseID);
		Function<DomDwoProfile, Promise<? extends JSONValue>>
		t = new Function<DomDwoProfile, Promise<? extends JSONValue>>() {

			@Override
			public Promise<JSONValue> apply(DomDwoProfile resolved)
		    {
				return courseManager.getCourseDescription(id, resolved,context, DwoGlobalVars.instance().getCurrentSchoolClass());
			}
			
		};
		
		
		return profile.flatMap(t);
	}
	
	private static final Logger LOG = Logger.getLogger("RPCHandlerV3");

	public Promise<Void> startExam(String key, String value) {
		GwtRestVars vars = GwtRestVars.getInstance();
		Map<String,String> headers = vars.getCustomHeaders();
		headers.put("X-ClassCourseID", Base64.btoa(key));
		headers.put("X-TOTP", "PLAIN "+Base64.btoa(value));
		return accountManager.verifyTOTP(getContext()).then(new Success<JSONValue, Void>() {

			@Override
			public Promise<Void> call(Promise<JSONValue> resolved) throws Exception {
				LOG.info("verifyTOTP:" + resolved.getValue());
                JSONBoolean ok = resolved.getValue().isBoolean();
                JSONString str = resolved.getValue().isString();
				if (str != null) {
			       headers.put("X-TOTP", str.stringValue());
			       ok = JSONBoolean.getInstance(true);
				}
				if(ok != null && ok.booleanValue())
				{ // switch to secure/student/exam stuff
				  scoManager = new SecuredUserScoContextManager(true); // not yet			  
	              scormApi = new SecuredStudentScoDataManager(true);
				  return null;
				}
				throw new Dwo2Exception(Dwo2ExceptionCode.Exam_AuthenticationError, "verification failed: " + resolved.getValue());
			}
		})
//		.recoverWith(new Function<Promise<?>, Promise<? extends Void>>() {
//
//			@Override
//			public Promise<Void> apply(Promise<?> t) {
//				LOG.log(Level.SEVERE, "verifyTOTP recovery",t.getFailure());
//				return Promises.resolved(null);
//			}})
		;
	}
		
	public Promise<List<DomStudentModelContext>> getStudentModels() {
		if(studentModelManager == null)
			return Promises.failed(new IllegalArgumentException());
		return studentModelManager.getStudentModels(getContext());
	}
	
	public Promise<DomStudentModelContext> getStudentModel(final PersistenceId id) {
		return getStudentModels().map(list -> {
			for(DomStudentModelContext item: list) {
				if(id.equals(item.getId()))
					return item;
			}
			return null;
		});
	}
	
	public Promise<DomStudentModelDataScore> getStudentModelDataScore(DomStudentModelContextId id) {
		if(studentModelManager == null)
			return Promises.failed(new IllegalArgumentException());
		return studentModelManager.getStudentModelDataScore(getContext(), id);
	}

	public Promise<XapiManager> getLRS() {
	  if (studentModelManager == null) {
	    return Promises.failed(new IllegalArgumentException());
	  }
	  return studentModelManager.getLRS(getContext()).map(lrs -> {
	    Defaults.ignoreJsonNulls();
	    Defaults.setAddXHttpMethodOverrideHeader(false);
	    XapiManager xapi = new XapiManager();
	    xapi.setServer(lrs.getEndpoint());
	    xapi.setAuth(lrs.getAuth());
	    xapi.setAgent(lrs.getAgent());
	    return xapi;
	  });
	}

  public Promise<DomUserFullwLoginContext> getUserFromOAuthToken(String authToken) {
    return oauthManager.authorization_token(authToken)
        .then( 
                p -> {
                  GwtRestVars.getInstance().setBearerToken(p.getValue().getAccess_token());
                  GwtRestVars.getInstance().setRefreshToken(p.getValue().getRefresh_token());
                  return accountManager.getLoginContext();
                }
        ).then(
                q -> { 
                    DomContext context = this.context;
                    context.setRealm(q.getValue().getRealm());
                    context.setDomHasRole(new DomHasRole());
                    context.getDomHasRole().setId(q.getValue().getHasRoleId());
                    context.getDomHasRole().setSchoolGroupId(q.getValue().getSchoolGroupId());
                    context.getDomHasRole().setUserId(q.getValue().getUserId());
                return accountManager.getAccountData(context).map( 
                        data -> { 
                    DomUserFullwLoginContext all = new DomUserFullwLoginContext();
                    all.setDomLoginContext(q.getValue());
                    all.setDomUserFull(data);
                    return all;
                });
            }
        );
      
  }
	
	
	
	
}
