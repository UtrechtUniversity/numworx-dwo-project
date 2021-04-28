package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext4Student;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.entities.RestStudentModelScorePerTeacher;

public interface SecuredTeacherStudentModelRestCaller extends RestService {

  @Path("/sec:{id}/teacher/studentmodel/getLRS")
  @PUT
  void getLRS(@PathParam("id") String id, RestContext rest, MethodCallback<DomLRS> callback);
  
  @Path("/sec:{id}/teacher/studentmodel/getReducedList")
  @PUT
  void getReducedList(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudentModelContext>> callback);

  @PUT
  @Path("/sec:{id}/teacher/studentmodel/getReduced")
  void getStudentModel(@PathParam("id") String id, RestStudentModelContext rest, MethodCallback<DomStudentModelContext> callback);

  @PUT
  @Path("/sec:{id}/teacher/studentmodel/getForClass")
  void getStudentModelForClass(@PathParam("id") String id, RestStudentModelContextId rest, MethodCallback<DomStudentModelContext4Student> callback);


  @PUT
  @Path("/sec:{id}/teacher/studentmodel/updateForClass")
  void updateModelForClass(@PathParam("id") String id, RestStudentModelContext4Student rest, MethodCallback<Boolean> callback);
  
  @PUT
  @Path("/sec:{id}/teacher/studentmodel/getScores")
  void getScores(@PathParam("id") String id, RestStudentModelScorePerTeacher rest, MethodCallback<DomStudentModelScorePerTeacher> callback);
}
