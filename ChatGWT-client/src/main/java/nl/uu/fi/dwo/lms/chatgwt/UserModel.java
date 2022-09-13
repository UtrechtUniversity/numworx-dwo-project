package nl.uu.fi.dwo.lms.chatgwt;

import java.util.LinkedList;
import java.util.List;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;

class UserModel {
	
	class Message {
		private final String content;
		private final String stamp;
		private final String sender;
		private Message(String sender, String stamp, String content) {
			this.sender = sender;
			this.stamp = stamp;
			this.content = content;
		}
		String getContent() {
			return content;
		}
		String getStamp() {
			return stamp;
		}
		String getSender() {
			return sender;
		}
	}
	
	
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

	List<Message> getUnseen() {
		return unseen;
	}

	UserModel(ChatUser user, ChatRoom room) {
		this.user = user;
		this.roomJit = ChatGWT.nick(user, room);
	}

	
	private boolean  online;
	
	private final List<Message> unseen = new LinkedList<>();


	Boolean hasUnseen() {
		return !unseen.isEmpty();
	}
	
	void addMessage(String sender, String stamp, String content) {
		unseen.add(new Message(sender, stamp, content));
	}
}
