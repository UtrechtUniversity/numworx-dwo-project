package nl.uu.fi.dwo.lms.gwtclient.gwt.modules;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import dagger.Lazy;
import jsinterop.annotations.JsMethod;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.ChatboxEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
@Singleton
public class ModulesPresenter implements SwitchViewEventHandler {

    private static final Logger LOG = Logger.getLogger(ModulesPresenter.class.getName());

    private static final String SHOWMAINNAV = "showMainNav";
    private static final String HIDEMAINNAV = "hideMainNav";
    private static final String ISMAINNAVVISIBLE = "isMainNavVisible";
    private static final String TRAIL = SelectedView.TRAIL.name();
    private static final String RETOUR = SelectedView.RETOUR.name();
    private static final String VISIBLE = "shown";
    private static final String INVISIBLE = "hidden";

    private final Failure FAILURE;
    private final EventBus eventBus;
    private Display view;
    private String url="/dwo/tablet/DWOplayer.jsp";
    @Inject AccountService account;
    private Promise<String> init;
    private PersistenceId roleId, schoolClassId;
    @Inject Lazy<BootPanelController> controller; // lazy anders cycle

    private HandlerRegistration register;

    private DwoGlobalVars dwoGlobalVars;

    private MainPresenter.Display mainView;

    private static final boolean tablet;
    static {
//     OsDetection osDetection = MGWT.getOsDetection();
// a tablet is a ipad, iphone, android, not a desktop
      tablet = true; // !osDetection.isDesktop();   // FIXME voor Teunis, als uitgeklapt goed werkt, weer aanzetten: tablet = ! osDetection.isDesktop(); 
      LOG.fine("OsDetection " + tablet);
    }

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    @Inject void setView(Display view) {
        this.view = view;
    }

    public interface Display extends BasicDisplay{
        public void openUrl(String url);

        public void setMainNavVisible(boolean b);
        public boolean isMainNavVisible();
        public void sendMessage(String message);
    }

    @Inject ModulesPresenter(SimpleEventBus anEventBus, DwoGlobalVars aDwoGlobalVars,  ViewFactory viewFactory) {
        eventBus = anEventBus;
        FAILURE = new LoggingFailure(LOG, anEventBus);
        injectEventListener(this);
        this.dwoGlobalVars = aDwoGlobalVars;
        this.mainView = viewFactory.getMainView();
    }

    public Promise<String> show(String message) {
    	return show().then(p -> { view.sendMessage(message); return p;});
    }
    
