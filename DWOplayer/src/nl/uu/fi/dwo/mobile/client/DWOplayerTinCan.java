package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;
//import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavPanel;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.NoLogging;

public class DWOplayerTinCan extends WiskOpdrDefaults {

	public DWOplayerTinCan() {
		super(null);
	}

	@Override
	public String getStubView() {
			return "";
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.DWOplayerNoordhoff#getLaunchData()
	 */
	@Override
	public String getLaunchData() {
		return null;
	}

	@Override
	public String getCDN() {
		return getHost();
	}

	@Override
	public String keyboardStyle() {
		return "noordhoff";
	}

	@Override
	public StatusBarIF getStatusBar(ActivityComponent a) {
		return new nl.uu.fi.dwo.mobile.client.ui.noordhoffkb.NoordhoffKeyboard();
	}
	@Override
	public ScoreNavIF getScoreNav(ActivityComponent a) {
		return new ScoreNavPanel(a, null);
	}

//	private HeaderCss headercss() {
//		NoordhoffPlayerClientBundle bundle = GWT.create(NoordhoffPlayerClientBundle.class);
//		return  bundle.headercss();
//	}

	@Override
	public String getDwoEnv() {
		return "tincan";
	}

}
