package nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatRoom;
import nl.uu.fi.dwo.lms.chatgwt.entities.ChatUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class ChatboxPresenter implements ValueChangeHandler<String>, LoginEventHandler, ChatboxEvent.ChatboxHandler, SwitchViewEventHandler {

	public interface Display extends BasicDisplay {

		void setLogin(ChatUser user);

		void openUrl(String url);

		void setUnseen(boolean b);
		
	}
	
	final private DwoGlobalVars vars;
	
	private Display view;
	private MainPresenter.Display mainView;

	private Optional<PersonsService> service;

	private ChatUser user = new ChatUser();

	private LoggingFailure FAILURE;
    private static final Logger LOG = Logger.getLogger(ChatboxPresenter.class.getName());
	private int profile;
	private final EventBus bus;
	
	@Inject ChatboxPresenter(DwoGlobalVars vars, EventBus bus, Optional<PersonsService> service, BootPanelController boot, SecuredUserAccountManager mgr) {
		this.vars = vars;
		this.service = service;
		this.profile = boot.getProfile();
		this.bus = bus;
	    FAILURE = new LoggingFailure(LOG, bus);
	    this.accountManager = mgr;
		
		//RestAuthenticator.instance.addValueChangeHandler(this);
		bus.addHandlerToSource(ValueChangeEvent.getType(), RestAuthenticator.instance, this); // resettable eventbus, helaas werkt niet want Authenticator gebruikt andere bus
		bus.addHandler(LoginEvent.TYPE, this);
		bus.addHandler(ChatboxEvent.TYPE, this);
	}
	
	@Inject void setView(Display view) {
		this.view = view;
		view.init();
		view.clear();
		view.setHelp(vars.buildHelpUrl("#chatbox"));
		inited=false;
	}
	
	@Inject void setMainView(ViewFactory viewFactory) {
		mainView = viewFactory.getMainView();
	}

	private boolean inited;

	private final Success<? super List<ChatRoom>, ? extends List<ChatRoom>> success = p -> {
		user.room = ( p.getValue() );
		view.setLogin(user);
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		view.openUrl("chatbox/?profile=" + profile + "&locale=" + locale);
		return p;
	};

	private SecuredUserAccountManager accountManager;
	
	public void init() {
		// basic login check
		
		
		
		if (inited) {
			accountManager.getLoginContext().then(p -> {
				view.setUnseen(false);
				return p;
			}
			, FAILURE);
			return;
		}
		inited = true;
		
		
		DomUserFull u = vars.getCurrentUser();
		if (u == null) {
			view.setLogin(null);
			view.clear();
			return;
		}
        RoleType role = vars.getRole(); 
        
        DomUser uu = new DomUser(u);
        uu = mapRealm(uu);
        user = new ChatUser(uu, role);
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
			.then( success, FAILURE)

//			.onResolve(() -> bus.fireEvent(new ChatboxEvent(":unseen")));

			;
			return;
		}
		view.clear();
	}

	private <T extends DomUser> T mapRealm(T uu) {
		String realm = Objects.toString(vars.getRealm(),"");
		String username = uu.getUserName();
		int at = username.indexOf('@');
		if (at == -1) {
			username += realm;
		} else if (at == username.length()-1) {
			username = username.substring(0, at);
		}
		uu.setUserName(username.replace('@', '%'));
		return uu;
	}

	private Promise<ChatRoom> roomOfSchoolClass(DomSchoolClass klas) {
		ChatRoom room = new ChatRoom(klas);
		if ( service.isPresent() ) {
			Promise<List<DomStudent>> students = service.get().getStudentsInSchoolClass(klas);
			Promise<List<DomTeacher>> teachers = service.get().getTeachersInSchoolClass(klas);
			
			Promise<Stream<ChatUser>> u1 = students.map(list -> list.stream().map(this::mapRealm).map(ChatUser::new));
			Promise<Stream<ChatUser>> u2 = teachers.map(list -> list.stream().map(this::mapRealm).map(ChatUser::new));
			
			Promise<List<Stream<ChatUser>>> all = Promises.all(u1, u2);
			return all.map(streams -> {
				Stream<ChatUser> s = streams.stream().flatMap(Function.identity());
				room.chatUser = s.filter(this::exceptMe).collect(Collectors.toList());
				return room;		
			});
		}
		room.chatUser = Collections.singletonList(new ChatUser(user));
		return Promises.resolved(room);
	}

	private boolean exceptMe(ChatUser u) {
		return ! u.jid.equals(user.jid);
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

	@Override
	public void onChatbox(ChatboxEvent event) {
		if (event.getParam().endsWith(":unseen")) {
			view.setUnseen(true);
			return;
		}		
		if (event.getParam().endsWith(":seen")) {
			view.setUnseen(false);
			return;
		}		
	}

	@Override
	public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
		switch (switchViewEvent.getEventValue()) {
			case ACCOUNT: 
			case RESULTS:
			case KNOWLEDGE:
			case PERSONS:
			case SCHOOLCLASSES:
			case MODULES:
			case WELCOME:
			case ORGANISATION:
				idleOn();
			default:
		}
		
	}
	
	private HandlerRegistration r;
	
	public void idleOff() {
		mainView.unsetIdleTimeout();
		if (r == null) r = bus.addHandler(SwitchViewEvent.TYPE, this);
	}
	public void idleOn() {
		if (r != null) {
			r.removeHandler();
			r =null;
		}
		mainView.setIdleTimeout(MainPresenter.IDLE);
	}
	
}
