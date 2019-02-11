package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

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
    public void setUserRole(RoleType userRole, boolean single) {
        if (single && userRole == RoleType.STUDENT)
          JsMainDisplay.setUserRole("SINGLESTUDENT");
        else
          JsMainDisplay.setUserRole(userRole.name());
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
    public void showSelectedResultsView() {
        JsMainDisplay.showSelectedResultsView();
    }
    
    @Override
    public void showStudentResultsView() {
        JsMainDisplay.showStudentResultsView();
    }


    @Override
    public void showSelectStudentResultsView() {
        JsMainDisplay.showSelectStudentResultsView();
    }

    @Override
    public void showStudentScoResultView() {
        JsMainDisplay.showStudentScoResultView();
    }
    
    @Override
    public void showSchoolclassesView() {
        JsMainDisplay.showSchoolclassesView();
    }

    @Override
    public void showEditSchoolclassView() {
        JsMainDisplay.showEditSchoolclassView();
    }

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
       JsMainDisplay.showEditCoursesOfSchoolClassView();
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

    @Override
    public void showAddPersonView() {
        JsMainDisplay.showAddPersonView();
    }

    @Override
    public void showEditPersonView() {
        JsMainDisplay.showEditPersonView();
    }

    @Override
    public void showImportPersonsView() {
        JsMainDisplay.showImportPersonsView();
    }

    @Override
    public void showLogResultsView() {
        JsMainDisplay.showLogResultsView();
      
    }

    @Override
    public void showStudentSchoolclassView() {
      JsMainDisplay.showStudentSchoolclassView();
    }

}
