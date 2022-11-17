package nl.uu.fi.dwo.lms.chatgwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.stanziq.strophe.client.Builder;
import com.stanziq.strophe.client.Connection;
import com.stanziq.strophe.client.Connection.Status;
import com.stanziq.strophe.client.Connection.StatusCallback;
import com.stanziq.strophe.client.Element;
import com.stanziq.strophe.client.Handler;
import com.stanziq.strophe.client.Namespace;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
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
import nl.uu.fi.dwo.lms.chatgwt.util.PersistIF;
import nl.uu.fi.dwo.lms.chatgwt.util.ResizeFlowPanel;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessages;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChatGWT implements EntryPoint, CombinedState, HasHeight, FormuleClipboardIF, com.google.gwt.view.client.SelectionChangeEvent.Handler {
	
	
	private static final TimeZone UTC = TimeZone.createTimeZone(0);
	private static final int COL_6 = 456;
	public  static final Text rb = GWT.create(Text.class);
	public  static final Dwo2LocaleMessages dworb = GWT.create(Dwo2LocaleMessages.class);

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
	
	Handler<Element> receive = new Handler<Element>() {
		
		public boolean handle(Element element) {
			LOG.info("received " + element.serialize());
			return false;
		}
	};

	class ChatPresence extends Handler<Element> implements HasValueChangeHandlers<Set<String>> {

		private Set<String> present = new HashSet<>();
		private EventBus bus = new SimpleEventBus();
		private ChatPresence() {
		}

		
		private void sendCreateRoom(String roomJid) {
			Builder builder = Builder.$iq(new String[][] { { "to", roomJid }, {"type", "set"}})
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
				int at = from.indexOf('/');
				sendCreateRoom(from.substring(0, at));
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
	
	public Set<String> getPresence() {
		if (presenceHandler == null) return Collections.emptySet();
		return presenceHandler.present;
	}

	class ChatMessage extends Handler<Element> {

		@Override
		public boolean handle(Element element) {
			LOG.info("received " + element.serialize());
//			String to = element.getAttribute("to");
			String type = element.getAttribute("type");
			String from = element.getAttribute("from");

			if (type.isEmpty()) {
				return handleMAM(element);
			}
	// FIXME if personal message from someone not on the users list, drop it on the floor!		
			
			String stamp = null;
			int at = from.indexOf('/');
			MessageModel m;
			m = getModel(at > 0 ? from.substring(0, at) : from);
			if (at>0) {
				if ("chat".equals(type) )
					from = from.substring(0,at);
				else
					from = from.substring(at+1);
			}
			NodeList<com.google.gwt.dom.client.Element> elems = element.getElementsByTagName("body");

			NodeList<com.google.gwt.dom.client.Element> delay = element.getElementsByTagName("delay");
			if (delay.getLength() > 0) {
				stamp = delay.getItem(0).getAttribute("stamp"); // <delay from="klas@conference.chat-dev.dwo.nl" stamp="2022-09-09T08:02:29Z" xmlns="urn:xmpp:delay"/>
			}
			if (("chat".equals(type)||"groupchat".equals(type)) && elems.getLength() > 0) {
				Element body = (Element) elems.getItem(0);
				String text = xmldecode(body.getText());
				String utc = stamp;
				if (stamp == null) {
					stamp = now();
					utc = utc();
				} 
				else stamp = iso(stamp);
				m.add(new Message(from, stamp, text, utc));
			}
			return true;
		}

		private boolean handleMAM(Element element) {
			String stamp = null;
			NodeList<com.google.gwt.dom.client.Element> delay = element.getElementsByTagName("delay");
			if (delay.getLength() > 0) {
				stamp = delay.getItem(0).getAttribute("stamp"); // <delay from="klas@conference.chat-dev.dwo.nl" stamp="2022-09-09T08:02:29Z" xmlns="urn:xmpp:delay"/>
			}
			String utc = stamp;
			if (stamp == null) { stamp = now(); utc = utc(); } else stamp = iso(stamp);
			
			NodeList<com.google.gwt.dom.client.Element> messages = element.getElementsByTagName("message");
			Element message = (Element) messages.getItem(0);
			NodeList<com.google.gwt.dom.client.Element> elems = message.getElementsByTagName("body");
			Element body = (Element) elems.getItem(0);
			String text = xmldecode(body.getText());
			String type = message.getAttribute("type"); // chat // groupchat
			String from = message.getAttribute("from");
			int at = from.indexOf('/');
			MessageModel m;
			String jid = from.substring(0, at);
			if (chatUser.jid.equals(jid)) {
				String to = message.getAttribute("to");
				m = getModel(to);
			} else {
				m = getModel(jid);
			}
			m.add(new Message(jid, stamp, text, utc));
			return true;
		}
			
		private String xmldecode(String text) {
			for (String[] r: replace) {
				text = text.replace(r[0], r[1]);
			}
			return text;
		}

	}
	static String[][] replace = { { "&apos;", "'" }, {"&lt;", "<"}, {"&gt;", ">"}, {"quot;", "\""}, {"&amp;" , "&"}};
	
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
	
	private void addToPanel(ValueChangeEvent<List<Message>> event) {
		MessageModel mm = (MessageModel) event.getSource();
		addToPanel(event.getValue());
		event.getValue().forEach(mm::setRead);
		persist.flush();
	}
	private void addToPanel(Collection<Message> msgs) {
		msgs.forEach(this::addToPanel);
	}
	private void addToPanel(Message msg) {
		msg.setRead(true);
		addToPanel(msg.getSender(), msg.getStamp(), msg.getContent(), msg.getUTC());
	}
	
	private void addToPanel(String from, String stamp, String text, String utc) {
		if (stamp == null||stamp.isEmpty()) {
			stamp = now();
		}
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
		hbox.addStyleName("profile-borderBox");
		hbox.add(afzender);
		hbox.add(time);
		if (chatUser.jid .equals(get(from).jid)) {
			hbox.addStyleName("profile-mymessage");
		}
		
		if (!compareStamp(lastPanel, stamp)) {
			Label datelabel = new Label(dateOnly(utc));
			FlowPanel flow = new FlowPanel();			
			flow.addStyleName("date-Label");
			flow.add(datelabel);
			
			panel.add(flow);
			lastPanel = stamp;
		}
		
		panel.add(hbox);
//		StubWidget message = new StubWidget(4);				
//		HashMap<String, Object> data = new HashMap<>();
//		data.put("rekenTool", Boolean.FALSE);
//		data.put("tekst", text);
//		data.put("balkZichtbaar", Boolean.FALSE);
//		data.put("boxMetRand", Boolean.FALSE);
//		data.put("editable", Boolean.FALSE);
//		message.init(COL_6-32-2, 100-30, data);				

		StubWidget message = tekstPanel(text, COL_6-32,100-30);
		message.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		message.getElement().getStyle().setMarginLeft(-4, Unit.PX);
		hbox.add(message);
		scroll.scrollToBottom();
	}
	
	private boolean compareStamp(String stamp1, String stamp2) {
		stamp1 = stamp1.split(" ")[0]; // alleen datum vak
		stamp2 = stamp2.split(" ")[0];
		return stamp1.equals(stamp2);
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
	            for(ChatRoom room: chatUser.room) { addToRoom(room); }
	            
	            sendMamRequest(); // after room presence
// Add to room	            
	            if (room != null) {
	            	switchToModel(get(room));
	            	//addToRoom(room);
	            }
	            break;
			case DISCONNECTED:
				LOG.info("stop talking");
				if (ref1 != null) { connection.removeHandler(ref1); ref1 = null; }
				if (ref2 != null) { connection.removeHandler(ref2); ref2 = null; }
			default:
			}
			
		}
		
	}
	

	private Connection connection;
	private TextBox username;
	private TextBox password;
	private VerticalPanel panel;
	private String lastPanel;
	private FlowPanel east;
	private ChatUser chatUser;
	private Combined combined = Combined.DESKTOP_ACTIVE;
	private ChangeHandler handler;
	private SimplePanel container;
	private Label sender;
	
	private Map<String, ChatUser> byJid = new HashMap<>();
	private Map<String, MessageModel> models = new HashMap<>();
	private HandlerRegistration addToPanelHandler;
	private NoSelectionModel<UserModel> noselection;
	private EastHeader eastHeader;
	private List<ChatRoom> rooms;
	protected PersistIF persist;
	
	public ChatGWT(PersistIF persist) {
		this.persist = persist;
	}

	public ChatGWT() {
		this(GWT.create(PersistIF.class));
	}
	
	
	private void put(ChatUser u) {
		byJid.put(u.jid,u);
	}
	private void put(MessageModel model) {
		models.put(model.getJid(), model);
	}
	private MessageModel getModel(String jid) {
		//return models.computeIfAbsent(jid, MessageModel::new);
		MessageModel m = models.get(jid);
		if (m == null) {
			m = new MessageModel(jid, persist);
			models.put(jid, m);
		}
		return m;
	}
	
	MessageModel get(ChatRoom room) {
		return getModel(room.jid);
	}
	
	MessageModel get(ChatUser user) {
		return getModel(user.jid);
	}
	
	private ChatUser get(String key) {
		key = addDomain(key);
		ChatUser u = byJid.get(key);
		return u;
	}

	public static String addDomain(String key) {
		if (!key.contains("@")) key += "@"+DOMAIN;
		return key;
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
		
//		StubWidget tekstpanel = tekstPanel("DIT IS DE $fx-y@ TEKST\nTWEEDE REGEL\n", COL_6, 300);
//		RootPanel.get().add(tekstpanel);
//		
//		
//		
//		if(true) return;

		formule = !"111".equals(Location.getParameter("profile"));

		
		RootLayoutPanel root = RootLayoutPanel.get();
		eastHeader  = new EastHeader();
		eastHeader.setUpdateRoom(this::updateRoom);
		
		int top = 0;
		try {
			String chatUserString = getParentChatUser();
			ChatUser u = CODEC.decode(chatUserString);
			toJid(u);
			persist.init(u.jid);
			if (u.room != null) {
				rooms = u.room;
				u.room.forEach(r -> {
					r.jid = r.jid.toLowerCase() + "@" + ROOMS;
					if (r.chatUser != null) {
						r.chatUser.forEach(this::toJid);
					}
					
				});
				eastHeader.init(u.room);
				this.room = eastHeader.getSelectedRoom();
				new RoomController(u.room, eastHeader).addHandler(this::get);
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
			room.chatUser.forEach(this::put);
			rooms = Arrays.asList(room, room2);
			eastHeader.init(rooms);
			new RoomController(rooms, eastHeader).addHandler(this::get);
			
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
		lastPanel = now();
		scroll = new ScrollPanel(panel);
		east  = new ResizeFlowPanel();
		
		eastHeader.setUpdateMultiChat(this::updateMultichat);

		east.add(eastHeader);
		
		selection = new SingleSelectionModel<>(UserModel::getRoomJit);
		noselection = new NoSelectionModel<>(UserModel::getRoomJit);
		selection.addSelectionChangeHandler(this);
		
		students = new UserTable(room, RoleType.STUDENT, noselection, this);
		
		east.add(students);
		
		teachers = new UserTable(room, RoleType.TEACHER, noselection, this);
		
		east.add(teachers);
		
		editor = new StubWidget(4);

		HashMap<String, Object> data = new HashMap<>();
		data.put("rekenTool", Boolean.FALSE);
		data.put("formuleKnop", formule);
		data.put("boxMetRand", Boolean.FALSE);
		data.put("balkZichtbaar", formule);
		editor.init(COL_6-4, formule?100:70, data);
		editor.addStyleName("box");
		editor.addStyleName("profile-borderBox");
		
		
		HashMap<String, Object> state = new HashMap<>();
		editor.setState(state);
	
		FlowPanel flow = new FlowPanel();
		InlineHTML btn;
		btn = new InlineHTML("<i class='send fa fa-2x fa-paper-plane' >");
		btn.addClickHandler(this::onClickInput);
		btn.getElement().getStyle().setFloat(Style.Float.RIGHT);
		if (room != null) sender = new Label(rb.messageFor(room.displayName));
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
		main.addSouth(editor, formule?104:74);
		main.addSouth(flow, 40);
		
		FocusOnTouch.installKeyboard(keyboard, this);

		keyboardFocus();
		
		setHeight(-keyboard.getKeyboardHeight());
		main.add(scroll);
		main.forceLayout();
		
		
		if (chatUser != null) 
			Scheduler.get().scheduleDeferred(this::login);
	}
	void keyboardFocus() {
		if (formule) keyboard.focus(); else keyboard.softFocus();
	}

	StubWidget tekstPanel(String content, int width, int height) {
		StubWidget tekstpanel = new StubWidget(9);
		HashMap<String, Object> launch = new HashMap<>();
		
		launch.put("teksten", new String[][] {{ content}});
		launch.put("breedtes", Arrays.asList(width));
		launch.put("hoogtes",  Arrays.asList(height));
		launch.put("font_size", 13);
		launch.put("pasAanH", Boolean.TRUE);
		
		tekstpanel.init(width, height, launch);
		return tekstpanel;
	}
	private void toJid(ChatUser u) {
		u.jid = u.jid.toLowerCase() + "@" + DOMAIN;
		put(u);
	}

	private void onClickLogout(ClickEvent event) {
		if (connection != null) {
			connection.disconnect("logout");
			connection = null;
			persist.flush();
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
		chatUser.room = rooms;
		persist.init(chatUser.jid);
		login();

		event.preventDefault();
	}

	private void login() {
		connection = new Connection(BOSH);		
		StatusCallback callback = new ChatStatusCallback(connection);
		connection.connect(chatUser.jid, chatUser.token, callback);
		FocusOnTouch.focus();
	}

	Consumer<String> sendTo = this::sendToRoom;
	
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
		sendTo.accept(value);

		keyboardFocus();
	}
	private void sendToRoom(String value) {
		if (room == null) return;
		String[][] attributes = { { "to", room.jid }, { "type", "groupchat" } };
		Builder reply = Builder.$msg(attributes).c("body",null).t(value);
		connection.send(reply);
	}
	
	private void sendMamRequest() {
		String[][] attributes = { { "type", "set" }};
		String[][] attributesQ = {{ "xmlns", "urn:xmpp:mam:2"},{"queryid", "fetchall"}};
		Builder request = Builder.$iq(attributes).c("query", attributesQ);
		connection.sendIq(request.tree(), 10000, receive, receive);
	}
	
	private void sendToUser(String value) {
		UserModel um = selection.getSelectedObject();
		if (um != null) {
			ChatUser u = um.getUser();
			String[][] attributes = { { "to", u.jid }, { "type", "chat" } };
			Builder reply = Builder.$msg(attributes).c("body",null).t(value);
			connection.send(reply);
			Message msg = new Message(chatUser.jid, now(), value, utc());
			um.getMessages().add(msg);
		} else {
			// select user 1st;
		}
	}
	private static final DateTimeFormatInfo INFO = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
	private static final DateTimeFormat DATE_TIME = DateTimeFormat.getFormat(INFO.dateTimeShort(INFO.timeFormatShort(),INFO.formatMonthNumDay()));
	private static final DateTimeFormat DATE_ONLY = DateTimeFormat.getFormat(INFO.formatMonthFullDay());
	
	public static String now() {
		return DATE_TIME.format( new Date());
	}
	public static String dateOnly(String utc) {
		return DATE_ONLY.format(fromDelay(utc));
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

	private void addToRoom(ChatRoom room) {
		Builder pres;
		pres = Builder.$pres(new String[][] { {"to", nick(chatUser, room) }});
		pres.c("x", new String[][] {{ "xmlns", Namespace.MUC.toString() }});
		connection.send(pres);
	}
	
//	void deleteFromRoom(ChatRoom room) {
//		Builder pres;
//		pres = Builder.$pres(new String[][] { {"to", nick(chatUser, room) }, {"type", "unavailable"} });
//		pres.c("x", new String[][] {{ "xmlns", Namespace.MUC.toString() }});
//		connection.send(pres);
//		
//	}
	
	void updateRoom(ChatRoom room) {
		if (this.room == room) return;

		if (this.room != null) {
			//deleteFromRoom(this.room);
			removeAddToPanel();
		}
		panel.clear();lastPanel = now();
		
		this.room = room;
		students.init(room);
		teachers.init(room);
		if (room != null) {
			sender.setText(rb.messageFor(room.displayName));
			MessageModel m = get(room);
			m.clear();
			switchToModel(m);
			//addToRoom(room);
		} else {
			sender.setText(rb.message());
		}
		eastHeader.setMultiChat(true);
		sendTo = this::sendToRoom;
	}
	private void removeAddToPanel() {
		if (addToPanelHandler != null) {
			addToPanelHandler.removeHandler();
			addToPanelHandler = null;
		}
	}
	
	void updateMultichat(boolean b) {
		selection.clear();
		if (b) {
			sender.setText(rb.messageFor(room.displayName));
			sendTo = this::sendToRoom;
			switchToModel(get(room));
			setSelection(noselection);
		} else {
			sender.setText(rb.message());
			removeAddToPanel();
			panel.clear();lastPanel = now();
			sendTo = this::sendToUser;
			setSelection(selection);
		}
	}

	private void setSelection(SelectionModel<UserModel> sel) {
		students.setSelectionModel(sel);
		teachers.setSelectionModel(sel);		
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		UserModel selectedObject = selection.getSelectedObject();
		if (!eastHeader.isMultichat() && selectedObject != null) {
			ChatUser user = selectedObject.getUser();
			MessageModel m = selectedObject.getMessages();
			switchToModel(m);
			sender.setText(rb.messageFor(user.nickName));
		}
	}
	private void switchToModel(MessageModel m) {
		removeAddToPanel();
		panel.clear();lastPanel = now();
		addToPanel(m.getMessages());
		m.getMessages().forEach(m::setRead);
		persist.flush();
		addToPanelHandler = m.addValueChangeHandler(this::addToPanel);
	}
	
	static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
	//private static final int Tlen = ISO8601_PATTERN.indexOf('T');
	static final DateTimeFormat ISO_DATETIME = DateTimeFormat.getFormat(ISO8601_PATTERN);

	private boolean formule;

	public static Date fromDelay(String delay) {
		if (delay.endsWith("Z")) delay = delay.substring(0, delay.length()-1) + "+0000"; // REMOVE Z, add GMT		
		return ISO_DATETIME.parseStrict(delay);
	}
	
	private String iso(String delay) {
		return DATE_TIME.format(fromDelay(delay));
	}
	public static String utc() {
		return ISO_DATETIME.format(new Date(), UTC);
	}
	
}
