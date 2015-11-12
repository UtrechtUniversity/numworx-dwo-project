package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.NavigationCss;
import nl.uu.fi.dwo.mobile.client.ui.NavigationBundle;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextAndroid;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextDefault;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextIpad;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.views.ScoreNavFacade;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.NoLogging;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.OsDetection;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeAndroid;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeAndroidTablet;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeIPad;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeIPadRetina;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeIPhone;
import com.googlecode.mgwt.ui.client.theme.base.MGWTClientBundleBaseThemeRetina;

public class DWOplayerDefaults implements DWOplayerParameters {

	@Override
	public String getStubView() {
		if(GWT.isProdMode())
			return "/dwo/apps/";
		return "";
	}

//	@Override
//	public String getLaunchData() {
//		return "https://ws.fisme.science.uu.nl/DWOmAccess/getJSONLaunchDataBytes?s=";
//	}

	@Override
	public String getResource(String resource) {
		return getStubView() + resource;
	}

	@Override
	public String keyboardStyle() {
		return "dwo";
	}

	@Override
	public HeaderCss headercss() {
		return MGWTStyle.getTheme().getMGWTClientBundle().getHeaderCss();
	}

	private NavigationBundle navigationBundle;
	protected String launchData;
	
	@Override
	public NavigationCss navigationcss() {		
		if(navigationBundle == null) {
		    OsDetection detection = MGWT.getOsDetection();

		    if (detection.isAndroidPhone()) {
		    	navigationBundle = GWT.create(NavigationTextAndroid.class);
		    }

		    if (detection.isAndroidTablet()) {
		    	navigationBundle = GWT.create(NavigationTextAndroid.class);
		    }

		    if (detection.isIPhone()) {
		        if (detection.isRetina()) {
		        	navigationBundle = GWT.create(NavigationTextIpad.class);
		        } else {
		        	navigationBundle = GWT.create(NavigationTextIpad.class);
		        }
		      }

		      if (detection.isIPad()) {
		        if (detection.isIPadRetina()) {
		        	navigationBundle = GWT.create(NavigationTextIpad.class);
		        } else {
		        	navigationBundle = GWT.create(NavigationTextIpad.class);
		        }
		      }
		    
		    
			if(navigationBundle == null)
				navigationBundle = GWT.create(NavigationTextDefault.class);
		}
		NavigationCss navigationcss = navigationBundle.navigationcss();
		navigationcss.ensureInjected();
		return navigationcss;
	}

	@Override
	public boolean isNavTitle() {
		return false;
	}
	
	@Override
	public int getWindowHeight() {
		return Window.getClientHeight();
	}

	@Override
	public StatusBarIF getStatusBar() {
		return new nl.uu.fi.dwo.mobile.client.ui.dwokb.DWOKeyboard();

		//return new nl.uu.fi.dwo.mobile.client.ui.dwokb.FormuleKeyboard();
	}

	@Override
	public ScoreNavIF getScoreNav() {
		return new ScoreNavFacade();
	}
	
	@Override
	public Logging getLogging() {
		return NoLogging.instance;
	}

	public DWOplayerDefaults() {
		super();
		String host = getHost();
		String http = Window.Location.getProtocol();
		launchData = http +"//"
				+ host
				+ "/DWOmAccess/getJSONLaunchDataBytes?s=";
	}

	protected String getDefaultHost() {
		return "ws.fisme.science.uu.nl";
	}
	
	public String getHost() {
//		if(GWT.isProdMode()) 
//			return Window.Location.getHost();
		return getDefaultHost();
	}

	@Override
	public String getLaunchData() {
		return launchData;
	}

	@Override
	public EventBus getEventBus() {
		return new SimpleEventBus();
	}
}
