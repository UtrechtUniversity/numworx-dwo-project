package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.gwt.event.shared.EventBus;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;

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
    public SwitchSchoolPresenter getSwitchSchoolPresenter();    
    public ResultsPresenter getResultsPresenter();
    public ScoResultsPresenter getScoResultsPresenter();
    public SchoolclassesPresenter getSchoolclassesPresenter();
    public StudentsInSchoolclassPresenter getStudentsInSchoolclassPresenter();
//    public TeachersInSchoolclassPresenter getTeacherssInSchoolclassPresenter();
    public AccountPresenter getAccountPresenter();
    public AddSchoolclassPresenter getAddSchoolclassPresenter();
    public EditSchoolclassPresenter getEditSchoolclassPresenter();
    public EditStudentPresenter getEditStudentPresenter();
    public MsgDialogPresenter getMsgDialogPresenter();
}