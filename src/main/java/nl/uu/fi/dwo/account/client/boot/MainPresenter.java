package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter implements SwitchViewEventHandler {

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

        public void showLoginView();

        public void showSwitchSchoolView();

        public void showResultsView();
    }

    private MainPresenter.Display display;

    MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
    }

    public void init() {
        display.showLoginView();

    }

    /**
     * @param display the display to set
     */
    public void setDisplay(MainPresenter.Display display) {
        this.display = display;
    }

    void goLogin() {
        display.showLoginView();
    }

    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
        switch(switchViewEvent.getEventValue()){
            case LOGIN:
                display.showLoginView();
                break;
            case SWITCHSCHOOL:
                display.showSwitchSchoolView();
                break;
            case RESULTS:
                display.showResultsView();
                break;
        }
    }
    
}
