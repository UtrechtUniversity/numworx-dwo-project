package nl.uu.fi.dwo.mobile.client;

public class DWOPlayerMC2 extends DWOplayerDefaults implements DWOplayerParameters {

	@Override
	public String getLaunchData() {
		return "http://9-dot-mc2dme.appspot.com/getJSONLaunchDataBytes?s=";
	}

}
