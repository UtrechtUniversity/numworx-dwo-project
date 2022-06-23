package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.views.ScoreNavFacade;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public class DWOplayerDefaults implements DWOplayerParameters {

	@Override
	public String getStubView() {
		// voor test met lokale widgets en super dev mode onderstaande twee regels uitzetten
//		/*
		if(GWT.isProdMode())
			return "/dwo/apps/";
//		*/
		return "";
	}

	@Override
	public String getResource(String resource) {
		return getStubView() + resource;
	}

	@Override
	public String keyboardStyle() {
		return "dwo";
	}


	protected String launchData;//, courseDescription;
	
	@Override
	public boolean isNavTitle() {
		return false;
	}
	
	@Override
	public int getWindowHeight() {
		return Window.getClientHeight();
	}

	@Override
	public StatusBarIF getStatusBar(ActivityComponent a) {
		return new nl.uu.fi.dwo.mobile.client.ui.dwokb.DWOKeyboard(a);
	}

	@Override
	public ScoreNavIF getScoreNav(ActivityComponent a) {
		return new ScoreNavFacade(a);
	}
	
	
	DWOplayerDefaults(String launchData) {
		super();
		this.launchData = launchData;
	}

	public DWOplayerDefaults() {
		super();
		String host = getHost();
		String http = Window.Location.getProtocol();
//		launchData = http +"//"
//				+ host
//				+ "/DWOmAccess/getJSONLaunchDataBytes?s=";
//		courseDescription = http + "//"
//				+ host
//				+ "/DWOmAccess/getCourseDescription?c=";
		launchData = http +"//"
				+ host
				+ "/dwo/rest/public/scoData/getJSONLaunchDataBytes?scoId=";
//		courseDescription = http + "//"
//				+ host
//				+ "/dwo/rest/public/course/getCourseDescription?courseId=";		
	}
	
	public String getHost() {
		//if(GWT.isProdMode()) 
			return Window.Location.getHost();
		//return getDefaultHost();
	}

	@Override
	public String getLaunchData() {
		return launchData;
	}
	
		
	@Override
	public String getCDN() {
		return getHost();
	}

	@Override
	public SecureMode getSecureMode() {
		return SecureMode.NORMAL;
	}

	public String getDwoEnv() {
		return "unknown";
	}
}
