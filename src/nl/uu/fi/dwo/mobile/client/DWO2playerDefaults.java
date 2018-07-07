package nl.uu.fi.dwo.mobile.client;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.DwoConstants;
import nl.uu.fi.dwo.mobile.client.sco.StudentModelLogger;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.LoggingProvider;

public class DWO2playerDefaults extends DWOplayerDefaults implements DwoConstants {

	public DWO2playerDefaults() {
		super(null);
		String host = getHost();
		String http = Window.Location.getProtocol();
		launchData = http +"//"
				+ host
				+ "/dwo/rest/public/scoData/getJSONLaunchDataBytes?scoId=";
		courseDescription = http + "//"
				+ host
				+ "/dwo/rest/public/course/getCourseDescription?courseId=";		
	}

	@Override
	public String getResource(String resource) {
		String base = GWT.getModuleBaseURL() + "../" + resource;
		return base;
	}

	public String getHost() {
//		if(GWT.isProdMode()) 
			return Window.Location.getHost();
//		return getDefaultHost();
	}

//	private String getDefaultHost() {
//		return "dummytwo.dwo.nl";
//	}

	@Override
	public String server() {
		String host = getHost();
		String http = Window.Location.getProtocol();
		return http + "//" + host + "/dwo/rest/";
	}

	@Override
	public String getCDN() {
		return "cdn.dwo.nl";
	}

	private static native String getSecureMode0() /*-{
		return $wnd.SECURE_MODE;
	}-*/;
	
	@Override
	public String getDwoEnv() {
		String parameter = Window.Location.getParameter("dwo_env");
		if(parameter == null) 
			return super.getDwoEnv();
        return parameter;
	}
	
	
	private SecureMode secureMode = SecureMode.NORMAL;
	{ 
		try {
			secureMode = SecureMode.valueOf(getSecureMode0());
		} catch (Exception e) {
		} 
	}
	
	@Override
	public SecureMode getSecureMode() {
		return secureMode;
	}

	public Provider<Logging> loggingProvider = new StudentModelLogger.Provider();
			//GWT.create(LoggingProvider.class);
	
	@Override
	public Logging getLogging() {
		return loggingProvider.get();
	}

	
}
