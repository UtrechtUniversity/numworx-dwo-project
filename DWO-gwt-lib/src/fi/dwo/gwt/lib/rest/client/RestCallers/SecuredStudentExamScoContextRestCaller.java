package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentExamScoContextRestCaller implements ScoContextRestCaller {

  interface Helper extends RestService {
	@PUT
    @Path("/sec:{id}/student/exam/scoContext/get")
    public void get(@PathParam("id") String id, RestScoContext restScoContext, MethodCallback<DomScoContext> callback);

	@PUT
	@Path("/sec:{id}/student/exam/scoContext/getScos")
	public void getScos(@PathParam("id") String id, RestCourse restCourse, MethodCallback<List<DomScoContext>> callback);
  }

  final Helper helper = GWT.create(Helper.class);
 
  @Override
  public void get(RestScoContext rest, MethodCallback<DomScoContext> callback) {
    helper.get(PathId.getId(rest.getRestContext()), rest, callback);    
  }

  @Override
  public void getScos(RestCourse rest, MethodCallback<List<DomScoContext>> callback) {
    helper.getScos(PathId.getId(rest.getRestContext()), rest, callback);
  }
}
