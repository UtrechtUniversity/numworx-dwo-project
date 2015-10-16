package nl.uu.fi.dwo.interaction.client.event;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class OpenAjaxEventBus extends EventBus {
	
	private Hub hub;
		
	final static class Hub extends JavaScriptObject {
		
		/**
		 * 
		 */
		protected Hub() {
			super();
		}

		final native void publish(String topic, JavaScriptObject data) /*-{
			this.publish(topic, data)
		}-*/;
		final native Object subscribe0(String topic, CallBack callback) /*-{
			return this.subscribe(topic, function(topic, pdata, data) {
				console.log("receiving " + topic)
				data.@nl.uu.fi.dwo.interaction.client.event.CallBack::call(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(topic, pdata)
			}, null, null, callback)
		}-*/;
		final native void unsubscribe(Object o) /*-{
				this.unsubscribe(o);
		}-*/;
	}
	
	// get the unmanaged hub.
	static native JavaScriptObject getHub() /*-{
		return $wnd.OpenAjax.hub;
	}-*/;
	// get the managed hub.
	static native JavaScriptObject getManagedHub() /*-{
		return $wnd.playerHub;
	}-*/;
	
	
	public static EventBus getUnManagedInstance() {
		OpenAjaxEventBus bus = new OpenAjaxEventBus();
		bus.hub = getHub().cast();
		return bus;
	}
	
	public static EventBus getManagedInstance() {
		OpenAjaxEventBus bus = new OpenAjaxEventBus();
		bus.hub = getManagedHub().cast();
		return bus;
		
	}
	
	private OpenAjaxEventBus() {
	}

	@Override
	public <H> HandlerRegistration addHandler(Type<H> type, H handler) {
		return null;
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(Type<H> type,
			Object source, H handler) {
		assert type == CBookEvent.TYPE;
		String topic = source.toString();
		CBookEventListener listener = (CBookEventListener) handler;
		CallBack callback = new CallBack(listener);
		final Object o  = hub.subscribe0(topic, callback);
		return new HandlerRegistration() {
			
			@Override
			public void removeHandler() {
				hub.unsubscribe(o);
			}
		};
	}

	@Override
	public void fireEvent(Event<?> event) {
		CBookEvent ev = (CBookEvent) event;
		fireEventFromSource(ev, event.getSource());
	}

	@Override
	public void fireEventFromSource(Event<?> event, Object source) {
		CBookEvent ev = (CBookEvent) event;
		String topic = source.toString();
		JavaScriptObject data = JSONUtilities.toJSONObject(ev.toObjectMap()).isObject().getJavaScriptObject();
		hub.publish(topic, data);
	}

}
