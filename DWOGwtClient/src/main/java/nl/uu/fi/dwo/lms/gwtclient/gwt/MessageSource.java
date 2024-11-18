package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

@Singleton
public class MessageSource {
	
	private final EventBus bus;
	private final MessageEvent fly = new MessageEvent(); // flyweight pattern

	@Inject MessageSource(EventBus bus) {
		this.bus = bus;
		injectEventListener(this);
	}
	
    private static native void injectEventListener(MessageSource p) /*-{
	    function postMessageListener(e) {
	        p.@nl.uu.fi.dwo.lms.gwtclient.gwt.MessageSource::onMessage(Ljava/lang/String;Ljava/lang/String;)(e.data, e.source.location.href); // call function with the name
	    }
	    // Listen to message from child window
	    if (window.addEventListener) {
	        // "Normal" browsers
	        $wnd.addEventListener("message", postMessageListener, false);
	    } else {
	        // fucking IE
	        $wnd.attachEvent("onmessage", postMessageListener, false);
	    }
	}-*/;

    public HandlerRegistration addMessageHandler(EventBus bus, MessageHandler h, String source) {
    	if (bus == null) bus = this.bus;
    	if (source == null) {
    		return bus.addHandler(MessageEvent.TYPE, h);
    	}
    	return bus.addHandlerToSource(MessageEvent.TYPE, source, h);
    }
    
    private void onMessage(String data, String origin) {
    	fly.setData(data);
    	bus.fireEventFromSource(fly, origin);
    }
}
