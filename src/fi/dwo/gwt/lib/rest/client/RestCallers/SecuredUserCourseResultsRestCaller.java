package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;

public interface SecuredUserCourseResultsRestCaller extends RestService {

    @PUT
    @Path("/secure/user/results/getCourseResults")
    public void getCourseResults(RestCourse aCourse,MethodCallback<List<DomStudentScoContext>> callback);
}
