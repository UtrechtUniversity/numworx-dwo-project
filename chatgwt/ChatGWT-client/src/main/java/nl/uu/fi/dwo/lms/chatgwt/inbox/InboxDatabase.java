package nl.uu.fi.dwo.lms.chatgwt.inbox;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.chatgwt.ChatGWT;
import nl.uu.fi.dwo.lms.chatgwt.Message;
import nl.uu.fi.dwo.lms.chatgwt.MessageModel;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.chatgwt.util.PersistIF;

public class InboxDatabase implements ValueChangeHandler<List<Message>> {

    public static final ProvidesKey<InboxInfo> KEY_PROVIDER = new ProvidesKey<InboxInfo>() {
	      @Override
	      public Object getKey(InboxInfo item) {
	        return item == null ? null : item.getId();
	      }
	    };

	PersistIF dummy = new PersistIF() {

		@Override
		public void init(String jid) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isSeen(String jid, Message message) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void seen(String jid, Message msg) {
			// TODO Auto-generated method stub
			
		}
		
	};
	    
	    
	public class InboxInfo implements Comparable<InboxInfo> {

		/**
	     * The key provider that provides the unique ID of a contact.
	     */

	    private Object id;
	    private MessageModel model;
	    private String title;
	    private HandlerRegistration reg;
	    private boolean u;
		
		public InboxInfo(Object id) {
			this.id = id;
			this.title = Objects.toString(id);
			this.model = new MessageModel(title, dummy);
		}

		public InboxInfo(MessageModel model, String title, boolean u) {
			this.model = model;
			this.id = model.getJid();
			this.title = title;
			this.u = u;
		}
		
		@Override
		public int compareTo(InboxInfo o) {
			return o.getUTC().compareTo(getUTC());
		}

		protected Object getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		String getUTC() {
			List<Message> list = model.getMessages();
			if (list.isEmpty()) return "";
			Message last = list.get(list.size()-1);
			return last.getUTC();	
		}
		
		
		public String getDate() {
			List<Message> list = model.getMessages();
			if (list.isEmpty()) return "";
			Message last = list.get(list.size()-1);
			return last.getStamp();
		}
		
		public boolean isUnseen() {
			return model.hasUnread();
		}
		
		public String getAuthor() {
			List<Message> list = model.getMessages();
			if (list.isEmpty()) return "";
			String sender = list.get(list.size()-1).getSender();
			if (u) {
				if (sender.equals(model.getJid()))
					return "";
			}
			return parent.getDisplayName(sender); 
		}

		public boolean isRoom() {
			return !u;
		}
		
	}
	  private ListDataProvider<InboxInfo> dataProvider = new ListDataProvider<InboxInfo>();
	  private Map<String, InboxInfo> asMap = new TreeMap<>();
	  public ChatGWT parent;
	  /**
	   * Add a display to the database. The current range of interest of the display
	   * will be populated with data.
	   * 
	   * @param display a {@Link HasData}.
	   */
	  public void addDataDisplay(HasData<InboxInfo> display) {
	    dataProvider.addDataDisplay(display);
	  }

	public InboxDatabase(ChatGWT parent) {
		this.parent = parent;
	}

	public void add(MessageModel model) {
		String jit = model.getJid();
		if (asMap.containsKey(jit)) return;
		ChatUser user = parent.get(jit);
		if (user != null) {
			add(model, user);
			return;
		}
		Optional<ChatRoom> room = parent.getRoom(jit);
		room.ifPresent(r -> add(model, r));
		
	}
	
	public void add(MessageModel model, ChatUser user) {
		String jit = model.getJid();
		if (asMap.containsKey(jit)) return;
		String display = user.nickName;
		InboxInfo info = new InboxInfo(model, display, true);
		asMap.put(jit, info);
		dataProvider.getList().add(0, info);
		info.reg = model.addValueChangeHandler(this);		
	}
	
	public void add(MessageModel model, ChatRoom room) {
		String jit = model.getJid();
		if (asMap.containsKey(jit)) return;
		String display = room.displayName;
		InboxInfo info = new InboxInfo(model, display, false);
		asMap.put(jit, info);
		dataProvider.getList().add(0, info);
		info.reg = model.addValueChangeHandler(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<List<Message>> event) {
		Collections.sort(dataProvider.getList());
		dataProvider.refresh();
	}
	
	public Optional<InboxInfo> get(MessageModel m) {
		String jid = m.getJid();
		return Optional.ofNullable(asMap.get(jid));
	}
}
