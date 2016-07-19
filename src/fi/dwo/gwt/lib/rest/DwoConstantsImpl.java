package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;

public class DwoConstantsImpl implements DwoConstants {

	@Override
	public String server() {
		final String http = Window.Location.getProtocol();
		String host = "dummytwo.dwo.nl";
		// TODO if testing host = localhost:8888, localhost:8080
		
		return http
				+ "//"
				+ host
				+ "/dwo/rest/";
	}

}
