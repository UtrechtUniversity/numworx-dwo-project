package nl.uu.fi.dwo.mobile.client;

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
	
// More to come....
}
