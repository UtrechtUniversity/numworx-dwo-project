package nl.uu.fi.dwo.lms.chatgwt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;

public class EastHeader extends Composite {

	private static EastHeaderUiBinder uiBinder = GWT.create(EastHeaderUiBinder.class);

	interface EastHeaderUiBinder extends UiBinder<Widget, EastHeader> {
	}

	public EastHeader() {
		klas = new RadioButton("");
		persoon = new RadioButton("");

		initWidget(uiBinder.createAndBindUi(this));		
		klas.setValue(true);
	}
	
	private Consumer<ChatRoom> updateRoom;
	private Consumer<Boolean>  updateSelect;
	private static final Predicate<ChatUser> FALSE = t -> false;
	private Predicate<ChatUser> isUnread = FALSE;

	@UiField(provided=true) RadioButton klas, persoon;
	@UiField ListBox naam;
	
	private List<ChatRoom> roomList = Collections.singletonList(null);

	public void init(List<ChatRoom> rooms) {
		
		naam.clear();
		
		if (rooms.size() == 1) {
			naam.addItem(rooms.get(0).displayName);
			this.roomList = Collections.singletonList(rooms.get(0));
		} else {
			naam.addItem("");
			this.roomList = new ArrayList<>();
			this.roomList.add(null);
			rooms.forEach(item -> { naam.addItem(item.displayName); roomList.add(item); });
		}		
		naam.setSelectedIndex(0);
	}
	
	public ChatRoom getSelectedRoom() {
		return roomList.get(naam.getSelectedIndex());		
	}
	
	public boolean isMultichat() {
		return klas.getValue();
	}
	
	public void setMultiChat(boolean b) {
		if (b) klas.setValue(Boolean.TRUE);
		else persoon.setValue(Boolean.TRUE);
	}
	
	
	public void setUnread(ChatRoom room, boolean unread) {
		if (room == null) return;
		int index = roomList.indexOf(room);
		if (room == getSelectedRoom() && isMultichat()) unread = false;
		else {
			if (!unread && childrenUnread(room.chatUser)) unread = true;
		}
		naam.setItemText(index, room.displayName + (unread?" ●":""));
	}
	
	private boolean childrenUnread(List<ChatUser> chatUser) {
		for (ChatUser person : chatUser) {
			if (isUnread.test(person)) return true;
		}
		return false;
	}

	public boolean isMultiRoom() {
		return roomList.size() > 1;
	}
	
	@UiHandler("klas") void onKlas(ValueChangeEvent<Boolean> ev) {
		if (updateSelect != null) 
			updateSelect.accept(Boolean.TRUE);	
		setUnread(getSelectedRoom(), false);
		//persoon.setValue(false, false);
	}
	
	@UiHandler("persoon") void onPersoon(ValueChangeEvent<Boolean> ev) {
		if (updateSelect != null) 
			updateSelect.accept(Boolean.FALSE);
		//klas.setValue(false, false);
	}
	
	@UiHandler("naam") void onNaam(ChangeEvent ev) {
		FocusOnTouch.focus();
		if (updateRoom!=null) {
			updateRoom.accept(getSelectedRoom());
		}
		setUnread(getSelectedRoom(), false);
	}
	
//	@UiHandler("naam") void onFocusNaam(FocusEvent ev) {
//		ev.preventDefault();
//		FocusOnTouch.focus();
//	}

	/**
	 * @return the updateRoom
	 */
	public Consumer<ChatRoom> getUpdateRoom() {
		return updateRoom;
	}

	/**
	 * @param updateRoom the updateRoom to set
	 */
	public void setUpdateRoom(Consumer<ChatRoom> updateRoom) {
		this.updateRoom = updateRoom;
	}

	Consumer<Boolean> getUpdateMultiChat() {
		return updateSelect;
	}

	void setUpdateMultiChat(Consumer<Boolean> updateSelect) {
		this.updateSelect = updateSelect;
	}

	void setIsUnread(Predicate<ChatUser> predicate) {
		if (predicate == null) isUnread = FALSE;
		else isUnread = predicate;
	}
	
}
