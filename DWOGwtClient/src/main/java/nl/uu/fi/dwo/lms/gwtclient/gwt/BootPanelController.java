package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.ResettableEventBus;

import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicStatusManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;

import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.GuestComponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.PresenterBuilder;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.SchoolAdminComponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.StudentComponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.TeacherComponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil.DwoMessageTranslator;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientModulesOnly;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;
import org.osgi.util.promise.Promise;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

import org.osgi.util.promise.Success;

/**
 * TeacherApplication
 *
 * @author Gert van der Plas
 */
@Singleton //required.
public class BootPanelController {

    public static native void topReload() /*-{
      $wnd.top.location.reload(false);
    }-*/;

	final class LoginHandler implements LoginEventHandler {
		
    @Override
    public void onLoginEvent(LoginEvent loginEvent) {
        DomUserFull user = dwoGlobalVars.getCurrentUser();
        final RoleType role = user == null ? RoleType.NONE : dwoGlobalVars.getRole();
        final State state = loginEvent.getState();
		final boolean single = user != null && user.getSingleSchool();
        if (state != State.LOGOUT)
        	resetPresenters(role,single); // changes in eventbus are not immediate
        Scheduler.get().scheduleDeferred(
            () -> {
                switch (state) {
                  case SUCCESS_GUEST:
                    setSession(false);
                    viewFactory.getMainView().setSchoolName("");
                    viewFactory.getMainView().setPresentationName("");
                    //eventBus.fireEvent(new SwitchViewEvent(SelectedView.MODULES));
                    sendSwitchEvent(SelectedView.MODULES);
                    break;
                  
//                  case SUCCESS:
                  case SUCCESS_WELCOME:
                    setSession(true);
                    LOG.log(Level.INFO, "Login succeeded. Showing welcome view.");
                    viewFactory.getMainView().setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
                    viewFactory.getMainView().setPresentationName(user.getDisplayName());
                    SelectedView view = initialView;
                    initialView = SelectedView.WELCOME;
                    //eventBus.fireEvent(new SwitchViewEvent(view));
                    sendSwitchEvent(view);
                    // viewFactory.getMainView().showPostLoginWidgets();
                    break;
                  case SUCCESS_ROLE:
                    LOG.log(Level.INFO, "Login succeeded. Showing account view for teacher.");
                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));      
                    break;
//                  case SUCCESS_RESULTS:
//                    setSession(true);
//                    LOG.log(Level.INFO, "Login succeeded. Showing results view.");
//                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
//                    // viewFactory.getMainView().showPostLoginWidgets();
//                    break;
//                  case SUCCESS_SCHOOLCLASSES:
//                    setSession(true);
//                    LOG.log(Level.INFO, "Login succeeded. Showing schoolclasses view.");
//                    eventBus
//                        .fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
//                    // viewFactory.getMainView().showPostLoginWidgets();
//                    break;
                  case FAIL:
                    LOG.log(Level.INFO, "Login failed, reload dialog.");
                    if (dwoGlobalVars.isSaml())
                    	topReload();
                    else
                    	eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.LOGIN));
                    break;
                  case LOGOUT:
                    dwoGlobalVars.clearCurrentUser();
                    setSession(false);
                    {
                      History.newItem("", false);
                      if (dwoGlobalVars.isSaml() || needLogout()) // running under SAML protection
                        logout();
                      else
                      { boolean test = Window.Location.getParameterMap().containsKey("a");
                        if (test) {
                          UrlBuilder builder = Window.Location.createUrlBuilder();
                          builder.removeParameter("a");
                          builder.setHash(null);
                          Window.Location.assign(builder.buildString());
                        }
                        else {  
                          Window.Location.reload();
                        }
                      }
                    } 
                    break;
                  default:
                    LOG.log(Level.SEVERE, "Login handling failed in app controller.");
                }
              });
    }
  }

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());

    @Inject
    ViewFactory viewFactory;
    
    PresenterFactory presenterFactory;
    SwitchViewEventHandler viewHandler;

    public final Runnable RETOUR_WELCOME_STUDENT = () -> {
    	Display mainView = viewFactory.getMainView();
        mainView.selectView(SelectedView.WELCOME);
        ModulesPresenter modules = presenterFactory.getModulesPresenter();
		modules.show();
        modules.gotoHome();
    };
    
    
    public final Runnable RETOUR_WELCOME = () -> {
    	viewFactory.getMainView().selectView(SelectedView.WELCOME);
    	viewFactory.getMainView().showWelcomeView();
    };

    public final Runnable RETOUR_STUDENT_KNOWLEDGE = () -> {
    	viewFactory.getMainView().selectView(SelectedView.KNOWLEDGE);
    	viewFactory.getMainView().showStudentResults();
    	if (presenterFactory instanceof PresenterFactoryGwt)
    		((PresenterFactoryGwt) presenterFactory).getSMResultsPresenter().showDescription();
    	else if (presenterFactory instanceof StudentPresenterFactory)
    		presenterFactory.getResultsPresenter().update();
    };

    public final Runnable RETOUR_TEACHER_KNOWLEDGE = () -> {
    	viewFactory.getMainView().selectView(SelectedView.KNOWLEDGE);
    	viewFactory.getMainView().showTeacherStudentModelView();
    };
    
    public final Runnable RETOUR_TEACHER_GRAPH = () -> {
    	viewFactory.getMainView().selectView(SelectedView.KNOWLEDGE);
    	viewFactory.getMainView().showStudentResultsGraphView();
    	presenterFactory.getResultsGraphPresenter().showDescription();
    };

    private final GuestComponent.Builder guestBuilder;
    @Inject
    TeacherComponent.Builder teacherBuilder;
    @Inject
    SchoolAdminComponent.Builder schoolAdminBuilder;
    
    @Inject StudentComponent.Builder studentBuilder;
    
    private static native void jsResetMainApp() /*-{
      $wnd.jsResetMainApp()
    }-*/;

    public static native String getBase() /*-{
		return $wnd.deploy;
	}-*/;

	static void insertStylesheet(String href) {
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setType("text/css");
		link.setHref(href);
		Element head = getHead();
		head.appendChild(link);
	}

	private static Element getHead() {
		return Document.get().getElementsByTagName("head").getItem(0);
	}

