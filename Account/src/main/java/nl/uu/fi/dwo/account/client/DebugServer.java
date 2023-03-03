package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.DwoConstants;

public class DebugServer implements DwoConstants {

	@Override
	public String server() {
		String host = Window.Location.getHost();
		String http = Window.Location.getProtocol();
		return http + "//" + host + "/dwo/rest/";
	}

}
