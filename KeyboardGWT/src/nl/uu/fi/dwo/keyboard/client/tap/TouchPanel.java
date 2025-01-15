package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.web.bindery.event.shared.HandlerRegistrations;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/*
 * Copyright 2010 Daniel Kurka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


class SimulatedTouch extends Touch {

	  public static native SimulatedTouch createTouch() /*-{
	    // need to native for GwtMockito to work
	    return {};
	  }-*/;

	  public native static JsArray<Touch> createTouchArray() /*-{
	    return [];
	  }-*/;

	  protected SimulatedTouch() {
	  }

	  public final native void setClientX(int clientX) /*-{
	    this.clientX = clientX;
	  }-*/;

	  public final native void setClientY(int clientY) /*-{
	    this.clientY = clientY;
	  }-*/;

	  public final native void setPageX(int pageX) /*-{
	    this.pageX = pageX;
	  }-*/;

	  public final native void setPageY(int pageY) /*-{
	    this.pageY = pageY;
	  }-*/;

	  public final native void setScreenX(int screenX) /*-{
	    this.screenX = screenX;
	  }-*/;

	  public final native void setScreenY(int screenY) /*-{
	    this.screenY = screenY;
	  }-*/;

	  public final native void setId(int touchId) /*-{
	    this.identifier = touchId
	  }-*/;
	}

/**
 * A simulated TouchMoveEvent that is actually a mouse move event.
 * <p>
 * This is used for testing in desktop browsers.
 *
 * @author Daniel Kurka
 */
class SimulatedTouchStartEvent extends TouchStartEvent {

  private final int clientX;
  private final int clientY;
  private final int pageX;
  private final int pageY;
  private int touchId;

  public SimulatedTouchStartEvent(MouseDownEvent event, int touchId) {
    this.touchId = touchId;
    clientX = event.getClientX();
    clientY = event.getClientY();
    pageX = event.getScreenX();
    pageY = event.getScreenY();
    setNativeEvent(event.getNativeEvent());
    setSource(event.getSource());
  }

  @Override
  public JsArray<Touch> getChangedTouches() {
    JsArray<Touch> array = SimulatedTouch.createTouchArray();
    SimulatedTouch touch = SimulatedTouch.createTouch();
    touch.setClientX(clientX);
    touch.setClientY(clientY);
    touch.setPageX(pageX);
    touch.setPageY(pageY);
    touch.setId(touchId);
    array.push(touch);
    return array;
  }

  @Override
  public JsArray<Touch> getTouches() {
    JsArray<Touch> array = SimulatedTouch.createTouchArray();
    SimulatedTouch touch = SimulatedTouch.createTouch();
    touch.setClientX(clientX);
    touch.setClientY(clientY);
    touch.setPageX(pageX);
    touch.setPageY(pageY);
    touch.setId(touchId);
    array.push(touch);
    return array;
  }
}
/**
 * A simulated TouchEndEvent that is actually a mouse up event.
 * <p>
 * This is used for testing in desktop browsers.
 *
 * @author Daniel Kurka
 */
class SimulatedTouchEndEvent extends TouchEndEvent {

  private final int clientX;
  private final int clientY;
  private final int pageX;
  private final int pageY;
  private int touchId;

  /**
   * Construct a simulated TouchEndEvent from a {@link MouseUpEvent}
   *
   * @param mouseUpEvent the data for the simulated event;
   * @param touchId
   * @param multiTouch
   */
  public SimulatedTouchEndEvent(MouseUpEvent mouseUpEvent, int touchId) {
    this.touchId = touchId;
    clientX = mouseUpEvent.getClientX();
    clientY = mouseUpEvent.getClientY();
    pageX = mouseUpEvent.getScreenX();
    pageY = mouseUpEvent.getScreenY();

    setNativeEvent(mouseUpEvent.getNativeEvent());
    setSource(mouseUpEvent.getSource());
  }

  @Override
  public JsArray<Touch> getChangedTouches() {
    JsArray<Touch> array = SimulatedTouch.createTouchArray();
    SimulatedTouch touch = SimulatedTouch.createTouch();
    touch.setClientX(clientX);
    touch.setClientY(clientY);
    touch.setPageX(pageX);
    touch.setPageY(pageY);
    touch.setId(touchId);
    array.push(touch);
    return array;
  }

  @Override
  public JsArray<Touch> getTouches() {
    return SimulatedTouch.createTouchArray();
  }
}

/**
 * A simulated TouchMoveEvent that is actually a mouse move event.
 * <p>
 * This is used for testing in desktop browsers.
 *
 * @author Daniel Kurka
 */
