package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.NavigationCss;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.utils.Logging;

import com.google.web.bindery.event.shared.EventBus;
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
	//void keyboardSetup();
	/**
	 * 
	 * @return StyleDependentName
	 */
	String keyboardStyle();
	/**
	 * Header style
	 */
	HeaderCss headercss();
	/**
	 * Navigational css 
	 */
	NavigationCss navigationcss();
	/**
	 * type of title
	 */
	boolean isNavTitle();
	
	
	int getWindowHeight();
	StatusBarIF getStatusBar();
	ScoreNavIF  getScoreNav();
	Logging getLogging();
	String getHost();
	EventBus getEventBus();
// More to come....
	OpdrNav.Prepare getPrepareInstance();
	String getCourseDescription();
	Text   getTextBundle();
	String getCDN();
}
