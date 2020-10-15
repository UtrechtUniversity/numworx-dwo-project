package nl.numworx.oauth2client.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OAuth2Client implements EntryPoint {

	private static final String TOKEN = "/dwo/saml/login";
	private Storage storage;
	
  	private static native String getToken0() /*-{
  		return $wnd.token
  	}-*/;
  	
  	private static String getToken() {
  		try {
  			return getToken0();
  		} catch(Exception e) {}
  		return TOKEN;
  	}
  
  
	static native private String getEndpoint0() /*-{
		return $wnd.endpoint
	}-*/;
	
	private String getEndpoint() {
	  String endpoint = storage.getItem("endpoint");
	  if (endpoint == null) {
	    endpoint = getEndpoint0();
	  }
	  return endpoint;
	}
	
	static native private String getSearch() /*-{
		return $wnd.search
	}-*/;
	static native private String getHash0() /*-{
		return $wnd.hash
	}-*/;
	
	private String getHash() {
	  String hash = storage.getItem("hash");
	  if (hash == null) {
	    hash = getHash0();
	  }
	  return hash;
	}
	
	
	static native private String getClientId() /*-{
		return $wnd.clientId
	}-*/;
	
	private String randomString(int length) {
		char[] possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		char[] result = new char[length];
		for(int i = 0; i < length; i++) {
			result[i] = possible[Random.nextInt(possible.length)];
		}
		return new String(result);
	}
	
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		String clientId = getClientId();
		if(clientId.isEmpty()) {
            String endpoint = getEndpoint0();
            String search = getSearch().substring(1);
            String hash = getHash0();
			String url = endpoint + "?" + search + hash;
			insertFrame(url);
			return;
		}

		storage = Storage.getSessionStorageIfSupported();
		
		String code = Window.Location.getParameter("code");
		if (code != null) {
			String state = Window.Location.getParameter("state");
			String org   = storage.getItem("state");
			if (Objects.equals(state, org)) {
				storage.setItem("code", code);
			}
			UrlBuilder builder = Window.Location.createUrlBuilder();
			builder.removeParameter("code");
			builder.removeParameter("state");
			Window.Location.assign(builder.buildString());
			return;
		}
// no code parameter,
			code = storage.getItem("code");
			if (code != null)
			{
				String verifier = storage.getItem("verifier");
                String endpoint = getEndpoint();
                String search = getSearch();
                String hash = getHash();
                String redirect_uri = storage.getItem("redirect_uri");
				storage.clear();
				install(verifier,code, redirect_uri);
				code = "parent";
				String url = endpoint + "?a="+URL.encodeQueryString(code) + search + hash;
				insertFrame(url);
				return;
			}
// initial
			UrlBuilder builder = Window.Location.createUrlBuilder();
			String verifier = randomString(64);
			storage.setItem("verifier", verifier);
			String state = randomString(64);
			storage.setItem("state", state);
			storage.setItem("hash", getHash0());
			storage.setItem("endpoint", getEndpoint0());
			
			Map<String, List<String>> map = Window.Location.getParameterMap();
			for(String key: map.keySet()) builder.removeParameter(key); // keyset is a copy
			builder.setHash(null);
			String returnUrl = builder.buildString();
	        storage.setItem("redirect_uri", returnUrl);
	
			Consumer<JavaScriptObject> consumer = new Consumer<JavaScriptObject>() {

				@Override
				public void accept(JavaScriptObject t) {
					Uint8Array bytes = Uint8ArrayNative.create(t);
					String challenge = btoa(OAuth2Client.toString(bytes));
					UrlBuilder token = Window.Location.createUrlBuilder();
					token.setPath(getToken());
					token.setParameter("response_type", "code");
					token.setParameter("redirect_uri", returnUrl);
					token.setParameter("code_challenge", challenge);
					token.setParameter("code_challence_method", "S256");
					token.setParameter("state", state);
					token.setParameter("client_id", getClientId());
					Window.Location.assign(token.buildString());
				} };
			digest(verifier, consumer);
			return;
	}

	private void insertFrame(String url) {
		Frame frame = new Frame(url);
		frame.getElement().setAttribute("allow", "fullscreen");
		frame.setStylePrimaryName("iframe");
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(frame);
		root.setWidgetLeftWidth(frame, 0, Unit.PCT, 100, Unit.PCT);
		root.setWidgetTopHeight(frame, 0, Unit.PCT, 100, Unit.PCT);
	}

	
	private static native String btoa(String digest) /*-{
		return btoa(digest)
                .replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_')
	}-*/;
	
	private static String toString(Uint8Array bytes) {
		int length = bytes.length();
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++) sb.append(  (char) bytes.get(i));
		return sb.toString();
	}

	private static native JavaScriptObject digest(String codeVerifier, Consumer<JavaScriptObject> consumer) /*-{
            var digest = crypto.subtle.digest("SHA-256",
                new TextEncoder().encode(codeVerifier));
            digest.then( function(t) { consumer.@java.util.function.Consumer::accept(Ljava/lang/Object;)(t); });     
	}-*/;
	
	
	private native void install(String verifier, String code, String redirect_uri) /*-{
	    var params = { "code_verifier": verifier, "code": code, "redirect_uri": redirect_uri}
		$wnd.getItem = function(key) {
			return params[key];
		}
        $wnd.logout = function() {
            $wnd.location = "/dwo/saml/logout.jsp"
        }
	}-*/;
}
