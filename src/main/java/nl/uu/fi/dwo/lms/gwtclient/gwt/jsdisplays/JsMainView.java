package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;

/**
 *
 * @author G.A.J. van der Plas
 */
public class JsMainView implements MainPresenter.Display{

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
    public void setCurrentPanelName(String panel) {
        JsMainDisplay.setCurrentPanelName();
    }


    @Override
    public boolean isMenuVisible() {
        return JsMainDisplay.isMenuVisible();
    }
    

    @Override
    public void showAccountView() {
        JsMainDisplay.showAccountView();
    }

    @Override
    public void showWelcomeView() {
        JsMainDisplay.showWelcomeView();
    }

    @Override
    public void showLoginView() {
        JsMainDisplay.showLoginView();
    }

    @Override
    public void showResultsView() {
        JsMainDisplay.showResultsView();
    }
    
    @Override
    public void showStudentResultsView() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showSchoolclassesView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showEditSchoolclassView() {
        JsMainDisplay.showEditSchoolclassView();
    }
//
//    @Override
//    public void showStudentsInSchoolclassView() {
//        JsMainDisplay.showStudentsInSchoolclassView();
//    }
//
//    @Override
//    public void showTeachersInSchoolclassView() {
//        JsMainDisplay.showTeachersInSchoolclassView();
//    }
//    @Override
//    public void showCoursesOfSchoolClassView() {
//    JsMainDisplay.showCoursesOfSchoolclassView();
//    }

    @Override
    public void showAddStudentToSchoolClassView() {
        JsMainDisplay.showAddStudentToSchoolClassView();
    }

    @Override
    public void showCopyOrMoveStudentToSchoolClassView() {
        JsMainDisplay.showCopyOrMoveStudentToSchoolClassView();
    }

    @Override
    public void showAddTeacherToSchoolClassView() {
       JsMainDisplay.showAddTeacherToSchoolClassView();
    }

    @Override
    public void showEditCoursesOfSchoolClassView() {
       //JsMainDisplay.showEditCoursesOfSchoolClassView();
    }

    @Override
    public void showPersonsView() {
        JsMainDisplay.showPersonsView();
    }

    @Override
    public void showModulesView() {
        JsMainDisplay.showModulesView();
    }

    @Override
    public void showOrganisationView() {
        JsMainDisplay.showOrganisationView();
    }
}
