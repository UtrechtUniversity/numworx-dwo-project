package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import fi.dwo.gwt.lib.rest.CallManagers.PublicProfileManager;
import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogEvent;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPromise;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2LocaleMessageGWTTranslator;
import java.util.logging.Level;

import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.FAIL;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.LOGOUT;
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.SUCCESS_RESULTS;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2LocaleMessageTranslator;
import org.osgi.util.promise.Promise;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Success;

/**
 * Controller for Login.
 *
 * @author Gert van der Plas
 */
class BootPanelController {

    private static final Logger LOG = Logger.getLogger(BootPanelController.class.getName());

    private ViewFactory viewFactory;
    private PresenterFactory presenterFactory;
    private DwoGlobalVars dwoGlobalVars;
    private int profile;
    private boolean hideGwtGui;
    private boolean testIsOn;

    static {
        //Initialize an Exception translator.imply removing all DOM elements can cause issues with other elements in the page.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        Dwo2LocaleMessageTranslator.setTranslator(new Dwo2LocaleMessageGWTTranslator());
    }

    EventBus eventBus;
    HasWidgets rootPanel;

    BootPanelController(EventBus eventBus) {
        this.eventBus = eventBus;
        testIsOn = false;
        hideGwtGui = false;
        profile = 77;

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
        //parse profile if it exists.
        String value = com.google.gwt.user.client.Window.Location.getParameter("profile");
        try {
            profile = Integer.parseInt(value);
        } catch (Exception e) {
            profile = 77;
        }
        value = com.google.gwt.user.client.Window.Location.getParameter("test");
        if (value != null && value.matches("on")) {
            testIsOn = true;
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
    public void go(RootLayoutPanel rootPanel) {
        /**
         * Testing stuff
         */

        //    testRestyMapConverter();
        parseUrlParam();
        LOG.log(Level.INFO, "profile=" + profile + ".");
        LOG.log(Level.INFO, "testIsOn=" + testIsOn + ".");
        parseGwtParam();
        LOG.log(Level.INFO, "HideGwt=" + hideGwtGui + ".");

        //intialize our global and environmental variables instance.
        try {
            dwoGlobalVars = new DwoGlobalVars();
            Promise<DomDwoProfileFull> promise = new PublicProfileManager().get(profile);
            dwoGlobalVars.setProfile(promise);
        } catch (Dwo2Exception e) {
            //ugly emergency code in case server fails.
            String msg = "Fatal server error! " + e.getDwo2Message();
            LOG.log(Level.INFO, e.getDwo2Message());
            DialogBox dialogBox = new DialogBox();
            Label label = new Label();
            label.setText(msg);
            dialogBox.add(label);
            dialogBox.add(new Button("OK"));
            dialogBox.setModal(true);
            dialogBox.setAutoHideEnabled(false);
            dialogBox.setGlassEnabled(true);
            dialogBox.setAnimationEnabled(true);
            dialogBox.center();
            dialogBox.show();
            return;
        }

        //show main panel
        this.rootPanel = rootPanel;

        //create client factories
        DwoPresenterFactory fac = new DwoPresenterFactory(new PresenterFactoryGwt(eventBus, dwoGlobalVars));
        presenterFactory = fac.getFac();

        LOG.log(Level.INFO, "ViewFactoryTeuniz assigned.");
        viewFactory = new ViewFactoryJs(presenterFactory);

        presenterFactory.bindViewFactory(viewFactory);

        //handle login events
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
            @Override
            public void onLoginEvent(LoginEvent loginEvent) {
                if(dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name())){
                switch (loginEvent.getState()) {
                    case SUCCESS:
                    case SUCCESS_WELCOME:
                        LOG.log(Level.INFO, "Login succeeded. Showing welcome view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.WELCOME));
              // viewFactory.getMainView().showPostLoginWidgets();
                        break;
                    case SUCCESS_RESULTS:
                        LOG.log(Level.INFO, "Login succeeded. Showing results view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
              // viewFactory.getMainView().showPostLoginWidgets();
                        break;
                    case SUCCESS_SCHOOLCLASSES:
                        LOG.log(Level.INFO, "Login succeeded. Showing schoolclasses view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
              // viewFactory.getMainView().showPostLoginWidgets();
                        break;
            // case SUCCESS_ROLE:
            // LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
            // eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
            // presenterFactory.getSwitchSchoolPresenter().init();
            // break;
                    case FAIL:
                        LOG.log(Level.INFO, "Login failed.");
//                        /eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, "Login failed.")));
                        MsgClickedDialogPromise p = new MsgClickedDialogPromise(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(dwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_AuthenticationError));
                        p.getPromise().then(new Success<Boolean, Void>() {
                            @Override
                            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                                LOG.log(Level.INFO, "returned value" + resolved.getValue());
                                Window.Location.replace(Window.Location.getHref());
                                return null;
                            }
                        }, new Failure() {
                            @Override
                            public void fail(Promise<?> resolved) throws Exception {
                                Window.Location.replace(Window.Location.getHref());
                            }
                        }
                        );
                        eventBus.fireEvent(new MsgClickedDialogEvent(MsgClickedDialogEvent.EventType.MsgClickedDialog, p));

                        break;
                    case LOGOUT:
                        dwoGlobalVars.clearCurrentUser();
              // viewFactory.getMainView().hideMenuButton();
                        presenterFactory.getMainPresenter().onSwitchViewEvent(new SwitchViewEvent(SwitchViewEvent.eventValue.LOGIN));
                    default:
                        LOG.log(Level.SEVERE, "Login handling failed in app controller.");
                }
                }else{
                    LOG.log(Level.INFO, "Login succeeded. Showing account view for teacher.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACCOUNT));
                        DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_SwitchTeacher()));
                        //viewFactory.getMainView().showPostLoginWidgets();
                }
            }
        });

        //handle switch deckpanel events.
        eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {
            @Override
            public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
                if(dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name())){
                switch (switchViewEvent.getEventValue()) {
                    case LOGIN:
                        presenterFactory.getLoginPresenter().init();
                        break;
                    case WELCOME:
                        presenterFactory.getWelcomePresenter().init();
                        break;
                    case ACCOUNT:
                        presenterFactory.getAccountPresenter().init();
                        break;
                    case PEOPLE:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case RESULTS:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case RESULTSSTUDENT:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case SCHOOLCLASSES:
                        presenterFactory.getSchoolclassesPresenter().init();
                        break;
                    case EDITSCHOOLCLASS:
                        presenterFactory.getEditSchoolclassPresenter().init();
                        break;
                    case ADDSTUDENTTOSCHOOLCLASS:
                        presenterFactory.getAddStudentToSchoolclassPresenter().init();
                        break;
                    case COPYORMOVESTUDENTTOSCHOOLCLASS:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case ADDTEACHERTOSCHOOLCLASS:
                        presenterFactory.getAddTeacherToSchoolclassPresenter().init();
                        break;
                    case EDITCOURSESOFSCHOOLCLASS:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case ORGANISATION:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    case MODULES:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        break;
                    default:
                        eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                        LOG.log(Level.SEVERE, "Switch panel failed in app controller.");
                }
                }else{
                    LOG.log(Level.INFO, "Showing account view, because not a teacher.");
                        presenterFactory.getAccountPresenter().init();
                }                
            }
        });
        LOG.log(Level.FINE, "Intiating Main view.");
        //MainPresenter.Display mainView = viewFactory.getMainView();
        rootPanel.setVisible(false);
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.FINE, "Intiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.FINE, "Initiated Main presenter.");
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.LOGIN));
    }
}
