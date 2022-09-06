package nl.uu.fi.dwo.lms.chatgwt.entities;

public class ChatUser {
	public ChatUser(String string) {
		this.jid = string;
	}
	public String jid;
	public String nickName;
	public ChatRoom room[];
}