    public Promise<String> show() {
      if (init == null || (init.isDone() && init.getFailure() != null)) {
        init();
      } 
      return Promises.all(dwoGlobalVars.getProfile(),init).then(p-> {
          if(register == null)
          {
        	  register = eventBus.addHandler(SwitchViewEvent.TYPE, this);
          }

          LOG.info("switch to modules view " + p.getValue());
          eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.MODULESVIEW));
          mainView.unsetIdleTimeout();
          if(tablet()) {
            view.setMainNavVisible(false);
            view.sendMessage(HIDEMAINNAV);
          } else {
            view.setMainNavVisible(true);
            view.sendMessage(SHOWMAINNAV);
          }
          return inited.getPromise();
        });
    }
 
    private boolean tablet() {
    	Promise<DomDwoProfileFull> profile = dwoGlobalVars.getProfile();
		if (profile.isDone() && dwoGlobalVars.getRole() != RoleType.ANONYMOUS) {
    		if (profile.getValue().getDwoProfileRights().contains("n"))
    			return false;
    	}
		return tablet;
	}

	boolean legal(String base) {
      RegExp r = RegExp.compile("^/[a-z]+(/[a-z]+)*/$");
      return r.test(base);
    }
   
    public void init() {
      view.clear();
      view.init();
      roleId = dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole().getId();
      DomSchoolClass klas = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchoolClass();
      schoolClassId = klas == null ? null : klas.getId();
      LOG.fine("role = " + roleId);
      inited = new Deferred<>();
//      lastSend = false;
      if (roleId != null)
        init = account.getBearerTokenV2().then(this::gotToken,FAILURE);
      else
      {
        UrlBuilder u = new UrlBuilder();
        u.setPath(url);
        u.setProtocol(Location.getProtocol());
        u.setHost(Location.getHost());
        u.setHash("#guest:");
        u.setParameter("header", "none");
        String base = Location.getParameter("base");
        if(base != null && !base.isEmpty() && legal(base)) {
          u.setParameter("base",base);
        }
        String profile = String.valueOf(controller.get().getProfile());
        u.setParameter("profile",profile);
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
        if ("default".equals(locale) ) locale =  "nl";
        u.setParameter("locale",locale);
        String responsive = Location.getParameter("responsive");
        if (responsive != null) {
        	u.setParameter("responsive", "true");
        }
        String string = u.buildString();
        LOG.info("open URL " + string);
        view.openUrl(string);
        init =  Promises.resolved(string);
       }
      }
    

    /*
     * bearer token v2 has arrived:
     */
    public Promise<String> gotToken(Promise<String> resolved) { // getBearerToken nieuwe stijl
     String token = resolved.getValue(); //format 2 of 5
     UrlBuilder u = new UrlBuilder();
     u.setPath(url);
     u.setProtocol(Location.getProtocol());
     u.setHost(Location.getHost());
     u.setParameter("a",token); // User Auth Token
     u.setParameter( "header","none");
     String base = Location.getParameter("base");
     if(base != null && !base.isEmpty() && legal(base)) {
       u.setParameter("base",base);
     }
     String profile = Location.getParameter("profile");
     if(profile == null || profile.isEmpty()|| !RegExp.compile("^\\d+$").test(profile)) profile = "77";
     u.setParameter("profile",profile);
     String locale = LocaleInfo.getCurrentLocale().getLocaleName();
     if ("default".equals(locale) ) locale =  "nl";
     u.setParameter("locale",locale);
     String responsive = Location.getParameter("responsive");
     if ("true".equals(responsive)) {
     	u.setParameter("responsive", "true");
     }
     String string = u.buildString();
     LOG.info("open URL " + string);
     view.openUrl(string);
     return Promises.resolved(string);
    }
    
    private native void injectEventListener(ModulesPresenter p) /*-{
      function postMessageListener(e) {
          //var curUrl = $wnd.location.protocol + "//" + $wnd.location.hostname;
          //if (e.origin !== curUrl) return; // security check to verify that we receive event from trusted source
          p.@nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter::onMessage(Ljava/lang/String;)(e.data); // call function with the name
      }
      // Listen to message from child window
      if (window.addEventListener) {
          // "Normal" browsers
          $wnd.addEventListener("message", postMessageListener, false);
      } else {
          // fucking IE
          $wnd.attachEvent("onmessage", postMessageListener, false);
      }
    }-*/;

    
    
    @JsMethod
    public void onMessage(String message) {
        LOG.fine("onMessage " + message);
        if (SHOWMAINNAV.equals(message)) {
          view.setMainNavVisible(true);
 //         view.sendMessage(SHOWMAINNAV);
        } else
        if (HIDEMAINNAV.equals(message)) {
          view.setMainNavVisible(false);
 //         view.sendMessage(HIDEMAINNAV);
        } else
        if (ISMAINNAVVISIBLE.equals(message)) {
          view.sendMessage( !tablet() ? SHOWMAINNAV : HIDEMAINNAV);
        } else 
        if (select(SelectedView.RESULTS, message) 
        		|| select(SelectedView.KNOWLEDGE, message)
        		|| select(SelectedView.CHATBOX, message)
        		|| select(SelectedView.MODULES, message)
        		|| select(SelectedView.PERSONS,message) 
        		|| select(SelectedView.SCHOOLCLASSES,message)
        		|| select(SelectedView.RETOUR, message)
        		|| select(SelectedView.ORGANISATION, message))
        {
          mainView.setIdleTimeout(MainPresenter.IDLE);
        } else if (select(SelectedView.TRAIL,message)||select(SelectedView.HOME, message)) {
        } 
        else if (message.startsWith(TRAIL +":")) {
          message = message.substring(TRAIL.length()+1);
          JavaScriptObject o = JSONParser.parseLenient(message).isArray().getJavaScriptObject();
          SwitchViewEvent event = new SwitchViewEvent(SelectedView.TRAIL, o);
          eventBus.fireEvent(event);
        } else if ("LOGOUT".equals(message)) {
        	if (logout != null)
        	{   view.clear();
        		logout.resolve(Boolean.TRUE);
        		logout = null;
        	}
        	else
        		eventBus.fireEvent(new LoginEvent(State.LOGOUT));
        } else if ("EXAM".equals(message)) {
          controller.get().setSession(false);
        } else if (isVisible() && select(SelectedView.MAYBELOGOUT, message)) {
          
        } else if (isVisible() && "LOGINNEEDED".equals(message)) {
        	loginNeeded();
        } else if ("INITED".equals(message)) {
        	if (!inited.getPromise().isDone())inited.resolve(message);
        } else if (message.startsWith("Chatbox:"))
        {
        	ChatboxEvent ev = new ChatboxEvent(message);
        	eventBus.fireEvent(ev);
        }
    }

    private void loginNeeded() {
		Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Rest_LoginNeeded, "")).then(null, FAILURE);
	}

