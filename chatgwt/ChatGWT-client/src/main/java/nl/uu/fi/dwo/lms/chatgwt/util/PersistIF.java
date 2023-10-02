package nl.uu.fi.dwo.lms.chatgwt.util;

import nl.uu.fi.dwo.lms.chatgwt.Message;

public interface PersistIF {
	void init(String jid);

	void flush();

	boolean isSeen(String jid, Message message);
	void seen(String jid, Message msg);
}
