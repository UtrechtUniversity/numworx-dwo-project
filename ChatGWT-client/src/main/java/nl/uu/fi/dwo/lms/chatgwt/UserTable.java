package nl.uu.fi.dwo.lms.chatgwt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.stanziq.strophe.client.Element;
import com.stanziq.strophe.client.Handler;

import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;


class UserTable extends Composite implements ProvidesKey<UserModel>, ValueChangeHandler<Set<String>> {

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

	Column<UserModel, Boolean> unseenColumn = new Column<UserModel, Boolean>(new CheckboxCell()) {
		@Override
		public Boolean getValue(UserModel object) {
			return object.hasUnseen();
		}		
	};
	private ListDataProvider<UserModel> provider;
	
	
	
	UserTable(ChatRoom room, RoleType role, SelectionModel<UserModel> selection) {
		table = new CellTable<>(this);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		table.setSelectionModel(selection);
		table.addStyleName("dwo");
		String naam = "naam";
		switch(role) {
		case TEACHER: naam = "docenten"; break;
		case STUDENT: naam = "studenten"; break;
		}
		table.addColumn(nameColumn, naam);
		onlineColumn.setCellStyleNames("blu");
		table.addColumn(onlineColumn, "online");
		table.addColumn(unseenColumn, "nieuwe berichten");
		
		// initialize the data
		
		List<UserModel> initialData = room.chatUser.stream().filter(item -> role == item.role).map(item -> new UserModel(item, room)).collect(Collectors.toList());
		
		provider = new ListDataProvider<>(initialData, this);
		data = provider.getList();
		provider.addDataDisplay(table);
		
		initWidget(table);
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
		for(int i = 0; i < size; i++) {
			UserModel item = data.get(i);
			boolean online = value.contains(item.getRoomJit());
			if (item.isOnline() != online) {
				item.setOnline(online);
				data.set(i, item);
			}
		}		
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
}
