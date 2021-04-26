package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays;

import com.google.gwt.core.client.JavaScriptObject;
import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 *
 * @author G.A.J. van der Plas
 */
public class JsMainView implements MainPresenter.Display{

    final boolean hasIdle = true;
  
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
    public void showTeacherStudentModelView() {
    	JsMainDisplay.showTeacherStudentModelView();
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
    public void showModulesView(boolean box) {
        JsMainDisplay.showModulesView();
        JsMainDisplay.setSearchBox(box);
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

    @Override
    public String getSearchInput() {
      return JsMainDisplay.getSearchInput();
    }
    
    @Override
    public void setTrails(JavaScriptObject jso) {
    	JsMainDisplay.setTrails(jso);
    }
    
    @Override
    public void selectView(SelectedView view) {
      JsMainDisplay.selectView(String.valueOf(view));
    }

	@Override
	public void showStudentResults() {
		JsMainDisplay.showStudentResultsView();
	}
	
	@Override
	public void setPremium(boolean set) {
		JsMainDisplay.setPremium(set);
	}
	
	@Override
	public void setSearchBox(boolean on) {
		JsMainDisplay.setSearchBox(on);
	}
	
	@Override
	public void setIdleTimeout(int millis) {
	  if (hasIdle)
		JsMainDisplay.setIdleTimeout(millis);
	}
	
	@Override
	public void unsetIdleTimeout() {
	  if (hasIdle)
		JsMainDisplay.unsetIdleTimeout();
	}
	
	@Override
	public void showStudentResultsGraphView() {
		JsMainDisplay.showStudentResultsGraphView();
	}
	
	@Override
	public void showTeacherSMClassResultsView() {
		JsMainDisplay.showTeacherSMClassResultsView();
	}
}
