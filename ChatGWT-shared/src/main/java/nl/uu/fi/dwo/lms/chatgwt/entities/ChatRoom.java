package nl.uu.fi.dwo.lms.chatgwt.entities;

import java.util.Collections;
import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class ChatRoom {
	public ChatRoom(String string) {
		jid = string;
		int at = string.indexOf('@');
		displayName = at >=0 ? jid.substring(0, at) : jid;
	}
	
	public ChatRoom() {}
	
	public String jid;
	public String displayName;
	public List<ChatUser> chatUser;
	
	public ChatRoom(DomSchoolClass klas) {
		displayName = klas.getSchoolClassName();
		jid = klas.getId().getIdString();
		chatUser = Collections.emptyList();
	}
}
