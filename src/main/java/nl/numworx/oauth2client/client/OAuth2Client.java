package nl.numworx.oauth2client.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OAuth2Client implements EntryPoint {

	private static final String TOKEN = "/dwo/saml/login";

	String endpoint = "/endpoint.html";
	
	
	
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
				
				Frame frame = new Frame(endpoint + "?a="+URL.encodeQueryString(code));
				RootPanel.get().add(frame);
				
				
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
			String verifier = "randomstring";
			storage.setItem("verifier", verifier);
			String state = "state";
			storage.setItem("state", state);
			Map<String, List<String>> map = Window.Location.getParameterMap();
			for(String key: map.keySet()) builder.removeParameter(key);
			builder.setHash(null);
			String returnUrl = builder.buildString();
			UrlBuilder token = Window.Location.createUrlBuilder();
			token.setPath(TOKEN);
			token.setParameter("response_type", "code");
			token.setParameter("redirect_url", returnUrl);
			token.setParameter("code_challenge", verifier);
			token.setParameter("code_challence_method", "plain");
			token.setParameter("state", state);
			Window.Location.assign(token.buildString());
			return;
	}



	private native void install(String verifier) /*-{
		$wnd.getItem = function(key) {
			return verifier;
		}
	}-*/;
}
