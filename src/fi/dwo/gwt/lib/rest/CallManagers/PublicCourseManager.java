package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.GwtRestVars;
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
	public Promise<List<DomCourseStudent>>getCourses(DomDwoProfile profile, DomContext context) {
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
	public Promise<List<DomCourseStudent>> getCourses(DomCourse course, DomDwoProfile profile, DomContext context) {
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
	public Promise<DomCourseStudent> getCourse(DomCourse course, DomDwoProfile profile, DomContext context) {
		PromiseCallback<DomCourseStudent> result = new PromiseCallback<DomCourseStudent>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(createContext());
		rest.setDomCourse(course);
		service.getCourse(rest, result);
		return result.getPromise();
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomDwoProfile profile, DomContext context) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<JSONValue> getCourseDescription(DomCourse id,
			DomDwoProfile profile, DomContext context) {
		final Deferred<JSONValue> defer = new Deferred<JSONValue>();
		String courseID = id.getId().getIdString();
		int komma = courseID.lastIndexOf(';'); // XXX ons kent ons
		courseID = courseID.substring(komma+1);
		String url = GwtRestVars.getInstance().getServer() + "public/course/getCourseDescription?courseId=" + courseID;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		rb.setTimeoutMillis(100000);
		try {
			rb.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == 200) {
						String text = response.getText();
						JSONValue value = JSONParser.parseStrict(text);
						defer.resolve(value);
					} else {
						defer.fail(new RequestException(response.getStatusText()));
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					defer.fail(exception);
				}
			});
		} catch (RequestException e) {
			defer.fail(e);
		}		
		return defer.getPromise();
	}

}
