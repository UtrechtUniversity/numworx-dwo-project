package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestNewUser;

public interface PublicUserRestCaller extends RestService {

	@PUT
    @Path("/public/user/submit")
    public void submitNewUser(RestNewUser restNewUser, MethodCallback<Boolean> callback);

}
