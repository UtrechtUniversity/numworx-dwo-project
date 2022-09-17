package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

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

	public boolean hasUnread() {
		int size = messages.size();
		ListIterator<Message> li = messages.listIterator(size);
		Stream<Message> m = Stream.generate(li::previous).limit(size);
		return ! m.allMatch(Message::isRead);
	}

	public void add(Message message) {
		messages.add(message);
		ValueChangeEvent.fire(this, Collections.singletonList(message));
	}

	public void clear() {
		messages.clear();
		ValueChangeEvent.fire(this, Collections.emptyList());
	}
	
	public void setRead(Message msg) {
		boolean oldread = msg.isRead();
		boolean old = !oldread;
		msg.setRead(true);
		if (old && !hasUnread()) {
			ValueChangeEvent.fire(this, Collections.emptyList());
		}
		
	}
	
}
