package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.PublicCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class PublicCourseManager implements CourseManager {

	private PublicCourseRestCaller service;
	
	private DomContext createContext() {
		return new DomContext();
	}
	
	public PublicCourseManager() {
		service = GWT.create(PublicCourseRestCaller.class);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.CourseManager#getCourses(nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
	 */
	@Override
	public Promise<List<DomCourseStudent>>getCourses(DomDwoProfile profile) {
		PromiseCallback<List<DomCourseStudent>> result = new PromiseCallback<List<DomCourseStudent>>();
		RestDwoProfile rest = new RestDwoProfile();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(createContext());
		service.getCourses(rest, result);
		return result.getPromise();
	}
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.CourseManager#getCourses(nl.uu.fi.dwo.rest.dom.entities.DomCourse, nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
	 */
	@Override
	public Promise<List<DomCourseStudent>> getCourses(DomCourse course, DomDwoProfile profile) {
		PromiseCallback<List<DomCourseStudent>> result = new PromiseCallback<List<DomCourseStudent>>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(createContext());
		rest.setDomCourse(course);
		service.getCourses(rest, result);
		return result.getPromise();
	}

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.CourseManager#getCourse(nl.uu.fi.dwo.rest.dom.entities.DomCourse, nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
	 */
	@Override
	public Promise<DomCourseStudent> getCourse(DomCourse course, DomDwoProfile profile) {
		PromiseCallback<DomCourseStudent> result = new PromiseCallback<DomCourseStudent>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(createContext());
		rest.setDomCourse(course);
		service.getCourse(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomDwoProfile profile) {
		return Promises.failed(new IllegalArgumentException());
	}

}
