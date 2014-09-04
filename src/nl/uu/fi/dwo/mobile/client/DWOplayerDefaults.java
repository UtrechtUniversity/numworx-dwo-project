package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.NavigationCss;
import nl.uu.fi.dwo.mobile.client.ui.NavigationBundle;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextAndroid;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextDefault;
import nl.uu.fi.dwo.mobile.client.ui.NavigationTextIpad;

import com.google.gwt.core.shared.GWT;
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
		return "";
	}

	@Override
	public String getLaunchData() {
		return "http://dwo.fisme.science.uu.nl/DWOmAccess/getJSONLaunchDataBytes?s=";
	}

	@Override
	public String getResource(String resource) {
		return resource;
	}

	@Override
	public void keyboardSetup() {
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
}
