package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

import nl.uu.fi.dwo.mobile.DWOplayer;
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
		return "https://ws.fisme.science.uu.nl/dwo/apps/noordhoff/" + resource;
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

	@Override
	public HeaderCss headercss() {
		return  DWOplayer.DWO_BUNDLE.headercss();
	}

	@Override
	public boolean isNavTitle() {
		return true;
	}
	
//	@Override
//	public int getWindowHeight() {
//		return 426;
//	}

	@Override
	public StatusBarIF getStatusBar() {
		return new nl.uu.fi.dwo.mobile.client.ui.noordhoffkb.NoordhoffKeyboard();
	}

	@Override
	public String getStubView() {
		if(!GWT.isProdMode())
			return "";
		return "../";
	}

	@Override
	public ScoreNavIF getScoreNav() {
		return new ScoreNavPanel();
	}

}
