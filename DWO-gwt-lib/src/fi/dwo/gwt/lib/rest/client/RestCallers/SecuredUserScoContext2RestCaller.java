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

public class SecuredUserScoContext2RestCaller implements ScoContextRestCaller {

	interface Helper extends RestService {
		@PUT
	    @Path("/sec:{id}/user/scoContext/get")
	    public void get(@PathParam("id") String id, RestScoContext restScoContext, MethodCallback<DomScoContext> callback);
	
		@PUT
		@Path("/sec:{id}/user/scoContext/getScos")
		public void getScos(@PathParam("id") String id, RestCourse restCourse, MethodCallback<List<DomScoContext>> callback);
	}

	private static Helper service = GWT.create(Helper.class);
	
	@Override
	public void get(RestScoContext restScoContext, MethodCallback<DomScoContext> callback) {
		service.get(PathId.getId(restScoContext.getRestContext()),restScoContext, callback);
	}

	@Override
	public void getScos(RestCourse restCourse, MethodCallback<List<DomScoContext>> callback) {
		service.getScos(PathId.getId(restCourse.getRestContext()), restCourse, callback);
	}
}
