package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Client factory interface for GWT app.
 * 
 * @author G.A.J. van der Plas
 */
public interface ViewFactory {
    public MainPresenter.Display getMainView();
    public LoginPresenter.Display getLoginView();
    public WelcomePresenter.Display getWelcomeView();
    public AccountPresenter.Display getAccountView();
    public EditSchoolclassPresenter.Display getEditSchoolclassView();
    public SchoolclassesPresenter.Display getSchoolclassesView();
    public AddStudentToSchoolclassPresenter.Display getAddStudentToSchoolclassView();
    public CopyOrMoveStudentToSchoolclassPresenter.Display getCopyOrMoveStudentToSchoolclassView();
    public ModulesOfSchoolclassPresenter.Display getModulesOfSchoolclassView();
    public AddTeacherToSchoolclassPresenter.Display getAddTeacherToSchoolclassView();
    public SelectedResultsPresenter.Display getSelectedResultsView();
    public ResultsPresenter.Display getResultsView();
    public AlertDialogWithConfirmCancelPresenter.Display getAlertDialogWithConfirmCancelView();
    public AlertDialogWithOKPresenter.Display getAlertDialogWithOKView();
    public MessageDialogWithOKPresenter.Display getMessageDialogWithOKView();
//    public PromisedDialogWithOKPresenter.Display getPromisedDialogWithOKView();
//    public MsgClickedDialogPresenter.Display getMsgClickedDialogView();
    public ProgressDialogWithAbortPresenter.Display getProgressDialogWithAbortView();
    public ModulesPresenter.Display getModulesView();

    public PersonsPresenter.Display getPersonsView();
    public EditStudentPresenter.Display getEditStudentView();
    public EditTeacherPresenter.Display getEditTeacherView();
    public AddStudentPresenter.Display getAddPersonView();
    public ImportPersonsPresenter.Display getImportPersonsView();
    public StudentScoResultPresenter.Display getStudentScoResultView();
    public LogResultsPresenter.Display getLogResultsView();
}
