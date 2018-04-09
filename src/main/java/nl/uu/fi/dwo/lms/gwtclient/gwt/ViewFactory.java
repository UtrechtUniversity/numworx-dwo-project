package nl.uu.fi.dwo.lms.gwtclient.gwt;

import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassPresenter;
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
    public ResultsPresenter.Display getResultsView();
    public SwitchSchoolPresenter.Display getSwitchSchoolView();    
    public ScoResultsPresenter.Display getScoResultsView();
    public SchoolclassesPresenter.Display getSchoolclassesView();
    public CoursesOfSchoolclassPresenter.Display getCoursesOfSchoolclassView();
    public StudentsInSchoolclassPresenter.Display getStudentsInSchoolclassView();
    public TeachersInSchoolclassPresenter.Display getTeachersInSchoolclassView();
    public AccountPresenter.Display getAccountView();
    public WelcomePresenter.Display getWelcomeView();
    public AddSchoolclassPresenter.Display getAddSchoolclassView();
    public EditSchoolclassPresenter.Display getEditSchoolclassView();
    public EditStudentPresenter.Display getEditStudentView();
    public AddStudentsPresenter.Display getAddStudentsView();
    public AlertDialogWithConfirmCancelPresenter.Display getAlertDialogWithConfirmCancelView();
    public AlertDialogWithConfirmPresenter.Display getAlertDialogWithConfirmView();
    public MessageDialogWithConfirmPresenter.Display getMessageDialogWithConfirmView();
    public MsgClickedDialogPresenter.Display getMsgClickedDialogView();
    public FileUploadStudentsPresenter.Display getFileUploadStudentsView();
    public ProgressDialogWithAbortPresenter.Display getProgressDialogView();
}