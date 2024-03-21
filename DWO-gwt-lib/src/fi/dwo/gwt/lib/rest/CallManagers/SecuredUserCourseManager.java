package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import fi.dwo.gwt.lib.rest.GwtRestVars.TriConsumer;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserCourseRestCaller;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
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

	public Promise<JSONValue> getCourseDescription0(DomCourse course,
			DomDwoProfile profile, DomContext context, DomSchoolClassId clsid) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		rest.setSchoolClassID(clsid);
		return F(service::getCourseDescription,PathId.getId(context),rest);
	}

	public Promise<JSONValue> getCourseDescription(DomCourse course, DomDwoProfile profile, DomContext context, DomSchoolClassId clsid) {
		if (clsid == null) {
			// oldschool
			return getCourseDescription0(course, profile, context, clsid);
		}
		
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		rest.setSchoolClassID(clsid);
		TriConsumer<RestCourse, MethodCallback<JSONValue>> f = new TriConsumer<RestCourse, MethodCallback<JSONValue>>() {
			@Override
			public void accept(String id, RestCourse rest, MethodCallback<JSONValue> callback) {
				Number courseId = (Number) PersistenceIdDecoderInterface.instance.idOf(rest.getDomCourse().getId(), PersistenceClassType.PersistentCourse);
				Number profile  = (Number) PersistenceIdDecoderInterface.instance.idOf(rest.getDomDwoProfile().getId(), PersistenceClassType.PersistentDwoProfile);
				Number classId  = (Number) PersistenceIdDecoderInterface.instance.idOf(rest.getSchoolClassID().getId(), PersistenceClassType.PersistentSchoolClass);
				service.getCourseDescription(id, courseId, profile, classId, callback);
			} };
		return F(f,PathId.getId(context),rest);

	}
	
	
}
