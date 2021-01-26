package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.entities.RestContext;

public interface SecuredTeacherStudentModelRestCaller extends RestService {

  @Path("/sec:{id}/teacher/studentmodel/getLRS")
  @PUT
  void getLRS(@PathParam("id") String id, RestContext rest, MethodCallback<DomLRS> callback);
}
