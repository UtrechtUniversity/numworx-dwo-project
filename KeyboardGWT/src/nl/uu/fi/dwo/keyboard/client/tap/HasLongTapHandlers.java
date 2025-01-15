package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasLongTapHandlers {

	HandlerRegistration addLongTapHandler(LongTapHandler handler);

}
