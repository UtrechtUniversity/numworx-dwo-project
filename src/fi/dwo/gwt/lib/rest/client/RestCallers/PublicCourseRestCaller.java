package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface PublicCourseRestCaller extends RestService {
	@PUT
    @Path("/public/course/getRoot")
    public void getCourses(RestDwoProfile restDwoProfile, MethodCallback<List<DomCourseStudent>> callback);

	@PUT
    @Path("/public/course/getChildren")
    public void getCourses(RestCourse restDwoProfile, MethodCallback<List<DomCourseStudent>> callback);

	@PUT
    @Path("/public/course/get")
    public void getCourse(RestCourse restDwoProfile, MethodCallback<DomCourseStudent> callback);

}
