package nl.uu.fi.dwo.keyboard.client.tap;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Timer;

class TouchCopy {

	  public static TouchCopy copy(Touch touch) {
	    return new TouchCopy(touch.getPageX(), touch.getPageY(), touch.getIdentifier());
	  }

	  private final int x;
	  private final int y;
	  private final int id;

	  public TouchCopy(int x, int y, int id) {
	    this.x = x;
	    this.y = y;
	    this.id = id;
	  }

	  public int getPageX() {
	    return x;
	  }

	  public int getPageY() {
	    return y;
	  }

	  public int getIdentifier() {
	    return id;
	  }
	}
/**
 * Propagate events
 *
 * @author Daniel Kurka
 *
 */
interface EventPropagator {
	/**
	 * fire the given event on the given source
	 *
	 * @param source the source to fire the event on
	 * @param event the event to fire
	 */
	void fireEvent(HasHandlers source, GwtEvent<?> event);
}
/**
 * Propagate events from a source. There is an issue on mobile webkit which gets
 * confused about events if an alert is shown from an event handler, see:
 * http://
 * blog.daniel-kurka.de/2012/05/mobile-webkit-alert-dialog-breaks-touch.html
 *
 * This class provides a workaround by propagating events with a
 * ScheduledCommand
 *
 * @author Daniel Kurka
 *
 */
class EventPropagatorMobileImpl implements EventPropagator {

	private static class SCommand implements ScheduledCommand {
		private final HasHandlers source;
		private final GwtEvent<?> event;

		public SCommand(HasHandlers source, GwtEvent<?> event) {
			this.source = source;
			this.event = event;
		}

		@Override
		public void execute() {
			source.fireEvent(event);
		}
	}

	@Override
	public void fireEvent(final HasHandlers source, final GwtEvent<?> event) {
		// see issue 135
		// http://code.google.com/p/mgwt/issues/detail?id=135
		Scheduler.get().scheduleDeferred(new SCommand(source, event));
	}
}

/**
 * A simple interface to make classes testable that require timed code execution
 *
 * @author Daniel Kurka
 *
 */
interface TimerExecutor {

	public void execute(CodeToRun codeToRun, int time);

	interface CodeToRun {
		void onExecution();
	}
}
/**
 * Execute code with a GWT timer
 *
 * @author Daniel Kurka
 *
 */
class TimerExecturGwtTimerImpl implements TimerExecutor {

	private static class InternalTimer extends Timer {

		private final CodeToRun codeToRun;

		public InternalTimer(CodeToRun codeToRun) {
			this.codeToRun = codeToRun;
		}

		@Override
		public void run() {
			codeToRun.onExecution();
		}
	}

	@Override
	public void execute(final CodeToRun codeToRun, int time) {
		new InternalTimer(codeToRun).schedule(time);
	}
}

/**
 * This class can recognize long taps
 *
 * @author Daniel Kurka
 */
public class LongTapRecognizer implements TouchHandler {

  public static final int DEFAULT_WAIT_TIME_IN_MS = 1500;
  public static final int DEFAULT_MAX_DISTANCE = 15;

  protected enum State {
    INVALID, READY, FINGERS_DOWN, FINGERS_UP, WAITING
  };

  protected State state;
  private final HasHandlers source;
  private final int numberOfFingers;
  private final int time;

  private List<TouchCopy> startPositions;
  private int touchCount;
  private final int distance;

  private TimerExecutor timerExecutor;

  private EventPropagator eventPropagator;

  private static EventPropagator DEFAULT_EVENT_PROPAGATOR;

  public LongTapRecognizer(HasHandlers source) {
    this(source, 1);
  }

  public LongTapRecognizer(HasHandlers source, int numberOfFingers) {
    this(source, numberOfFingers, DEFAULT_WAIT_TIME_IN_MS);
  }

  /**
   * Construct a LongTapRecognizer
   *
   * @param source the source on which to fire events on
   * @param numberOfFingers the number of fingers that should be detected
   * @param time the time the fingers need to touch
   */
  public LongTapRecognizer(HasHandlers source, int numberOfFingers, int time) {
    this(source, numberOfFingers, time, DEFAULT_MAX_DISTANCE);
  }

