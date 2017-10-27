package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEventHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter implements SwitchViewEventHandler, LoginEventHandler {

    private static final DwoLocalesForGWT rb = GWT.create(DwoLocalesForGWT.class);
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

        public void showCoursesOfSchoolclassView();

        public void showStudentsInSchoolclassView();

        public void showAddStudentsView();

        public void showTeachersInSchoolclassView();

        public void showScoResultsView();

        public void showMenuButton();

        public void hideMenuButton();

        public void currentDeckWidgetName(String panel);

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
                setDWO(this);
        eventBus.addHandler(SwitchViewEvent.TYPE, this);
        eventBus.addHandler(LoginEvent.TYPE, this);
    }

    public void init() {
        display.showLoginView();


    }

    //** should become part of the PresenterFactories ie. DWO.LoginPresenter.loginClicked.
    private native static void setDWO(MainPresenter q) /*-{
    	var apis = {
    			"showResultsView" : function() {
    				return q.@nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter::showResultsViewJS()()
                        },
    			"showSchoolclassesView" : function() {
    				return q.@nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter::showSchoolclassesViewJS()()
                        },
    			"jstest" : function() {
    				return q.@nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter::getjstest()()
                        },
    			"showAccountView" : function() {
    				return q.@nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter::showAccountViewJS()()                      
                        }
    		};
    	$wnd.DwoMainPresenter = apis;

    }-*/;

    public String showResultsViewJS() {
        this.selectView(SwitchViewEvent.SelectedView.RESULTS);
        return "true";
    }
    
    public jstest getjstest(){
        return new jstest();
    }

    public void showSchoolclassesViewJS() {
        display.showSchoolclassesView();
    }

    public void showAccountViewJS() {
        display.showAccountView();;
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
            //display.showMenuButton();
        } else {//if (selectedView == selectedView.SWITCHSCHOOL) {
            try {
                display.setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
                display.setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
                display.showPostLoginWidgets();
            } catch (Exception e) {
            }
        }
        try {
            if (dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
                display.showMenuButton();
            }
            RoleType rt = RoleType.valueOf(dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName());
            switch (rt) {
                case STUDENT:
                    display.setUserRole(rb.TEACHER());
                    break;
                case ADMIN:
                    display.setUserRole(rb.ADMIN());
                    break;
                case SCHOOLADMIN:
                    display.setUserRole(rb.SCHOOLADMIN());
                    break;
                case TEACHER:
                    display.setUserRole(rb.TEACHER());
                    break;
                default:
                    LOG.log(Level.SEVERE, "unknown role type to display.");
            }
            //display.setUserRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName());
        } catch (Exception e) {

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
            case COURSESOFSCHOOLCLASS:
                display.showCoursesOfSchoolclassView();
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
