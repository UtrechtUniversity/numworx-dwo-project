package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Objects;

public class Message implements Comparable<Message> {
	private final String content;
	private final String stamp, utc, id;
	private final String sender;
	private boolean read;

	Message(String sender, String stamp, String content, String utc, String id) {
		this.sender = sender;
		this.stamp = stamp;
		this.content = content;
		this.utc = utc;
		this.id = id;
	}
	String getContent() {
		return content;
	}
	public String getStamp() {
		return stamp;
	}
	public String getSender() {
		return sender;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getUTC() {
		return utc;
	}
	
	public String getID() {
		return id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (id == null || other.id == null) return false; // if id = null then id is unique		
		return Objects.equals(id, other.id);
	}

	@Override
	public int compareTo(Message o) {
		return utc.compareTo(o.utc);
	}
	
	
}