  /**
   * Construct a LongTapRecognizer
   *
   * @param source the source on which to fire events on
   * @param numberOfFingers the number of fingers that should be detected
   * @param time the time the fingers need to touch
   * @param maxDistance the maximum distance each finger is allowed to move
   */
  public LongTapRecognizer(HasHandlers source, int numberOfFingers, int time, int maxDistance) {

    if (source == null) {
      throw new IllegalArgumentException("source can not be null");
    }
    if (numberOfFingers < 1) {
      throw new IllegalArgumentException("numberOfFingers > 0");
    }

    if (time < 200) {
      throw new IllegalArgumentException("time > 200");
    }

    if (maxDistance < 0) {
      throw new IllegalArgumentException("maxDistance > 0");
    }

    this.source = source;
    this.numberOfFingers = numberOfFingers;
    this.time = time;
    this.distance = maxDistance;

    state = State.READY;
    startPositions = new ArrayList<>();
    touchCount = 0;
  }

  @Override
  public void onTouchStart(TouchStartEvent event) {

    JsArray<Touch> touches = event.getTouches();
    touchCount++;

    switch (state) {
      case INVALID:
        break;
      case READY:
        startPositions.add(TouchCopy.copy((touches.get(touchCount - 1))));
        state = State.FINGERS_DOWN;
        break;
      case FINGERS_DOWN:
        startPositions.add(TouchCopy.copy(touches.get(touchCount - 1)));
        break;
      case FINGERS_UP:
      default:
        state = State.INVALID;
        break;
    }

    if (touchCount == numberOfFingers) {
      state = State.WAITING;
      getTimerExecutor().execute(new TimerExecutor.CodeToRun() {

        @Override
        public void onExecution() {
          if (state != State.WAITING) {
            // something else happened forget it
            return;
          }

          getEventPropagator().fireEvent(source, new LongTapEvent(source, numberOfFingers, time, startPositions));
          reset();

        }
      }, time);
    }

    if (touchCount > numberOfFingers) {
      state = State.INVALID;
    }
  }

  @Override
  public void onTouchMove(TouchMoveEvent event) {
    switch (state) {
      case WAITING:
      case FINGERS_DOWN:
      case FINGERS_UP:
        // compare positions
        JsArray<Touch> currentTouches = event.getTouches();
        for (int i = 0; i < currentTouches.length(); i++) {
          Touch currentTouch = currentTouches.get(i);
          for (int j = 0; j < startPositions.size(); j++) {
            TouchCopy startTouch = startPositions.get(j);
            if (currentTouch.getIdentifier() == startTouch.getIdentifier()) {
              if (Math.abs(currentTouch.getPageX() - startTouch.getPageX()) > distance || Math.abs(currentTouch.getPageY() - startTouch.getPageY()) > distance) {
                state = State.INVALID;
                break;
              }
            }
            if (state == State.INVALID) {
              break;
            }
          }
        }

        break;

      default:
        state = State.INVALID;
        break;
    }
  }

  @Override
  public void onTouchEnd(TouchEndEvent event) {
    int currentTouches = event.getTouches().length();
    switch (state) {
      case WAITING:
        state = State.INVALID;
        break;

      case FINGERS_DOWN:
        state = State.FINGERS_UP;
        break;
      case FINGERS_UP:
        // are we ready?
        if (currentTouches == 0 && touchCount == numberOfFingers) {
          // fire and reset

          reset();
        }
        break;

      case INVALID:
      default:
        if (currentTouches == 0)
          reset();
        break;
    }
  }

  @Override
  public void onTouchCancel(TouchCancelEvent event) {
    state = State.INVALID;
    int currentTouches = event.getTouches().length();
    if (currentTouches == 0) {
      reset();
    }
  }

  protected void reset() {
    state = State.READY;
    touchCount = 0;
  }

  // Visible for testing
  TimerExecutor getTimerExecutor() {
    if (timerExecutor == null) {
      timerExecutor = new TimerExecturGwtTimerImpl();
    }
    return timerExecutor;
  }

  // Visible for testing
  EventPropagator getEventPropagator() {
    if (eventPropagator == null) {
      if (DEFAULT_EVENT_PROPAGATOR == null) {
        DEFAULT_EVENT_PROPAGATOR = new EventPropagatorMobileImpl();
      }
      eventPropagator = DEFAULT_EVENT_PROPAGATOR;
    }
    return eventPropagator;
  }
}
