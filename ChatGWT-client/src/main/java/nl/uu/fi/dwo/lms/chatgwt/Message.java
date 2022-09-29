package nl.uu.fi.dwo.lms.chatgwt;

class Message {
	private final String content;
	private final String stamp;
	private final String sender;
	private boolean read;

	Message(String sender, String stamp, String content) {
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
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
}