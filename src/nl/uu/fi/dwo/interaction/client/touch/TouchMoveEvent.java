package nl.uu.fi.dwo.interaction.client.touch;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;

public class TouchMoveEvent extends com.google.gwt.event.dom.client.TouchMoveEvent {

	private List<Touch> touches;
	private HumanInputEvent<?> event;

	public TouchMoveEvent(MouseMoveEvent event) {
		this.event = event;
		touches = Collections.singletonList(new Touch(event.getClientX(), event.getClientY()));
	}

	public TouchMoveEvent(com.google.gwt.event.dom.client.TouchMoveEvent event) {
		this.event = event;
		final com.google.gwt.dom.client.Touch touch = event.getTouches().get(0);
		touches = Collections.singletonList(new Touch(touch.getClientX(), touch.getClientY()));
	}

	public List<Touch> touches() {
		return this.touches;
	}

	public void preventDefault() {
		event.preventDefault();
	}

	public void stopPropagation() {
		event.stopPropagation();
	}

	public Object getSource() {
		return event.getSource();
	}

	
}
