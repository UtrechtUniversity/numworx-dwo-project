package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

@Deprecated
interface SecuredUserScoContextRestCaller extends RestService, ScoContextRestCaller {

	@PUT
    @Path("/sec:{id}/user/scoContext/get")
    public void get(@PathParam("id") String id, RestScoContext restScoContext, MethodCallback<DomScoContext> callback);

	@PUT
	@Path("/sec:{id}/user/scoContext/getScos")
	public void getScos(@PathParam("id") String id, RestCourse restCourse, MethodCallback<List<DomScoContext>> callback);

}
