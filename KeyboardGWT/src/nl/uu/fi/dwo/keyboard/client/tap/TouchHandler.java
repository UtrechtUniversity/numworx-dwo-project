package nl.uu.fi.dwo.keyboard.client.tap;

import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;

public interface TouchHandler extends TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler {
	
}
