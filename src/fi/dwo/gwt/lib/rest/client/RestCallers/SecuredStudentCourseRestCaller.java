package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;

public interface SecuredStudentCourseRestCaller extends RestService {
    @PUT
    @Path("/secure/student/coursesofschoolclass/get")
    void getCoursesClass(RestSchoolClassAndProfile rest, MethodCallback<DomCoursesOfSchoolClass> callback);
}
