package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.shared.GwtEvent;

public class LongTapEvent extends GwtEvent<LongTapHandler> {

	private static final Type<LongTapHandler> TYPE = new Type<LongTapHandler>();

	/**
	 * Returns the type of the event
	 *
	 * @return the type of the event
	 */
	public static Type<LongTapHandler> getType() {
		return TYPE;
	}

	@Override
	public Type<LongTapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LongTapHandler handler) {
		handler.onLongTap(this);
	}


}
