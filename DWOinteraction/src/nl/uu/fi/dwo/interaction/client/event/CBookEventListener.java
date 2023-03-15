package nl.uu.fi.dwo.interaction.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CBookEventListener extends EventHandler {
	/**
	 * Accept the command and parameters of this event.
	 * @param event
	 */
	void acceptCBookEvent(CBookEvent event);
}
