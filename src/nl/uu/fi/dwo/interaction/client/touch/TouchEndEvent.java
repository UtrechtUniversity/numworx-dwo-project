package nl.uu.fi.dwo.interaction.client.touch;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

public class TouchEndEvent extends com.google.gwt.event.dom.client.TouchEndEvent {

	private DomEvent event;

	public TouchEndEvent(MouseUpEvent event) {
		this.event = event;
	}
	public TouchEndEvent(com.google.gwt.event.dom.client.TouchEndEvent event) {
		this.event = event;
	}
	public Object getSource() {
		return event.getSource();
	}

}
