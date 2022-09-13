package nl.uu.fi.dwo.lms.chatgwt.entities;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class ChatUser {
	public ChatUser(String string) {
		this.jid = string;
		int at = string.indexOf('@');
		nickName = (at>=0) ? string.substring(0, at) : string;
	}
	public ChatUser() {};
	
	public String jid;
	public String nickName;
	public List<ChatRoom> room;
	public String token;
	public RoleType role;
	
	public ChatUser(DomUser user, RoleType role) {
		jid = user.getUserName();
		nickName = user.getDisplayName();
		this.role = role;
	}
	
	public ChatUser(DomStudent student) {
		this(student, RoleType.STUDENT);
	}
	public ChatUser(DomTeacher teacher) {
		this(teacher, RoleType.TEACHER);
	}
}
