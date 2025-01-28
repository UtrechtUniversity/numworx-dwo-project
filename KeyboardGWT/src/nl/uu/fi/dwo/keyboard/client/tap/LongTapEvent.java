package nl.uu.fi.dwo.keyboard.client.tap;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * A long tap event is produced if the user touches an area of the display for a
 * given time without moving his finger(s)
 *
 * @author Daniel Kurka
 *
 */
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

	private final List<TouchCopy> startPositions;
	private final int numberOfFingers;
	private final int time;

	/**
	 * Construct a LongTapEvent
	 *
	 * @param source - the source of the event
	 * @param numberOfFingers the number of fingers used
	 * @param time the time the fingers where touching
	 * @param startPositions the start position of each finger
	 */
	public LongTapEvent(Object source, int numberOfFingers, int time, List<TouchCopy> startPositions) {
		this.numberOfFingers = numberOfFingers;
		this.time = time;
		this.startPositions = startPositions;
		setSource(source);
	}

	@Override
	public Type<LongTapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LongTapHandler handler) {
		handler.onLongTap(this);
	}

	/**
	 * the number of fingers that created this event
	 *
	 * @return
	 */
	public int getNumberOfFingers() {
		return numberOfFingers;
	}

	/**
	 * the start position of all fingers
	 *
	 * @return the array of start positions
	 */
	public List<TouchCopy> getStartPositions() {
		return startPositions;
	}

	/**
	 * the time the user held the fingers
	 *
	 * @return the time in milliseconds
	 */
	public int getTime() {
		return time;
	}
}
