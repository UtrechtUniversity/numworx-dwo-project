package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;
//import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;
//import com.googlecode.mgwt.ui.client.widget.header.HeaderAppearance;
//import com.googlecode.mgwt.ui.client.widget.header.HeaderPanel;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavPanel;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.dwokb.FormuleKeyBoardButtons;
import nl.uu.fi.dwo.mobile.client.ui.views.ScoreNavFacade;

public class DWOplayerNoordhoff extends DWOplayerDefaults implements DWOplayerParameters {


	@Override
	public String getLaunchData() {
		//if(GWT.isProdMode()) return  null;
		return  super.getLaunchData();
	}

	@Override
	public String getResource(String resource) {
		return "https://www.dwo.nl/apps/noordhoff/" + resource;
		//return "http://cdplogica.toegang.nu/noordhoff/vo/fi/dwo/2014_v1_0/" + resource;
	}


//	@Override
//	public void keyboardSetup() {
////		FormuleKeyBoardButtons.setupWN();
//	}

	@Override
	public String keyboardStyle() {
		return "noordhoff";
	}

//	private HeaderCss headercss() {
//		NoordhoffPlayerClientBundle bundle = GWT.create(NoordhoffPlayerClientBundle.class);
//		return  bundle.headercss();
//	}
	
//	private HeaderAppearance headercss() {
////		NoordhoffPlayerClientBundle bundle = GWT.create(NoordhoffPlayerClientBundle.class);
//		return HeaderPanel.DEFAULT_APPEARANCE; // nog geen customizatie
//	}
	

	@Override
	public boolean isNavTitle() {
		return true;
	}
	
//	@Override
//	public int getWindowHeight() {
//		return 426;
//	}

	@Override
	public StatusBarIF getStatusBar(ActivityComponent a) {
		return new nl.uu.fi.dwo.mobile.client.ui.noordhoffkb.NoordhoffKeyboard(a);
	}

	@Override
	public String getStubView() {
		if(!GWT.isProdMode())
			return "";
		return "../";
	}

	@Override
	public ScoreNavIF getScoreNav(ActivityComponent a) {
		return new ScoreNavPanel(a, null);
	}

}
