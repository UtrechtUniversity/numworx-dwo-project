package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

public interface SecuredUserCourseRestCaller extends RestService {

	@PUT
    @Path("/sec:{id}/user/course/getRoot")
	void getCourses(@PathParam("id") String id, RestDwoProfile rest, MethodCallback<List<DomCourseStudent>> result);

	@PUT
    @Path("/sec:{id}/user/course/getChildren")
	void getCourses(@PathParam("id") String id, RestCourse rest, MethodCallback<List<DomCourseStudent>> result);

	@PUT
    @Path("/sec:{id}/user/course/get")
	void getCourse(@PathParam("id") String id, RestCourse rest, MethodCallback<DomCourseStudent> result);

	@PUT
    @Path("/sec:{id}/user/course/getSchool")
	void getCoursesSchool(@PathParam("id") String id, RestDwoProfile rest, MethodCallback<List<DomCourseStudent>> result);

	@PUT
    @Path("/sec:{id}/user/course/getCourseDescription")
	void getCourseDescription(@PathParam("id") String id, RestCourse rest, MethodCallback<JSONValue> result);

	@PUT
	@Path("/sec:{id}/user/course/getAll")
	public void getAllCourses(@PathParam("id") String id, RestDwoProfile rest, MethodCallback<List<DomCourse>> callback);

	@GET
	@Path("/sec:{id}/user/course/getCourseDescription")
	public void getCourseDescription(@PathParam("id") String id, @QueryParam("courseId") Number courseId, 
				@QueryParam("profile") Number profile, 
				@QueryParam("classId") Number classId,
				MethodCallback<JSONValue> result
			);
	
}
