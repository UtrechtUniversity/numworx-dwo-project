package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyBoardButtons;

public class DWOplayerNoordhoff extends DWOplayerDefaults implements DWOplayerParameters {


	@Override
	public String getLaunchData() {
		if(GWT.isProdMode()) return  null;
		return  super.getLaunchData();
	}

	@Override
	public String getResource(String resource) {
		return "http://www.fisme.science.uu.nl/dwo/apps/noordhoff/" + resource;
	}

	@Override
	public String getStubView() {
		return super.getStubView();
	}

	@Override
	public void keyboardSetup() {
		FormuleKeyBoardButtons.setupWN();
	}

}
