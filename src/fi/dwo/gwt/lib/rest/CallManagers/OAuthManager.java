package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.OAuthRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public class OAuthManager {

	private OAuthRestCaller service = GWT.create(OAuthRestCaller.class);
		
	private static native String clientId() /*-{
			$wnd.clientId = $wnd.parent.clientId
			return $wnd.parent.clientId;
	}-*/;

	private static native String redirectUri() /*-{
		return $wnd.parent.location.href.replace($wnd.parent.location.search, '');
	}-*/;
	
	private static native String codeVerifier0() /*-{
		$wnd.getItem = $wnd.parent.getItem
		return $wnd.parent.getItem("code_verifier");
	}-*/;
	private static String codeVerifier() {
		try {
			return codeVerifier0();
		} catch(Exception e) {
			return null;
		}
	}
	
	
	public Promise<DomToken> authorization_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.authorize("authorization_code", token, clientId(), redirectUri(), codeVerifier(), defer);
		return defer.getPromise();
	}
	
	public Promise<DomToken> refresh_token(String token) {
		PromiseCallback<DomToken> defer = new PromiseCallback<>();
		service.refresh("refresh_token", token, defer);
		return defer.getPromise();
	}
	
	public Promise<DomToken> client_credentials(String client, String secret) {
      PromiseCallback<DomToken> defer = new PromiseCallback<>();
      service.client("client_credentials", client, secret, defer);
      return defer.getPromise();
	  
	}
}
