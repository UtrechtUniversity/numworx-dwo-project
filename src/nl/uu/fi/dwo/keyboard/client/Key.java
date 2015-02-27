package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class Key extends CustomButton implements MouseDownHandler, MouseUpHandler, TouchStartHandler, TouchEndHandler {

	public Key() {
		setStyleName("kbd-Key");
		addMouseDownHandler(this);
		addMouseUpHandler(this);
		if ( TouchEvent.isSupported() ) {
			addTouchStartHandler(this);
			addTouchEndHandler(this);
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		setDown(false);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		setDown(true);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		setDown(false);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		setDown(true);
	}

}
