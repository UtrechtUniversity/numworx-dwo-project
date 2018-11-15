package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

@RoleScope
public class TeacherViewHandler implements SwitchViewEventHandler {
    private static final Logger LOG = Logger.getLogger(TeacherViewHandler.class.getName());

    @Inject DwoGlobalVars dwoGlobalVars;
    @Inject ViewFactory viewFactory;
    @Inject PresenterFactoryGwt presenterFactory;
    @Inject BootPanelController controller;
    @Inject EventBus eventBus;
   
    @Inject TeacherViewHandler() {}
    
  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      if (SwitchViewEvent.eventValue != SelectedView.LOGIN
              && (dwoGlobalVars.getActiveSchoolRoleAndClass() == null
              || dwoGlobalVars.getActiveSchoolRoleAndClass().getRole() == null
              || !dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name()))) {
          LOG.log(Level.INFO, "Showing account view, because not a teacher.");
          viewFactory.getMainView().setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
          viewFactory.getMainView().setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
          presenterFactory.getAccountPresenter().init();
          viewFactory.getMainView().showAccountView();
      } else {
          if (SwitchViewEvent.eventValue != SelectedView.LOGIN) {
              viewFactory.getMainView().setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
              viewFactory.getMainView().setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
          }
          switch (switchViewEvent.getEventValue()) {
              case LOGIN:
                  viewFactory.getMainView().showLoginView();
                  presenterFactory.getLoginPresenter().init();
                  if (controller.authToken != null) {
                      String token = controller.authToken;
                      controller.authToken = null;
                      presenterFactory.getLoginPresenter().tokenLogin(token, controller.user_id, controller.org_id);
                  }
                  break;
              case WELCOME:
                  viewFactory.getMainView().showWelcomeView();
                  presenterFactory.getWelcomePresenter().init();
                  break;
              case ACCOUNT:
                  viewFactory.getMainView().showAccountView();
                  presenterFactory.getAccountPresenter().init();
                  break;
              case PERSONS:
                  viewFactory.getMainView().showPersonsView();
                  presenterFactory.getPersonsPresenter().init();
                  break;
              case ADDPERSON:
                  viewFactory.getMainView().showAddPersonView();
                  presenterFactory.getAddStudentPresenter().init();
                  break;
              case EDITSTUDENT:
                  viewFactory.getMainView().showEditPersonView();
                  presenterFactory.getEditStudentPresenter().init(switchViewEvent.getUser());
                  break;
              case EDITTEACHER:
                  viewFactory.getMainView().showEditPersonView();
                  presenterFactory.getEditTeacherPresenter().init(switchViewEvent.getUser());
                  break;
              case IMPORTPERSONS:
                  viewFactory.getMainView().showImportPersonsView();
                  presenterFactory.getImportPersonsPresenter().init(switchViewEvent.getFile());
                  break;
              case RESULTS:
                  viewFactory.getMainView().showResultsView();
                  presenterFactory.getResultsPresenter().init();
                  break;
              case SELECTEDRESULTS:
                  viewFactory.getMainView().showSelectedResultsView();
                  presenterFactory.getSelectedResultsPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                  break;
              case BACKTORESULTS:
                  viewFactory.getMainView().showResultsView();
                  presenterFactory.getResultsPresenter().init(switchViewEvent.getResultState());
                  break;
              case SELECTEDRESULTSRETURN:
                  viewFactory.getMainView().showSelectedResultsView();
                  presenterFactory.getSelectedResultsPresenter().reinit(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                  break;
              case RESULTSSTUDENT:
                  //eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  viewFactory.getMainView().showStudentScoResultView();
                  presenterFactory.getStudentScoResultPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultStudentScoContext(), switchViewEvent.getResultState(), switchViewEvent.getUserState());
                  break;
              case LOGRESULTS:
                  viewFactory.getMainView().showLogResultsView();
                  presenterFactory.getLogResultsPresenter().init(switchViewEvent.getResultTree(),switchViewEvent.getScoResult(),switchViewEvent.getSchoolClass(),switchViewEvent.getResultState());
                  break;
              case SCHOOLCLASSES:
                  viewFactory.getMainView().showSchoolclassesView();
                  presenterFactory.getSchoolclassesPresenter().init();
                  break;
              case EDITSCHOOLCLASS:
                  viewFactory.getMainView().showEditSchoolclassView();
                  presenterFactory.getEditSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case ADDSTUDENTTOSCHOOLCLASS:
                  viewFactory.getMainView().showAddStudentToSchoolClassView();
                  presenterFactory.getAddStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case COPYORMOVESTUDENTTOSCHOOLCLASS:
                  viewFactory.getMainView().showCopyOrMoveStudentToSchoolClassView();
                  presenterFactory.getCopyOrMoveStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case ADDTEACHERTOSCHOOLCLASS:
                  viewFactory.getMainView().showAddTeacherToSchoolClassView();
                  presenterFactory.getAddTeacherToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case EDITCOURSESOFSCHOOLCLASS:
                  LOG.log(Level.INFO, "Init panel EDITCOURSESOFSCHOOLCLASS.");
                  viewFactory.getMainView().showEditCoursesOfSchoolClassView();
                  presenterFactory.getModulesOfSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case ORGANISATION:
                  eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  break;
              case MODULES:
                  presenterFactory.getModulesPresenter().show();
                  break;
              case MODULESVIEW:
                  viewFactory.getMainView().showModulesView();
                  break;
              default:
                  eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  LOG.log(Level.SEVERE, "Switch panel failed in app controller.");
          }
      }
  }
}