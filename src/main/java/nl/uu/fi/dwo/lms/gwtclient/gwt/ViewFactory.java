package nl.uu.fi.dwo.lms.gwtclient.gwt;

import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithConfirmPresenter;
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
    public AddTeacherToSchoolclassPresenter.Display getAddTeacherToSchoolclassView();
    public ResultsPresenter.Display getResultsView();
    public AlertDialogWithConfirmCancelPresenter.Display getAlertDialogWithConfirmCancelView();
    public AlertDialogWithConfirmPresenter.Display getAlertDialogWithConfirmView();
    public MessageDialogWithConfirmPresenter.Display getMessageDialogWithConfirmView();
    public MsgClickedDialogPresenter.Display getMsgClickedDialogView();
    public FileUploadStudentsPresenter.Display getFileUploadStudentsView();
    public ProgressDialogWithAbortPresenter.Display getProgressDialogView();
}