package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserCourseResultsRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;

public class SecuredUserResultsManager implements UserResultsManager {

	SecuredUserCourseResultsRestCaller service;
	public SecuredUserResultsManager() {
		service = GWT.create(SecuredUserCourseResultsRestCaller.class);
	}

	@Override
	public Promise<DomResultsPerStudentCourse> getCourseResults(DomContext context, DomCourse course, DomDwoProfile profile) {
		PromiseCallback<DomResultsPerStudentCourse> defer = new PromiseCallback<DomResultsPerStudentCourse>();
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		service.getCourseResults(rest, defer);
		return defer.getPromise();
	}

}
