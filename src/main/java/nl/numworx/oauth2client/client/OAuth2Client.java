package nl.numworx.oauth2client.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.typedarrays.client.Uint16ArrayNative;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OAuth2Client implements EntryPoint {

	private static final String TOKEN = "/dwo/saml/login";
	
	static native private String getEndpoint() /*-{
		return $wnd.endpoint
	}-*/;
	static native private String getSearch() /*-{
		return $wnd.search
	}-*/;
	static native private String getHash() /*-{
		return $wnd.hash
	}-*/;
	
	
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
		Storage storage = Storage.getSessionStorageIfSupported();
		
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
				storage.clear();
				install(verifier);
				String endpoint = getEndpoint();
				String search = getSearch();
				String hash = getHash();
				Frame frame = new Frame(endpoint + "?a="+URL.encodeQueryString(code) + search + hash);
				frame.getElement().setAttribute("allow", "fullscreen");
				frame.setStylePrimaryName("iframe");
				RootLayoutPanel root = RootLayoutPanel.get();
				root.add(frame);
				root.setWidgetLeftWidth(frame, 0, Unit.PCT, 100, Unit.PCT);
				root.setWidgetTopHeight(frame, 0, Unit.PCT, 100, Unit.PCT);
				
//				Label label = new Label("Code = " + code);
//				RootPanel.get().add(label);
//				String verifier = storage.getItem("verifier");
//				//storage.clear();
//				label = new Label("verifier = " + verifier);
//				RootPanel.get().add(label);
//				UrlBuilder token = Window.Location.createUrlBuilder();
//				token.setPath(TOKEN);
//				String url = token.buildString();
//				String request = "code_verifier=" + verifier + "&authorization_code=" + code;
//
//				RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, url);
//				rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
//				try {
//					rb.sendRequest(request, new RequestCallback() {
//
//						@Override
//						public void onResponseReceived(Request request, Response response) {
//							String text = response.getText();
//							Label l = new Label(text);
//							RootPanel.get().add(l);
//							
//						}
//
//						@Override
//						public void onError(Request request, Throwable exception) {
//							String text = exception.toString();
//							Label l = new Label(text);
//							RootPanel.get().add(l);
//							
//							
//						}} );
//				} catch (RequestException e) {
//					GWT.log("send request", e);
//				}

				return;
			}
// initial
			UrlBuilder builder = Window.Location.createUrlBuilder();
			String verifier = randomString(64);
			storage.setItem("verifier", verifier);
			String state = randomString(64);
			storage.setItem("state", state);
			Map<String, List<String>> map = Window.Location.getParameterMap();
			for(String key: map.keySet()) builder.removeParameter(key); // keyset is a copy
			builder.setHash(null);
			String returnUrl = builder.buildString();

			
			Consumer<JavaScriptObject> consumer = new Consumer<JavaScriptObject>() {

				@Override
				public void accept(JavaScriptObject t) {
					Uint8Array bytes = Uint8ArrayNative.create(t);
					String challenge = btoa(OAuth2Client.toString(bytes));
					UrlBuilder token = Window.Location.createUrlBuilder();
					token.setPath(TOKEN);
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
	
	
	private native void install(String verifier) /*-{
		$wnd.getItem = function(key) {
			return verifier;
		}
	}-*/;
}
