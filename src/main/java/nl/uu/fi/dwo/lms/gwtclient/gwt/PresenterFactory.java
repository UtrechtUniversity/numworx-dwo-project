package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.web.bindery.event.shared.EventBus;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
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
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Client factory interface for GWT App.
 * 
 * @author G.A.J. van der Plas
 */
public interface PresenterFactory {
    public DwoGlobalVars getDwoGlobalVars();
    public EventBus getEventBus();
    public MainPresenter getMainPresenter();
    public LoginPresenter getLoginPresenter();
    public WelcomePresenter getWelcomePresenter();
    public AccountPresenter getAccountPresenter();
    public SchoolclassesPresenter getSchoolclassesPresenter();
    public EditSchoolclassPresenter getEditSchoolclassPresenter();
    public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter();
    public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter();
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter();
    public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter();
    public ModulesPresenter getModulesPresenter();
    public ResultsPresenter getResultsPresenter();
    public SelectedResultsPresenter getSelectedResultsPresenter();
    public SelectStudentResultsPresenter getSelectStudentResultsPresenter();
    public StudentScoResultPresenter getStudentScoResultPresenter();
    public PersonsPresenter getPersonsPresenter();
    public AddPersonPresenter getAddPersonPresenter();
    public ImportPersonsPresenter getImportPersonsPresenter();
    public EditStudentPresenter getEditStudentPresenter();
    public EditTeacherPresenter getEditTeacherPresenter();

    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter();
    public AlertDialogWithOKPresenter getAlertDialogWithOKPresenter();
    public MessageDialogWithOKPresenter getMessageDialogWithOKPresenter();
    public ProgressDialogPresenter getProgressDialogPresenter();
    public FileUploadStudentsPresenter getFileUploadStudentsPresenter();
    public void bindViewFactory(ViewFactory viewFactory);
}