//    Promise<DomDwoProfileFull> insertcss(Promise<DomDwoProfileFull> p) {
//    	String css = p.getValue().getDwoProfileName();
//    	if (profile == 111 || profile == 112 || p.getValue().getDwoProfileRights().contains("c")) // het 'inf' profiel en het 'numworx' profiel
//    	{
//    		css = URL.encodePathSegment(css);
//    		insertStylesheet( getBase() + "css/" + css + ".css");    	
//    	}
//    	return p;
//    }
    
    final static GwtClientModulesOnly moduleResources = GWT.create(GwtClientModulesOnly.class);
    
    Promise<DomDwoProfileFull> hasChat(Promise<DomDwoProfileFull> p) {
    	String rights = p.getValue().getDwoProfileRights();
		dwoGlobalVars.setInf(rights.contains("I"));
		dwoGlobalVars.setRemedial(rights.contains("R"));
		dwoGlobalVars.setModulesOnly(rights.contains("4"));
    	return p;
    	
    }
    
    
    private void resetPresenters(RoleType role, boolean single) {
        eventBus.removeHandlers();
        eventBus.addHandler(LoginEvent.TYPE, LOGIN_HANDLER);
        PresenterBuilder build;
        Display mainView = viewFactory.getMainView();
		switch (role) {
          case TEACHER:
              build = teacherBuilder.build();
              mainView.setUserRole(role, false);
              mainView.setPremium(dwoGlobalVars.isPremium()); // knowledge for teacher is premium
              if (dwoGlobalVars.isModulesOnly() && dwoGlobalVars.isPremium()) {
            	  JsMainDisplay.setModulesOnly(true);
            	  DwoMessageTranslator.add(moduleResources);
              } else {
               	  JsMainDisplay.setModulesOnly(false);
            	  DwoMessageTranslator.remove(moduleResources);
              }
              mainView.showChat( hasChatbox());
              break;
          case SCHOOLADMIN:
             build = schoolAdminBuilder.build();
             mainView.setUserRole(role, false);
             mainView.setPremium(false); // no schooladmin premium features
             mainView.showChat(false);
             break;
          case STUDENT:
            //if (stage > 0)
            {
              build = studentBuilder.build();
              mainView.setUserRole(role, single);
              DomSchoolClass sc = dwoGlobalVars.getCurrentSchoolClass();
              mainView.setPremium(dwoGlobalVars.isPremium() && sc != null); // knowledge for student is premium and only in class
              JsMainDisplay.setModulesOnly(false);
              mainView.showChat(hasChatbox() && sc != null);
            break;
            }
          default:
            mainView.setUserRole(RoleType.ANONYMOUS, false);
            mainView.setPremium(false);
            JsMainDisplay.setModulesOnly(false);
            mainView.showChat(false);
            build = guestBuilder.build();
        }
        presenterFactory = build.presenterFactory();
        presenterFactory.setStage(stage);
        viewHandler = build.viewHandler();
        eventBus.addHandler(SwitchViewEvent.TYPE, viewHandler);
        DwoPresenterFactory.getDwoPresenterFactory().setFac(presenterFactory);
        // set translators....
        jsResetMainApp(); // now js code gets new presenters
    }
    
    @Inject
    DwoGlobalVars dwoGlobalVars;
    private int profile;
    private String profileStr;

    public int getProfile() {
		return profile;
	}
    public boolean hasChatbox() {
    	return dwoGlobalVars.isPremium() && dwoGlobalVars.isInf();
    }

	private int stage;
    //private boolean hideGwtGui;
    String authToken;
    private boolean session = false;

    static {
        //Initialize an Exception translator.imply removing all DOM elements can cause issues with other elements in the page.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        Dwo2LocaleMessageTranslator.setTranslator(new Dwo2LocaleMessageGWTTranslator());
    }

    ResettableEventBus eventBus;
    HasWidgets rootPanel;

    private SelectedView initialView = SelectedView.WELCOME;
    private final LoginHandler LOGIN_HANDLER = new LoginHandler();
    private Runnable retourHandler = RETOUR_WELCOME;

    @Inject
    BootPanelController(ResettableEventBus eventBus, GuestComponent.Builder initialBuilder) {
        this.eventBus = eventBus;
        this.guestBuilder = initialBuilder;
        //hideGwtGui = false;
        profile = 77;
        profileStr = "VO";
        stage = 1;
        
        History.addValueChangeHandler(evt -> this.eventBus.fireEventFromSource(evt, this));
    }

    static void sendSwitchEvent(SwitchViewEvent.SelectedView view) {
    	History.newItem(view.name());
    }
    
