package nl.uu.fi.dwo.mobile.client;

import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.dwokb.NoStatusKeyboard;

@Singleton
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
    public StatusBarIF getStatusBar(ActivityComponent a) {
        if ("none".equals(Window.Location.getParameter("footer")))
            return new NoStatusKeyboard(a);
        return super.getStatusBar(a);
    }

	@Override
	public String getDwoEnv() {
		String env = Window.Location.getParameter("env");
		if (env != null) return env;
		return super.getDwoEnv();
	}
    
}
