package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.CallManagers.PublicStatusManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.FAIL;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.LOGOUT;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;
import org.osgi.util.promise.Promise;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Success;

/**
 * TeacherApplication
 *
 * @author Gert van der Plas
 */
@Singleton //not required.
public class BootPanelController {

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());
    static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
    static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
    static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";

    @Inject
    ViewFactory viewFactory;
    @Inject
    PresenterFactoryGwt presenterFactory;
    @Inject
    DwoGlobalVars dwoGlobalVars;
    private int profile;
    private int stage;
    private boolean hideGwtGui;
    private boolean test = false;
    private String authToken, user_id, org_id;
    private boolean session = false;

    static {
        //Initialize an Exception translator.imply removing all DOM elements can cause issues with other elements in the page.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        Dwo2LocaleMessageTranslator.setTranslator(new Dwo2LocaleMessageGWTTranslator());
    }

    EventBus eventBus;
    HasWidgets rootPanel;

    private SelectedView initialView = SelectedView.WELCOME;

    @Inject
    BootPanelController(EventBus eventBus) {
        this.eventBus = eventBus;
        test = false;
        hideGwtGui = false;
        profile = 77;
        stage = 1;

    }

    public static native String getHideGwtGuiString()/*-{
        return  $wnd.hideGwtGui;
    }-*/;

    public static native Object getDwoDisplay()/*-{
        return  $wnd.dwoDisplay;
    }-*/;

    private void parseGwtParam() {
        hideGwtGui = Boolean.parseBoolean(getHideGwtGuiString());
    }

    private void parseUrlParam() {
        LOG.info("url?" + Window.Location.getQueryString());
        //parse profile if it exists.
        String value = Window.Location.getParameter("profile");
        try {
            profile = Integer.parseInt(value);
        } catch (Exception e) {
            profile = 77;
        }
        value = Window.Location.getParameter("test");
        if (value != null && value.matches("on")) {
            test = true;
        }
        value = Window.Location.getParameter("stage");
        if (value != null && value.matches("on")) {
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
// Saml login, deprecated 
        if(authToken == null) {
          user_id = Cookies.getCookie(DWO_SAML_USER_ID);
          org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
          authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
          LOG.severe("SAML User " + user_id + " " + org_id + " " + authToken);
        } else {
          user_id = org_id = null; // modern authtoken.
        }
    }

//    public void testRestyMapConverter() {
//        RestyMapCodec codec = GWT.create(RestyMapCodec.class);
//        
//        Map map = new HashMap<String, String>();
//        map.put("key", "value");
//        JSONValue json = codec.encode(map);
//        System.out.println(json);
//        // decoding an object to from JSON
//        Map other = codec.decode(json);
//        System.out.println(other);
//    }
    public static native void forceReload() /*-{
      $wnd.location.reload(true);
    }-*/;

    /**
     * Sets a tooltip on the logo for the current version.
     */
    public static native void setLogoVersionTip(String version) /*-{
            $wnd.document.getElementById('logo').title=version;
            $wnd.document.getElementById('loginLinks').title=version;
    }-*/;
    
    
    public void go(RootLayoutPanel rootPanel) {
        //todo dwo/rest/public/status/getHeartBeat
        //force reload if not current
        //todo
        /**
         * Testing stuff
         */
        //    testRestyMapConverter();
        parseUrlParam();

        //fetch current version
        String softwareVersion = BUILD.version;
        String svnRevision = BUILD.buildNumber;
        String buildTimeStamp = BUILD.timeStamp;
        LOG.log(Level.INFO, "Software version " + softwareVersion + " subversion revision " + svnRevision + " build timestamp " + buildTimeStamp + ".");
        LOG.log(Level.INFO, "forceNewAersion = " + test + ".");
        setLogoVersionTip(softwareVersion);
        
        final int flag = 1;
        //fetch remote version
        PublicStatusManager statusManager = new PublicStatusManager();
        Promise<DomHeartBeat> p = statusManager.getHeartBeat();
        p.then(new Success<DomHeartBeat, Void>() {
            @Override
            public Promise<Void> call(Promise<DomHeartBeat> resolved) throws Exception {
                DomHeartBeat beat = resolved.getValue();
                if (flag == 0) {
                    LOG.log(Level.FINE, "unmatching version");
                    Window.alert("outdated version, reloading");
                    forceReload();
                }
                if (beat.getHtmlClientVersion() == null
                        || (BUILD.version != null && BUILD.version.equals(beat.getHtmlClientVersion()))) {
                    //equals server version
                    LOG.log(Level.FINE, "matching version");
                } else {
                    //incompatible version
                    LOG.log(Level.FINE, "unmatching version");
                    Window.alert("outdated version, reloading");
                    forceReload();
                    //return false;
                }
                return null;
            }

        });

//        if (!testIsOn) {
//            Window.Location.replace("http://www.dwo.nl");
//        }
        LOG.log(Level.INFO, "profile=" + profile + ".");
//        LOG.log(Level.INFO, "testIsOn=" + testIsOn + ".");
        parseGwtParam();
//        LOG.log(Level.INFO, "HideGwt=" + hideGwtGui + ".");

        //intialize our global and environmental variables instance.
//        try {
        //dwoGlobalVars = new DwoGlobalVars(); // INJECTED
        Promise<DomDwoProfileFull> promise = new PublicProfileManager().get(profile)
        .filter(v -> v != null);
        dwoGlobalVars.setProfile(promise);
//        } catch (Dwo2Exception e) {
//            //ugly emergency code in case server fails.
//            String msg = "Fatal server error! " + e.getDwo2Message();
//            LOG.log(Level.INFO, e.getDwo2Message());
//            DialogBox dialogBox = new DialogBox();
//            Label label = new Label();
//            label.setText(msg);
//            dialogBox.add(label);
//            dialogBox.add(new Button("OK"));
//            dialogBox.setModal(true);
//            dialogBox.setAutoHideEnabled(false);
//            dialogBox.setGlassEnabled(true);
//            dialogBox.setAnimationEnabled(true);
//            dialogBox.center();
//            dialogBox.show();
//            return;
//        }

        //show main panel
        this.rootPanel = rootPanel;

        //create client factories
        DwoPresenterFactory fac = new DwoPresenterFactory(presenterFactory);
//        presenterFactory = fac.getFac();
        LOG.log(Level.INFO, "ViewFactoryTeuniz assigned.");
//        viewFactory = new ViewFactoryJs(presenterFactory);
        presenterFactory.bindViewFactory(viewFactory);

        //handle login events
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
            @Override
            public void onLoginEvent(LoginEvent loginEvent) {
                if (loginEvent.getState() == FAIL || loginEvent.getState() == LOGOUT || dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name())) {
                    switch (loginEvent.getState()) {
                        case SUCCESS:
                        case SUCCESS_WELCOME:
                            setSession(true);
                            LOG.log(Level.INFO, "Login succeeded. Showing welcome view.");
                            SelectedView view = initialView;
                            initialView = SelectedView.WELCOME;
                            eventBus.fireEvent(new SwitchViewEvent(view));
                            // viewFactory.getMainView().showPostLoginWidgets();
                            break;
                        case SUCCESS_ROLE:
                            LOG.log(Level.INFO, "Login succeeded. Showing account view for teacher.");
                            eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                            break;
                        case SUCCESS_RESULTS:
                            setSession(true);
                            LOG.log(Level.INFO, "Login succeeded. Showing results view.");
                            eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
                            // viewFactory.getMainView().showPostLoginWidgets();
                            break;
                        case SUCCESS_SCHOOLCLASSES:
                            setSession(true);
                            LOG.log(Level.INFO, "Login succeeded. Showing schoolclasses view.");
                            eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                            // viewFactory.getMainView().showPostLoginWidgets();
                            break;
                        case FAIL:
                            LOG.log(Level.INFO, "Login failed, showing dialog.");
                            eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.LOGIN));
                            break;
                        case LOGOUT:
                            dwoGlobalVars.clearCurrentUser();
                            setSession(false);
                            if(!test){
                                Window.Location.reload();
                            }else{
                            //we should also clear user, view and presenter states, but that is never bug free.
                            //however a reload works too.
                            
                            UrlBuilder url = Window.Location.createUrlBuilder();
                            url.setPath("/dwo/tablet/DWOplayer.jsp"); // switch to  /leerling
                            url.removeParameter("a");
                            url.removeParameter("view");
                            Window.Location.replace(url.buildString());
                            }
                            break;
                        default:
                            LOG.log(Level.SEVERE, "Login handling failed in app controller.");
                    }
                } else {
                    LOG.log(Level.INFO, "Login succeeded. Showing account view for teacher.");
                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                    //DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
                    //eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_SwitchTeacher()));
                    //viewFactory.getMainView().showPostLoginWidgets();
                }
            }
        });

        //handle switch deckpanel events.
        eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {
            @Override
            public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
                if (SwitchViewEvent.eventValue != SwitchViewEvent.eventValue.LOGIN
                        && (dwoGlobalVars.getActiveSchoolRoleAndClass() == null
                        || dwoGlobalVars.getActiveSchoolRoleAndClass().getRole() == null
                        || !dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name()))) {
                    LOG.log(Level.INFO, "Showing account view, because not a teacher.");

                    presenterFactory.getAccountPresenter().init();
                    viewFactory.getMainView().showAccountView();
                } else {
                    if (SwitchViewEvent.eventValue != SwitchViewEvent.eventValue.LOGIN) {
                        viewFactory.getMainView().setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
                        viewFactory.getMainView().setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
                    }
                    switch (switchViewEvent.getEventValue()) {
                        case LOGIN:
                            viewFactory.getMainView().showLoginView();
                            presenterFactory.getLoginPresenter().init();
                            if (authToken != null) {
                                String token = authToken;
                                authToken = null;
                                presenterFactory.getLoginPresenter().tokenLogin(token, user_id, org_id);
                            }
                            break;
                        case WELCOME:
                            viewFactory.getMainView().showWelcomeView();
                            presenterFactory.getWelcomePresenter().init();
                            break;
                        case ACCOUNT:
                            viewFactory.getMainView().showAccountView();
                            presenterFactory.getAccountPresenter().init();
                            break;
                        case PERSONS:
                            viewFactory.getMainView().showPersonsView();
                            presenterFactory.getPersonsPresenter().setStage(stage);
                            presenterFactory.getPersonsPresenter().init();
                            break;
                        case ADDPERSON:
                            viewFactory.getMainView().showAddPersonView();
                            presenterFactory.getAddStudentPresenter().init();
                            break;
                        case EDITSTUDENT:
                            viewFactory.getMainView().showEditPersonView();
                            presenterFactory.getEditStudentPresenter().init(switchViewEvent.getUser());
                            break;
                        case EDITTEACHER:
                            viewFactory.getMainView().showEditPersonView();
                            presenterFactory.getEditTeacherPresenter().init(switchViewEvent.getUser());
                            break;
                        case IMPORTPERSONS:
                            viewFactory.getMainView().showImportPersonsView();
                            presenterFactory.getPersonsPresenter().init();
                            break;
                        case RESULTS:
                            viewFactory.getMainView().showResultsView();
                            presenterFactory.getResultsPresenter().init();
                            break;
                        case SELECTEDRESULTS:
                            viewFactory.getMainView().showSelectedResultsView();
                            presenterFactory.getSelectedResultsPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                            break;
                        case BACKTORESULTS:
                            viewFactory.getMainView().showResultsView();
                            presenterFactory.getResultsPresenter().init(switchViewEvent.getResultState());
                            break;
                        case SELECTEDRESULTSRETURN:
                            viewFactory.getMainView().showSelectedResultsView();
                            presenterFactory.getSelectedResultsPresenter().reinit(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                            break;
                        case RESULTSSTUDENT:
                            //eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                            viewFactory.getMainView().showStudentScoResultView();
                            presenterFactory.getStudentScoResultPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultStudentScoContext(), switchViewEvent.getResultState(), switchViewEvent.getUserState());
                            break;
                        case SCHOOLCLASSES:
                            viewFactory.getMainView().showSchoolclassesView();
                            presenterFactory.getSchoolclassesPresenter().init();
                            break;
                        case EDITSCHOOLCLASS:
                            viewFactory.getMainView().showEditSchoolclassView();
                            presenterFactory.getEditSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                            break;
                        case ADDSTUDENTTOSCHOOLCLASS:
                            viewFactory.getMainView().showAddStudentToSchoolClassView();
                            presenterFactory.getAddStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                            break;
                        case COPYORMOVESTUDENTTOSCHOOLCLASS:
                            viewFactory.getMainView().showCopyOrMoveStudentToSchoolClassView();
                            presenterFactory.getCopyOrMoveStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                            break;
                        case ADDTEACHERTOSCHOOLCLASS:
                            viewFactory.getMainView().showAddTeacherToSchoolClassView();
                            presenterFactory.getAddTeacherToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                            break;
                        case EDITCOURSESOFSCHOOLCLASS:
                            LOG.log(Level.INFO, "Init panel EDITCOURSESOFSCHOOLCLASS.");
                            viewFactory.getMainView().showEditCoursesOfSchoolClassView();
                            presenterFactory.getModulesOfSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                            break;
                        case ORGANISATION:
                            eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                            break;
                        case MODULES:
                            presenterFactory.getModulesPresenter().show();
                            break;
                        case MODULESVIEW:
                            viewFactory.getMainView().showModulesView();
                            break;
                        default:
                            eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                            LOG.log(Level.SEVERE, "Switch panel failed in app controller.");
                    }
                }
            }
        });
        LOG.log(Level.FINE, "Intiating Main view.");
        //MainPresenter.Display mainView = viewFactory.getMainView();
        rootPanel.setVisible(false);
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.FINE, "Initiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.FINE, "Initiated Main presenter.");
        SwitchViewEvent ev = new SwitchViewEvent(SwitchViewEvent.SelectedView.LOGIN);
        eventBus.fireEvent(ev);

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
    }
}
