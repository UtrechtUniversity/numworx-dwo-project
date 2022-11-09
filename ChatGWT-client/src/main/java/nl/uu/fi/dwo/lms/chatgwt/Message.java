package nl.uu.fi.dwo.lms.chatgwt;

class Message {
	private final String content;
	private final String stamp, utc;
	private final String sender;
	private boolean read;

	Message(String sender, String stamp, String content, String utc) {
		this.sender = sender;
		this.stamp = stamp;
		this.content = content;
		this.utc = utc;
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
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getUTC() {
		return utc;
	}
}