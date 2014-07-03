package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

public class TriforkParameters extends DWOplayerDefaults {
	@Override
	public HeaderCss headercss() {
		return  DWOplayer.DWO_BUNDLE.headercss();
	}

}
