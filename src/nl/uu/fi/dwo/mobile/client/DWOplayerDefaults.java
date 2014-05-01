package nl.uu.fi.dwo.mobile.client;

public class DWOplayerDefaults implements DWOplayerParameters {

	@Override
	public String getStubView() {
		return "";
	}

	@Override
	public String getLaunchData() {
		return "http://ws-dev.fisme.science.uu.nl/DWOmAccess/getJSONLaunchDataBytes?s=";
	}

}
