package nl.uu.fi.dwo.account.client;

import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.CourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicCourseManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicScoContextManager;
import fi.dwo.gwt.lib.rest.CallManagers.ScoContextManager;

public class RPCHandlerV3 extends RPCHandlerV2 {

		
	ScoContextManager scoManager;	
	CourseManager courseManager;
	PublicProfileManager profileManager;
	
	protected Promise<DomDwoProfileFull> profile;
	
	
	public RPCHandlerV3(String server, int profile) {
		super(server, profile);
		
		scoManager = new PublicScoContextManager();
		courseManager = new PublicCourseManager();
		profileManager = new PublicProfileManager();
		
		
		this.profile = getDwoProfile(profile);
	}

// What if fails?
	private Promise<DomDwoProfileFull> getDwoProfile(final int id) {
		return profileManager.get(id)
				.recover(new Function<Promise<?>, DomDwoProfileFull>() {

			@Override
			public DomDwoProfileFull apply(Promise<?> t) {
				DomDwoProfileFull recover = new DomDwoProfileFull();
				recover.setDwoProfileDescription("");
				recover.setDwoProfileName("");
				recover.setDwoProfileRights("rl");
				recover.setDwoProfileText("");
				recover.setId(idOf(id, PersistenceClassType.PersistentDwoProfile));
				return recover;
			}
		})
		;
	}

	@Override
	public void getUserResults(Object courseID, Object userID,
			AsyncCallback<List<Map<String, Object>>> getUserResultsCallback) {
		// TODO Auto-generated method stub

	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses() {
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				DomDwoProfile p = resolved.getValue();
				return courseManager.getCourses(p);
			}
		});
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		// TODO Auto-generated method stub
		return super.getCoursesSchool(school);
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(
			DomSchoolClass schoolclass) {
		// TODO Auto-generated method stub
		return super.getCoursesClass(schoolclass);
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses(Object id) {
		final DomCourse parent = toCourse(id);	
		return profile.then(new Success<DomDwoProfile, List<DomCourseStudent>>() {

			@Override
			public Promise<List<DomCourseStudent>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return courseManager.getCourses(parent, resolved.getValue());
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
				return courseManager.getCourse(course, resolved.getValue());
			}
		});
	}

	@Override
	public Promise<DomDwoProfileFull> getDwoProfile() {
		return profile;
	}

	@Override
	public Promise<List<DomScoContext>> getScos(Object id) {
		final DomCourse parent = toCourse(id);
		return profile.then(new Success<DomDwoProfile, List<DomScoContext>>(){

			@Override
			public Promise<List<DomScoContext>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				return scoManager.getScos(parent, resolved.getValue());
			}});
	}

	@Override
	public Promise<DomScoContext> getSco(Object scoID) {
		final DomScoContext dummy = toScoContext(scoID);
		return profile.then(new Success<DomDwoProfile, DomScoContext>() {

			@Override
			public Promise<DomScoContext> call(Promise<DomDwoProfile> resolved)
					throws Exception {
				return scoManager.getSco(dummy, resolved.getValue());
			}
			
		});
	}

	private DomScoContext toScoContext(Object scoID) {
		if(scoID instanceof DomScoContext) return (DomScoContext) scoID;
		DomScoContext sco = new DomScoContext();
		sco.setId(idOf(scoID, PersistenceClassType.PersistentScoContext));
		return sco;
	}

}
