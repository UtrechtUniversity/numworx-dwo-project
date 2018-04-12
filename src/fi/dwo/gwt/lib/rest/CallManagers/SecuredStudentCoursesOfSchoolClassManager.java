package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.CoursesOfSchoolRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentCourse2RestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentCourseRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

public class SecuredStudentCoursesOfSchoolClassManager implements CoursesOfSchoolClassManager {
	
	private final CoursesOfSchoolRestCaller service;
	
	public SecuredStudentCoursesOfSchoolClassManager() {
		service = GWT.create(SecuredStudentCourse2RestCaller.class);
	}
	public SecuredStudentCoursesOfSchoolClassManager(boolean secure) {
		service = secure
				? GWT.<SecuredStudentExamCourseRestCaller>create(SecuredStudentExamCourseRestCaller.class)
				: GWT.<SecuredStudentCourseRestCaller>create(SecuredStudentCourse2RestCaller.class);			
	}
	

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile) {
		PromiseCallback<DomCoursesOfSchoolClass> callback = new PromiseCallback<DomCoursesOfSchoolClass>();
		RestSchoolClassAndProfile rest = new RestSchoolClassAndProfile();
		DomSchoolClassAndProfile  dom  = new DomSchoolClassAndProfile();
		rest.setRestContext(context);
		rest.setDomSchoolClassAndProfile(dom);
		dom.setDomDwoProfile(profile);
		dom.setDomSchoolClass(schoolClass);
		service.getCoursesClass(rest, callback);
		return callback.getPromise();
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCourseClass(DomContext context, DomSchoolClass schoolClass,
			DomCourse course, DomDwoProfile profile) {
		PromiseCallback<DomCoursesOfSchoolClass> callback = new PromiseCallback<DomCoursesOfSchoolClass>();
		RestCourse rest = new RestCourse();
		rest.setRestContext(context);
		rest.setDomCourse(course);
		rest.setDomDwoProfile(profile);
		rest.setSchoolClassID(schoolClass);
		service.getCoursesClass(rest, callback);	
		return callback.getPromise();
	}
	@Override
	public Promise<DomCoursesOfSchoolClass> getScoContextClass(DomContext context, DomSchoolClass schoolClass,
			DomScoContext sco, DomDwoProfile profile) {
		PromiseCallback<DomCoursesOfSchoolClass> callback = new PromiseCallback<DomCoursesOfSchoolClass>();
		RestScoContext rest = new RestScoContext();
		rest.setRestContext(context);
		rest.setDomDwoProfile(profile);
		rest.setDomScoContext(sco);
		rest.setSchoolClassID(schoolClass);		
		service.getCoursesClass(rest, callback);
		return callback.getPromise();
	}

}
