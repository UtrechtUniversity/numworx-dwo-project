package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.chatbox;

import javax.inject.Inject;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.*;

public class JsChatboxView implements ChatboxPresenter.Display {
	
	
	interface ChatUserCodec extends JsonEncoderDecoder<ChatUser> { };
	
	static ChatUserCodec CODEC = GWT.create(ChatUserCodec.class);

	@Inject JsChatboxView() {}

	@Override
	public void init() {
		JsChatboxDisplay.init();
		
	}

	@Override
	public void clear() {
		JsChatboxDisplay.clear();
	}

	@Override
	public void setHelp(String url) {
		JsChatboxDisplay.setHelp(url);
	}

	@Override public void setLogin(ChatUser user) {
		String obj = null;
		if (user != null) {
			obj = CODEC.encode(user).toString();
		}
		JsChatboxDisplay.setLogin(obj);
	}
	@Override public void openUrl(String url) {
		JsChatboxDisplay.openUrl(url);
	}
}
