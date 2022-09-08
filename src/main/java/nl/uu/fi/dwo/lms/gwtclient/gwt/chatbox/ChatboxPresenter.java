package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class ChatboxPresenter implements ValueChangeHandler<String> {

	public interface Display extends BasicDisplay {

		void setLogin(ChatUser user);

		void openUrl(String url);
		
	}
	
	final private DwoGlobalVars vars;
	
	private Display view;

	private Optional<PersonsService> service;

	private ChatUser user = new ChatUser();
	
	
	@Inject ChatboxPresenter(DwoGlobalVars vars, EventBus bus, Optional<PersonsService> service) {
		this.vars = vars;
		this.service = service;
		
		//RestAuthenticator.instance.addValueChangeHandler(this);
		bus.addHandlerToSource(ValueChangeEvent.getType(), RestAuthenticator.instance, this); // resettable eventbus, helaas werkt niet want Authenticator gebruikt andere bus
		view.openUrl("about:blank");
	}
	
	@Inject void setView(Display view) {
		this.view = view;
	}

	public void init() {
		DomUserFull u = vars.getCurrentUser();
		if (u == null) {
			view.setLogin(null);
			view.openUrl("about:blank");
			return;
		}
		user.nickName = u.getUniqueDisplayName();
		user.jid = u.getId().getIdString();
		String password = RestAuthenticator.instance.getAuthorization(); // access token of so
		user.token = strip(password);
        RoleType role = RoleType.valueOf(vars.getActiveSchoolRoleAndClass().getRole().getRoleName());    
		user.role  = role;
		if (role == RoleType.STUDENT) {
			DomSchoolClass klas = vars.getActiveSchoolRoleAndClass().getSchoolClass();
			ChatRoom room = roomOfSchoolClass(klas);
			user.room = Collections.singletonList( room );
			view.setLogin(user);			
			view.openUrl("chatbox.jsp");
		} else if (role == RoleType.TEACHER) {
			// teacher
			service.get().getTeachersSchoolClasses().map(this::roomOfSchoolClass)
			.then( p -> {
				user.room = p.getValue();
				view.setLogin(user);				
				view.openUrl("chatbox.jsp");
				return p;
			});
			return;
		}
		view.openUrl("about:blank");
	}

	private ChatRoom roomOfSchoolClass(DomSchoolClass klas) {
		ChatRoom room = new ChatRoom();
		room.displayName = klas.getSchoolClassName();
		room.jid = klas.getId().getIdString();
		return room;
	}
	
	private List<ChatRoom> roomOfSchoolClass(List<DomSchoolClass> list) {
		return list.stream().map(this::roomOfSchoolClass).collect(Collectors.toList());
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		DomUserFull u = vars.getCurrentUser();
		if (u != null) {
			String password = event.getValue();
			user.token = strip(password);
			view.setLogin(user);
		}
		else {
			view.setLogin(null);
			view.openUrl("about:blank");			
		}
	}

	private String strip(String password) {
		if (password.toLowerCase().startsWith("basic ")) return password.substring(6);
		if (password.toLowerCase().startsWith("bearer ")) return password.substring(7);
		return "None";
	}
}
