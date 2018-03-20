package nl.uu.fi.dwo.lms.gwtclient.gwt;

import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;


import fi.dwo.gwt.lib.rest.ui.MsgConfirmDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.account.JsAccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsConfirmDialogView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.login.JsLoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.ui.JsMsgDialogView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsSchoolClassesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.roleswitch.JsSwitchSchoolView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsAddSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsEditSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsStudentsInSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsTeachersInSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.welcome.JsWelcomeView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassPresenter;
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
    private final SwitchSchoolPresenter.Display switchSchoolView;
    private final ScoResultsPresenter.Display scoResultsView;
    private final SchoolclassesPresenter.Display schoolclassesView;
    private final AccountPresenter.Display accountView;
    private final WelcomePresenter.Display welcomeView;
    private final AddSchoolclassPresenter.Display addSchoolclassView;
    private final CoursesOfSchoolclassPresenter.Display coursesOfSchoolclassView;
    private final StudentsInSchoolclassPresenter.Display studentsInSchoolclassView;
    private final TeachersInSchoolclassPresenter.Display teachersInSchoolclassView;
    private final EditSchoolclassPresenter.Display editSchoolclassView;
    private final EditStudentPresenter.Display editStudentView;
    private final MsgDialogPresenter.Display msgDialogView;
    private final MsgConfirmDialogPresenter.Display msgConfirmDialogView;
    private final MsgClickedDialogPresenter.Display msgClickedDialogView;
    private final AddStudentsPresenter.Display addStudentsView;
    private final FileUploadStudentsPresenter.Display fileUploadStudentsView;

    public ViewFactoryJs(PresenterFactory pf) {
        mainView = new JsMainView();
        loginView = new JsLoginView();
        resultsView = new JsResultsView();
        switchSchoolView = new JsSwitchSchoolView();
        scoResultsView = new ScoResultsView(pf.getScoResultsPresenter());
        accountView = new JsAccountView();
        welcomeView = new JsWelcomeView();
        //ordered!
        schoolclassesView = new JsSchoolClassesView();
        addSchoolclassView = new JsAddSchoolClassView();
        coursesOfSchoolclassView = new CoursesOfSchoolclassView(pf.getCoursesOfSchoolclassPresenter());
        studentsInSchoolclassView = new JsStudentsInSchoolClassView();
        teachersInSchoolclassView = new JsTeachersInSchoolClassView();
        editSchoolclassView = new JsEditSchoolClassView();
        editStudentView = new EditStudentView(pf.getEditStudentPresenter());
        addStudentsView = new AddStudentsView(pf.getAddStudentsPresenter());
        msgDialogView = new JsMsgDialogView();
        msgConfirmDialogView = new JsConfirmDialogView();
        msgClickedDialogView = null;//new JsClickedDialogView();
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

    @Override
    public SwitchSchoolPresenter.Display getSwitchSchoolView() {
        return switchSchoolView;
    }

    @Override
    public ScoResultsPresenter.Display getScoResultsView() {
        return scoResultsView;
    }

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

    @Override
    public AddSchoolclassPresenter.Display getAddSchoolclassView() {
        return addSchoolclassView;
    }

    @Override
    public CoursesOfSchoolclassPresenter.Display getCoursesOfSchoolclassView() {
        return coursesOfSchoolclassView;
    }
    
    @Override
    public StudentsInSchoolclassPresenter.Display getStudentsInSchoolclassView() {
        return studentsInSchoolclassView;
    }

    @Override
    public EditSchoolclassPresenter.Display getEditSchoolclassView() {
        return editSchoolclassView;
    }

    /**
     * @return the editStudentView
     */
    public EditStudentPresenter.Display getEditStudentView() {
        return editStudentView;
    }

    /**
     * @return the msgDialogView
     */
    public MsgDialogPresenter.Display getMsgDialogView() {
        return msgDialogView;
    }

    /**
     * @return the msgConfirmDialogView
     */
    public MsgConfirmDialogPresenter.Display getMsgConfirmDialogView() {
        return msgConfirmDialogView;
    }

    /**
     * @return the teachersInSchoolclassView
     */
    public TeachersInSchoolclassPresenter.Display getTeachersInSchoolclassView() {
        return teachersInSchoolclassView;
    }

    /**
     * @return the addStudentsView
     */
    public AddStudentsPresenter.Display getAddStudentsView() {
        return addStudentsView;
    }

    /**
     * @return the fileUploadStudentsView
     */
    public FileUploadStudentsPresenter.Display getFileUploadStudentsView() {
        return fileUploadStudentsView;
    }

    @Override
    public MsgClickedDialogPresenter.Display getMsgClickedDialogView() {
        return msgClickedDialogView;
    }


}
