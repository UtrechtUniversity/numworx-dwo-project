package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.dwokb.TriforkFormuleKeyboard;

import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

public class TriforkParameters extends DWOplayerDefaults {
	@Override
	public HeaderCss headercss() {
		return  DWOplayer.DWO_BUNDLE.headercss();
	}

	@Override
	public StatusBarIF getStatusBar() {
		return new TriforkFormuleKeyboard();
	}

}
