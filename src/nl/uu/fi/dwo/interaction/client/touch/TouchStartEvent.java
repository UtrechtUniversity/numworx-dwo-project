package nl.uu.fi.dwo.interaction.client.touch;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;

public class TouchStartEvent extends com.google.gwt.event.dom.client.TouchStartEvent {

	private HumanInputEvent<?> event;
	private List<Touch> touches;

	public TouchStartEvent(MouseDownEvent event) {
		Touch t = new Touch(event.getClientX(), event.getClientY());
		this.touches = Collections.singletonList(t);
		this.event = event;
	}

	public TouchStartEvent(
			com.google.gwt.event.dom.client.TouchStartEvent event2) {
		com.google.gwt.dom.client.Touch t = event2.getTouches().get(0);
		this.touches = Collections.singletonList(new Touch(t.getClientX(), t.getClientY()));
		this.event = event2;
	}

	public List<Touch> touches() {
		return touches;
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
