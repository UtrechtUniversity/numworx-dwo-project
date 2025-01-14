package nl.uu.fi.dwo.mobile.client;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

public class TriforkParameters extends DWOplayerDefaults {

	@Override
	public StatusBarIF getStatusBar(ActivityComponent a) {
		return new nl.uu.fi.dwo.mobile.client.ui.dwokb.TriforkFormuleKeyboard(a);
	}

}
