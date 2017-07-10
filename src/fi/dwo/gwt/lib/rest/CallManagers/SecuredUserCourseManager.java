package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

public class SecuredUserCourseManager implements CourseManager {

	private SecuredUserCourseRestCaller service = GWT.create(SecuredUserCourseRestCaller.class);
	
	@Override
	public Promise<List<DomCourseStudent>> getCourses(DomDwoProfile profile, DomContext context) {
		PromiseCallback<List<DomCourseStudent>> result = new PromiseCallback<List<DomCourseStudent>>();
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		service.getCourses(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses(DomCourse course, DomDwoProfile profile, DomContext context) {
		PromiseCallback<List<DomCourseStudent>> result = new PromiseCallback<List<DomCourseStudent>>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		service.getCourses(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<DomCourseStudent> getCourse(DomCourse course, DomDwoProfile profile, DomContext context) {
		PromiseCallback<DomCourseStudent> result = new PromiseCallback<DomCourseStudent>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		service.getCourse(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomDwoProfile profile, DomContext context) {
		PromiseCallback<List<DomCourseStudent>> result = new PromiseCallback<List<DomCourseStudent>>();
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		service.getCoursesSchool(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<JSONValue> getCourseDescription(DomCourse course,
			DomDwoProfile profile, DomContext context) {
		PromiseCallback<JSONValue> result = new PromiseCallback<JSONValue>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		service.getCourseDescription(rest, result);
		return result.getPromise();
	}

}
