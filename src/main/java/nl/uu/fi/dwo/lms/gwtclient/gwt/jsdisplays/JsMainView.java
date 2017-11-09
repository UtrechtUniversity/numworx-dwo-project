package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;

/**
 *
 * @author G.A.J. van der Plas
 */
public class JsMainView implements MainPresenter.Display{

    @Override
    public void showPostLoginWidgets() {
        JsMainDisplay.showPostLoginWidgets();
    }

    @Override
    public void hidePostLoginWidgets() {
        JsMainDisplay.hidePostLoginWidgets();
    }

    @Override
    public void setSchoolName(String schoolName) {
        JsMainDisplay.setSchoolName(schoolName);
    }

    @Override
    public void setUserRole(String userRole) {
        JsMainDisplay.setUserRole(userRole);
    }

    @Override
    public void setPresentationName(String presentationName) {
        JsMainDisplay.setPresentationName(presentationName);
    }

    @Override
    public void showAccountView() {
        JsMainDisplay.showAccountView();
    }

    @Override
    public void showLoginView() {
        JsMainDisplay.showLoginView();
    }

    @Override
    public void showSwitchSchoolView() {
        JsMainDisplay.showSwitchSchoolView();
    }

    @Override
    public void showResultsView() {
        JsMainDisplay.showResultsView();
    }

    @Override
    public void showSchoolclassesView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showCoursesOfSchoolclassView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showStudentsInSchoolclassView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showAddStudentsView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showTeachersInSchoolclassView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showScoResultsView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showMenuButton() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void hideMenuButton() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void setCurrentPanelName(String panel) {
        JsMainDisplay.setCurrentPanelName();
    }

    @Override
    public void showMenuView() {
        JsMainDisplay.showMenuView();
    }

    @Override
    public void hideMenuView() {
       JsMainDisplay.hideMenuView();
    }

    @Override
    public boolean isMenuVisible() {
        return JsMainDisplay.isMenuVisible();
    }
}
