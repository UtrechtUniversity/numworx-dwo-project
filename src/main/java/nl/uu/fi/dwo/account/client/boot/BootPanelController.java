package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
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

    public void go(RootPanel rootPanel) {
        try {
            dwoGlobalVars = new DwoGlobalVars();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            PopupPanel popup = new PopupPanel();
            popup.add(new Label("Programmers-error"));
        }
        this.rootPanel = rootPanel;
        //show main panel
        MainView mainView = new MainView(rootPanel);
        this.rootPanel.add(mainView);
        MainPresenter.Display  mainDisplay = mainView;
        MainPresenter mainPresenter = new MainPresenter(mainDisplay, dwoGlobalVars, eventBus);
        mainPresenter.init();
        //show login screen
//        mainPresenter.goLogin();
        
        // init rest services
    }
//
//    public Promise<DomLoginContext> logout() {
//        return accountManager.logout();
//    }
}