// true if modules/chatbox visible.    
    private boolean isVisible() {
		return !mainView.isIdleOn();
	}

	private boolean select(SelectedView select, String message) {
      if(select.name().equals(message)) {
        eventBus.fireEvent(new SwitchViewEvent(select));
        return true;
      }
      return false;
    }

    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      SelectedView select = switchViewEvent.getEventValue();
      switch(select) {
        case ACCOUNT:
          mainView.setIdleTimeout(MainPresenter.IDLE);
          break;
        case GOTO:
          String cmd = switchViewEvent.getSearch().get("message");
          LOG.info("goto "+cmd);
          view.sendMessage(cmd);
        case ARROWUP:
          LOG.info("sending arrowUp message");
          view.sendMessage(select.name());
          return;
        case SEARCH:
          final Map<String, String> search = (switchViewEvent.getSearch());
          LOG.info("sending search message " + search);
          String message = select.name() + ":" + toString(search);
          view.sendMessage(message);
        case TRAIL: 
        case MODULES:
        case MODULESVIEW:
        case MAYBELOGOUT:
        case GOTO_URL:
        case HOME:
          return;
        case CLOSING:
        	view.sendMessage(select.name());
        	return;
        default:
      }
      if(select == SelectedView.WELCOME) {
        LOG.fine( "old role "  + roleId);
        PersistenceId newRole = dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole().getId();
        DomSchoolClass klas = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchoolClass();
        PersistenceId newSchoolClass = klas == null ? null : klas.getId();
        LOG.fine(" new role " + newRole + " new klas = " + newSchoolClass);
        if (!equals(roleId, newRole) || !equals(schoolClassId, newSchoolClass) )
        { LOG.info("hasRole changed"); 
          roleId = null;
          schoolClassId = null;
          init = null;
          inited = new Deferred<>();
          view.clear();
          if(register != null) {
            register.removeHandler();register = null;
          }
        } else {
          view.sendMessage("GOTO:");
          final RoleType role = dwoGlobalVars.getRole(); // FIXME herontwerp getactive...
          if (role == RoleType.STUDENT || role == RoleType.ANONYMOUS) return;
        }
        mainView.setIdleTimeout(MainPresenter.IDLE);
        view.sendMessage(INVISIBLE);
      }
// switch to other view.     
      LOG.info("switch " + select);
      view.setMainNavVisible(true);
//     view.sendMessage(SHOWMAINNAV);
    }

    private String toString(Map<String, String> search) {
      JSONObject obj = new JSONObject();
      search.forEach((k,v) -> obj.put(k, new JSONString(v)));
      return obj.toString();      
    }

    private static boolean equals(PersistenceId id1, PersistenceId id2) {
      if (id1 == null) return id2 == null;
      return id1.equals(id2);
    }

    Deferred<Boolean> logout;
    Deferred<String>  inited;
    public Promise<Boolean> logout(Promise<Boolean> p) {
      if (p.getValue() && init != null) {
        logout = new Deferred<>();
        view.sendMessage("LOGOUT");
        return logout.getPromise();
      }
      return p;
    }
//	@Override
//	public void onLoginEvent(LoginEvent loginEvent) {
//		if (loginEvent.getState() == State.LOGOUT)
//			view.sendMessage("LOGOUT");
//	}

	public void gotoHome() {
		inited.getPromise().then( p -> {
			view.sendMessage("GOTO:TreeModulePlace:0");
			return p;
		});		
	}
	
//	private boolean lastSend; // is geen success!
	public void gotoLast() {
//		if (lastSend || tablet()) return;
//		lastSend=true;
//		inited.getPromise().then( p -> {
//			view.sendMessage("GOTO:last:");
//			return p;
//		});		
	}

	public void setVisible(boolean b) {
		if (b)
			view.sendMessage(VISIBLE);
		else
			view.sendMessage(INVISIBLE);
		
	}

}
