package nl.uu.fi.dwo.lms.chatgwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.lms.chatgwt.entities.*;

public class RoomController implements ValueChangeHandler<List<Message>> {
	
	Map<String, ChatRoom> rooms;
	EastHeader east;
	
	@Override
	public void onValueChange(ValueChangeEvent<List<Message>> event) {
		MessageModel model = (MessageModel) event.getSource();
		ChatRoom room = rooms.get(model.getJid());
		east.setUnread(room, !event.getValue().isEmpty());
	}

	RoomController(List<ChatRoom> rooms, EastHeader east) {
		this.east = east;
		Function<ChatRoom, String> keyMapper = ChatRoom::getJid;
		Function<ChatRoom, ChatRoom> identity = Function.<ChatRoom>identity();
		this.rooms = rooms.stream().collect(Collectors.<ChatRoom,String,ChatRoom>toMap(keyMapper, identity));
	}
	
	HandlerRegistration addHandler(Function<ChatRoom, MessageModel> mapper) {
		List<HandlerRegistration> list = rooms.values().stream().map(m -> mapper.apply(m).addValueChangeHandler(this)).collect(Collectors.toList());
		if (list.size() == 1) return list.get(0);
		return () -> list.forEach(HandlerRegistration::removeHandler);
		
	}
}
