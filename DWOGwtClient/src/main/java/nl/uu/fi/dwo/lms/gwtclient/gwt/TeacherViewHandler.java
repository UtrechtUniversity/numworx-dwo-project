package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter.Display;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.ChatboxPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

@RoleScope
public class TeacherViewHandler implements SwitchViewEventHandler {
    private static final Logger LOG = Logger.getLogger(TeacherViewHandler.class.getName());

    private final DwoGlobalVars dwoGlobalVars;
    @Inject ViewFactory viewFactory;
    @Inject PresenterFactoryGwt presenterFactory;
    final private BootPanelController controller;
    @Inject EventBus eventBus;
    
    private final Lazy<ChatboxPresenter> chatbox;
   
    @Inject TeacherViewHandler(Lazy<ChatboxPresenter> chatbox, DwoGlobalVars dwoGlobalVars, BootPanelController controller) {
    	this.chatbox = chatbox;
    	this.dwoGlobalVars = dwoGlobalVars;
    	this.controller = controller;
        controller.setRetourHandler(controller.RETOUR_WELCOME);   	
    	if (controller.hasChatbox()) {
    		chatbox.get().init();
    	}
    }
    
  @Override
  public void onSwitchViewEvent(SwitchViewEvent switchViewEvent) {
      Display mainView = viewFactory.getMainView();
      if (switchViewEvent.getEventValue() != SelectedView.LOGIN
              && (dwoGlobalVars.getActiveSchoolRoleAndClass() == null
              || dwoGlobalVars.getActiveSchoolRoleAndClass().getRole() == null
              || !dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().matches(RoleType.TEACHER.name()))) {
          LOG.log(Level.INFO, "Showing account view, because not a teacher.");
          mainView.setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
          mainView.setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
          presenterFactory.getAccountPresenter().init();
          mainView.showAccountView();
      } else {
          if (switchViewEvent.getEventValue() != SelectedView.LOGIN) {
              mainView.setSchoolName(dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
              mainView.setPresentationName(dwoGlobalVars.getCurrentUser().getDisplayName());
          }
          switch (switchViewEvent.getEventValue()) {
              case LOGIN:
                  mainView.showLoginView();
                  presenterFactory.getLoginPresenter().init();
                  if (controller.authToken != null) {
                      String token = controller.authToken;
                      controller.authToken = null;
                      presenterFactory.getLoginPresenter().tokenLogin(token, controller.user_id, controller.org_id);
                  }
                  break;
              case RETOUR:
            	  controller.getRetourHandler().run();
            	  break;
              case WELCOME:
                  controller.RETOUR_WELCOME.run();
                  presenterFactory.getWelcomePresenter().init();
                  break;
              case ACCOUNT:
                  mainView.selectView(SelectedView.ACCOUNT);
                  mainView.showAccountView();
                  presenterFactory.getAccountPresenter().init();
                  break;
              case PERSONS:
                  mainView.selectView(SelectedView.PERSONS);
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
              case RESULTS:
                  mainView.selectView(SelectedView.RESULTS);
                  mainView.showResultsView();
                  presenterFactory.getResultsPresenter().init();
                  break;
              case SELECTEDRESULTS:
                  mainView.showSelectedResultsView();
                  presenterFactory.getSelectedResultsPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                  break;
              case BACKTORESULTS:
                  mainView.showResultsView();
                  presenterFactory.getResultsPresenter().init(switchViewEvent.getResultState());
                  break;
              case SELECTEDRESULTSRETURN:
                  mainView.showSelectedResultsView();
                  presenterFactory.getSelectedResultsPresenter().reinit(switchViewEvent.getResultTree(), switchViewEvent.getResultState());
                  break;
              case RESULTSSTUDENT:
                  //eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  mainView.showStudentScoResultView();
                  presenterFactory.getStudentScoResultPresenter().init(switchViewEvent.getResultTree(), switchViewEvent.getResultStudentScoContext(), switchViewEvent.getResultState(), switchViewEvent.getUserState());
                  break;
              case LOGRESULTS:
                  mainView.showLogResultsView();
                  presenterFactory.getLogResultsPresenter().init(switchViewEvent.getResultTree(),switchViewEvent.getScoResult(),switchViewEvent.getSchoolClass(),switchViewEvent.getResultState());
                  break;
              case SCHOOLCLASSES:
                  mainView.selectView(SelectedView.SCHOOLCLASSES);
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
              case EDITCOURSESOFSCHOOLCLASS:
                  LOG.log(Level.INFO, "Init panel EDITCOURSESOFSCHOOLCLASS.");
                  mainView.showEditCoursesOfSchoolClassView();
                  presenterFactory.getModulesOfSchoolclassPresenter().init(switchViewEvent.getSchoolClass());
                  break;
              case ORGANISATION:
                  eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  break;
              case MODULES:
                  mainView.selectView(SelectedView.MODULES);
                  presenterFactory.getModulesPresenter().show();
                  break;
              case CHATBOX:
            	  if (controller.hasChatbox()) {
	            	  mainView.selectView(SelectedView.CHATBOX);
	            	  mainView.showChatboxView();
	            	  chatbox.get().init();
	            	  chatbox.get().idleOff();
            	  }
            	  break;
              case GOTO_URL:
                  mainView.selectView(SelectedView.MODULES);
                  presenterFactory.getModulesPresenter().show(switchViewEvent.getSearch().get("message"));              
              case ARROWUP: case SEARCH: case GOTO: case CLOSING:
                  break;
              case MODULESVIEW:
            	  presenterFactory.getMainPresenter().showModulesView();
            	  presenterFactory.getModulesPresenter().setVisible(true);
                  break;
              case TRAIL:
            	  presenterFactory.getMainPresenter().setTrails(switchViewEvent.getResultState());
                break;
              case MAYBELOGOUT:
                  presenterFactory.getMainPresenter().maybeLogout();
                  break;

             case KNOWLEDGE:
             case TEACHERSTUDENTMODEL:
             case STUDENTRESULTS: // is return van graph ook voor docent.
            	  if (dwoGlobalVars.isPremium()) {
              		  controller.RETOUR_TEACHER_KNOWLEDGE.run();
	            	  controller.setRetourHandler(controller.RETOUR_TEACHER_KNOWLEDGE);
	            	  presenterFactory.getStudentModelPresenter().init(switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
	                  break;
            	  }
             case STUDENTRESULTSGRAPH:
             	if (dwoGlobalVars.isPremium()) {
                	viewFactory.getMainView().selectView(SelectedView.KNOWLEDGE);
                	viewFactory.getMainView().showStudentResultsGraphView();
             		controller.setRetourHandler(controller.RETOUR_TEACHER_GRAPH);
             		presenterFactory.getResultsGraphPresenter().init(switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
            		break;
             	}
             case SMCLASSRESULTS:
            	 if (dwoGlobalVars.isPremium()) {
            		 mainView.selectView(SelectedView.KNOWLEDGE);
            		 mainView.showTeacherSMClassResultsView();
            		 presenterFactory.getSMClassResultsPresenter().init(switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
            		 break;
            	 }
             case SMSTUDENTRESULTS:
            	 if (dwoGlobalVars.isPremium()) {
            		 mainView.selectView(SelectedView.KNOWLEDGE);
            		 mainView.showStudentResults();
            		 controller.setRetourHandler(controller.RETOUR_STUDENT_KNOWLEDGE);
            		 presenterFactory.getSMResultsPresenter().init(switchViewEvent.getUser(),switchViewEvent.getSchoolClass(), switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
            		 break;
            	 }
             case SMSTUDENTRESULTSGRAPH:
              	if (dwoGlobalVars.isPremium()) {
              		mainView.selectView(SelectedView.KNOWLEDGE);
              		mainView.showStudentResultsGraphView();
             		controller.setRetourHandler(controller.RETOUR_TEACHER_GRAPH);
             		presenterFactory.getResultsGraphPresenter().init(switchViewEvent.getUser(),switchViewEvent.getSchoolClass(), switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
             		break;
              	}
             case SMCLASSFILTER:
            	 if (dwoGlobalVars.isPremium()) {
            		 mainView.selectView(SelectedView.KNOWLEDGE);
            		 mainView.showTeacherClassFilterView();
            		 presenterFactory.getTeacherClassFilterPresenter().init(switchViewEvent.getStudentModelContext(), switchViewEvent.getResultState());
            		 break;
            	 }
             default:
                  eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
                  LOG.log(Level.SEVERE, "Switch panel failed in app controller.");
          }
      }
  }
}