package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;

public class SecuredStudentCoursesOfSchoolClassManager implements CoursesOfSchoolClassManager {
	
	private SecuredStudentCourseRestCaller service;
	
	public SecuredStudentCoursesOfSchoolClassManager() {
		service = GWT.create(SecuredStudentCourseRestCaller.class);
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile) {
		PromiseCallback<DomCoursesOfSchoolClass> callback = new PromiseCallback<DomCoursesOfSchoolClass>();
		RestSchoolClassAndProfile rest = new RestSchoolClassAndProfile();
		rest.setRestContext(context);
		rest.setDomDwoProfile(profile);
		rest.setDomSchoolClass(schoolClass);
		service.getCoursesClass(rest, callback);
		return callback.getPromise();
	}

}
