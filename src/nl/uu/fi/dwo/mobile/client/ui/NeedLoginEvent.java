package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;

public class NeedLoginEvent extends Event<NeedLoginHandler> {
    public static final Event.Type<NeedLoginHandler> TYPE = new Event.Type<>();

    Promise<?> resolved;
    
	@Override
	public Type<NeedLoginHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NeedLoginHandler handler) {
		handler.onNeedLogin(this);
		
	}		
}