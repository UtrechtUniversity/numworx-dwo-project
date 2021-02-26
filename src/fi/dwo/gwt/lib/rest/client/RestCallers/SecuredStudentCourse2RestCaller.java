package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

public interface SecuredStudentCourse2RestCaller extends RestService, CoursesOfSchoolRestCaller {

	
	    @PUT
	    @Path("/sec:{id}/student/coursesofschoolclass/get")
	    void getCoursesClass(@PathParam("id") String id, RestSchoolClassAndProfile rest,  MethodCallback<DomCoursesOfSchoolClass> callback);

	    @PUT
	    @Path("/sec:{id}/student/coursesofschoolclass/getCourse")
	    void getCoursesClass(@PathParam("id") String id, RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);
	    @PUT
	    @Path("/sec:{id}/student/coursesofschoolclass/getScoContext")
	    void getCoursesClass(@PathParam("id") String id, RestScoContext rest, MethodCallback<DomCoursesOfSchoolClass> callback);

        @PUT
        @Path("/sec:{id}/student/coursesofschoolclass/getClassCourse")
	    void getCoursesClass(String id, RestClassCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);

	    
}
