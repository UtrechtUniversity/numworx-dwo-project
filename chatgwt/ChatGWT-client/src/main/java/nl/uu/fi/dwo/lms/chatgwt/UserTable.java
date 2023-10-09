package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.stanziq.strophe.client.Element;
import com.stanziq.strophe.client.Handler;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;


import static nl.uu.fi.dwo.lms.chatgwt.ChatGWT.dworb;
import static nl.uu.fi.dwo.lms.chatgwt.ChatGWT.rb;

class UserTable extends Composite implements ProvidesKey<UserModel>, ValueChangeHandler<Set<String>> {

	
	static CellTable.Resources RESOURCES = GWT.create(UserTable.Resources.class);
	
	interface Resources extends CellTable.Resources {

		@Source("nl/uu/fi/dwo/lms/chatgwt/resources/UserTable.css")
		Style cellTableStyle();
		
	}
			
			
	
	private CellTable<UserModel> table;
	private List<UserModel> data;

    TextColumn<UserModel> nameColumn = new TextColumn<UserModel>() {
        @Override
        public String getValue(UserModel item) {
          return item.getUser().nickName;
        }
      };

    TextColumn<UserModel> onlineColumn = new TextColumn<UserModel>() {
    	
		@Override
		public String getValue(UserModel object) {
			return object.isOnline() ? "●" : "";
		}		
	};

//	TextColumn<UserModel> unseenColumn = new TextColumn<UserModel>() {
//		@Override
//		public String getValue(UserModel object) {
//			return object.hasUnseen()? "●" : "";
//		}		
//	};
	private ListDataProvider<UserModel> provider;
	private ChatRoom room;
	private RoleType role;
	private ChatGWT parent;
	
	void setSelectionModel(SelectionModel<UserModel> selection, boolean gui) {
		table.setSelectionModel(selection);
		table.setStyleName("selected-gui", gui);
	}
	
	static final ChatRoom NULL = new ChatRoom(); static { NULL.chatUser = Collections.emptyList(); }
	
	UserTable(ChatRoom room2, RoleType role, SelectionModel<UserModel> selection, ChatGWT parent) {
		this.room = room2;
		this.role = role;
		this.parent = parent;
		if (room == null) {
			room = NULL;
		}
		table = new CellTable<>(Short.MAX_VALUE, RESOURCES, this);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.setSelectionModel(selection);
		table.addStyleName("dwo");
		String naam = "name";
		switch(role) {
		case TEACHER: naam = dworb.NUM_LBL_TEACHERS(); break;
		case STUDENT: naam = dworb.NUM_LBL_STUDENTS(); break;
		default:
		}
		nameColumn.setSortable(true);
		onlineColumn.setSortable(true);
		//unseenColumn.setSortable(true);
		table.setTableLayoutFixed(true);
		table.addColumn(nameColumn, naam);
		onlineColumn.setCellStyleNames("blu");
		//unseenColumn.setCellStyleNames("gre");
		table.addColumn(onlineColumn, rb.online());
		//table.addColumn(unseenColumn, rb.newMessages());
		
		table.setColumnWidth(onlineColumn, 100, Unit.PX);
		//table.setColumnWidth(unseenColumn, 150, Unit.PX);
		// initialize the data
		
		List<UserModel> initialData = toUserModelList();
		
		provider = new ListDataProvider<>(initialData, this);
		data = provider.getList();
		provider.addDataDisplay(table);
		
		initWidget(table);
	}

	List<UserModel> toUserModelList() {
		Set<String>  presence = parent.getPresence();
		return room.chatUser.stream().filter(item -> this.role == item.role).map(item -> {
			MessageModel messageModel = parent.get(item);
			UserModel userModel = new UserModel(item, room, messageModel);
			userModel.setOnline(presence.contains(userModel.getRoomJit()));
			userModel.setRegistration(messageModel.addValueChangeHandler(this::onMessageModelChange));
			return userModel;
		}).collect(Collectors.toList());
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}


	@Override
	public Object getKey(UserModel item) {
		return item.getRoomJit();
	}

	@Override
	public void onValueChange(ValueChangeEvent<Set<String>> event) {
		final Set<String> value = event.getValue();
		int size = data.size();
		boolean set = false;
		for(int i = 0; i < size; i++) {
			UserModel item = data.get(i);
			boolean online = value.contains(item.getRoomJit());
			if (item.isOnline() != online) {
				item.setOnline(online);
				data.set(i, item);
				set = true;
			}
		}
		if (set) {
			provider.flush();
			table.flush();
		}
	}
	
	private void onMessageModelChange(ValueChangeEvent<List<Message>> event) {
		provider.refresh();
		provider.flush();
		table.flush();
	}
	
	
	private class MyHandler extends Handler<Element> {

		@Override
		public boolean handle(Element element) {
			return true;
		}
		
	}
	
	Handler<Element> handler = new MyHandler();
	
	
	Handler<Element> asHandler() {
		return handler;
	}

	public void init(ChatRoom room) {
		if (room == null) {
			room = NULL;
		}

		this.room = room;
		data.forEach(UserModel::clearRegistration);
		data.clear();
		data.addAll(toUserModelList());
		provider.refresh();
	}
}
