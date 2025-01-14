package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.shared.GwtEvent;

public class TapEvent extends GwtEvent<TapHandler> {

	private static final Type<TapHandler> TYPE = new Type<TapHandler>();

	public static Type<TapHandler> getType() {
		return TYPE;
	}

	@Override
	public Type<TapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TapHandler handler) {
		handler.onTap(this);
	}

}
