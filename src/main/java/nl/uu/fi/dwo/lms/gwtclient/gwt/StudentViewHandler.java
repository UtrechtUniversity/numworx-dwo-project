package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

@RoleScope // one per Role change
public class StudentViewHandler implements SwitchViewEventHandler {
  @Inject DwoGlobalVars dwoGlobalVars;
  @Inject PresenterFactory presenterFactory;
  @Inject BootPanelController controller;
  @Inject EventBus eventBus;

  private MainPresenter.Display mainView;

  @Inject StudentViewHandler(ViewFactory viewFactory) {
    mainView = viewFactory.getMainView();
  }

  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
    SelectedView value = switchViewEvent.getEventValue();
    if (!withUser()) value = SelectedView.LOGIN;
    switch (value) {
      default:
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
      case WELCOME:
      case MODULES:
        presenterFactory.getModulesPresenter().show();
        break;
      case MODULESVIEW:
        mainView.showModulesView();
        break;
      case ACCOUNT:
        mainView.showAccountView();
        presenterFactory.getAccountPresenter().init();
        break;
      case SCHOOLCLASSES:
          mainView.showStudentSchoolclassView();
          presenterFactory.getStudentSchoolclassPresenter().init();
          break;
      case LOGIN:
        mainView.showLoginView();
        presenterFactory.getLoginPresenter().init();
        if (controller.authToken != null) {
            String token = controller.authToken;
            controller.authToken = null;
            presenterFactory.getLoginPresenter().tokenLogin(token, controller.user_id, controller.org_id);
        }
      case ARROWUP:
        break;
    }  }

  private boolean withUser() {
    DomSchoolRoleAndClassV2 active = dwoGlobalVars.getActiveSchoolRoleAndClass();
    DomUserFull currentUser = dwoGlobalVars.getCurrentUser();
    return currentUser != null && active != null;
  }

}
