package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.entities.RestClassCourseFull;

public interface SecuredTeacherClassCourseRestCaller extends RestService {

  @Path("/sec:{id}/teacher/classcourse/update")
  @PUT
  void update(@PathParam("id") String id, RestClassCourseFull rest, MethodCallback<DomClassCourseFull> callback);
}
