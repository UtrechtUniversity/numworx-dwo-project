package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.gwt.event.shared.EventBus;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassPresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class PresenterFactoryImpl implements PresenterFactory {

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final MainPresenter mainPresenter;
    private final LoginPresenter loginPresenter;
    private final SwitchSchoolPresenter switchSchoolPresenter;
    private final ResultsPresenter resultsPresenter;
    private final ScoResultsPresenter scoResultsPresenter;
    private final SchoolclassesPresenter schoolclassesPresenter;
    private final StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
    private final TeachersInSchoolclassPresenter teachersInSchoolclassPresenter;
    private final AccountPresenter accountPresenter;
    private final AddSchoolclassPresenter addSchoolclassPresenter;
    private final EditSchoolclassPresenter editSchoolclassPresenter;
    private final EditStudentPresenter editStudentPresenter;
    private final AddStudentsPresenter addStudentsPresenter;
    private final MsgDialogPresenter msgDialogPresenter;

    public PresenterFactoryImpl(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
        eventBus = anEventBus;
        mainPresenter = new MainPresenter(eventBus, dwoGlobalVars);
        loginPresenter = new LoginPresenter(eventBus, dwoGlobalVars);
        resultsPresenter = new ResultsPresenter(eventBus, dwoGlobalVars);
        switchSchoolPresenter = new SwitchSchoolPresenter(eventBus, dwoGlobalVars);
        scoResultsPresenter = new ScoResultsPresenter(eventBus, dwoGlobalVars);
        schoolclassesPresenter = new SchoolclassesPresenter(eventBus, dwoGlobalVars);
        studentsInSchoolclassPresenter = new StudentsInSchoolclassPresenter(eventBus, dwoGlobalVars);
        teachersInSchoolclassPresenter = new TeachersInSchoolclassPresenter(eventBus, dwoGlobalVars);
        accountPresenter = new AccountPresenter(eventBus, dwoGlobalVars);
        addSchoolclassPresenter = new AddSchoolclassPresenter(eventBus, dwoGlobalVars);
        editSchoolclassPresenter = new EditSchoolclassPresenter(eventBus, dwoGlobalVars);
        editStudentPresenter = new EditStudentPresenter(eventBus, dwoGlobalVars);
        addStudentsPresenter = new AddStudentsPresenter(eventBus, dwoGlobalVars);
        msgDialogPresenter = new MsgDialogPresenter(eventBus, dwoGlobalVars);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * @return the dwoGlobalVars
     */
    public DwoGlobalVars getDwoGlobalVars() {
        return dwoGlobalVars;
    }

    /**
     * @return the mainPresenter
     */
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    /**
     * @return the loginPresenter
     */
    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    /**
     * @return the resultsPresenter
     */
    public ResultsPresenter getResultsPresenter() {
        return resultsPresenter;
    }

    /**
     * @return the switchSchoolPresenter
     */
    public SwitchSchoolPresenter getSwitchSchoolPresenter() {
        return switchSchoolPresenter;
    }

    /**
     * @return the scoResultsPresenter
     */
    public ScoResultsPresenter getScoResultsPresenter() {
        return scoResultsPresenter;
    }
    
    public SchoolclassesPresenter getSchoolclassesPresenter(){
        return schoolclassesPresenter;
    }

    public StudentsInSchoolclassPresenter getStudentsInSchoolclassPresenter(){
        return studentsInSchoolclassPresenter;
    }
    /**
     * @return the accountPresenter
     */
    public AccountPresenter getAccountPresenter() {
        return accountPresenter;
    }
    
    
    /**
     * @return the addSchoolclassPresenter
     */
    public AddSchoolclassPresenter getAddSchoolclassPresenter() {
        return addSchoolclassPresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    public EditSchoolclassPresenter getEditSchoolclassPresenter() {
        return editSchoolclassPresenter;
    }

    /**
     * @return the editStudentPresenter
     */
    public EditStudentPresenter getEditStudentPresenter() {
        return editStudentPresenter;
    }

    /**
     * @return the msgDialogPresenter
     */
    public MsgDialogPresenter getMsgDialogPresenter() {
        return msgDialogPresenter;
    }

    /**
     * @return the teachersInSchoolclassPresenter
     */
    public TeachersInSchoolclassPresenter getTeachersInSchoolclassPresenter() {
        return teachersInSchoolclassPresenter;
    }

    /**
     * @return the addStudentsPresenter
     */
    public AddStudentsPresenter getAddStudentsPresenter() {
        return addStudentsPresenter;
    }
}
