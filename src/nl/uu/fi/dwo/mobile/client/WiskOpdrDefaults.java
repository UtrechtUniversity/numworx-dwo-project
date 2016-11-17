package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;

public class WiskOpdrDefaults extends DWOplayerDefaults {
	@Override
	public String getResource(String resource) {
		String base = GWT.getModuleBaseURL() + "../" + resource;
		return base;
	}

}
