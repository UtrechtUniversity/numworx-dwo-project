package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.CoursesOfSchoolRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentCourse2RestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentExamCourseRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentCoursesOfSchoolClassManager implements CoursesOfSchoolClassManager {
	
	private final CoursesOfSchoolRestCaller service;
	
	public SecuredStudentCoursesOfSchoolClassManager() {
		service = GWT.create(SecuredStudentCourse2RestCaller.class);
	}
	public SecuredStudentCoursesOfSchoolClassManager(boolean secure) {
		service = secure
				? GWT.<SecuredStudentExamCourseRestCaller>create(SecuredStudentExamCourseRestCaller.class)
				: GWT.<SecuredStudentCourse2RestCaller>create(SecuredStudentCourse2RestCaller.class);			
	}
	

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomContext context, DomSchoolClass schoolClass, DomDwoProfile profile) {
		RestSchoolClassAndProfile rest = new RestSchoolClassAndProfile();
		DomSchoolClassAndProfile  dom  = new DomSchoolClassAndProfile();
		rest.setRestContext(context);
		rest.setDomSchoolClassAndProfile(dom);
		dom.setDomDwoProfile(profile);
		dom.setDomSchoolClass(schoolClass);
		return F(service::getCoursesClass,PathId.getId(context),rest);
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCourseClass(DomContext context, DomSchoolClass schoolClass,
			DomCourse course, DomDwoProfile profile) {
		RestCourse rest = new RestCourse();
		rest.setRestContext(context);
		rest.setDomCourse(course);
		rest.setDomDwoProfile(profile);
		rest.setSchoolClassID(schoolClass);
		return F(service::getCoursesClass,PathId.getId(context), rest);	
	}
	@Override
	public Promise<DomCoursesOfSchoolClass> getScoContextClass(DomContext context, DomSchoolClass schoolClass,
			DomScoContext sco, DomDwoProfile profile) {
		RestScoContext rest = new RestScoContext();
		rest.setRestContext(context);
		rest.setDomDwoProfile(profile);
		rest.setDomScoContext(sco);
		rest.setSchoolClassID(schoolClass);		
		return F(service::getCoursesClass,PathId.getId(context),rest);
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getClassCourse(DomContext context, DomClassCourse classcourse, DomDwoProfile profile) {
	    RestClassCourse rest = new RestClassCourse();
	    rest.setRestContext(context);
	    rest.setDomClassCourse(classcourse);
	    rest.setDomDwoProfile(profile);
	    return F(service::getCoursesClass, PathId.getId(context), rest);
	}

	@Override
	public Promise<String> getClassCourseURL(DomContext context, DomClassCourse classcourse, String base) {
		String id = PathId.getId(context);
		String cc = classcourse.getId().getIdString();
		Promise<JSONValue> p = F( (i, c, callback) -> service.getCoursesClassURL(id, base, cc, callback), id, null);
		return	p.map( (JSONValue v) -> v.isObject().get("url").isString().stringValue());
	}
	
	
}
