package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.OAuthRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public class OAuthManager {

	private OAuthRestCaller service = GWT.create(OAuthRestCaller.class);
		
	public Promise<DomToken> authorization_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.token("authorization_code", token, null, defer);
		return defer.getPromise();
	}
	
	public Promise<DomToken> refresh_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.token("refresh_token", null, token, defer);
		return defer.getPromise();
	}
	
}
