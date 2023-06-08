package nl.numworx.uploadwidgetgwt.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;

import gwtupload.client.ISession.CORSSession;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class UploadSession extends CORSSession implements Constants {

	
	public static OpdrNavIF comRoot;
	
	@Override
	protected RequestBuilder createRequest(Method method, int timeout, String... params) {
		RequestBuilder builder = super.createRequest(method, timeout, params);
		ObjectMap context = comRoot.getContext();
		String authorization = context.getString(AUTHORIZATION);
		builder.setHeader(AUTHORIZATION, authorization);
		return builder;
	}

	UploadSession() {
		super();
	}

}
