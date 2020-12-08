package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentSchoolclassPresenter;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.OrganisationPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectStudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Client factory interface for GWT App.
 * 
 * @author G.A.J. van der Plas
 */
public interface PresenterFactory {
//    public DwoGlobalVars getDwoGlobalVars();
//    public EventBus getEventBus();
    public MainPresenter getMainPresenter();
    public LoginPresenter getLoginPresenter();
    public WelcomePresenter getWelcomePresenter();
    public AccountPresenter getAccountPresenter();
    public SchoolclassesPresenter getSchoolclassesPresenter();
    public EditSchoolclassPresenter getEditSchoolclassPresenter();
    public StudentSchoolclassPresenter getStudentSchoolclassPresenter();
    public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter();
    public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter();
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter();
    public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter();
    public ModulesPresenter getModulesPresenter();
    public AbstractResultsPresenter getResultsPresenter();
    public AbstractResultsPresenter getResultsGraphPresenter();
    public SelectedResultsPresenter getSelectedResultsPresenter();
    public SelectStudentResultsPresenter getSelectStudentResultsPresenter();
    public StudentScoResultPresenter getStudentScoResultPresenter();
    public LogResultsPresenter getLogResultsPresenter();
    public PersonsPresenter getPersonsPresenter();
    public AddPersonPresenter getAddStudentPresenter();
    public ImportPersonsPresenter getImportPersonsPresenter();
    public EditStudentPresenter getEditStudentPresenter();
    public EditTeacherPresenter getEditTeacherPresenter();
    
    public OrganisationPresenter getOrganisationPresenter();

    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter();
    public AlertDialogWithOKPresenter getAlertDialogWithOKPresenter();
    public MessageDialogWithOKPresenter getMessageDialogWithOKPresenter();
    public ProgressDialogWithAbortPresenter getProgressDialogWithAbortPresenter();
    public void bindViewFactory(ViewFactory viewFactory);
    public void setStage(int stage);
}