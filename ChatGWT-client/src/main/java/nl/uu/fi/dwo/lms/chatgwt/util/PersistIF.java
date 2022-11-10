package nl.uu.fi.dwo.lms.chatgwt.util;

public interface PersistIF {
	void init(String jid);

	void flush();

	boolean isSeen(String jid, String stamp);
	void seen(String jid, String stamp);
}
