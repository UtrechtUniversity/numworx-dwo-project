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
	void refresh(@FormParam("grant_type") String grantType, @FormParam("refresh_token") String refreshToken, MethodCallback<DomToken> callback);

	@POST
    @Path("/oauth2/token")
    void client(@FormParam("grant_type") String grantType, @FormParam("client_id") String code, @FormParam("client_secret") String refreshToken, MethodCallback<DomToken> callback);

	@POST
	@Path("/oauth2/token")
	void authorize(@FormParam("grant_type") String grantType, 
			@FormParam("code") String code,
			@FormParam("client_id") String clientId,
			@FormParam("redirect_uri") String redirectUri,
			@FormParam("code_verifier") String verifier,
			MethodCallback<DomToken> callback);

}
