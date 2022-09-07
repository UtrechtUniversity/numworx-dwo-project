package nl.uu.fi.dwo.lms.chatgwt;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
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
public class ChatGWT implements EntryPoint, CombinedState, HasHeight, FormuleClipboardIF {
	
	private static final ChangeEvent CHANGE_EVENT = new ChangeEvent() {};
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
			int at = from.indexOf('/');
			if (at>0) from = from.substring(at+1);
			String type = element.getAttribute("type");

			NodeList<com.google.gwt.dom.client.Element> elems = element.getElementsByTagName("body");

			if (("chat".equals(type)||"groupchat".equals(type)) && elems.getLength() > 0) {
				Element body = (Element) elems.getItem(0);
				Label afzender = new Label("From: " + from);
				
				panel.add(afzender);
				StubWidget message = new StubWidget();
				
				HashMap<String, Object> data = new HashMap<>();
				data.put("rekenTool", Boolean.FALSE);
				data.put("tekst", body.getText());
				data.put("balkZichtbaar", Boolean.FALSE);
				data.put("boxMetRand", Boolean.FALSE);
				data.put("editable", Boolean.FALSE);
				message.init(800, 100, data);				
				HashMap<String, Object> state = null;
				//message.setState(state);

				message.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				panel.add(message);
				scroll.scrollToBottom();
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
	private ChatUser chatUser;
	private Combined combined;
	private ChangeHandler handler;
	private SimplePanel container;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get();
		
// dummy login schreen
		FlowPanel login = new FlowPanel();
		username = new TextBox();
		login.add(username);
		password = new PasswordTextBox();
		password.setValue("");
		login.add(password);
		
		Button btn = new Button("LOGIN");
		btn.addClickHandler(this::onClickLogin);
		login.add(btn);
		btn = new Button("LOGOUT");
		btn.addClickHandler(this::onClickLogout);
		login.add(btn);
		
		root.add(login);
		root.setWidgetTopHeight(login, 0, Unit.PX, 40, Unit.PX);
		
		
		main = new DockLayoutPanel(Unit.PX);
		main.addStyleName("main");
		FocusPanel wrap;
		root.add(wrap = FocusOnTouch.wrap(main));
		root.setWidgetTopBottom(wrap, 40, Unit.PX, 0, Unit.PX);
		Style style = main.getElement().getStyle();
		style.setHeight(100, Unit.PCT);
		
		
		panel = new VerticalPanel();
		scroll = new ScrollPanel(panel);
		
		
		editor = new StubWidget();
		editor.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

		HashMap<String, Object> data = new HashMap<>();
		data.put("rekenTool", Boolean.FALSE);
		editor.init(800, 100, data);
		
		
		HashMap<String, Object> state = new HashMap<>();
		editor.setState(state);
		
		btn = new Button("SEND");
		btn.addClickHandler(this::onClickInput);
		main.addSouth(btn, 40);
		
		
		
		// keyboard:
		KeyboardFactory factory = new DWOCombinedKeyboardFactory();
		factory.setPremium(true);
		factory.setCombinedState(this);
		
		keyboard = factory.getKeyboard();
		container = new SimpleLayoutPanel();
		style = container.getElement().getStyle();
		style.setBackgroundColor("#e5e7e9");
		style.setProperty("maxWidth", 1024, Unit.PX);
		container.setWidget(keyboard);

		main.addSouth(container, 200);
		main.getWidgetContainerElement(container).getStyle().setBackgroundColor("#e5e7e9");
		keyboard.setSoortKeyboard(0);
		keyboard.setWriteMathSet(0);
		keyboard.setScrollPanel(this, 0);
		keyboard.setEditor(editor);

		main.addSouth(editor, 100);

		FocusOnTouch.installKeyboard(keyboard, this);
		FocusOnTouch.focus();
		
		setHeight(-keyboard.getKeyboardHeight());
		main.add(scroll);
		main.forceLayout();
	}

	private void onClickLogout(ClickEvent event) {
		if (connection != null) {
			connection.disconnect("logout");
			connection = null;
		}
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
		FocusOnTouch.focus();
		event.preventDefault();
	}

	private void onClickInput(ClickEvent event) {		
		HashMap<String, Object> map = editor.getState();
		LOG.info("editor state = " + map);
		String value = JSONUtilities.wrapMap(map).getString("tekst");
		if (value == null || value.isEmpty() || connection == null)
		{
			return;		
		}
		editor.clearAll();
		editor.insert("");
		LOG.info("send " + value);
		
		String[][] attributes = { { "to", room.jid }, { "type", "groupchat" } };
		Builder reply = Builder.$msg(attributes).c("body",null).t(value);
		connection.send(reply);
		FocusOnTouch.focus();
		keyboard.focus();
	}

		
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		this.handler = handler;
		return () -> addChangeHandler(null);
	}

	@Override
	public void setCombined(Combined state) {
		combined = state;
		if (handler != null) handler.onChange(CHANGE_EVENT);
	}

	@Override
	public Combined getCombined() {
		return combined;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public void setHeight(int px) {
		container.setPixelSize(-1, -px);
		main.setWidgetSize(container, -px);
	}


	private String clipboard = "";
	private StubWidget editor;
	private DockLayoutPanel main;
	private AbstractKeyboard keyboard;
	private ScrollPanel scroll;
	@Override
	public String getClipboard() {
		return clipboard;
	}

	@Override
	public void setClipboard(String formule) {
		clipboard = formule;
	}
	

	
	
}
