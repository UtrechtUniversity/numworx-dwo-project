package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.OrganisationPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenterSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenterSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenterSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectStudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

@RoleScope
public class SchoolAdminPresenterFactory implements PresenterFactory {

//  @Inject DwoGlobalVars dwoGlobalVars;
//  @Inject EventBus eventBus;
  @Inject MainPresenter mainPresenter;
  @Inject LoginPresenter loginPresenter;
  @Inject AccountPresenter accountPresenter;
  @Inject WelcomePresenter welcomePresenter;
  @Inject AlertDialogWithConfirmCancelPresenter alertDialogWithConfirmCancelPresenter;
  @Inject AlertDialogWithOKPresenter alertDialogWithOKPresenter;
  @Inject MessageDialogWithOKPresenter messageDialogWithOKPresenter;
  @Inject ProgressDialogWithAbortPresenter progressDialogWithAbortPresenter;
  @Inject ModulesPresenter modulesPresenter;

  @Inject PersonsPresenterSchoolAdmin personsPresenter;
  @Inject AddPersonPresenterSchoolAdmin addPersonPresenter;
  @Inject EditStudentPresenterSchoolAdmin editStudentPresenter;
  @Inject ImportPersonsPresenter importPersonsPresenter;
  @Inject SchoolclassesPresenter schoolclassesPresenter;
  @Inject EditSchoolclassPresenter editSchoolclassPresenter;
  @Inject AddTeacherToSchoolclassPresenter addTeacherToSchoolclassPresenter;
  @Inject AddStudentToSchoolclassPresenter addStudentToSchoolclassPresenter;
  @Inject CopyOrMoveStudentToSchoolclassPresenter copyOrMoveStudentToSchoolclassPresenter;
  @Inject EditTeacherPresenter editTeacherPresenter;
  @Inject OrganisationPresenter organisationPresenter;

  @Inject SchoolAdminPresenterFactory() {
  }

//  @Override
//  public DwoGlobalVars getDwoGlobalVars() {
//    return dwoGlobalVars;
//  }
//
//  @Override
//  public EventBus getEventBus() {
//    return eventBus;
//  }

  @Override @JsMethod
  public MainPresenter getMainPresenter() {
    return mainPresenter;
  }

  @Override @JsMethod
  public LoginPresenter getLoginPresenter() {
    return loginPresenter;
  }

  @Override //@JsMethod
  public WelcomePresenter getWelcomePresenter() {
    return welcomePresenter;
  }

  @Override @JsMethod
  public AccountPresenter getAccountPresenter() {
    return accountPresenter;
  }

  @Override @JsMethod
  public SchoolclassesPresenter getSchoolclassesPresenter() {
    return schoolclassesPresenter;
  }

  @Override @JsMethod
  public EditSchoolclassPresenter getEditSchoolclassPresenter() {
    return editSchoolclassPresenter;
  }

  @Override @JsMethod
  public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter() {
    return addStudentToSchoolclassPresenter;
  }

  @Override @JsMethod
  public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter() {
    return copyOrMoveStudentToSchoolclassPresenter;
  }

  @Override @JsMethod
  public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter() {
    return addTeacherToSchoolclassPresenter;
  }

  @Override @JsMethod
  public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter() {
    return null;
  }

  @Override @JsMethod
  public ModulesPresenter getModulesPresenter() {
    return modulesPresenter;
  }

  @Override @JsMethod
  public ResultsPresenter getResultsPresenter() {
    return null;
  }

  @Override @JsMethod
  public SelectedResultsPresenter getSelectedResultsPresenter() {
    return null;
  }

  @Override @JsMethod
  public SelectStudentResultsPresenter getSelectStudentResultsPresenter() {
    return null;
  }

  @Override @JsMethod
  public StudentScoResultPresenter getStudentScoResultPresenter() {
    return null;
  }

  @Override @JsMethod
  public LogResultsPresenter getLogResultsPresenter() {
    return null;
  }

  @Override @JsMethod
  public PersonsPresenter getPersonsPresenter() {
    return personsPresenter;
  }

  @Override @JsMethod
  public AddPersonPresenter getAddStudentPresenter() {
    return addPersonPresenter;
  }

  @Override @JsMethod
  public ImportPersonsPresenter getImportPersonsPresenter() {
    return importPersonsPresenter;
  }

  @Override @JsMethod
  public EditStudentPresenter getEditStudentPresenter() {
    return editStudentPresenter;
  }

  @Override @JsMethod
  public EditTeacherPresenter getEditTeacherPresenter() {
    return editTeacherPresenter;
  }

  @Override @JsMethod
  public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter() {
    return alertDialogWithConfirmCancelPresenter;
  }

  @Override @JsMethod
  public AlertDialogWithOKPresenter getAlertDialogWithOKPresenter() {
    return alertDialogWithOKPresenter;
  }

  @Override @JsMethod
  public MessageDialogWithOKPresenter getMessageDialogWithOKPresenter() {
    return messageDialogWithOKPresenter;
  }

  @Override @JsMethod
  public ProgressDialogWithAbortPresenter getProgressDialogWithAbortPresenter() {
    return progressDialogWithAbortPresenter;
  }

  @Override @Inject
  public void bindViewFactory(ViewFactory viewFactory) {
    accountPresenter.setView(viewFactory.getAccountView());
    addPersonPresenter.setView(viewFactory.getAddPersonView());
    addStudentToSchoolclassPresenter.setView(viewFactory.getAddStudentToSchoolclassView());
    addTeacherToSchoolclassPresenter.setView(viewFactory.getAddTeacherToSchoolclassView());
    copyOrMoveStudentToSchoolclassPresenter.setView(viewFactory.getCopyOrMoveStudentToSchoolclassView());
    editSchoolclassPresenter.setView(viewFactory.getEditSchoolclassView());
    editStudentPresenter.setView(viewFactory.getEditStudentView());
    editTeacherPresenter.setView(viewFactory.getEditTeacherView());
    importPersonsPresenter.setView(viewFactory.getImportPersonsView());
    loginPresenter.setView(viewFactory.getLoginView());
    mainPresenter.setView(viewFactory.getMainView());
    personsPresenter.setView(viewFactory.getPersonsView());
    schoolclassesPresenter.setView(viewFactory.getSchoolclassesView());
    welcomePresenter.setView(viewFactory.getWelcomeView());
        
    alertDialogWithConfirmCancelPresenter.setView(viewFactory.getAlertDialogWithConfirmCancelView());
    alertDialogWithOKPresenter.setView(viewFactory.getAlertDialogWithOKView());
    messageDialogWithOKPresenter.setView(viewFactory.getMessageDialogWithOKView());
    progressDialogWithAbortPresenter.setView(viewFactory.getProgressDialogWithAbortView());
  }

  @Override
  public void setStage(int stage) {
    getLoginPresenter().setStage(stage);
    getMainPresenter().setStage(stage);
  }

  @Override @JsMethod
  public OrganisationPresenter getOrganisationPresenter() {
    return organisationPresenter;
  }

  @Override
  public StudentSchoolclassPresenter getStudentSchoolclassPresenter() {
    return null;
  }

  @Override
  public AbstractResultsPresenter getResultsGraphPresenter() {
	return null;
  }

}
