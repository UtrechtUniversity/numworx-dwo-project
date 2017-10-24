package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

public interface SecuredStudentExamCourseRestCaller extends RestService, CoursesOfSchoolRestCaller {
    @PUT
    @Path("/secure/student/exam/coursesofschoolclass/get")
    void getCoursesClass(RestSchoolClassAndProfile rest, MethodCallback<DomCoursesOfSchoolClass> callback);

    @PUT
    @Path("/secure/student/exam/coursesofschoolclass/getCourse")
    void getCoursesClass(RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);

    @PUT
    @Path("/secure/student/exam/coursesofschoolclass/getScoContext")
    void getCoursesClass(RestScoContext rest, MethodCallback<DomCoursesOfSchoolClass> callback);
}
