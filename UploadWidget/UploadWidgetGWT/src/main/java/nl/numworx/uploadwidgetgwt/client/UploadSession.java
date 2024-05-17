package nl.numworx.uploadwidgetgwt.client;

import java.io.IOException;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import gwtupload.client.ISession.CORSSession;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public class UploadSession extends CORSSession implements Constants {

	
	public static String authorization;
	public static String refresh_token;

	public static void setComRoot(OpdrNavIF comRoot) {
		ObjectMap context = comRoot.getContext();
		authorization = context.getString(AUTHORIZATION);
		refresh_token = context.getString(REFRESH_TOKEN);
	}
	
	@Override
	protected RequestBuilder createRequest(Method method, int timeout, String... params) {
		RequestBuilder builder = super.createRequest(method, timeout, params);
		builder.setHeader(AUTHORIZATION, authorization);
		return builder;
	}

	UploadSession() {
		super();
	}

	static void retry(Wrap callback) {
		RequestBuilder req = new RequestBuilder(RequestBuilder.POST, "/dwo/rest/oauth2/token");
		String data = "grant_type=refresh_token&refresh_token=" + refresh_token;
		req.setRequestData(data);
		req.setHeader("Content-Type", "application/x-www-form-urlencoded");
		req.setCallback(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				int code = response.getStatusCode();
				if (code == 200) {
					String data = response.getText();
					DomToken t = DomTokenCodec.toValue(data);
					authorization = t.getToken_type() + " " + t.getAccess_token(); // of zoiets
					refresh_token = t.getRefresh_token();
					callback.retry.run();
					return;
				}
				callback.onError(request, new IOException());
			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onError(request, exception);
			}});
		try {
			req.send();
		} catch (RequestException e) {
			callback.onError(null, e);
		}
	}

	public static RequestCallback wrap(Runnable run, RequestCallback delegate) {
		return new Wrap(run, delegate);
	}
	
	public static class Wrap implements RequestCallback {
		final Runnable retry;
		final RequestCallback delegate;
		
		private Wrap(Runnable retry, RequestCallback delegate) {
			this.retry = retry;
			this.delegate = delegate;
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			if (response.getStatusCode() == 401) {
				retry(this);
			} else {
				delegate.onResponseReceived(request, response);
			}		
		}

		@Override
		public void onError(Request request, Throwable exception) {
			delegate.onError(request, exception);
		}
		
	}
}
