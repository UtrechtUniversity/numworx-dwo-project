package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.OAuthRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public class OAuthManager implements Success<DomToken, DomToken> {

	private OAuthRestCaller service = GWT.create(OAuthRestCaller.class);
    private GwtRestVars instance;
		
	public Promise<DomToken> authorization_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.token("authorization_code", token, null, defer);
		return defer.getPromise().then(this);
	}
	
	public Promise<DomToken> refresh_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.token("refresh_token", null, token, defer);
		return defer.getPromise().then(this);
	}

	@Override
	public Promise<DomToken> call(Promise<DomToken> resolved) throws Exception {
		String bearer = resolved.getValue().getAccess_token();
		instance.setBearerToken(bearer);
		
		return resolved;
	}
	
	public OAuthManager() {
        instance = GwtRestVars.instance();
	}
}
