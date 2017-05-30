package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
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
        try {
            dwoGlobalVars = new DwoGlobalVars();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            PopupPanel popup = new PopupPanel();
            popup.add(new Label("Programmers-error"));
        }
        LOG.log(Level.INFO, "Intiated DwoGlobalsVars.");
        //show main panel
        this.rootPanel = rootPanel;
        
        presenterFactory = new PresenterFactoryImpl(eventBus, dwoGlobalVars);
        viewFactory = new ViewFactoryImpl(presenterFactory);
        
        //add handlers do this using hasEventHandlers and add all eventhandlers off class
        //add: onLogin, onLogout, onBack, onSwitchSchool, onManageClass, onProfile..
        eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler()     {
        @Override
        public void onLoginEvent(LoginEvent loginEvent) {
            switch(loginEvent.getState()){
                case SUCCESS_RESULTS:
                LOG.log(Level.INFO,"Login succeeded. Showing results view.");
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
                break;
                case SUCCESS:
                LOG.log(Level.INFO,"Login succeeded. Showing switch role view.");
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SWITCHSCHOOL));
                    break;
                default:
                LOG.log(Level.INFO,"Login fail in app controller.");
            }
        }
    });
        
        MainView mainView = viewFactory.getMainView();
        mainView.init(viewFactory);
        this.rootPanel.add(mainView);
        LOG.log(Level.INFO,"Intiated Main view.");
        MainPresenter mainPresenter = presenterFactory.getMainPresenter();
        LOG.log(Level.INFO,"Intiating Main presenter. Showing login screen.");
        mainPresenter.init();
        LOG.log(Level.INFO,"Initiated Main presenter.");
    }
//
//    public Promise<DomLoginContext> logout() {
//        return accountManager.logout();
//    }
    
    //add 
}
