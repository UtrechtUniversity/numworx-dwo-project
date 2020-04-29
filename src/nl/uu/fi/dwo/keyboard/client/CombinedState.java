package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

public interface CombinedState {
	HandlerRegistration addChangeHandler(ChangeHandler handler);
	void setCombined(Combined state);
	Combined getCombined();
	int getWidth();
}
