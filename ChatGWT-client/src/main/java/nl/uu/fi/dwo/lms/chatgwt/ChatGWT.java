package nl.uu.fi.dwo.lms.chatgwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
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
import nl.uu.fi.dwo.lms.chatgwt.util.ResizeFlowPanel;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChatGWT implements EntryPoint, CombinedState, HasHeight, FormuleClipboardIF {
	
	
	private static final int COL_6 = 456;

	interface ChatUserCodec extends JsonEncoderDecoder<ChatUser> {};
	
	private static final ChatUserCodec CODEC = GWT.create(ChatUserCodec.class);
	
//	private class EastUpdater implements ValueChangeHandler<Set<String>> {
//
//		@Override
//		public void onValueChange(ValueChangeEvent<Set<String>> event) {
//			east.clear();
//			int off = room.jid.length()+1;
//			event.getValue().stream()
//			.filter(t -> t.startsWith(room.jid))
//			.sorted().forEach(s -> east.add(new Label(
//					getDisplayName(s.substring(off)))));
//
//		}
//
//	}

	private static native String getParentChatUser() /*-{
		return $wnd.parent.chatUser;
	}-*/;
	
	
	private static final ChangeEvent CHANGE_EVENT = new ChangeEvent() {};
	private static String DOMAIN = "chat-dev.dwo.nl";
	private static String ROOMS = "conference." + DOMAIN;
	private static String BOSH = "wss://chat-dev.dwo.nl/xmpp-websocket";
	private static Logger LOG = Logger.getLogger(ChatGWT.class.getName());
	
	private ChatRoom room = new ChatRoom("klas@" + ROOMS);
	{
		List<ChatUser> users = new ArrayList<>();
		ChatUser wim = new ChatUser("project_wim@" + DOMAIN);
		wim.role = RoleType.TEACHER;
		users.add(wim);
		ChatUser meesterwim = new ChatUser("meesterwim@" + DOMAIN);
		meesterwim.role = RoleType.STUDENT;
		users.add(meesterwim);
		ChatUser peter = new ChatUser("peterb_pr@" + DOMAIN);
		peter.role = RoleType.TEACHER;
		users.add(peter);
		room.chatUser = users;
		
	}
	
	class ChatPresence extends Handler<Element> implements HasValueChangeHandlers<Set<String>> {

		private Set<String> present = new TreeSet<>();
		private EventBus bus = new SimpleEventBus();
		private ChatPresence() {
		}

		Handler<Element> receive = new Handler<Element>() {
		
			public boolean handle(Element element) {
				LOG.info("received " + element.serialize());
				return false;
			}
		};
		
		private void sendCreateRoom() {
			Builder builder = Builder.$iq(new String[][] { { "to", room.jid }, {"type", "set"}})
				.c("query", new String[][] {{"xmlns", Namespace.MUC + "#owner"} })
				.c("x", new String[][] {{ "xmlns", "jabber:x:data"}, {"type", "submit"}});
			connection.sendIq(builder.tree(), 1000, receive, receive);
		}
		
		
		
		@Override
		public boolean handle(Element element) {
			receive.handle(element);
			String from = element.getAttribute("from");
			String type = element.getAttribute("type");
			boolean leave = "unavailable".equals(type);
			if (leave) present.remove(from); else present.add(from);
			NodeList<com.google.gwt.dom.client.Element> status = element.getElementsByTagName("status");
			if (status.getLength()>0 && "201".equals(status.getItem(0).getAttribute("code"))) { // Chatroom on hold.
				sendCreateRoom();
			}
			
			ValueChangeEvent.fire(this, present);
			return true;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			bus.fireEventFromSource(event, this);			
		}

		@Override
		public HandlerRegistration addValueChangeHandler(
				ValueChangeHandler<Set<String>> handler) {
			return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
		}
	
	}
	
	ChatPresence presenceHandler;

	class ChatMessage extends Handler<Element> {

		@Override
		public boolean handle(Element element) {
			LOG.info("received " + element.serialize());
//			String to = element.getAttribute("to");
			String from = element.getAttribute("from");
			String stamp = null;
			int at = from.indexOf('/');
			if (at>0) from = from.substring(at+1);
			String type = element.getAttribute("type");

			NodeList<com.google.gwt.dom.client.Element> elems = element.getElementsByTagName("body");

			NodeList<com.google.gwt.dom.client.Element> delay = element.getElementsByTagName("delay");
			if (delay.getLength() > 0) {
				stamp = delay.getItem(0).getAttribute("stamp"); // <delay from="klas@conference.chat-dev.dwo.nl" stamp="2022-09-09T08:02:29Z" xmlns="urn:xmpp:delay"/>
			}
			if (stamp == null||stamp.isEmpty()) {
				stamp = new Date().toString(); // Date format?
			}
			
			
			if (("chat".equals(type)||"groupchat".equals(type)) && elems.getLength() > 0) {
				Element body = (Element) elems.getItem(0);
				InlineLabel afzender = new InlineLabel(getDisplayName(from));
				afzender.addStyleName("sender");
				afzender.getElement().getStyle().setFloat(Style.Float.LEFT);
				afzender.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
				InlineLabel time = new InlineLabel(stamp);
				time.addStyleName("name");
				time.getElement().getStyle().setFloat(Style.Float.RIGHT);
				Panel hbox = new FlowPanel();
				hbox.addStyleName("message");
				hbox.addStyleName("lightbox");
				hbox.add(afzender);
				hbox.add(time);
				panel.add(hbox);

				StubWidget message = new StubWidget();				
				HashMap<String, Object> data = new HashMap<>();
				data.put("rekenTool", Boolean.FALSE);
				data.put("tekst", body.getText());
				data.put("balkZichtbaar", Boolean.FALSE);
				data.put("boxMetRand", Boolean.FALSE);
				data.put("editable", Boolean.FALSE);
				message.init(COL_6-32-2, 100-30, data);				

				message.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				hbox.add(message);
				scroll.scrollToBottom();
			}
			return true;
		}
		
	}
	
	class ChatAll extends Handler<Element> {

		Map<String, Handler<Element>> byTag = new HashMap<>();
		
		@Override
		public boolean handle(Element element) {
			LOG.info("received: " + element.serialize());
			String name = element.getTagName();
			LOG.info("tag = " + name);
			Handler<Element> h = byTag.get(name);
			if (h != null) {
				h.handle(element);
			}
			return true; // keep
		}

		public Handler<Element> put(String key, Handler<Element> value) {
			return byTag.put(key, value);
		}
		
	}
	
	class ChatStatusCallback extends StatusCallback {
		

		Handler.Reference ref1, ref2;
		final Connection connection;

		ChatStatusCallback(Connection connection) {
			this.connection = connection;
		}
		@Override
		public void statusChanged(Status status, String reason) {
			LOG.info("status = " + status + " reason = " + reason);
			switch(status) {
			case CONNECTED:
				LOG.info("start talking");
				ChatMessage handler = new ChatMessage();
				ref1 = connection.addHandler(null, "message", null, null, null, handler);
				presenceHandler = new ChatPresence();
				presenceHandler.addValueChangeHandler(students);
				presenceHandler.addValueChangeHandler(teachers);
				ref2 = connection.addHandler(null, "presence", null, null, null, presenceHandler);
//				ChatAll all = new ChatAll();
//				all.put("message", handler);
//				all.put("presence", presenceHandler);
				//ref2 = connection.addHandler(null, null, null, null, null, all);
				
				Builder pres = Builder.$pres(null);
	            connection.send(pres);
// Add to room	            
	            if (room != null) addToRoom(room);
	            break;
			case DISCONNECTED:
				LOG.info("stop talking");
				if (ref1 != null) { connection.removeHandler(ref1); ref1 = null; }
				if (ref2 != null) { connection.removeHandler(ref2); ref2 = null; }
			}
			
		}
		
	}
	

	private Connection connection;
	private TextBox username;
	private TextBox password;
	private VerticalPanel panel;
	private FlowPanel east;
	private ChatUser chatUser;
	private Combined combined;
	private ChangeHandler handler;
	private SimplePanel container;
	private Label sender;
	
	private Map<String, ChatUser> byJid = new HashMap<>();
	private NoSelectionModel<UserModel> noselection;
	private EastHeader eastHeader;

	private void put(ChatUser u) {
		byJid.put(u.jid,u);
	}
	private ChatUser get(String key) {
		if (!key.contains("@")) key += "@"+DOMAIN;
		ChatUser u = byJid.get(key);
		return u;
	}
	
	private String getDisplayName(String jid) {
		ChatUser u = get(jid);
		return u == null ? jid : u.nickName;
	}
	
	static String nick (ChatUser u, ChatRoom room) {
		int at = u.jid.indexOf('@');
		return room.jid + "/" + u.jid.substring(0,at);
	}
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get();
		eastHeader  = new EastHeader();
		eastHeader.setUpdateRoom(this::updateRoom);
		
		int top = 0;
		try {
			String chatUserString = getParentChatUser();
			ChatUser u = CODEC.decode(chatUserString);
			toJid(u);
			if (u.room != null) {
				u.room.forEach(r -> {
					r.jid = r.jid.toLowerCase() + "@" + ROOMS;
					if (r.chatUser != null) {
						r.chatUser.forEach(this::toJid);
					}
					
				});
				eastHeader.init(u.room);
				this.room = eastHeader.getSelectedRoom();
			}
			chatUser = u;
		} catch(Exception oops) {
		
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
			top = 40;

			ChatRoom room2 = new ChatRoom("klas2@" + ROOMS);
			room2.chatUser = room.chatUser;
			
			eastHeader.init(Arrays.asList(room, room2));
			room = null;
		
		}
		
		main = new DockLayoutPanel(Unit.PX);
		main.addStyleName("main");
		FocusPanel wrap;
		root.add(wrap = FocusOnTouch.wrap(main));
		root.setWidgetTopBottom(wrap, top, Unit.PX, 0, Unit.PX);
		Style style = main.getElement().getStyle();
		style.setHeight(100, Unit.PCT);
		
		
		panel = new VerticalPanel();
		scroll = new ScrollPanel(panel);
		east  = new ResizeFlowPanel();
		

		east.add(eastHeader);
		
		selection = new SingleSelectionModel<>(UserModel::getRoomJit);
		noselection = new NoSelectionModel<>(UserModel::getRoomJit);
		
		students = new UserTable(room, RoleType.STUDENT, noselection);
		
		east.add(students);
		
		teachers = new UserTable(room, RoleType.TEACHER, noselection);
		
		east.add(teachers);
		
		editor = new StubWidget();

		HashMap<String, Object> data = new HashMap<>();
		data.put("rekenTool", Boolean.FALSE);
		data.put("boxMetRand", Boolean.FALSE);
		editor.init(COL_6-4, 100, data);
		editor.addStyleName("box");
		
		
		HashMap<String, Object> state = new HashMap<>();
		editor.setState(state);
	
		FlowPanel flow = new FlowPanel();
		InlineHTML btn;
		btn = new InlineHTML("<i class='send fa fa-2x fa-paper-plane' >");
		btn.addClickHandler(this::onClickInput);
		btn.getElement().getStyle().setFloat(Style.Float.RIGHT);
		if (room != null) sender = new Label("Bericht voor " + room.displayName);
		else sender = new Label();
		sender.addStyleName("header");
		flow.add(sender);
		flow.add(btn);
		
		
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
		main.addEast(east, COL_6);
		main.addEast(new SimplePanel(), 18);
		
		main.getWidgetContainerElement(container).getStyle().setBackgroundColor("#e5e7e9");
		keyboard.setSoortKeyboard(0);
		keyboard.setWriteMathSet(0);
		keyboard.setScrollPanel(this, 0);
		keyboard.setEditor(editor);
		main.addSouth(new SimplePanel(), 30); // border
		main.addSouth(editor, 104);
		main.addSouth(flow, 40);
		
		FocusOnTouch.installKeyboard(keyboard, this);
		FocusOnTouch.focus();
		
		setHeight(-keyboard.getKeyboardHeight());
		main.add(scroll);
		main.forceLayout();
		
		
		if (chatUser != null) 
			Scheduler.get().scheduleDeferred(this::login);
	}
	private void toJid(ChatUser u) {
		u.jid = u.jid.toLowerCase() + "@" + DOMAIN;
		put(u);
	}

	private void onClickLogout(ClickEvent event) {
		if (connection != null) {
			connection.disconnect("logout");
			connection = null;
		}
	}

	private void onClickLogin(ClickEvent event) {
		String u = username.getValue();
		chatUser = new ChatUser(u + "@" + DOMAIN);
		String password = this.password.getValue();
		// password = base64( u + ":" + md5(password))
		password = MD5.md5(password);
		password = u + ":" + password;
		password = Base64.btoa(password);
		chatUser.token = password;
		chatUser.nickName = "Username: " + u;
		
		login();

		event.preventDefault();
	}

	private void login() {
		connection = new Connection(BOSH);		
		StatusCallback callback = new ChatStatusCallback(connection);
		connection.connect(chatUser.jid, chatUser.token, callback);
		FocusOnTouch.focus();
	}

	private void onClickInput(ClickEvent event) {
		if (room == null) return;
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
	private UserTable students;
	private UserTable teachers;
	private SingleSelectionModel<UserModel> selection;
	@Override
	public String getClipboard() {
		return clipboard;
	}

	@Override
	public void setClipboard(String formule) {
		clipboard = formule;
	}

	void addToRoom(ChatRoom room) {
		Builder pres;
		pres = Builder.$pres(new String[][] { {"to", nick(chatUser, room) }});
		pres.c("x", new String[][] {{ "xmlns", Namespace.MUC.toString() }});
		connection.send(pres);
	}
	
	void deleteFromRoom(ChatRoom room) {
		Builder pres;
		pres = Builder.$pres(new String[][] { {"to", nick(chatUser, room) }, {"type", "unavailable"} });
		pres.c("x", new String[][] {{ "xmlns", Namespace.MUC.toString() }});
		connection.send(pres);
		
	}
	
	void updateRoom(ChatRoom room) {
		if (this.room == room) return;

		if (this.room != null) {
			deleteFromRoom(this.room);
		}
		panel.clear();
		
		this.room = room;
		students.init(room);
		teachers.init(room);
		if (room != null) {
			sender.setText("Bericht voor " + room.displayName);
			addToRoom(room);
		} else {
			sender.setText("Bericht");
		}
	}
	
}
