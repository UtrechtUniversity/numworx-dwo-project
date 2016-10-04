package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.DwoConstants;

public class DWO2playerDefaults extends DWOplayerDefaults implements DwoConstants {

	public DWO2playerDefaults() {
		super(null);
		String host = getHost();
		String http = Window.Location.getProtocol();
		launchData = http +"//"
				+ host
				+ "/dwo/rest/public/scoData/getJSONLaunchDataBytes?scoId=";
		courseDescription = http + "//"
				+ host
				+ "/dwo/rest/public/course/getCourseDescription?courseId=";		
	}

	@Override
	public String getResource(String resource) {
		String base = GWT.getModuleBaseURL() + "../" + resource;
		return base;
	}

	public String getHost() {
//		if(GWT.isProdMode()) 
			return Window.Location.getHost();
//		return getDefaultHost();
	}

//	private String getDefaultHost() {
//		return "dummytwo.dwo.nl";
//	}

	@Override
	public String server() {
		String host = getHost();
		String http = Window.Location.getProtocol();
		return http + "//" + host + "/dwo/rest/";
	}

}
