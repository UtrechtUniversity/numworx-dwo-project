package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.web.bindery.event.shared.EventBus;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Client factory interface for GWT app.
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
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter();
//    public CoursesOfSchoolclassPresenter getCoursesOfSchoolclassPresenter();
//    public StudentsInSchoolclassPresenter getStudentsInSchoolclassPresenter();
//    public TeachersInSchoolclassPresenter getTeachersInSchoolclassPresenter();
//    public ResultsPresenter getResultsPresenter();
//    public EditStudentPresenter getEditStudentPresenter();
//    public AddStudentsPresenter getAddStudentsPresenter();
    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter();
    public AlertDialogWithConfirmPresenter getAlertDialogWithConfirmPresenter();
    public MessageDialogWithConfirmPresenter getMessageDialogWithConfirmPresenterr();
    public ProgressDialogPresenter getProgressDialogPresenter();
    public FileUploadStudentsPresenter getFileUploadStudentsPresenter();
    public void bindViewFactory(ViewFactory viewFactory);
}