package nl.uu.fi.dwo.mobile.client;

import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

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
}