class SimulatedTouchMoveEvent extends TouchMoveEvent {

  private final int clientX;
  private final int clientY;
  private final int pageX;
  private final int pageY;
  private int touchId;

  public SimulatedTouchMoveEvent(MouseMoveEvent event, int touchId) {
    this.touchId = touchId;
    clientX = event.getClientX();
    clientY = event.getClientY();
    pageX = event.getScreenX();
    pageY = event.getScreenY();
    setNativeEvent(event.getNativeEvent());
    setSource(event.getSource());
  }

  @Override
  public JsArray<Touch> getChangedTouches() {
    JsArray<Touch> array = SimulatedTouch.createTouchArray();
    SimulatedTouch touch = SimulatedTouch.createTouch();
    touch.setClientX(clientX);
    touch.setClientY(clientY);
    touch.setPageX(pageX);
    touch.setPageY(pageY);
    touch.setId(touchId);
    array.push(touch);
    return array;
  }

  @Override
  public JsArray<Touch> getTouches() {
    JsArray<Touch> array = SimulatedTouch.createTouchArray();
    SimulatedTouch touch = SimulatedTouch.createTouch();
    touch.setClientX(clientX);
    touch.setClientY(clientY);
    touch.setPageX(pageX);
    touch.setPageY(pageY);
    touch.setId(touchId);
    array.push(touch);
    return array;
  }
}


/**
 * Convert TouchStartHandlers to mouse down handlers for non touch devices or dev mode
 *
 * @author Daniel Kurka
 */
class TouchStartToMouseDownHandler implements MouseDownHandler {

  private final TouchStartHandler handler;

  public static int lastTouchId = 0;

  public TouchStartToMouseDownHandler(TouchStartHandler handler) {
    this.handler = handler;
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    lastTouchId++;
    SimulatedTouchStartEvent simulatedTouchStartEvent = new SimulatedTouchStartEvent(event, lastTouchId);
    handler.onTouchStart(simulatedTouchStartEvent);
  }
}

/**
 * Convert TouchMoveHandlers to MouseMoveHandlers for non touch devices or dev
 * mode
 *
 * @author Daniel Kurka
 */
class TouchMoveToMouseMoveHandler implements MouseMoveHandler, MouseDownHandler, MouseUpHandler {

	private boolean ignoreEvent;
	private final TouchMoveHandler touchMoveHandler;

	public TouchMoveToMouseMoveHandler(TouchMoveHandler touchMoveHandler) {
		this.touchMoveHandler = touchMoveHandler;
		ignoreEvent = true;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (ignoreEvent)
			return;
		touchMoveHandler.onTouchMove(new SimulatedTouchMoveEvent(event, TouchStartToMouseDownHandler.lastTouchId));
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ignoreEvent = true;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		ignoreEvent = false;
	}
}

/**
 * Convert TouchEndHandlers to MouseUpHandlers for non touch devices or dev mode
 *
 * @author Daniel Kurka
 */
class TouchEndToMouseUpHandler implements MouseUpHandler {
  private final TouchEndHandler handler;

  public TouchEndToMouseUpHandler(TouchEndHandler handler) {
    this.handler = handler;
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
    SimulatedTouchEndEvent simulatedTouchEndEvent =
        new SimulatedTouchEndEvent(event, TouchStartToMouseDownHandler.lastTouchId);
    handler.onTouchEnd(simulatedTouchEndEvent);
  }
}


/**
 * Always touch. Convert mouse to touch. Convert touch to tap/longtap
 */
public class TouchPanel extends FocusPanel {

	public HandlerRegistration addLongTapHandler(LongTapHandler handler) {
		return addHandler(handler, LongTapEvent.getType());
	}

	public HandlerRegistration addTapHandler(TapHandler handler) {
		return addHandler(handler, TapEvent.getType());
	}
	
	public HandlerRegistration addTouchHandler(TouchHandler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration x;
		if (TouchEvent.isSupported()) {
			x = HandlerRegistrations.compose(
						addTouchStartHandler(handler),
						addTouchMoveHandler(handler),
						addTouchEndHandler(handler),
						addTouchCancelHandler(handler));
			
		} else {
			TouchMoveToMouseMoveHandler mover = new TouchMoveToMouseMoveHandler(handler);
			x = HandlerRegistrations.compose(
						addMouseDownHandler(new TouchStartToMouseDownHandler(handler)),
						addMouseMoveHandler(mover),addMouseUpHandler(mover), addMouseDownHandler(mover),
						addMouseUpHandler(new TouchEndToMouseUpHandler(handler)));
		}
		return x::removeHandler;		
	}


}
