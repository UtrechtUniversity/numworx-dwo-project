package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.interaction.client.event.OpenAjaxEventBus;
import nl.uu.fi.dwo.mobile.utils.LaTransport;
import nl.uu.fi.dwo.mobile.utils.Logging;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

public class DWOPlayerMC2 extends DWOplayerDefaults implements DWOplayerParameters {

	public DWOPlayerMC2() {
		String host = getHost();
		String http = Window.Location.getProtocol();
		launchData = http +"//"
				+ host
				+ "/getJSONLaunchDataBytes?s=";
	}

	public String getDefaultHost() {
		return "mc2dme.appspot.com";
	}
	@Override
	public String getStubView() {
		if(!GWT.isProdMode())
			return "";
		return "/dwo/apps/";
	}

	@Override
	public Logging getLogging() {
		return LaTransport.newInstance();
	}

	@Override
	public EventBus getEventBus() {
		return OpenAjaxEventBus.getManagedInstance();
	}	
}
