package nl.uu.fi.dwo.mobile.client;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.DwoConstants;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
//import nl.uu.fi.dwo.mobile.client.sco.StudentModelLogger;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.dwokb.NoStatusKeyboard;
//import nl.uu.fi.dwo.mobile.utils.LaTransport;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.NoLogging;

public class DWO2playerDefaults extends DWOplayerDefaults implements DwoConstants {

	public static IdleDetect idle;

	public DWO2playerDefaults() {
		super(null);
		launchData =
				"/dwo/rest/public/scoData/getJSONLaunchDataBytes?scoId=";

	 //  loggingProvider = () -> LaTransport.newTAOinstance();

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

	private static native String getDwoEnv0() /*-{
		return $wnd.dwo_env
	}-*/;
	

	@Override
	public String getDwoEnv() {
		try {
			return getDwoEnv0();
		} catch (Exception e) {
			return super.getDwoEnv();
		}
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

	public Provider<Logging> loggingProvider = 
	    new SMLogger.Provider(	    
	      () -> NoLogging.instance);
	      //new StudentModelLogger.Provider());
			//GWT.create(LoggingProvider.class);
	
	@Override
	public Logging getLogging() {
		return loggingProvider.get();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.DWOplayerDefaults#getStatusBar()
	 */
	@Override
	public StatusBarIF getStatusBar() {
		if ("none".equals(Window.Location.getParameter("footer")))
			return new NoStatusKeyboard();
		return super.getStatusBar();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.DWOplayerParameters#tickle()
	 */
	@Override
	public void tickle() {
		idle.reset();
	}

	
	
}
