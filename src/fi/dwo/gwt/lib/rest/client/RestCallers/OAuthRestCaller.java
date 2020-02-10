package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public interface OAuthRestCaller extends RestService {

	@POST
	@Path("/oauth2/token")
	void token(@FormParam("grant_type") String grantType, @FormParam("code") String code, @FormParam("refresh_token") String refreshToken, MethodCallback<DomToken> callback);
}
