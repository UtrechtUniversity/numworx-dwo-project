package nl.uu.fi.dwo.lms.chatgwt.entities;

public class ChatUser {
	public ChatUser(String string) {
		this.jid = string;
		int at = string.indexOf('@');
		nickName = string.substring(0, at);
	}
	public ChatUser() {};
	
	public String jid;
	public String nickName;
	public ChatRoom room[];
}
