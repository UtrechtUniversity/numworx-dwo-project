package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class ChatboxPresenter implements ValueChangeHandler<String>, LoginEventHandler {

	public interface Display extends BasicDisplay {

		void setLogin(ChatUser user);

		void openUrl(String url);
		
	}
	
	final private DwoGlobalVars vars;
	
	private Display view;

	private Optional<PersonsService> service;

	private ChatUser user = new ChatUser();

	private LoggingFailure FAILURE;
    private static final Logger LOG = Logger.getLogger(ChatboxPresenter.class.getName());
	
	
	@Inject ChatboxPresenter(DwoGlobalVars vars, EventBus bus, Optional<PersonsService> service) {
		this.vars = vars;
		this.service = service;
	    FAILURE = new LoggingFailure(LOG, bus);
		
		//RestAuthenticator.instance.addValueChangeHandler(this);
		bus.addHandlerToSource(ValueChangeEvent.getType(), RestAuthenticator.instance, this); // resettable eventbus, helaas werkt niet want Authenticator gebruikt andere bus
		bus.addHandler(LoginEvent.TYPE, this);
	}
	
	@Inject void setView(Display view) {
		this.view = view;
		view.init();
		view.clear();
		inited=false;
	}

	private boolean inited;

	private final Success<? super List<ChatRoom>, ? extends List<ChatRoom>> success = p -> {
		user.room = ( p.getValue() );
		view.setLogin(user);			
		view.openUrl("chatbox/");
		return p;
	};
	
	public void init() {
		if (inited) return;
		inited = true;
		
		
		DomUserFull u = vars.getCurrentUser();
		if (u == null) {
			view.setLogin(null);
			view.clear();
			return;
		}
        RoleType role = vars.getRole();    
        user = new ChatUser(u, role);
		String password = RestAuthenticator.instance.getAuthorization(); // access token of so
		user.token = strip(password);
		if (role == RoleType.STUDENT) {
			DomSchoolClass klas = vars.getActiveSchoolRoleAndClass().getSchoolClass();
			Promise<ChatRoom> room = roomOfSchoolClass(klas);
			
			room
			.map(Collections::singletonList)
			.then( success, FAILURE);
			return;
		} else if (role == RoleType.TEACHER) {
			// teacher
			service.get().getTeachersSchoolClasses().flatMap(this::roomOfSchoolClass)
			.then( success, FAILURE);
			return;
		}
		view.clear();
	}

	private Promise<ChatRoom> roomOfSchoolClass(DomSchoolClass klas) {
		ChatRoom room = new ChatRoom(klas);
		if ( service.isPresent() ) {
			Promise<List<DomStudent>> students = service.get().getStudentsInSchoolClass(klas);
			Promise<List<DomTeacher>> teachers = service.get().getTeachersInSchoolClass(klas);
			
			Promise<Stream<ChatUser>> u1 = students.map(list -> list.stream().map(ChatUser::new));
			Promise<Stream<ChatUser>> u2 = teachers.map(list -> list.stream().map(ChatUser::new));
			
			Promise<List<Stream<ChatUser>>> all = Promises.all(u1, u2);
			return all.map(streams -> {
				Stream<ChatUser> s = streams.stream().flatMap(Function.identity());
				room.chatUser = s.collect(Collectors.toList());
				return room;		
			});
		}
		room.chatUser = Collections.singletonList(new ChatUser(user));
		return Promises.resolved(room);
	}

	private Promise<List<ChatRoom>> roomOfSchoolClass(List<DomSchoolClass> list) {
		List<Promise<ChatRoom>> promises = list.stream().map(this::roomOfSchoolClass).collect(Collectors.toList());
		Promise<List<ChatRoom>> result = Promises.all(promises);
		return result;
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
			view.clear();			
		}
	}

	private String strip(String password) {
		if (password.toLowerCase().startsWith("basic ")) return password.substring(6);
		if (password.toLowerCase().startsWith("bearer ")) return password.substring(7);
		return "None";
	}

	@Override
	public void onLoginEvent(LoginEvent loginEvent) {
		LOG.info("catch " + loginEvent.getState());
		view.clear();
		inited = false;
	}
}
