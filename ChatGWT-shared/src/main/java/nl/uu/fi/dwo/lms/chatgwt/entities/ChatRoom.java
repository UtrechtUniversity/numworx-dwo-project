package nl.uu.fi.dwo.lms.chatgwt.entities;

public class ChatRoom {
	public ChatRoom(String string) {
		jid = string;
		int at = string.indexOf('@');
		displayName = jid.substring(0, at);
	}
	
	public ChatRoom() {}
	
	public String jid;
	public String displayName;
}
