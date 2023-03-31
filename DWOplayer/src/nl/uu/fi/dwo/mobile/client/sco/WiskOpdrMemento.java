package nl.uu.fi.dwo.mobile.client.sco;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;

public class WiskOpdrMemento extends Memento {

	public WiskOpdrMemento(ActivityComponent activity, Scorm2004IF api) {
		super(activity, api);
	}

	@Override
	public void onClose(CloseEvent<Window> event) {
		close();
	}

	@Override
	public void onWindowClosing(ClosingEvent event) {
		close();
	}

	final static String GOTO_URL = "dme.goto_url";
	@Override
	public void gotoUrl(String href) {
		boolean result = setValue(GOTO_URL, href);
		if (result) {
			close();
		}
	}


}
