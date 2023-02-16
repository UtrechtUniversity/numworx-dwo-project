package nl.uu.fi.dwo.lms.chatgwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.chatgwt.entities.*;

public class RoomController implements ValueChangeHandler<List<Message>> {
		
	class UserController implements ValueChangeHandler<List<Message>> {

		private ChatRoom room;
		private MessageModel roomModel;
				
		@Override
		public void onValueChange(ValueChangeEvent<List<Message>> event) {
			if (east.getSelectedRoom() != room)
				east.setUnread(room, roomModel.hasUnread());
		}

		private UserController(ChatRoom room, MessageModel roomModel) {
			this.room = room;
			this.roomModel = roomModel;
		}
		
	}
		
	Map<String, ChatRoom> rooms;
	EastHeader east;
	
	@Override
	public void onValueChange(ValueChangeEvent<List<Message>> event) {
		MessageModel model = (MessageModel) event.getSource();
		ChatRoom room = rooms.get(model.getJid());
		east.setUnread(room, hasUnread(event.getValue()));
	}

	public boolean hasUnread(List<Message> messages) {
		for(Message m: messages) {
			if (!m.isRead()) return true;
			
		}
		return false;
	}
	
	RoomController(List<ChatRoom> rooms, EastHeader east) {
		this.east = east;
		Function<ChatRoom, String> keyMapper = ChatRoom::getJid;
		Function<ChatRoom, ChatRoom> identity = Function.<ChatRoom>identity();
		this.rooms = rooms.stream().collect(Collectors.<ChatRoom,String,ChatRoom>toMap(keyMapper, identity));
	}
	
	HandlerRegistration addHandler(Function<ChatRoom, MessageModel> roommapper, Function<ChatUser, MessageModel> usermapper) {
		List<HandlerRegistration> listu = new ArrayList<>();
		for(ChatRoom room: rooms.values()) {
			MessageModel roomModel = roommapper.apply(room);
			List<ChatUser> users = room.chatUser;
			UserController controller = new UserController(room, roomModel);
			for (ChatUser u : users) {
				MessageModel m = usermapper.apply(u);
				listu.add(m.addValueChangeHandler(controller));
			}
		}		
		List<HandlerRegistration> list = rooms.values().stream().map(m -> roommapper.apply(m).addValueChangeHandler(this)).collect(Collectors.toList());
		list.addAll(listu);
		if (list.size() == 1) return list.get(0);
		return () -> list.forEach(HandlerRegistration::removeHandler);
		
	}
}
