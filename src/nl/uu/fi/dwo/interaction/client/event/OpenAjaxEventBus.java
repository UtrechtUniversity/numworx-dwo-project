package nl.uu.fi.dwo.interaction.client.event;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class OpenAjaxEventBus extends EventBus {
	
	private Hub hub;
		
	static class Hub extends JavaScriptObject {
		native void publish(String topic, JavaScriptObject data) /*={
			this.publish(topic, data)
		}-*/;
		native Object subscribe(String topic, CallBack callback) /*-{
			return this.subscribe(topic, function(topic, pdata, data) {
				data.@nl.uu.fi.dwo.interaction.client.event.OpenAjaxEventBus.CallBack::call(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(topic, pdata)
			}, null, data )
		}-*/;
		native void unsubscribe(Object o) /*-{
				this.unsubscribe(o);
		}-*/;
	}
	
	static class CallBack {
		CBookEventListener listener;
		
		void call(String topic, JavaScriptObject jso) {
			JSONObject j = new JSONObject(jso);
			ObjectMap map = JSONUtilities.wrapMap(j);
			CBookEvent event = new CBookEvent(map);
			listener.acceptCBookEvent(event);
		}
		CallBack(CBookEventListener listener) {
			this.listener = listener;
		}
	}

// get the unmanaged hub.
	static native JavaScriptObject getHub() /*-{
		return $wnd.OpenAjax.hub;
	}-*/;
	
	
	public static EventBus getUnManagedInstance() {
		OpenAjaxEventBus bus = new OpenAjaxEventBus();
		bus.hub = getHub().cast();
		return bus;
	}
	
	public static EventBus getManagedInstance() {
		OpenAjaxEventBus bus = new OpenAjaxEventBus();
		bus.hub = getHub().cast();
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
		final Object o  = hub.subscribe(topic, callback);
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
