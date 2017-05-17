package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

public interface PublicScoContextRestCaller extends RestService {

	@PUT
    @Path("/public/scoContext/get")
    public void get(RestScoContext restScoContext, MethodCallback<DomScoContext> callback);

	@PUT
	@Path("/public/scoContext/getScos")
	public void getScos(RestCourse restCourse, MethodCallback<List<DomScoContext>> callback);
	
}
