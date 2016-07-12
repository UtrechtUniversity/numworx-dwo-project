package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.interaction.client.event.OpenAjaxEventBus;
import nl.uu.fi.dwo.mobile.client.text.MC2Text;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.views.ScoreNavMC2;
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
		return "8-dot-mc2dme.appspot.com";
	}

	@Override
	public Logging getLogging() {
		return LaTransport.newJSInstance();
	}

	@Override
	public EventBus getEventBus() {
		return OpenAjaxEventBus.getManagedInstance();
	}	
	public String getHost() {
		if(!GWT.isProdMode()) 
			return getDefaultHost();
		return Window.Location.getHost();
	}

	public OpdrNav.Prepare getPrepareInstance() {
		return new OpdrNav.MC2Prepare();
	}

	@Override
	public ScoreNavIF getScoreNav() {
		return new ScoreNavMC2();
	}

	@Override
	public Text getTextBundle() {
		return GWT.create(MC2Text.class);
	}

}
