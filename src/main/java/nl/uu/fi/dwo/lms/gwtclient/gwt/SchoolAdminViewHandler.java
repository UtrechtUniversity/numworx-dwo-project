package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class SchoolAdminViewHandler implements SwitchViewEventHandler {

  @Inject DwoGlobalVars dwoGlobalVars;
  @Inject ViewFactory viewFactory;
  @Inject PresenterFactory presenterFactory;
  @Inject BootPanelController controller;
  @Inject EventBus eventBus;

  @Inject SchoolAdminViewHandler() {}

  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      SelectedView value = switchViewEvent.getEventValue();
      if (!withUser()) value = SelectedView.LOGIN;
      else {
        // if not a schooladmin, value = SelectedView.ACCOUNT;
      }
      switch (value) {
        default:
          eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
        case WELCOME:
          viewFactory.getMainView().showWelcomeView();
          presenterFactory.getWelcomePresenter().init();
          break;
        case MODULES:
          presenterFactory.getModulesPresenter().show();
          break;
        case MODULESVIEW:
          viewFactory.getMainView().showModulesView();
          break;
        case ACCOUNT:
          viewFactory.getMainView().showAccountView();
          presenterFactory.getAccountPresenter().init();
          break;
        case RESULTS:
          eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
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

        
        case LOGIN:
          viewFactory.getMainView().showLoginView();
          presenterFactory.getLoginPresenter().init();
          if (controller.authToken != null) {
              String token = controller.authToken;
              controller.authToken = null;
              presenterFactory.getLoginPresenter().tokenLogin(token, controller.user_id, controller.org_id);
          }
          break;
      }
  }

  private boolean withUser() {
    DomSchoolRoleAndClassV2 active = dwoGlobalVars.getActiveSchoolRoleAndClass();
    DomUserFull currentUser = dwoGlobalVars.getCurrentUser();
    return currentUser != null && active != null;
  }

}
