package nl.uu.fi.dwo.lms.chatgwt.entities;

import java.util.List;
import java.util.Objects;

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
	
	public ChatUser(ChatUser org) {
		jid = org.jid;
		nickName = org.nickName;
		role = org.role;
		// no password and no room.
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(jid, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatUser other = (ChatUser) obj;
		return Objects.equals(jid, other.jid) && role == other.role;
	}
}
