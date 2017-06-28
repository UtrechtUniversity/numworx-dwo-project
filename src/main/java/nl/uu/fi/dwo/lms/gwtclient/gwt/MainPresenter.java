package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter implements SwitchViewEventHandler, LoginEventHandler {

    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {

        public void init(ViewFactory clientFactory);

        public Widget asWidget();

        public MainView getViewInstance();

//        public HasClickHandlers getMenuButton(); // handle clicking on button
        public void showPostLoginWidgets();

        public void hidePostLoginWidgets();

        public void setSchoolName(String schoolName);

        public void setUserRole(String userRole);

        public void setPresentationName(String presentationName);

        public void setStatusMsg(String statusMsg);

        void clear();

        public void showAccountView();

        public void showLoginView();

        public void showSwitchSchoolView();

        public void showResultsView();

        public void showSchoolclassesView();
        
        public void showStudentsInSchoolclassView();
        
        public void showAddStudentsView();

        public void showTeachersInSchoolclassView();

        public void showScoResultsView();

        public void showMenuButton();

        public void hideMenuButton();

        public void showMenuView();

        public void hideMenuView();

        public void showMessageDialog(String msg);

        public void showErrorDialog(String msg);

        public boolean menuVisible();
    }

    private MainPresenter.Display display;

    MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
        eventBus.addHandler(LoginEvent.TYPE, this);
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
//
//    void goLogin() {
//        display.showLoginView();
//    }

    public void menuButtonClicked() {
        if (display.menuVisible()) {
            display.hideMenuView();
        } else {
            display.showMenuView();
        }
    }

    public void selectView(SwitchViewEvent.SelectedView selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(selectedView));
    }

    @Override
    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
        onSwitchViewEvent(switchViewEvent.getEventValue());
    }

    private void onSwitchViewEvent(SwitchViewEvent.SelectedView selectedView) {
        if (selectedView == selectedView.LOGIN) {
            display.hideMenuButton();
            display.hidePostLoginWidgets();
        } else {
            display.showMenuButton();
            display.showPostLoginWidgets();
        }

        switch (selectedView) {
            case ACCOUNT:
                display.showAccountView();
                break;
            case LOGIN:
                display.showLoginView();
                break;
            case SWITCHSCHOOL:
                display.showSwitchSchoolView();
                break;
            case RESULTS:
                display.showResultsView();
                break;
            case ACTIVERESULTS:
                display.showResultsView();
                break;
            case SCHOOLCLASSES:
                display.showSchoolclassesView();
                break;
            case STUDENTSINSCHOOLCLASS:
                display.showStudentsInSchoolclassView();
                break;
            case ADDSTUDENTS:
                display.showAddStudentsView();
                break;
            case TEACHERSINSCHOOLCLASS:
                display.showTeachersInSchoolclassView();
                break;
            case SCORESULTS:
                display.showScoResultsView();
        }
    }

    @Override
    public void onLoginEvent(LoginEvent loginEvent) {
        display.showPostLoginWidgets();
    }

}
