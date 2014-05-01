package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;

public class DWOplayerScorm extends DWOplayerDefaults implements DWOplayerParameters {


	@Override
	public String getLaunchData() {
		if(GWT.isProdMode())
			return null;
		else return super.getLaunchData();
	}

}
