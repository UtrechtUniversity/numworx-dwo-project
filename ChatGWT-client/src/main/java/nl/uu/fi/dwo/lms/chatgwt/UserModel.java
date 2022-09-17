package nl.uu.fi.dwo.lms.chatgwt;

import com.google.gwt.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;

class UserModel {
	
	private final ChatUser user;
	private final String roomJit;

	boolean isOnline() {
		return online;
	}

	void setOnline(boolean online) {
		this.online = online;
	}

	ChatUser getUser() {
		return user;
	}

	String getRoomJit() {
		return roomJit;
	}


	public MessageModel getMessages() {
		return messages;
	}

	public void setMessages(MessageModel messages) {
		this.messages = messages;
	}

	UserModel(ChatUser user, ChatRoom room, MessageModel messageModel) {
		this.user = user;
		this.roomJit = ChatGWT.nick(user, room);
		this.messages = messageModel;
	}

	private boolean  online;
	
	private MessageModel messages;
	private HandlerRegistration reg;

	Boolean hasUnseen() {
		return messages.hasUnread();
	}
	
	void setRegistration(HandlerRegistration reg) {
		clearRegistration();
		this.reg = reg;
	}

	void clearRegistration() {
		if (reg != null) reg.removeHandler();
		reg = null;		
	}
}
