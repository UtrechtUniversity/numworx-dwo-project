package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceController.DefaultDelegate;
import com.google.gwt.place.shared.PlaceController.Delegate;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

@Singleton
public class OnCloseDelegate extends DefaultDelegate implements Delegate, ClosingHandler {

	@Inject OnCloseDelegate() { }
	
	private boolean onClose;
	private ClosingHandler handler;

	/**
	 * @return the onClose
	 */
	public boolean isOnClose() {
		return onClose;
	}

	@Override
	public HandlerRegistration addWindowClosingHandler(ClosingHandler handler) {
		this.handler = handler;
		return super.addWindowClosingHandler(this);
	}

	@Override
	public void onWindowClosing(ClosingEvent event) {
		try {
			onClose = true;
			handler.onWindowClosing(event);
		} finally {
			onClose = false;
		}
	}
	
	
}
