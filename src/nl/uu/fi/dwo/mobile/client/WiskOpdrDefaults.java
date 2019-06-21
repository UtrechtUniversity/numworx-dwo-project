package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.dwokb.NoStatusKeyboard;

public class WiskOpdrDefaults extends DWOplayerDefaults {
	@Override
	public String getResource(String resource) {
		String base = GWT.getModuleBaseURL() + "../" + resource;
		return base;
	}

	@Override
	public String getCDN() {
		return "www.dwo.nl";
	}

	public WiskOpdrDefaults() {
		super();
	}

	public WiskOpdrDefaults(String launchData) {
		super(launchData);
	}

	@Override
	public String getStubView() {
		return "";
	}
    /* (non-Javadoc)
     * @see nl.uu.fi.dwo.mobile.client.DWOplayerDefaults#getStatusBar()
     */
    @Override
    public StatusBarIF getStatusBar() {
        if ("none".equals(Window.Location.getParameter("footer")))
            return new NoStatusKeyboard();
        return super.getStatusBar();
    }

}
