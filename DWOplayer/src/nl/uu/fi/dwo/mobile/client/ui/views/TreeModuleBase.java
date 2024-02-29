package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class TreeModuleBase extends ResizeComposite implements HasResizeHandlers {

	
	public TreeModuleBase() {
	}

	@Override
	public void onResize() {
		super.onResize();
		int width = getOffsetWidth();
		int height = getOffsetHeight();
		ResizeEvent.fire(this, width, height);
	}

	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

}
