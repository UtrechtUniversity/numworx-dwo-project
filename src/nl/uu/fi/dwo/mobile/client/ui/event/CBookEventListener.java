package nl.uu.fi.dwo.mobile.client.ui.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public interface CBookEventListener extends EventHandler {
	/**
	 * Accept the command and parameters of this event.
	 * @param event
	 */
	void acceptCBookEvent(CBookEvent event);

	EventBus BUS = new SimpleEventBus();
}
