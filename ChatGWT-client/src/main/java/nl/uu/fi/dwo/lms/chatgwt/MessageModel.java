package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

public class MessageModel implements HasValueChangeHandlers<List<Message>> {

	private EventBus bus = new SimpleEventBus();
	private List<Message> messages = new LinkedList<>();
	private final String jid; // user of room
	
	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getJid() {
		return jid;
	}

	MessageModel(String jid) {
		this.jid = jid;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		bus.fireEventFromSource(event, this);		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Message>> handler) {
		return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
	}

	public boolean since(Object stamp) {
		return !messages.isEmpty();
	}

	private Object stamp = null;
	
	public Object getStamp() {
		return stamp;
	}

	public Object add(Message message) {
		messages.add(message);
		stamp = messages.size();
		ValueChangeEvent.fire(this, Collections.singletonList(message));
		return stamp;
	}

	public void clear() {
		messages.clear();
		stamp = null;
		ValueChangeEvent.fire(this, Collections.emptyList());
	}
	
}
