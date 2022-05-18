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
  @Inject StudentPresenterFactory presenterFactory;
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
    case STUDENTRESULTSGRAPH:
    	if (dwoGlobalVars.isPremium()) {
    		mainView.selectView(value);
    		mainView.showStudentResultsGraphView();
    		presenterFactory.getResultsGraphPresenter().init(switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
   		break;
    	}
    case RETOUR:
    case RESULTS:
    case STUDENTRESULTS:
    case KNOWLEDGE:
    	if (dwoGlobalVars.isPremium()) {
    		mainView.selectView(SelectedView.KNOWLEDGE);
    		mainView.showStudentResults();
    		presenterFactory.getResultsPresenter().init(switchViewEvent.getResultState());
    		break;
    	}
      default:
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
      case WELCOME:
      case MODULES:
        mainView.selectView(value);
        presenterFactory.getModulesPresenter().show();
        break;
      case MODULESVIEW:
    	  presenterFactory.getMainPresenter().showModulesView();
        break;
      case ACCOUNT:
        mainView.selectView(value);
        mainView.showAccountView();
        presenterFactory.getAccountPresenter().init();
        break;
      case SCHOOLCLASSES:
          mainView.selectView(value);
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
      case GOTO_URL:
          mainView.selectView(SelectedView.MODULES);
          presenterFactory.getModulesPresenter().show(switchViewEvent.getSearch().get("message"));              
      case ARROWUP:
      case SEARCH:
      case GOTO:
      case CLOSING:
        break;
      case TRAIL:
    	  presenterFactory.getMainPresenter().setTrails(switchViewEvent.getResultState());
        break;
      case MAYBELOGOUT:
        presenterFactory.getMainPresenter().maybeLogout();
        break;
      case LOGOUT:
    	  presenterFactory.getMainPresenter().forceLogout();
    	  break;
   }  }

  private boolean withUser() {
    DomSchoolRoleAndClassV2 active = dwoGlobalVars.getActiveSchoolRoleAndClass();
    DomUserFull currentUser = dwoGlobalVars.getCurrentUser();
    return currentUser != null && active != null;
  }

}
