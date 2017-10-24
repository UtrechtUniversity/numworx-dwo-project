package nl.uu.fi.dwo.account.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.CourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.CoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicCoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicUserResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.ScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentCoursesOfSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserResultsManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.StudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.UserResultsManager;

public class RPCHandlerV3 extends RPCHandlerV2 {

	ScoContextManager scoManager;	
	CourseManager courseManager;
	PublicProfileManager profileManager;
	CoursesOfSchoolClassManager studentManager;
	UserResultsManager resultManager;
	StudentScoDataManager scormApi;
	
	protected Promise<DomDwoProfileFull> profile;
	
	
	public RPCHandlerV3(String server, int profile, boolean secure) {
		super(server, profile);
		this.secure = secure;
		scoManager = new PublicScoContextManager();
		courseManager = new PublicCourseManager();
		profileManager = new PublicProfileManager();
		studentManager = new PublicCoursesOfSchoolClassManager();
		resultManager = new PublicUserResultsManager();
		scormApi = new PublicStudentScoDataManager();
		
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

	@Override
	public Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID) {
		final DomCourse course = toCourse(courseID);
		return profile.then(new Success<DomDwoProfile, DomResultsPerStudentCourse>() {

			@Override
			public Promise<DomResultsPerStudentCourse> call(Promise<DomDwoProfile> resolved) throws Exception {
				return resultManager.getCourseResults(getContext(), course, resolved.getValue());
			}
		});
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses() {
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return courseManager.getCourses(p,context);
			}
		});
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return courseManager.getCoursesSchool(p,context);
			}
		});
	}

	@Override
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

	@Override
	public Promise<List<DomCourseStudent>> getCourses(Object id) {
		final DomCourse parent = toCourse(id);	
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return courseManager.getCourses(parent, resolved.getValue(),context);
			}
		});
	}

	private DomCourse toCourse(Object id) {
		if(id instanceof DomCourse) return (DomCourse) id;
		DomCourse result = new DomCourse();
		result.setId(idOf(id, PersistenceClassType.PersistentCourse));
		return result;
	}

	@Override
	public Promise<DomCourseStudent> getCourse(Object courseID) {
		final DomCourse course = toCourse(courseID);	
		return profile.then(new Success<DomDwoProfile, DomCourseStudent>() {

			@Override
			public Promise<DomCourseStudent> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return courseManager.getCourse(course, resolved.getValue(),context);
			}
		});
	}

	@Override
	public Promise<DomDwoProfileFull> getDwoProfile() {
		return profile;
	}

	DomSchoolClass getSchoolClass() {
		return DwoGlobalVars.instance().getCurrentSchoolClass();
	}
	
	
	@Override
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

	private DomContext getContext() {
		return context;
	}

	@Override
	public Promise<DomScoContext> getSco(Object scoID) {
		final DomScoContext dummy = toScoContext(scoID);
		return profile.then(new Success<DomDwoProfile, DomScoContext>() {

			@Override
			public Promise<DomScoContext> call(Promise<DomDwoProfile> resolved)
					throws Exception {
				return scoManager.getSco(dummy, resolved.getValue(), getSchoolClass(), getContext());
			}
			
		});
	}

	private DomScoContext toScoContext(Object scoID) {
		if(scoID instanceof DomScoContext) return (DomScoContext) scoID;
		DomScoContext sco = new DomScoContext();
		sco.setId(idOf(scoID, PersistenceClassType.PersistentScoContext));
		return sco;
	}

	private DomContext context = new DomContext();
	private boolean secure = false;
	
	
	@Override
	public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		return super.getSchoolLogins().then(new Success<DomSchoolsRolesAndClassesV2, DomSchoolsRolesAndClassesV2>() {

			@Override
			public Promise<DomSchoolsRolesAndClassesV2> call(Promise<DomSchoolsRolesAndClassesV2> resolved)
					throws Exception {
				
				DomHasRole hasRole = resolved.getValue().getActiveSchoolRoleAndClass().getHasRole();
				context.setDomHasRole(hasRole);
				scoManager = new SecuredUserScoContextManager(secure);
				courseManager = new SecuredUserCourseManager();
				studentManager = new SecuredStudentCoursesOfSchoolClassManager(secure);
				resultManager = new SecuredUserResultsManager();
				scormApi = new SecuredStudentScoDataManager(secure);
				return resolved;
			}
		});
	}

	@Override
	public Promise<Void> logout() {
		return super.logout().then(new Success<Void,Void>(){

			@Override
			public Promise<Void> call(Promise<Void> resolved) throws Exception {
				GwtRestVars.instance().getCustomHeaders().clear();
				context.setDomHasRole(null);
				scoManager = new PublicScoContextManager();
				courseManager = new PublicCourseManager();
				studentManager = new PublicCoursesOfSchoolClassManager();
				resultManager = new PublicUserResultsManager();
				scormApi = new PublicStudentScoDataManager();
				return null;
			}});

		
	}

	@Override
	@Deprecated
	public void getUserResults(Object courseID, Object userID,
			AsyncCallback<List<Map<String, Object>>> getUserResultsCallback) {
		getUserResultsCallback.onFailure(new Error());
	}

	@Override
	public Promise<Map<String, String>> getValues(Object scoID,
			Collection<String> keys) {
		DomScoContext sco = toScoContext(scoID);
		return scormApi.getValues(sco, getSchoolClass(), getContext(), keys);
	}

	@Override
	public Promise<?> setValues(Object scoID, Map<String, String> values) {
		DomScoContext sco = toScoContext(scoID);
		return scormApi.setValues(sco, getSchoolClass(), getContext(), values);
	}

	@Override
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

	@Override
	public Promise<JSONValue> getCourseDescription(Object courseID) {
		final DomCourse id = toCourse(courseID);
		Function<DomDwoProfile, Promise<? extends JSONValue>>
		t = new Function<DomDwoProfile, Promise<? extends JSONValue>>() {

			@Override
			public Promise<JSONValue> apply(DomDwoProfile resolved)
		    {
				return courseManager.getCourseDescription(id, resolved,context);
			}
			
		};
		
		
		return profile.flatMap(t);
	}

	static native String btoa(String bytes) /*-{
		return btoa(bytes)
	}-*/;
	
	private static final Logger LOG = Logger.getLogger("RPCHandlerV3");
	@SuppressWarnings("deprecation")
	public Promise<Void> startExam(String key, String value) {
		GwtRestVars vars = GwtRestVars.getInstance();
		Map<String,String> headers = vars.getCustomHeaders();
		headers.put("X-ClassCourseID", btoa(key));
		headers.put("X-TOTP", "PLAIN "+btoa(value));
		return accountManager.verifyTOTP().then(new Success<JSONValue, Void>() {

			@Override
			public Promise<Void> call(Promise<JSONValue> resolved) throws Exception {
				LOG.info("verifyTOTP:" + resolved.getValue());
				return null;
			}
		})
		.recoverWith(new Function<Promise<?>, Promise<? extends Void>>() {

			@Override
			public Promise<Void> apply(Promise<?> t) {
				LOG.log(Level.SEVERE, "verifyTOTP recovery",t.getFailure());
				return Promises.resolved(null);
			}})
		;
	}
}
