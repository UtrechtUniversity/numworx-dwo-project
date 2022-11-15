package nl.uu.fi.dwo.lms.chatgwt;

import com.google.gwt.i18n.client.Messages;

public interface Text extends Messages {
	String message();
	String messageFor(String receiver);
	String newMessages();
	String online();
	
	String classMessages();
	String personalMessages();
}
