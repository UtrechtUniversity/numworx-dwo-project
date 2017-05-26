package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.Results.ResultsView;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
class MainPresenter {

    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {
        Widget asWidget();
        MainView getViewInstance();
        HasClickHandlers getMenuButton(); // handle clicking on button
        void showPostLoginWidgets(); 
        void hidePostLoginWidgets(); 
        public void setSchoolName(String schoolName);
        public void setUserRole(String userRole);
        public void setPresentationName(String presentationName);
        public void setStatusMsg(String statusMsg);
        void clear();
        public void showLogin();
    }

    MainPresenter.Display display;
//    DeckPanel mainDeckPanel = new DeckPanel();
//    @UiField(provided = true)
//    Widget loginWidget = new LoginView();
//    @UiField(provided = true)
//    Widget resultWidget = new ResultsView();
//    @UiField(provided = true)
//    Widget switchSchoolWidget = new SwitchSchoolPanel();

MainPresenter(MainPresenter.Display display, DwoGlobalVars dwoGlobalVars, EventBus eventBus) {
        this.dwoGlobalVars = dwoGlobalVars;
        this.eventBus = eventBus;
    }

    public void init() {

    }


    void goLogin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
