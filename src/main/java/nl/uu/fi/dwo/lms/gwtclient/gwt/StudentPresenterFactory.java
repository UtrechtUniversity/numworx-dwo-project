package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.OrganisationPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
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
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraphPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

@RoleScope // one per Role change
public class StudentPresenterFactory implements PresenterFactory {
  @Inject MainPresenter mainPresenter;
  @Inject LoginPresenter loginPresenter;
  @Inject AccountPresenter accountPresenter;
  @Inject ModulesPresenter modulesPresenter;
  @Inject StudentSchoolclassPresenter studentSchoolclassPresenter;
  @Inject StudentResultsPresenter studentResultsPresenter;
  @Inject StudentResultsGraphPresenter studentResultsGraphPresenter;

  @Override  @JsMethod
  public StudentSchoolclassPresenter getStudentSchoolclassPresenter() {
    return studentSchoolclassPresenter;
  }

  @Inject AlertDialogWithConfirmCancelPresenter alertDialogWithConfirmCancelPresenter;
  @Inject AlertDialogWithOKPresenter alertDialogWithOKPresenter;
  @Inject MessageDialogWithOKPresenter messageDialogWithOKPresenter;
  @Inject ProgressDialogWithAbortPresenter progressDialogWithAbortPresenter;

  @Inject StudentPresenterFactory() {
    // TODO Auto-generated constructor stub
  }

  @Override @JsMethod
  public MainPresenter getMainPresenter() {
    return mainPresenter;
  }

  @Override  @JsMethod
  public LoginPresenter getLoginPresenter() {
    return loginPresenter;
  }

  @Override @JsMethod
  public WelcomePresenter getWelcomePresenter() {
    return null;
  }

  @Override @JsMethod
  public AccountPresenter getAccountPresenter() {
    return accountPresenter;
  }

  @Override @JsMethod
  public SchoolclassesPresenter getSchoolclassesPresenter() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EditSchoolclassPresenter getEditSchoolclassPresenter() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override @JsMethod
  public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter() {
    return null;
  }

  @Override
  public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter() {
    return null;
  }

  @Override @JsMethod
  public ModulesPresenter getModulesPresenter() {
    return modulesPresenter;
  }

  @Override @JsMethod
  public StudentResultsPresenter getResultsPresenter() {
    return studentResultsPresenter;
  }
  
  @JsMethod @Override
  public StudentResultsGraphPresenter getResultsGraphPresenter() {
	  return studentResultsGraphPresenter;
  }

  @Override @JsMethod
  public SelectedResultsPresenter getSelectedResultsPresenter() {
    return null;
  }

  @Override @JsMethod
  public SelectStudentResultsPresenter getSelectStudentResultsPresenter() {
    return null;
  }

  @Override
  public StudentScoResultPresenter getStudentScoResultPresenter() {
    return null;
  }

  @Override
  public LogResultsPresenter getLogResultsPresenter() {
    return null;
  }

  @Override
  public PersonsPresenter getPersonsPresenter() {
    return null;
  }

  @Override
  public AddPersonPresenter getAddStudentPresenter() {
    return null;
  }

  @Override
  public ImportPersonsPresenter getImportPersonsPresenter() {
    return null;
  }

  @Override
  public EditStudentPresenter getEditStudentPresenter() {
    return null;
  }

  @Override
  public EditTeacherPresenter getEditTeacherPresenter() {
    return null;
  }

  @Override
  public OrganisationPresenter getOrganisationPresenter() {
    return null;
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
    mainPresenter.setView(viewFactory.getMainView());
    loginPresenter.setView(viewFactory.getLoginView());
    accountPresenter.setView(viewFactory.getAccountView());
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

}
