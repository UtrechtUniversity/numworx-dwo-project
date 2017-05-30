package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

public interface SecuredUserCourseRestCaller extends RestService {

	@PUT
    @Path("/secure/user/course/getRoot")
	void getCourses(RestDwoProfile rest, MethodCallback<List<DomCourseStudent>> result);

	@PUT
    @Path("/secure/user/course/getChildren")
	void getCourses(RestCourse rest, MethodCallback<List<DomCourseStudent>> result);

	@PUT
    @Path("/secure/user/course/get")
	void getCourse(RestCourse rest, MethodCallback<DomCourseStudent> result);

	@PUT
    @Path("/secure/user/course/getSchool")
	void getCoursesSchool(RestDwoProfile rest, MethodCallback<List<DomCourseStudent>> result);

}