//    public static native String getHideGwtGuiString()/*-{
//        return  $wnd.hideGwtGui;
//    }-*/;

    public static native Object getDwoDisplay()/*-{
        return  $wnd.dwoDisplay;
    }-*/;

    private void parseGwtParam() {
//        hideGwtGui = Boolean.parseBoolean(getHideGwtGuiString());
    }

    private void parseUrlParam() {
        LOG.info("url?" + Window.Location.getQueryString());
        //parse profile if it exists.
        String value = Window.Location.getParameter("profile");
        try {
        	if (value != null) 
        	{	profileStr = value;
            	profile = Integer.parseInt(value);
        	}
        } catch (Exception e) {
        }
        
        //dwoGlobalVars.setInf(profile == PROFILE_INF);
        
        //LOG.severe("inf = " + dwoGlobalVars.isInf());
        value = Window.Location.getParameter("stage");
        if (value != null) {
            stage = Integer.parseInt(value);
        }
// features: login with authToken, switch after login to initialview
        value = Window.Location.getParameter("a");
        authToken = value;
        value = Window.Location.getParameter("view");
        try {
            initialView = SelectedView.valueOf(value);
        } catch (Exception ignore) {
            initialView = SelectedView.WELCOME;
        };
    }

    public static native void forceReload() /*-{
      $wnd.location.reload(true);
    }-*/;


    native static void logout() /*-{
      $wnd.logout();
    }-*/;
    
    native static boolean needLogout() /*-{
    	try {
    		return $wnd.needLogout;
    	} catch(e) {
    		return false;
    	}
    }-*/;

    /**
     * Sets a tooltip on the logo for the current version.
     */
    public static native void setLogoVersionTip(String version) /*-{
            $wnd.document.getElementById('logo').title=version;
            $wnd.document.getElementById('loginLinks').title=version;
    }-*/;
    
    
    public void go(RootLayoutPanel rootPanel) {
      GuestComponent build = guestBuilder.build();
      presenterFactory = build.presenterFactory();
      viewHandler = build.viewHandler();
       //todo dwo/rest/public/status/getHeartBeat
        //force reload if not current
        //todo
        /**
         * Testing stuff, stage = 1,2...
         */
        parseUrlParam();
        presenterFactory.setStage(stage);

        //fetch current version
        String softwareVersion = BUILD.version;
        String svnRevision = BUILD.buildNumber;
        String buildTimeStamp = BUILD.timeStamp;
        LOG.log(Level.INFO, "Software version " + softwareVersion + " subversion revision " + svnRevision + " build timestamp " + buildTimeStamp + ".");
        //LOG.log(Level.INFO, "forceNewAersion = " + test + ".");
        setLogoVersionTip(softwareVersion);
        
        final int flag = 1;
        //fetch remote version
        PublicStatusManager statusManager = new PublicStatusManager();
        Promise<DomHeartBeat> p = statusManager.getHeartBeat();
        p.then(new Success<DomHeartBeat, Void>() {
            @Override
            public Promise<Void> call(Promise<DomHeartBeat> resolved) throws Exception {
                DomHeartBeat beat = resolved.getValue();
                String dwo_env = beat.getEnv();
                if (dwo_env != null && dwo_env.contains("test")) dwoGlobalVars.setTest(true);
                if (dwo_env != null && dwo_env.contains("saml")) dwoGlobalVars.setSaml(true);
                if (dwo_env != null && dwo_env.contains("kiosk")) dwoGlobalVars.setKiosk(true);
                if (flag == 0) {
                    LOG.log(Level.FINE, "unmatching version flag=0");
                    if (Window.confirm("outdated version, reloading"))
                    	forceReload();
                    return null;
                }
                if (beat.getHtmlClientVersion() == null
                        || (BUILD.version != null && BUILD.version.equals(beat.getHtmlClientVersion()))) {
                    //equals server version
                    LOG.log(Level.FINE, "matching version");
                } else {
                    //incompatible version
                    LOG.log(Level.FINE, "unmatching version " + beat.getHtmlClientVersion() + " " + BUILD.version);
                    if (Window.confirm("outdated version, reloading?"))
                    	forceReload();
                    //return false;
                }
                return null;
            }

        });

//        if (!testIsOn) {
//            Window.Location.replace("http://www.dwo.nl");
//        }
        LOG.log(Level.INFO, "profile=" + profileStr + ".");
//        LOG.log(Level.INFO, "testIsOn=" + testIsOn + ".");
        parseGwtParam();
//        LOG.log(Level.INFO, "HideGwt=" + hideGwtGui + ".");

        //intialize our global and environmental variables instance.
        Promise<DomDwoProfileFull> promise = new PublicProfileManager().get(profileStr)
        .filter(v -> v != null).then(prom -> {
        	profile = prom.getValue().asLong().intValue();
        	return prom;} );
        dwoGlobalVars.setProfile(promise);
        //promise.then(this::insertcss);
        promise.then(this::hasChat);
        //show main panel
        this.rootPanel = rootPanel;

        //create client factories
        DwoPresenterFactory fac = new DwoPresenterFactory(presenterFactory);
//        presenterFactory = fac.getFac();
        LOG.log(Level.INFO, "ViewFactoryTeuniz assigned.");
//        viewFactory = new ViewFactoryJs(presenterFactory);
        presenterFactory.bindViewFactory(viewFactory);

        
        eventBus.addHandler(LoginEvent.TYPE, LOGIN_HANDLER);

        //handle switch deckpanel events.
        eventBus.addHandler(SwitchViewEvent.TYPE, viewHandler);
        LOG.log(Level.FINE, "Intiating Main view.");
        //MainPresenter.Display mainView = viewFactory.getMainView();
        rootPanel.setVisible(false);
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.FINE, "Initiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.FINE, "Initiated Main presenter.");
 //       SwitchViewEvent ev = new SwitchViewEvent(SwitchViewEvent.SelectedView.LOGIN);
 //       eventBus.fireEvent(ev);
        History.replaceItem(SelectedView.LOGIN.name());
        
        // bootFromAuthToken();
    }
//
//    private void bootFromAuthToken() {
//        if (authToken != null) {
//            LoginPresenter presenter = presenterFactory.getLoginPresenter();
//            //presenter.loginFromAuthToken(authToken);
//        }
//
//    }

    /**
     * @return the session
     */
    public boolean isSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(boolean session) {
        this.session = session;
        
        if (session) {
        	presenterFactory.getMainPresenter().setIdleTimeout(MainPresenter.IDLE); // 10 sec, word 900,000ms
        } else {
        	presenterFactory.getMainPresenter().unsetIdleTimeout();
        }
    }


	Runnable getRetourHandler() {
		return retourHandler;
	}


	void setRetourHandler(Runnable retourHandler) {
		this.retourHandler = retourHandler;
	}
}
