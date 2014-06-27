package nl.uu.fi.dwo.mobile.client;

import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

/**
 * Module parameters. Waar staat wat.
 * @author velth101
 *
 */

public interface DWOplayerParameters {
	/** 
	 * Locatie van de StubView HTML bestanden.
	 * @return URL 
	 */
	String getStubView();
	/**
	 * Locatie van de getLaunchData server
	 * @return prefix
	 */
	String getLaunchData();
	/**
	 * Locatie van resources.
	 * @return full resource url
	 */
	
	String getResource(String resource);
	
	/**
	 * 
	 */
	void keyboardSetup();
	/**
	 * 
	 * @return StyleDependentName
	 */
	String keyboardStyle();
	/**
	 * Header style
	 */
	HeaderCss headercss();
// More to come....
}
