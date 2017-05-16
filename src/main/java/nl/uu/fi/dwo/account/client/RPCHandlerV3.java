package nl.uu.fi.dwo.account.client;

import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class RPCHandlerV3 extends RPCHandlerV2 {

	
	private static final DomContext DOM_CONTEXT = new DomContext();

	interface CourseManager {

		Promise<List<DomCourseStudent>> getCourses(RestDwoProfile rp);

		Promise<DomCourseStudent> getCourse(RestCourse rest);} {
		
	}
	interface ProfileManager {

		Promise<DomDwoProfileFull> getDwoProfile(int profile2);
		
	}
	
	class RestCourse extends RestDwoProfile {

		public void setDomCourse(DomCourse parent) {			
		} 
		
	}
	class RestScoContext extends RestDwoProfile {

		public void setDomScoContext(DomScoContext dummy) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	interface ScoManager{

		Promise<List<DomScoContext>> getScos(RestCourse rest);

		Promise<DomScoContext> getSco(RestScoContext sco);}
	ScoManager scoManager;
	
	CourseManager courseManager;
	ProfileManager profileManager;
	
	protected Promise<DomDwoProfileFull> profile;
	
	
	public RPCHandlerV3(String server, int profile) {
		super(server, profile);
		this.profile = getDwoProfile(profile);
	}

	private Promise<DomDwoProfileFull> getDwoProfile(int profile2) {
		return profileManager.getDwoProfile(profile2);
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
				RestDwoProfile rp = new RestDwoProfile();
				rp.setRestContext(getDomContext());
				rp.setDomDwoProfile(resolved.getValue());
				return courseManager.getCourses(rp);
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
				RestCourse rest = new RestCourse();
				rest.setRestContext(getDomContext());
				rest.setDomDwoProfile(resolved.getValue());
				rest.setDomCourse(parent);
				return courseManager.getCourses(rest);
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
		DomCourse course = toCourse(courseID);	
		final RestCourse rest = new RestCourse();
		rest.setRestContext(getDomContext());
		rest.setDomCourse(course);
		return profile.then(new Success<DomDwoProfile, DomCourseStudent>() {

			@Override
			public Promise<DomCourseStudent> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				rest.setDomDwoProfile(resolved.getValue());
				return courseManager.getCourse(rest);
			}
		});
	}

	DomContext getDomContext() {
		return DOM_CONTEXT;
	}

	@Override
	public Promise<DomDwoProfileFull> getDwoProfile() {
		return profile;
	}

	@Override
	public Promise<List<DomScoContext>> getScos(Object id) {
		DomCourse parent = toCourse(id);
		final RestCourse rest = new RestCourse();
		return profile.then(new Success<DomDwoProfile, List<DomScoContext>>(){

			@Override
			public Promise<List<DomScoContext>> call(
					Promise<DomDwoProfile> resolved) throws Exception {
				rest.setDomDwoProfile(resolved.getValue());
				return scoManager.getScos(rest);
			}});
	}

	@Override
	public Promise<DomScoContext> getSco(Object scoID) {
		final RestScoContext sco = new RestScoContext();
		DomScoContext dummy = toScoContext(scoID);
		sco.setRestContext(getDomContext());
		sco.setDomScoContext(dummy);
		return profile.then(new Success<DomDwoProfile, DomScoContext>() {

			@Override
			public Promise<DomScoContext> call(Promise<DomDwoProfile> resolved)
					throws Exception {
				sco.setDomDwoProfile(resolved.getValue());
				return scoManager.getSco(sco);
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
