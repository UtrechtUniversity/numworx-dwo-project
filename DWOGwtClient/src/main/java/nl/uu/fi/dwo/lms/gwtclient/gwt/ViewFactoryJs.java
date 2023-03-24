package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;

import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account.JsAccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsAlertDialogWithConfirmCancelView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.login.JsLoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsLogResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsSchoolClassesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsEditSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsProgressDialogWithAbortView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.welcome.JsWelcomeView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.modules.JsModulesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsAddPersonView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsEditStudentView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsEditTeacherView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsImportPersonsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.persons.JsPersonsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsSelectStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsSelectedResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentScoResultView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsAddStudentToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsAddTeacherToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsCopyOrMoveStudentToSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsModulesOfSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsAlertDialogWithOKView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsMessageDialogWithOKView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
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
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
@Singleton
public class ViewFactoryJs implements ViewFactory {

    private final MainPresenter.Display mainView;
    private final LoginPresenter.Display loginView;
    @Inject
    JsResultsView resultsView;
    @Inject
    JsSelectedResultsView resultsSelectsView;
    private final JsSelectStudentResultsView selectStudentResultsView;
    @Inject
    JsStudentScoResultView studentScoResultView;
    @Inject JsLogResultsView logResultsView;
    private final SchoolclassesPresenter.Display schoolclassesView;
    private final AddStudentToSchoolclassPresenter.Display addStudentToSchoolclassView;
    private final CopyOrMoveStudentToSchoolclassPresenter.Display copyOrMoveStudentToSchoolclassView;
    private final ModulesOfSchoolclassPresenter.Display modulesOfSchoolclassView;
    private final AddTeacherToSchoolclassPresenter.Display addTeacherToSchoolclassView;
    private final AccountPresenter.Display accountView;
    private final WelcomePresenter.Display welcomeView;
    private final EditSchoolclassPresenter.Display editSchoolclassView;
    @Inject
    JsModulesView modulesView; // non final non private, concrete class for @Inject

    private final PersonsPresenter.Display personsView;
    private final AddPersonPresenter.Display addPersonView;
    private final EditStudentPresenter.Display editStudentView;
    private final EditTeacherPresenter.Display editTeacherView;
    private final ImportPersonsPresenter.Display importPersonsView;

    private final AlertDialogWithOKPresenter.Display alertDialogWithOKView;
    private final MessageDialogWithOKPresenter.Display messageDialogWithOKView;
//    private final PromisedDialogWithOKPresenter.Display promisedDialogWithOKView;
    private final AlertDialogWithConfirmCancelPresenter.Display alertDialogWithConfirmCancelView;
    private final ProgressDialogWithAbortPresenter.Display progressDialogWithAbortView;

    @Inject
    ViewFactoryJs() {
        mainView = new JsMainView();
        loginView = new JsLoginView();
        welcomeView = new JsWelcomeView();
        accountView = new JsAccountView();
        schoolclassesView = new JsSchoolClassesView();
        editSchoolclassView = new JsEditSchoolClassView();
        addStudentToSchoolclassView = new JsAddStudentToSchoolclassView();
        copyOrMoveStudentToSchoolclassView = new JsCopyOrMoveStudentToSchoolclassView();
        modulesOfSchoolclassView = new JsModulesOfSchoolclassView();
        addTeacherToSchoolclassView = new JsAddTeacherToSchoolclassView();
        //ordered!
        //resultsView = new JsResultsView();
        //resultsSelectsView = new JsSelectedResultsView();
        selectStudentResultsView = new JsSelectStudentResultsView();
        //studentScoResultView = new JsStudentScoResultView();
        personsView = new JsPersonsView();
        editStudentView = new JsEditStudentView();
        editTeacherView = new JsEditTeacherView();
        addPersonView = new JsAddPersonView();
        importPersonsView = new JsImportPersonsView();

        alertDialogWithOKView = new JsAlertDialogWithOKView();
        messageDialogWithOKView = new JsMessageDialogWithOKView();
        alertDialogWithConfirmCancelView = new JsAlertDialogWithConfirmCancelView();
        progressDialogWithAbortView = new JsProgressDialogWithAbortView();
//        promisedDialogWithOKView = new JsPromisedDialogWithOKView();
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

    @Override
    public SelectedResultsPresenter.Display getSelectedResultsView() {
        return resultsSelectsView;
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
    public AlertDialogWithOKPresenter.Display getAlertDialogWithOKView() {
        return alertDialogWithOKView;
    }

    /**
     * @return the messageDialogWithOKView
     */
    public MessageDialogWithOKPresenter.Display getMessageDialogWithOKView() {
        return messageDialogWithOKView;
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

//
//    @Override
//    public MsgClickedDialogPresenter.Display getMsgClickedDialogView() {
//        return alertDialogWithConfirmCancelView;
//    }

    @Override
    public ProgressDialogWithAbortPresenter.Display getProgressDialogWithAbortView() {
        return progressDialogWithAbortView;
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

    /**
     * @return the modulesOfSchoolclassView
     */
    @Override
    public ModulesOfSchoolclassPresenter.Display getModulesOfSchoolclassView() {
        return modulesOfSchoolclassView;
    }

    @Override
    public ModulesPresenter.Display getModulesView() {
        return modulesView;
    }

    @Override
    public PersonsPresenter.Display getPersonsView() {
        return personsView;
    }

    @Override
    public EditStudentPresenter.Display getEditStudentView() {
        return editStudentView;
    }

    @Override
    public EditTeacherPresenter.Display getEditTeacherView() {
        return editTeacherView;
    }

    @Override
    public AddPersonPresenter.Display getAddPersonView() {
        return addPersonView;
    }

    @Override
    public ImportPersonsPresenter.Display getImportPersonsView() {
        return importPersonsView;
    }

    /**
     * @return the selectStudentResultsView
     */
    protected SelectStudentResultsPresenter.Display getSelectStudentResultsView() {
        return selectStudentResultsView;
    }

    /**
     * @return the studentScoResultView
     */
    public StudentScoResultPresenter.Display getStudentScoResultView() {
        return studentScoResultView;
    }

    @Override
    public LogResultsPresenter.Display getLogResultsView() {
      return logResultsView;
    }

//    @Override
//    public PromisedDialogWithOKPresenter.Display getPromisedDialogWithOKView() {
//        return promisedDialogWithOKView;
//    }

}
