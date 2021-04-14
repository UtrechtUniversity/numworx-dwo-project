package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;

public interface SecuredTeacherStudentModelRestCaller extends RestService {

  @Path("/sec:{id}/teacher/studentmodel/getLRS")
  @PUT
  void getLRS(@PathParam("id") String id, RestContext rest, MethodCallback<DomLRS> callback);
  
  @Path("/sec:{id}/teacher/studentmodel/getReducedList")
  @PUT
  void getReducedList(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudentModelContext>> callback);
}
