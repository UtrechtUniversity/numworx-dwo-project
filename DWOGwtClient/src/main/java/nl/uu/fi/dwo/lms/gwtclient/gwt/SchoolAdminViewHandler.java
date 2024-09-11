package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class SchoolAdminViewHandler implements SwitchViewEventHandler {

  @Inject DwoGlobalVars dwoGlobalVars;
  @Inject ViewFactory viewFactory;
  @Inject PresenterFactory presenterFactory;
  private final BootPanelController controller;
  @Inject EventBus eventBus;

  @Inject SchoolAdminViewHandler(BootPanelController controller) {
	  this.controller = controller;
      controller.setRetourHandler(controller.RETOUR_WELCOME);   	
  }

  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      SelectedView value = switchViewEvent.getEventValue();
      if (!withUser()) value = SelectedView.LOGIN;
      else {
        // if not a schooladmin, value = SelectedView.ACCOUNT;
      }
      Display mainView = viewFactory.getMainView();
      switch (value) {
        default:
          eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
        case RETOUR:
        case WELCOME: 
          mainView.selectView(value);
          mainView.showWelcomeView();
          presenterFactory.getWelcomePresenter().init();
          break;
        case MODULES:
          mainView.selectView(value);
         presenterFactory.getModulesPresenter().show();
          break;
        case MODULESVIEW:
        	presenterFactory.getMainPresenter().showModulesView();
      	    presenterFactory.getModulesPresenter().setVisible(true);
          break;
        case ACCOUNT:
          mainView.selectView(value);
          mainView.showAccountView();
          presenterFactory.getAccountPresenter().init();
          break;
        case RESULTS:
          mainView.selectView(value);
          eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
          break;
        case PERSONS:
          mainView.selectView(value);
          mainView.showPersonsView();
          presenterFactory.getPersonsPresenter().init();
          break;
        case ADDPERSON:
          if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
            break;
          };
            mainView.showAddPersonView();
            presenterFactory.getAddStudentPresenter().init();
            break;
        case EDITSTUDENT:
            mainView.showEditPersonView();
            presenterFactory.getEditStudentPresenter().init(switchViewEvent.getUser());
            break;
        case EDITTEACHER:
            mainView.showEditPersonView();
            presenterFactory.getEditTeacherPresenter().init(switchViewEvent.getUser());
            break;
        case IMPORTPERSONS:
          if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
            break;
          };
            mainView.showImportPersonsView();
            presenterFactory.getImportPersonsPresenter().init(switchViewEvent.getFile());
            break;
        case SCHOOLCLASSES:
            mainView.selectView(value);
            mainView.showSchoolclassesView();
            presenterFactory.getSchoolclassesPresenter().init();
            break;
        case EDITSCHOOLCLASS:
            mainView.showEditSchoolclassView();
            presenterFactory.getEditSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
            break;
        case ADDSTUDENTTOSCHOOLCLASS:
            mainView.showAddStudentToSchoolClassView();
            presenterFactory.getAddStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
            break;
        case COPYORMOVESTUDENTTOSCHOOLCLASS:
            mainView.showCopyOrMoveStudentToSchoolClassView();
            presenterFactory.getCopyOrMoveStudentToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
            break;
        case ADDTEACHERTOSCHOOLCLASS:
            mainView.showAddTeacherToSchoolClassView();
            presenterFactory.getAddTeacherToSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
            break;
        case ORGANISATION:
            mainView.selectView(value);
            mainView.showOrganisationView();
            presenterFactory.getOrganisationPresenter().init();
            break;
        case LOGIN:
          mainView.showLoginView();
          presenterFactory.getLoginPresenter().init();
          if (controller.authToken != null) {
              String token = controller.authToken;
              controller.authToken = null;
              presenterFactory.getLoginPresenter().tokenLogin(token, controller.user_id, controller.org_id);
          }
        case LOGOUT:
      	  presenterFactory.getMainPresenter().forceLogout();
      	  break;
        case GOTO_URL:
            mainView.selectView(SelectedView.MODULES);
            presenterFactory.getModulesPresenter().show(switchViewEvent.getSearch().get("message"));              
        case ARROWUP:
        case SEARCH:
        case GOTO:
        case CLOSING:
          break;
        case HOME:
          //mainView.selectView(SelectedView.MODULES);
          break;
        case TRAIL:
          //mainView.selectView(SelectedView.MODULES);
          presenterFactory.getMainPresenter().setTrails(switchViewEvent.getResultState());
          break;
        case MAYBELOGOUT:
          presenterFactory.getMainPresenter().maybeLogout();
          break;
     }
  }

  private boolean withUser() {
    DomSchoolRoleAndClassV2 active = dwoGlobalVars.getActiveSchoolRoleAndClass();
    DomUserFull currentUser = dwoGlobalVars.getCurrentUser();
    return currentUser != null && active != null;
  }

}
