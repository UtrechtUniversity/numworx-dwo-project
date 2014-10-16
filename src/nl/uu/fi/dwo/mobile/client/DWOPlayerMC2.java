package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public class DWOPlayerMC2 extends DWOplayerDefaults implements DWOplayerParameters {

	private String launchData;
	
	public DWOPlayerMC2() {
		String host = Window.Location.getHost();
		if(!GWT.isProdMode())
			host = "9-dot-mc2dme.appspot.com";

		launchData = "http://"
				+ host
				+ "/getJSONLaunchDataBytes?s=";
	}
		
	@Override
	public String getLaunchData() {
		return launchData;
	}

}
