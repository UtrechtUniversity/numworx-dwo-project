package nl.uu.fi.dwo.lms.chatgwt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.chatgwt.util.Notification;

public class Seen implements ValueChangeHandler<List<Message>>, AnimationCallback {

	private static final String CHATBOX_SEEN = "Chatbox:seen";
	final Notification note;
	final Map<MessageModel, HandlerRegistration> models;
	private AnimationHandle schedule;
	
	private void sendSeen() {
		if (note.getLast() != CHATBOX_SEEN)
			note.send(CHATBOX_SEEN);
	}
	
	public void add(MessageModel model) {
		HandlerRegistration old = models.put(model, model.addValueChangeHandler(this));
		if (old != null) old.removeHandler();
	}
	
	public void remove(MessageModel model) { 
		HandlerRegistration old = models.remove(model);
		if (old != null) old.removeHandler();
	}
		
	public Seen(Notification note) {
		this.note = note;
		this.models = new HashMap<>();;
	}

	@Override
	public void onValueChange(ValueChangeEvent<List<Message>> event) {
		if (schedule == null) 
		 schedule = AnimationScheduler.get().requestAnimationFrame(this);
	}

	private void run() {
		schedule = null;
		if (!models.keySet().stream().anyMatch(MessageModel::hasUnread))
			sendSeen();
	}

	@Override
	public void execute(double timestamp) {
		run();		
	}

}
