package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolView;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.ui.MsgConfirmDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgConfirmDialogView;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgDialogView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsAccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsLoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsSchoolClasssesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsWelcomeView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class ViewFactoryTeuniz implements ViewFactory {

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
    private final AddStudentsPresenter.Display addStudentsView;
    private final FileUploadStudentsPresenter.Display fileUploadStudentsView;

    public ViewFactoryTeuniz(PresenterFactory pf) {
        mainView = new JsMainView();
        loginView = new JsLoginView();
        resultsView = new ResultsView(pf.getResultsPresenter());
        switchSchoolView = new SwitchSchoolView(pf.getSwitchSchoolPresenter());
        scoResultsView = new ScoResultsView(pf.getScoResultsPresenter());
        accountView = new JsAccountView();
        welcomeView = new JsWelcomeView();
        //ordered!
        schoolclassesView = new JsSchoolClasssesView();
        addSchoolclassView = new AddSchoolclassView(pf.getAddSchoolclassPresenter());
        coursesOfSchoolclassView = new CoursesOfSchoolclassView(pf.getCoursesOfSchoolclassPresenter());
        studentsInSchoolclassView = new StudentsInSchoolclassView(pf.getStudentsInSchoolclassPresenter());
        teachersInSchoolclassView = new TeachersInSchoolclassView(pf.getTeachersInSchoolclassPresenter());
        editSchoolclassView = new EditSchoolclassView(pf.getEditSchoolclassPresenter());
        editStudentView = new EditStudentView(pf.getEditStudentPresenter());
        addStudentsView = new AddStudentsView(pf.getAddStudentsPresenter());
        nl.uu.fi.dwo.lms.gwtclient.gwt.resources.DwoResources resources = GWT.create(nl.uu.fi.dwo.lms.gwtclient.gwt.resources.DwoResources.class);
        nl.uu.fi.dwo.lms.gwtclient.gwt.resources.DwoStyle style = resources.style();
        style.ensureInjected();
        msgDialogView = new MsgDialogView(pf.getMsgDialogPresenter(),style);
        msgConfirmDialogView = new MsgConfirmDialogView(pf.getMsgConfirmDialogPresenter(),style);
 
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

}
