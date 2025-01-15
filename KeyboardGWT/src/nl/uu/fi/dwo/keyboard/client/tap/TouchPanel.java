package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.web.bindery.event.shared.HandlerRegistrations;

public class TouchPanel extends FocusPanel {

	public HandlerRegistration addLongTapHandler(LongTapHandler handler) {
		return addHandler(handler, LongTapEvent.getType());
	}

	public HandlerRegistration addTapHandler(TapHandler handler) {
		return addHandler(handler, TapEvent.getType());
	}
	
	public HandlerRegistration addTouchHandler(TouchHandler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration x = 
				HandlerRegistrations.compose(
						addTouchStartHandler(handler),
						addTouchMoveHandler(handler),
						addTouchEndHandler(handler),
						addTouchCancelHandler(handler));
		return x::removeHandler;
		
	}

}
