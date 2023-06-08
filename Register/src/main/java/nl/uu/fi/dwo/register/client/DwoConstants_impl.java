package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.DwoConstants;

public class DwoConstants_impl implements DwoConstants {

	private String getHost() {
		return Window.Location.getHost();
	}


	@Override
	public String server() {
		String host = getHost();
		String http = Window.Location.getProtocol();
		return http + "//" + host + "/dwo/rest/";
	}

}
