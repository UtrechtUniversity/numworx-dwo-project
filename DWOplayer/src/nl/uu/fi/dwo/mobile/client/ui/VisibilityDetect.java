package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;

@Singleton 
public class VisibilityDetect implements MessageEventHandler, HasValueChangeHandlers<Boolean> {

	public static final String VISIBLE = "shown";
	public static final String INVISIBLE = "hidden";
	
	private EventBus bus;
	private boolean visible = true;

	@Inject VisibilityDetect(EventBus bus) {
		this.bus = bus;
		if (Actions.isAvailable()) {
			bus.addHandler(MessageEvent.TYPE, this);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean old = this.visible;
		this.visible = visible;
		ValueChangeEvent.fireIfNotEqual(this, old, visible);
	}

	@Override
	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		if (VISIBLE.equals(message) )
			setVisible(true);
		else if (INVISIBLE.equals(message)) 
			setVisible(false);
		
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		bus.fireEventFromSource(event, this);		
	}

	@Override
	public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		com.google.web.bindery.event.shared.HandlerRegistration wrap =  bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);		
		return wrap::removeHandler;
	}

	public com.google.web.bindery.event.shared.HandlerRegistration addValueChangeHandler(EventBus bus, ValueChangeHandler<Boolean> handler) {
		com.google.web.bindery.event.shared.HandlerRegistration wrap =  bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);		
		return wrap;
	}
	
	

}
