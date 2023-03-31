package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;



public class DWOplayerCDPLogica extends DWOplayerNoordhoff {
	@Override
	public String getResource(String resource) {
		//return "http://www.fisme.science.uu.nl/dwo/apps/noordhoff/" + resource;
		//return "http://cdplogica.toegang.nu/noordhoff/vo/fi/dwo/2014_v1_0/" + resource;
		return GWT.getModuleBaseURL() + "../" + resource;
	}

	@Override
	public String getStubView() {
			return "";
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.DWOplayerNoordhoff#getLaunchData()
	 */
	@Override
	public String getLaunchData() {
		return null;
	}

}
