package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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

public class Key extends CustomButton implements MouseDownHandler, MouseUpHandler, TouchStartHandler, TouchEndHandler, KeyPressHandler, KeyDownHandler, KeyUpHandler {

	public Key() {
		setStyleName("kbd-Key");
//		addMouseDownHandler(this);
//		addMouseUpHandler(this);
//		if ( TouchEvent.isSupported() ) {
//			addTouchStartHandler(this);
//			addTouchEndHandler(this);
//		}
		addKeyPressHandler(this);
		addKeyDownHandler(this);
		//addKeyUpHandler(this);
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

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if(event.getCharCode() == '\n' )
		{	event.stopPropagation();
			event.preventDefault();
			FocusOnTouch.instance.onKeyPress(event);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int c = event.getNativeKeyCode();
		if(c == 13) {
			FocusOnTouch.instance.enter(event);
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		event.preventDefault();
		event.stopPropagation();
	}

}
