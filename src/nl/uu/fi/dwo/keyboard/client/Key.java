package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class Key extends CustomButton implements MouseDownHandler, MouseUpHandler {

	public Key() {
		setStyleName("kbd-Key");
		addMouseDownHandler(this);
		addMouseUpHandler(this);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		setDown(false);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		setDown(true);
	}

}
