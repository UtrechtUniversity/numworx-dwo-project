package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

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
    private MainPresenter handler;

    static {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
    }

    private final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
    EventBus eventBus;
    HasWidgets rootPanel;

    BootPanelController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void go(RootLayoutPanel rootPanel) {
//        try {
        dwoGlobalVars = DwoGlobalVars.instance();
        String value = com.google.gwt.user.client.Window.Location.getParameter("profile");
        if (value != null) {
            Integer profile = Integer.parseInt(value);
            dwoGlobalVars.setProfileId(profile);
        }
//        } catch (Dwo2Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            PopupPanel popup = new PopupPanel();
//            popup.add(new Label("Programmers-error"));
//        }
        LOG.log(Level.INFO, "Intiated DwoGlobalsVars.");
        //show main panel
        this.rootPanel = rootPanel;

        presenterFactory = new PresenterFactoryImpl(eventBus, dwoGlobalVars);
        viewFactory = new ViewFactoryImpl(presenterFactory);

        //add handlers do this using hasEventHandlers and add all eventhandlers off class
        //add: onLogin, onLogout, onBack, onSwitchSchool, onManageClass, onProfile..
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
            @Override
            public void onLoginEvent(LoginEvent loginEvent) {
                switch (loginEvent.getState()) {
                    case SUCCESS_RESULTS:
                        LOG.log(Level.INFO, "Login succeeded. Showing results view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
                        viewFactory.getMainView().showMenuButton();
                        viewFactory.getMainView().showPostLoginWidgets();
                        presenterFactory.getResultsPresenter().init();
                        break;
                    case SUCCESS:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                        viewFactory.getMainView().showMenuButton();
                        viewFactory.getMainView().showPostLoginWidgets();
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case LOGOUT:
                        LOG.log(Level.INFO, "Login succeeded. Showing switch role view.");
                        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                        viewFactory.getMainView().hideMenuButton();
                        dwoGlobalVars.clearCurrentUser();
                        presenterFactory.getMainPresenter().onSwitchViewEvent(new SwitchViewEvent(SwitchViewEvent.eventValue.LOGIN));
                    default:
                        LOG.log(Level.INFO, "Login fail in app controller.");
                }
            }
        });
        eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {
            @Override
            public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
                switch (switchViewEvent.getEventValue()) {
                    case LOGIN:
                        presenterFactory.getLoginPresenter().init();
                        break;
                    case SWITCHSCHOOL:
                        presenterFactory.getSwitchSchoolPresenter().init();
                        break;
                    case RESULTS:
                        presenterFactory.getResultsPresenter().init();
                        break;
                    default:
                        LOG.log(Level.INFO, "Switch fail in app controller.");
                }
            }
        });        

        MainView mainView = viewFactory.getMainView();
        mainView.init(viewFactory);
        this.rootPanel.add(mainView);
        LOG.log(Level.INFO, "Intiated Main view.");
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.INFO, "Intiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.INFO, "Initiated Main presenter.");
    }
//
//    public Promise<DomLoginContext> logout() {
//        return accountManager.logout();
//    }

    //add 
}
