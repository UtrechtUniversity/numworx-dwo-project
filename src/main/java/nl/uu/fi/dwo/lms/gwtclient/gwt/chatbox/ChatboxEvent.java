package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import com.google.web.bindery.event.shared.Event;

import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.ChatboxEvent.ChatboxHandler;

public class ChatboxEvent extends Event<ChatboxHandler>{

	public interface ChatboxHandler {
		void onChatbox(ChatboxEvent event);
	}

	static public final Type<ChatboxHandler> TYPE = new Type<ChatboxHandler>();
	private final String param;

	public ChatboxEvent(String param) {
		this.param = param;
	}

	@Override
	public Type<ChatboxHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChatboxHandler handler) {
		handler.onChatbox(this);		
	}

	/**
	 * @return the param
	 */
	public String getParam() {
		return param;
	}

}
