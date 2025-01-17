package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

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
	 * @return StyleDependentName
	 */
	String keyboardStyle();
	/**
	 * type of title
	 */
	boolean isNavTitle();
	
	
	//int getWindowHeight();
	StatusBarIF getStatusBar(ActivityComponent a);
	ScoreNavIF  getScoreNav(ActivityComponent a);

	String getHost();
// More to come....
//	String getCourseDescription();
	String getCDN();
	SecureMode getSecureMode();
	String getDwoEnv();
	default void tickle() {}
// SEB only
	default boolean inExam() {
		  return SecureMode.SEB == getSecureMode();
		}
// SEB and KIOSK
	default boolean inKiosk() {
		  return SecureMode.NORMAL != getSecureMode();
		}
// MGWT 
	boolean isDesktop();
}
