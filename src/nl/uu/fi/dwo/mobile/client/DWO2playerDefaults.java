package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class DWO2playerDefaults extends DWOplayerDefaults {

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

	public String getHost() {
		if(GWT.isProdMode()) 
			return Window.Location.getHost();
		return getDefaultHost();
	}

	private String getDefaultHost() {
		return "dummytwo.dwo.nl";
	}

}
