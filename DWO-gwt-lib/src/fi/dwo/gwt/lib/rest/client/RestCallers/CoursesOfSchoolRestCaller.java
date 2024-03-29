package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.json.client.JSONValue;

public interface CoursesOfSchoolRestCaller {

	public void getCoursesClass(String id, RestSchoolClassAndProfile rest,
			MethodCallback<DomCoursesOfSchoolClass> callback);

    void getCoursesClass(String id, RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);
    void getCoursesClass(String id, RestScoContext rest, MethodCallback<DomCoursesOfSchoolClass> callback);
    void getCoursesClass(String id, RestClassCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);
    void getCoursesClassURL(String id, String base, String classcourseid, MethodCallback<JSONValue> callback);

}