package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.mobile.utils.LaTransport;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class DWOPlayerMC2LMS extends DWOPlayerMC2  {

	@Override
	public Logging getLogging() {
		return LaTransport.newInstance();
	}

	@Override
	public String getStubView() {
		if(!GWT.isProdMode())
			return "";
		return "/dwo/apps/";
	}

}
