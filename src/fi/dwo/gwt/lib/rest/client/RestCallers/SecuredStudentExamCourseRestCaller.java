package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentExamCourseRestCaller implements CoursesOfSchoolRestCaller {
  interface Helper extends RestService {
  
    @PUT
    @Path("/sec:{id}/student/exam/coursesofschoolclass/get")
    void getCoursesClass(@PathParam("id") String id, RestSchoolClassAndProfile rest, MethodCallback<DomCoursesOfSchoolClass> callback);

    @PUT
    @Path("/sec:{id}/student/exam/coursesofschoolclass/getCourse")
    void getCoursesClass(@PathParam("id") String id, RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback);

    @PUT
    @Path("/sec:{id}/student/exam/coursesofschoolclass/getScoContext")
    void getCoursesClass(@PathParam("id") String id, RestScoContext rest, MethodCallback<DomCoursesOfSchoolClass> callback);
  }

  final Helper helper = GWT.create(Helper.class);
  @Override
  public void getCoursesClass(RestSchoolClassAndProfile rest,
      MethodCallback<DomCoursesOfSchoolClass> callback) {
    helper.getCoursesClass(PathId.getId(rest.getRestContext()), rest, callback);   
  }

  @Override
  public void getCoursesClass(RestCourse rest, MethodCallback<DomCoursesOfSchoolClass> callback) {
   helper.getCoursesClass(PathId.getId(rest.getRestContext()), rest, callback);   
  }

  @Override
  public void getCoursesClass(RestScoContext rest,
      MethodCallback<DomCoursesOfSchoolClass> callback) {
   helper.getCoursesClass(PathId.getId(rest.getRestContext()), rest, callback);
  }
}
