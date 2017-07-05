package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsView;
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

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class ViewFactoryImpl implements ViewFactory {

    private final MainPresenter.Display mainView;
    private final LoginPresenter.Display loginView;
    private final ResultsPresenter.Display resultsView;
    private final SwitchSchoolPresenter.Display switchSchoolView;
    private final ScoResultsPresenter.Display scoResultsView;
    private final SchoolclassesPresenter.Display schoolclassesView;
    private final AccountPresenter.Display accountView;
    private final AddSchoolclassPresenter.Display addSchoolclassView;
    private final StudentsInSchoolclassPresenter.Display studentsInSchoolclassView;
    private final TeachersInSchoolclassPresenter.Display teachersInSchoolclassView;
    private final EditSchoolclassPresenter.Display editSchoolclassView;
    private final EditStudentPresenter.Display editStudentView;
    private final MsgDialogPresenter.Display msgDialogView;
    private final MsgConfirmDialogPresenter.Display msgConfirmDialogView;
    private final AddStudentsPresenter.Display addStudentsView;
    private final FileUploadStudentsPresenter.Display fileUploadStudentsView;

    public ViewFactoryImpl(PresenterFactory pf) {
        mainView = new MainView(pf.getMainPresenter());
        loginView = new LoginView(pf.getLoginPresenter());
        resultsView = new ResultsView(pf.getResultsPresenter());
        switchSchoolView = new SwitchSchoolView(pf.getSwitchSchoolPresenter());
        scoResultsView = new ScoResultsView(pf.getScoResultsPresenter());
        accountView = new AccountView(pf.getAccountPresenter());
        //ordered!
        schoolclassesView = new SchoolclassesView(pf.getSchoolclassesPresenter(), this);
        addSchoolclassView = new AddSchoolclassView(pf.getAddSchoolclassPresenter());
        studentsInSchoolclassView = new StudentsInSchoolclassView(pf.getStudentsInSchoolclassPresenter());
        teachersInSchoolclassView = new TeachersInSchoolclassView(pf.getTeachersInSchoolclassPresenter());
        editSchoolclassView = new EditSchoolclassView(pf.getEditSchoolclassPresenter());
        editStudentView = new EditStudentView(pf.getEditStudentPresenter());
        addStudentsView = new AddStudentsView(pf.getAddStudentsPresenter());
        msgDialogView = new MsgDialogView(pf.getMsgDialogPresenter());
        msgConfirmDialogView = new MsgConfirmDialogView(pf.getMsgConfirmDialogPresenter());
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
    public AddSchoolclassPresenter.Display getAddSchoolclassView() {
        return addSchoolclassView;
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
