package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface PublicProfileRestCaller extends RestService {

	@GET
    @Path("/public/profile/get/{id}")
    public void get(@PathParam("id") String id, MethodCallback<DomDwoProfileFull> callback);

}
