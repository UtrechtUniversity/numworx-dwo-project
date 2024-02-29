package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public interface PublicProfileRestCaller extends RestService {

	@GET
    @Path("/public/profile/{id}")
    public void get(@PathParam("id") String id, MethodCallback<DomDwoProfileFull> callback);
	
	@PUT
	@Path("/public/profile/description")
	public void getDescription(RestDwoProfile rest, MethodCallback<JSONValue> callback);

	@GET
	@Path("/public/profile/description")
	public void getDescription(@QueryParam("id") Number id, PromiseCallback<JSONValue> result);

}
