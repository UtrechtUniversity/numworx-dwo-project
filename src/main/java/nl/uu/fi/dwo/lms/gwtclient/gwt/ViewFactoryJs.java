package nl.uu.fi.dwo.lms.gwtclient.gwt;

import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;


import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account.JsAccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsAlertDialogWithConfirmCancelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.login.JsLoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsSchoolClassesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsEditSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsProgressDialogWithAbortView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.welcome.JsWelcomeView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsView;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsAddStudentToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsAddTeacherToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsCopyOrMoveStudentToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsAlertDialogWithConfirmView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsMessageDialogWithConfirmView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class ViewFactoryJs implements ViewFactory {

    private final MainPresenter.Display mainView;
    private final LoginPresenter.Display loginView;
    private final ResultsPresenter.Display resultsView;
    private final SchoolclassesPresenter.Display schoolclassesView;
    private final AddStudentToSchoolclassPresenter.Display addStudentToSchoolclassView;
    private final CopyOrMoveStudentToSchoolclassPresenter.Display copyOrMoveStudentToSchoolclassView;
    private final AddTeacherToSchoolclassPresenter.Display addTeacherToSchoolclassView;
    private final AccountPresenter.Display accountView;
    private final WelcomePresenter.Display welcomeView;
    private final EditSchoolclassPresenter.Display editSchoolclassView;
    private final AlertDialogWithConfirmPresenter.Display alertDialogWithConfirmView;
    private final MessageDialogWithConfirmPresenter.Display messageDialogWithConfirmView;
    private final AlertDialogWithConfirmCancelPresenter.Display alertDialogWithConfirmCancelView;
    private final FileUploadStudentsPresenter.Display fileUploadStudentsView;
    private final ProgressDialogWithAbortPresenter.Display progressDialogView;

    public ViewFactoryJs(PresenterFactory pf) {
        mainView = new JsMainView();
        loginView = new JsLoginView();
        welcomeView = new JsWelcomeView();
        accountView = new JsAccountView();
        schoolclassesView = new JsSchoolClassesView();
        editSchoolclassView = new JsEditSchoolClassView();
        addStudentToSchoolclassView =  new JsAddStudentToSchoolclassView();
        copyOrMoveStudentToSchoolclassView =  new JsCopyOrMoveStudentToSchoolclassView();
        addTeacherToSchoolclassView =  new JsAddTeacherToSchoolclassView();
        //ordered!
        resultsView = new JsResultsView();
//        editStudentView = new EditStudentView(pf.getEditStudentPresenter());
//        addStudentsView = new AddStudentsView(pf.getAddStudentsPresenter());
        alertDialogWithConfirmView = new JsAlertDialogWithConfirmView();
        messageDialogWithConfirmView = new JsMessageDialogWithConfirmView();
        alertDialogWithConfirmCancelView = new JsAlertDialogWithConfirmCancelView();
        progressDialogView = new JsProgressDialogWithAbortView();
        fileUploadStudentsView = new FileUploadStudentsView(pf.getFileUploadStudentsPresenter());
    }

    @Override
    public MainPresenter.Display getMainView() {
        return mainView;
    }

    @Override
    public LoginPresenter.Display getLoginView() {
        return loginView;
    }

    @Override
    public ResultsPresenter.Display getResultsView() {
        return resultsView;
    }
//
//    @Override
//    public SwitchSchoolPresenter.Display getSwitchSchoolView() {
//        return switchSchoolView;
//    }
//
//    @Override
//    public ScoResultsPresenter.Display getScoResultsView() {
//        return scoResultsView;
//    }

    @Override
    public SchoolclassesPresenter.Display getSchoolclassesView() {
        return schoolclassesView;
    }

    @Override
    public AccountPresenter.Display getAccountView() {
        return accountView;
    }

    @Override
    public WelcomePresenter.Display getWelcomeView() {
        return welcomeView;
    }
//
//    @Override
//    public CoursesOfSchoolclassPresenter.Display getCoursesOfSchoolclassView() {
//        return coursesOfSchoolclassView;
//    }
//    
//    @Override
//    public StudentsInSchoolclassPresenter.Display getStudentsInSchoolclassView() {
//        return studentsInSchoolclassView;
//    }

    @Override
    public EditSchoolclassPresenter.Display getEditSchoolclassView() {
        return editSchoolclassView;
    }
//
//    /**
//     * @return the editStudentView
//     */
//    public EditStudentPresenter.Display getEditStudentView() {
//        return editStudentView;
//    }

    /**
     * @return the alertDialogWithConfirmView
     */
    public AlertDialogWithConfirmPresenter.Display getAlertDialogWithConfirmView() {
        return alertDialogWithConfirmView;
    }

    /**
     * @return the messageDialogWithConfirmView
     */
    public MessageDialogWithConfirmPresenter.Display getMessageDialogWithConfirmView() {
        return messageDialogWithConfirmView;
    }
//
//    /**
//     * @return the teachersInSchoolclassView
//     */
//    public TeachersInSchoolclassPresenter.Display getTeachersInSchoolclassView() {
//        return teachersInSchoolclassView;
//    }
//
//    /**
//     * @return the addStudentsView
//     */
//    public AddStudentsPresenter.Display getAddStudentsView() {
//        return addStudentsView;
//    }

    /**
     * @return the fileUploadStudentsView
     */
    public FileUploadStudentsPresenter.Display getFileUploadStudentsView() {
        return fileUploadStudentsView;
    }

    @Override
    public MsgClickedDialogPresenter.Display getMsgClickedDialogView() {
        return alertDialogWithConfirmCancelView;
    }

    @Override
    public ProgressDialogPresenter.Display getProgressDialogView() {
        return progressDialogView;
    }

    @Override
    public AlertDialogWithConfirmCancelPresenter.Display getAlertDialogWithConfirmCancelView() {
        return alertDialogWithConfirmCancelView;
    }
//
//    @Override
//    public AddStudentToSchoolclassPresenter.Display getAddStudentToSchoolclassView() {
//        return AddStudentToSchoolclass
//    }

    @Override
    public AddTeacherToSchoolclassPresenter.Display getAddTeacherToSchoolclassView() {
        return addTeacherToSchoolclassView;
    }

    @Override
    public AddStudentToSchoolclassPresenter.Display getAddStudentToSchoolclassView() {
       return addStudentToSchoolclassView;
    }

    @Override
    public CopyOrMoveStudentToSchoolclassPresenter.Display getCopyOrMoveStudentToSchoolclassView() {
        return copyOrMoveStudentToSchoolclassView;
    }


}
