package nl.uu.fi.dwo.lms.chatgwt;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.stanziq.strophe.client.Builder;
import com.stanziq.strophe.client.Connection;
import com.stanziq.strophe.client.Connection.Status;
import com.stanziq.strophe.client.Connection.StatusCallback;
import com.stanziq.strophe.client.Element;
import com.stanziq.strophe.client.Handler;
import com.stanziq.strophe.client.Namespace;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;
import nl.uu.fi.dwo.keyboard.client.Combined;
import nl.uu.fi.dwo.keyboard.client.CombinedState;
import nl.uu.fi.dwo.keyboard.client.DWOCombinedKeyboardFactory;
import nl.uu.fi.dwo.keyboard.client.KeyboardFactory;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.chatgwt.util.Base64;
import nl.uu.fi.dwo.lms.chatgwt.util.MD5;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChatGWT implements EntryPoint, CombinedState {
	
	private static String DOMAIN = "chat-dev.dwo.nl";
	private static String ROOMS = "conference." + DOMAIN;
	private static String BOSH = "wss://chat-dev.dwo.nl/xmpp-websocket";
	private static Logger LOG = Logger.getLogger(ChatGWT.class.getName());
	
	private ChatRoom room = new ChatRoom("klas@" + ROOMS);
	
	class ChatMessage extends Handler<Element> {

		@Override
		public boolean handle(Element element) {
			LOG.info("received " + element.serialize());
//			String to = element.getAttribute("to");
			String from = element.getAttribute("from");
			String type = element.getAttribute("type");

			NodeList<com.google.gwt.dom.client.Element> elems = element.getElementsByTagName("body");

			if (("chat".equals(type)||"groupchat".equals(type)) && elems.getLength() > 0) {
				Element body = (Element) elems.getItem(0);
				Label afzender = new Label("From: " + from);
				panel.add(afzender);
				Label message = new Label(body.getText());
				panel.add(message);
			}
			return true;
		}
		
	}
	
	class ChatStatusCallback extends StatusCallback {

		@Override
		public void statusChanged(Status status, String reason) {
			LOG.info("status = " + status + " reason = " + reason);
			
			if (status == Status.CONNECTED) {
				LOG.info("start talking");
				Handler<com.stanziq.strophe.client.Element> handler = new ChatMessage();
				connection.addHandler(null, "message", null, null, null, handler);
				Builder pres = Builder.$pres(null);
	            connection.send(pres);
// Add to room	            
	            pres = Builder.$pres(new String[][] { {"to", room.jid + "/" + chatUser.nickName }});
	            pres.c("x", new String[][] {{ "xmlns", Namespace.MUC.toString() }});
	            connection.send(pres);
			}
			
		}
		
	}
	

	private Connection connection;
	private TextBox username;
	private TextBox password;
	private VerticalPanel panel;
	private TextBox input;
	private ChatUser chatUser;
	private Combined combined;
	private ChangeHandler handler;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		
		username = new TextBox();
		RootPanel.get().add(username);
		password = new PasswordTextBox();
		password.setValue("");
		RootPanel.get().add(password);
		
		Button btn = new Button("LOGIN");
		btn.addClickHandler(this::onClickLogin);
		RootPanel.get().add(btn);
		btn = new Button("LOGOUT");
		btn.addClickHandler(this::onClickLogout);
		RootPanel.get().add(btn);
		
		panel = new VerticalPanel();
		RootPanel.get().add(panel);
		
		input = new TextBox();
		RootPanel.get().add(input);	
		
		StubEditor editor = new StubEditor();
		
		HashMap<String, Object> data = new HashMap<>();
		
		editor.init(800, 200, data);
		
		RootPanel.get().add(editor);
		
		HashMap<String, Object> state = new HashMap<>();
		editor.setState(state);
		
		btn = new Button("SEND");
		btn.addClickHandler(this::onClickInput);
		RootPanel.get().add(btn);
		
		
		
		// keyboard:
		KeyboardFactory factory = new DWOCombinedKeyboardFactory();
		factory.setPremium(true);
		factory.setCombinedState(this);
		
		AbstractKeyboard keyboard = factory.getKeyboard();
		RootPanel.get().add(keyboard);		
		keyboard.setEnterType(EnterType.ENTER);
		keyboard.setEditor(editor);
	}

	private void onClickLogout(ClickEvent event) {
		connection.disconnect("logout");
		connection = null;
	}

	private void onClickLogin(ClickEvent event) {
		connection = new Connection(BOSH);
		
		StatusCallback callback = new ChatStatusCallback();
		String u = username.getValue();
		chatUser = new ChatUser(u + "@" + DOMAIN);
		String password = this.password.getValue();
		// password = base64( u + ":" + md5(password))
		password = MD5.md5(password);
		password = u + ":" + password;
		password = Base64.btoa(password);

		connection.connect(chatUser.jid, password, callback);		
	}

	private void onClickInput(ClickEvent event) {
		String value = input.getValue();
		LOG.info("send " + value);
		
		String[][] attributes = { { "to", room.jid }, { "type", "groupchat" } };
		Builder reply = Builder.$msg(attributes).c("body",null).t(value);
		connection.send(reply);
	}

		
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		this.handler = handler;
		return () -> addChangeHandler(null);
	}

	@Override
	public void setCombined(Combined state) {
		combined = state;		
	}

	@Override
	public Combined getCombined() {
		return combined;
	}

	@Override
	public int getWidth() {
		return 0;
	}
	
	
	
	
}
