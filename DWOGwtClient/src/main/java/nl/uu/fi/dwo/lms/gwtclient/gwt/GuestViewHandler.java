package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class GuestViewHandler implements SwitchViewEventHandler {

  @Inject DwoGlobalVars dwoGlobalVars;
  @Inject ViewFactory viewFactory;
  @Inject PresenterFactory presenterFactory;
  @Inject BootPanelController controller;
  @Inject EventBus eventBus;
  
  @Inject GuestViewHandler() {
  }

  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      SelectedView value = switchViewEvent.getEventValue();
      
      Display mainView = viewFactory.getMainView();
      switch (value) {
//        case WELCOME:
//          // show alert
//          eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_User_NoTeacher()));
        default:
        case ACCOUNT:
          if (withUser()) {
            mainView.setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
            mainView.setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
            mainView.showAccountView();
            presenterFactory.getAccountPresenter().init();
            break;
          }
        case LOGIN:
          //mainView.showLoginView();
          presenterFactory.getLoginPresenter().init();
          if (controller.authToken != null) {
              String token = controller.authToken;
              controller.authToken = null;
              presenterFactory.getLoginPresenter().tokenLogin(token);
          }
          else mainView.showLoginView();
          break;
        case MODULES: case WELCOME:
          presenterFactory.getModulesPresenter().show();
          break;
        case MODULESVIEW:
        	presenterFactory.getMainPresenter().showModulesView();
      	    presenterFactory.getModulesPresenter().setVisible(true);
         break;
        case TRAIL:
        	presenterFactory.getMainPresenter().setTrails(switchViewEvent.getResultState());
          break;
        case SEARCH: case ARROWUP: case GOTO:
        case MAYBELOGOUT: case CLOSING:
        case HOME:
          break;
      }
  }

  private boolean withUser() {
    DomSchoolRoleAndClassV2 active = dwoGlobalVars.getActiveSchoolRoleAndClass();
    DomUserFull currentUser = dwoGlobalVars.getCurrentUser();
    return currentUser != null && active != null;
  }

}
