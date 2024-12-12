package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.web.bindery.event.shared.Event;

public class MessageEvent extends Event<MessageHandler> {
	
	public static Type<MessageHandler> TYPE = new Type<>();

	@Override
	public Type<MessageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MessageHandler handler) {
		handler.onMessage(this);
	}

	private String data;
	
	public String getData() { 
		return data;
	}
	
	void setData(String data) {
		this.data = data;
	}
}
