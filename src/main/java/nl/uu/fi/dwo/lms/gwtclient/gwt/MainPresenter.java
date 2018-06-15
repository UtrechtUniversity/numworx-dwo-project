package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Handler for BootPanel actions.
 *
 * @author Gert van der Plas
 */
public class MainPresenter {

    private static final DwoLocalesForGWT rb = GWT.create(DwoLocalesForGWT.class);
    private static final Logger LOG = Logger.getLogger(MainPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;

    public interface Display {

        public boolean isMenuVisible();

        public void setSchoolName(String schoolName);

        public void setUserRole(String userRole);

        public void setPresentationName(String presentationName);

        public void showWelcomeView();

        public void showAccountView();

        public void showLoginView();

        public void showResultsView();
        
        public void showSelectedResultsView();

        public void showStudentResultsView();

        public void showSelectStudentResultsView();

        public void showStudentScoResultView();
        
        public void showSchoolclassesView(); // has AddSchoolClass function

        public void showEditSchoolclassView();

        public void showAddStudentToSchoolClassView();

        public void showCopyOrMoveStudentToSchoolClassView();

        public void showAddTeacherToSchoolClassView();

        public void showEditCoursesOfSchoolClassView();

//        public void showStudentsInSchoolclassView();
//
//        public void showTeachersInSchoolclassView();
//
//        public void showCoursesOfSchoolClassView();

        public void setCurrentPanelName(String panel);

        public void showPersonsView();
        public void showAddPersonView();
        public void showEditPersonView();
        public void showImportPersonsView();

        public void showModulesView();

        public void showOrganisationView();
    }

    private MainPresenter.Display display;

    MainPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
//                setDWO(this);
//        eventBus.addHandler(SwitchViewEvent.TYPE, this);
//        eventBus.addHandler(LoginEvent.TYPE, this);
    }

    public void init() {

    }

    /**
     * @param display the display to set
     */
    public void setView(MainPresenter.Display display) {
        this.display = display;
    }
//
//    void goLogin() {
//        display.showLoginView();
//    }
    //
    // @JsMethod
    // public void menuButtonClicked() {
    // if (display.isMenuVisible()) {
    // display.hideMenuView();
    // } else {
    // display.showMenuView();
    // }
    // }

    @JsMethod
    public void selectView(String selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.valueOf(selectedView)));
    }

    public void selectView(SwitchViewEvent.SelectedView selectedView) {
        eventBus.fireEvent(new SwitchViewEvent(selectedView));
    }

//////    @Override
//////    public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
////////        onSwitchViewEvent(switchViewEvent.getEventValue());
//////    }
////
////    private void onSwitchViewEvent(SwitchViewEvent.SelectedView selectedView) {
////        if (selectedView != selectedView.LOGIN) {
////            try {
////                display.setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
////                display.setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
////                // display.showPostLoginWidgets();
////            } catch (Exception e) {
////            }
////        }
////        if (SwitchViewEvent.eventValue != SwitchViewEvent.eventValue.LOGIN
////                        && (dwoGlobalVars.getActiveSchoolRoleAndClass() == null
////                        || dwoGlobalVars.getActiveSchoolRoleAndClass().getRole() == null
////                        || !dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name()))) {
////                    LOG.log(Level.INFO, "Showing account view, because not a teacher.");
////                    //eventBus.fireEvent(new DialogEvent(DwoLocalesForGWT.instance.GUI_SwitchTeacher()));
////                    display.showAccountView();
////                } else {
//////        if (dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
////            switch (selectedView) {
////                case LOGIN:
////                    display.showLoginView();
////                    break;
////                case WELCOME:
////                    display.showWelcomeView();
////                    break;
////                case ACCOUNT:
////                    display.showAccountView();
////                    break;
////                case PEOPLE:
////                    display.showPersonsView();
////                    break;
////                case SCHOOLCLASSES:
////                    display.showSchoolclassesView();
////                    break;
////                case EDITSCHOOLCLASS:
////                    display.showEditSchoolclassView();
////                    break;
////                case ADDSTUDENTTOSCHOOLCLASS:
////                    display.showAddStudentToSchoolClassView();
////                    break;
////                case COPYORMOVESTUDENTTOSCHOOLCLASS:
////                    display.showCopyOrMoveStudentToSchoolClassView();
////                    break;
////                case ADDTEACHERTOSCHOOLCLASS:
////                    display.showAddTeacherToSchoolClassView();
////                    break;
////                case EDITCOURSESOFSCHOOLCLASS:
////                    display.showEditCoursesOfSchoolClassView();
////                    break;
////                case RESULTS:
////                    display.showResultsView();
////                    break;
////                case SELECTEDRESULTS:
////                    display.showSelectedResultsView();
////                    break;
////                case RESULTSSTUDENT:
////                    display.showStudentResultsView();
////                    break;
////                case MODULES:
////                    display.showModulesView();
////                    break;
////                case ORGANISATION:
////                    display.showOrganisationView();
////                    break;
////                // case ADDSTUDENTS:
////                // display.showAddStudentsView();
////                // break;
////                // case TEACHERSINSCHOOLCLASS:
////                // display.showTeachersInSchoolclassView();
////                // break;
////                // case SCORESULTS:
////                // display.showScoResultsView();
////            }
//////        } else {
//////            display.showAccountView();
////        }
////    }
//
//    @Override
//    public void onLoginEvent(LoginEvent loginEvent
//    ) {
//        /// display.showPostLoginWidgets();
//    }
//
//    @JsMethod
//    public String getTranslation(String key) {
//        return rb.getString(key);
//    }
}
