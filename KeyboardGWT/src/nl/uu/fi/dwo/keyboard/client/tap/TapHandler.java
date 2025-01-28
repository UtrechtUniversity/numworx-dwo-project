package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.shared.EventHandler;

public interface TapHandler extends EventHandler {

	void onTap(TapEvent event);

}
