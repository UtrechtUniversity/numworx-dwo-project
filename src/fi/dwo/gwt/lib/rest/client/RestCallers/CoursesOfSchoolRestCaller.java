package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

import org.fusesource.restygwt.client.MethodCallback;

public interface CoursesOfSchoolRestCaller {

	public void getCoursesClass(RestSchoolClassAndProfile rest,
			MethodCallback<DomCoursesOfSchoolClass> callback);

    void getCoursesClass(RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);
    void getCoursesClass(RestScoContext rest, MethodCallback<DomCoursesOfSchoolClass> callback);
}