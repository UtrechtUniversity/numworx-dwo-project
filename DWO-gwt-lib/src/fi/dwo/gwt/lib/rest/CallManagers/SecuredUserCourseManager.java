package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserCourseRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredUserCourseManager implements CourseManager {

	private SecuredUserCourseRestCaller service = GWT.create(SecuredUserCourseRestCaller.class);
	
	@Override
	public Promise<List<DomCourse>> getAllCourses(DomDwoProfile profile, DomContext context) {
		RestDwoProfile rest = new RestDwoProfile(profile, context);
		return F(service::getAllCourses, PathId.getId(context), rest);
	}
	
	
	@Override
	public Promise<List<DomCourseStudent>> getCourses(DomDwoProfile profile, DomContext context) {
		RestDwoProfile rest = new RestDwoProfile(profile, context);
		return F(service::getCourses,PathId.getId(context),rest);
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses(DomCourse course, DomDwoProfile profile, DomContext context) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		return F(service::getCourses,PathId.getId(context),rest);
	}

	@Override
	public Promise<DomCourseStudent> getCourse(DomCourse course, DomDwoProfile profile, DomSchoolClassId schoolClassId, DomContext context) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setSchoolClassID(schoolClassId);
		rest.setDomCourse(course);
		return F(service::getCourse,PathId.getId(context),rest);
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomDwoProfile profile, DomContext context) {
		RestDwoProfile rest = new RestDwoProfile(profile, context);
		return F(service::getCoursesSchool,PathId.getId(context),rest);
	}

	@Override
	public Promise<JSONValue> getCourseDescription(DomCourse course,
			DomDwoProfile profile, DomContext context) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		return F(service::getCourseDescription,PathId.getId(context),rest);
	}

	@Override
	public Promise<JSONValue> getCourseDescription(DomCourse course,
			DomDwoProfile profile, DomContext context, DomSchoolClassId clsid) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		rest.setSchoolClassID(clsid);
		return F(service::getCourseDescription,PathId.getId(context),rest);
	}

}
