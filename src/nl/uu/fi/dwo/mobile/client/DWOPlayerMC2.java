package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public class DWOPlayerMC2 extends DWOplayerDefaults implements DWOplayerParameters {

	private String launchData;
	
	public DWOPlayerMC2() {
		String host = Window.Location.getHost();
		String http = Window.Location.getProtocol();
		if(!GWT.isProdMode())
			host = "mc2dme.appspot.com";

		launchData = http +"//"
				+ host
				+ "/getJSONLaunchDataBytes?s=";
	}
		
	@Override
	public String getLaunchData() {
		return launchData;
	}
	@Override
	public String getStubView() {
		if(!GWT.isProdMode())
			return "";
		return "/dwo/apps/";
	}

	@Override
	public String getResource(String resource) {
		return getStubView() + super.getResource(resource);
	}

	
	
}
