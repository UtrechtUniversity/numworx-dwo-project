package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.EventHandler;

public interface MessageHandler extends EventHandler {
	public void onMessage(MessageEvent event);
}
