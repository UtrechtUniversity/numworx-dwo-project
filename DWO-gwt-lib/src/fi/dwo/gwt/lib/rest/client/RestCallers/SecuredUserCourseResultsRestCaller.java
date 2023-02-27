package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;

public interface SecuredUserCourseResultsRestCaller extends RestService {

    @PUT
    @Path("/sec:{id}/user/results/getCourseResults")
    public void getCourseResults(@PathParam("id") String id, RestCourse aCourse,MethodCallback<List<DomStudentScoContext>> callback);

//    @PUT
//    @Path("/sec:{id}/user/results/getClassCourseResults")
//    public void getCourseResults(@PathParam("id") String id, RestClassCourse aCourse,MethodCallback<List<DomStudentScoContext>> callback);
}
