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
import static nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent.State.SUCCESS_ROLE;

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

    public void go(RootLayoutPanel rootPanel) {
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

        ViewFactoryGwt gwtView=null;
        if (hideGwtGui) {
             LOG.log(Level.INFO, "ViewFactoryTeuniz assigned.");
            viewFactory = new ViewFactoryTeuniz(presenterFactory);
        } else {
             LOG.log(Level.INFO, "ViewFactoryGwt assigned.");
            gwtView = new ViewFactoryGwt(presenterFactory);
            viewFactory = gwtView;
        }

        presenterFactory.bindViewFactory(viewFactory);

        //handle login events
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
            @Override
            public void onLoginEvent(LoginEvent loginEvent) {
                switch (loginEvent.getState()) {
                    case SUCCESS:
                    case SUCCESS_WELCOME:
                        LOG.log(Level.INFO, "Login succeeded. Showing welcome view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.WELCOME));
                        viewFactory.getMainView().showPostLoginWidgets();
                        break;
                    case SUCCESS_RESULTS:
                        LOG.log(Level.INFO, "Login succeeded. Showing results view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
                        viewFactory.getMainView().showPostLoginWidgets();
                        break;
                    case SUCCESS_SCHOOLCLASSES:
                        LOG.log(Level.INFO, "Login succeeded. Showing schoolclasses view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                        viewFactory.getMainView().showPostLoginWidgets();
                        break;
                    case SUCCESS_ROLE:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case FAIL:
                        eventBus.fireEvent(new DialogEvent("Login failed."));
                        Window.Location.replace(Window.Location.getHref());
                        break;
                    case LOGOUT:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                        viewFactory.getMainView().hideMenuButton();
                        dwoGlobalVars.clearCurrentUser();
                        presenterFactory.getMainPresenter().onSwitchViewEvent(new SwitchViewEvent(SwitchViewEvent.eventValue.LOGIN));
                    default:
                        LOG.log(Level.SEVERE, "Login handling failed in app controller.");
                }
            }
        });

        //handle switch deckpanel events.
        eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {
            @Override
            public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
                switch (switchViewEvent.getEventValue()) {
                    case ACCOUNT:
                        presenterFactory.getAccountPresenter().init();
                        break;
                    case LOGIN:
                        presenterFactory.getLoginPresenter().init();
                        break;
                    case SWITCHSCHOOL:
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case ACTIVERESULTS:
                        //do nothing presenterFactory.getResultsPresenter().init();
                        break;
                    case RESULTS:
                        presenterFactory.getResultsPresenter().init();
                        break;
                    case SCHOOLCLASSES:
                        presenterFactory.getSchoolclassesPresenter().init();
                        break;
                    case COURSESOFSCHOOLCLASS:
                        presenterFactory.getCoursesOfSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case STUDENTSINSCHOOLCLASS:
                        presenterFactory.getStudentsInSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case ADDSTUDENTS:
                        presenterFactory.getAddStudentsPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case TEACHERSINSCHOOLCLASS:
                        presenterFactory.getTeachersInSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                        break;
                    case SCORESULTS:
                        presenterFactory.getScoResultsPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultScoContext(), switchViewEvent.getResultStudent(), switchViewEvent.getSchoolClass());
                        break;
                    case WELCOME:
                        presenterFactory.getWelcomePresenter().init();
                        break;
                    default:
                        LOG.log(Level.SEVERE, "Switch panel failed in app controller.");
                }
            }
        });
        LOG.log(Level.FINE, "Intiating Main view.");
        //MainPresenter.Display mainView = viewFactory.getMainView();
        if (hideGwtGui) {
            rootPanel.setVisible(false);
            LOG.log(Level.INFO, "Not showing GwtGui. hideGwtGui = " + getHideGwtGuiString());
        } else {
            this.rootPanel.add(gwtView.asWidget());
            LOG.log(Level.INFO, "Showing GwtGui. hideGwtGui = " + getHideGwtGuiString());
        }
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.FINE, "Intiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.FINE, "Initiated Main presenter.");
    }
}
