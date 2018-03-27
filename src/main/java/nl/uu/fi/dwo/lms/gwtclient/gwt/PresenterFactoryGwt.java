package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.roleswitch.SwitchSchoolPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.gwt.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;

import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CoursesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentsInSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.TeachersInSchoolclassPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithConfirmPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
public class PresenterFactoryGwt implements PresenterFactory {

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final MainPresenter mainPresenter;
    private final LoginPresenter loginPresenter;
    private final SwitchSchoolPresenter switchSchoolPresenter;
    private ResultsPresenter resultsPresenter;
    private final ScoResultsPresenter scoResultsPresenter;
    private final SchoolclassesPresenter schoolclassesPresenter;
    private final CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
    private final StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
    private final TeachersInSchoolclassPresenter teachersInSchoolclassPresenter;
    private final AccountPresenter accountPresenter;
    private final WelcomePresenter welcomePresenter;
    private final AddSchoolclassPresenter addSchoolclassPresenter;
    private final EditSchoolclassPresenter editSchoolclassPresenter;
    private final EditStudentPresenter editStudentPresenter;
    private final AddStudentsPresenter addStudentsPresenter;
    private final MessageDialogWithConfirmPresenter messageDialogWithConfirmPresenter;
    private final AlertDialogWithConfirmPresenter alertDialogWithConfirmPresenter;
    private final AlertDialogWithConfirmCancelPresenter alertDialogWithConfirmCancelPresenter;
    private final FileUploadStudentsPresenter fileUploadStudentsPresenter;
    private final ProgressDialogPresenter progressDialogPresenter;

    public PresenterFactoryGwt(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
        eventBus = anEventBus;
        mainPresenter = new MainPresenter(eventBus, dwoGlobalVars);
        loginPresenter = new LoginPresenter(eventBus, dwoGlobalVars);
        resultsPresenter = new ResultsPresenter(eventBus, dwoGlobalVars);
        switchSchoolPresenter = new SwitchSchoolPresenter(eventBus, dwoGlobalVars);
        scoResultsPresenter = new ScoResultsPresenter(eventBus, dwoGlobalVars);
        schoolclassesPresenter = new SchoolclassesPresenter(eventBus, dwoGlobalVars);
        coursesOfSchoolclassPresenter = new CoursesOfSchoolclassPresenter(eventBus, dwoGlobalVars);
        studentsInSchoolclassPresenter = new StudentsInSchoolclassPresenter(eventBus, dwoGlobalVars);
        teachersInSchoolclassPresenter = new TeachersInSchoolclassPresenter(eventBus, dwoGlobalVars);
        accountPresenter = new AccountPresenter(eventBus, dwoGlobalVars);
        welcomePresenter = new WelcomePresenter(eventBus, dwoGlobalVars);
        addSchoolclassPresenter = new AddSchoolclassPresenter(eventBus, dwoGlobalVars);
        editSchoolclassPresenter = new EditSchoolclassPresenter(eventBus, dwoGlobalVars);
        editStudentPresenter = new EditStudentPresenter(eventBus, dwoGlobalVars);
        addStudentsPresenter = new AddStudentsPresenter(eventBus, dwoGlobalVars);
        messageDialogWithConfirmPresenter = new MessageDialogWithConfirmPresenter(eventBus);
        fileUploadStudentsPresenter = new FileUploadStudentsPresenter(eventBus, dwoGlobalVars);
        alertDialogWithConfirmPresenter = new AlertDialogWithConfirmPresenter(eventBus);
        alertDialogWithConfirmCancelPresenter = new AlertDialogWithConfirmCancelPresenter(eventBus);
        progressDialogPresenter = new ProgressDialogPresenter(eventBus);

    }

    @Override
    public void bindViewFactory(ViewFactory viewFactory) {
        loginPresenter.setView(viewFactory.getLoginView());
        resultsPresenter.setView(viewFactory.getResultsView());
        switchSchoolPresenter.setView(viewFactory.getSwitchSchoolView());
        scoResultsPresenter.setView(viewFactory.getScoResultsView());
        schoolclassesPresenter.setView(viewFactory.getSchoolclassesView());
        coursesOfSchoolclassPresenter.setView(viewFactory.getCoursesOfSchoolclassView());
        studentsInSchoolclassPresenter.setView(viewFactory.getStudentsInSchoolclassView());
        teachersInSchoolclassPresenter.setView(viewFactory.getTeachersInSchoolclassView());
        accountPresenter.setView(viewFactory.getAccountView());
        addSchoolclassPresenter.setView(viewFactory.getAddSchoolclassView());
        editSchoolclassPresenter.setView(viewFactory.getEditSchoolclassView());
        editStudentPresenter.setView(viewFactory.getEditStudentView());
        addStudentsPresenter.setView(viewFactory.getAddStudentsView());
        fileUploadStudentsPresenter.setView(viewFactory.getFileUploadStudentsView());
        messageDialogWithConfirmPresenter.setView(viewFactory.getMessageDialogWithConfirmView());
        alertDialogWithConfirmPresenter.setView(viewFactory.getAlertDialogWithConfirmView());
        alertDialogWithConfirmCancelPresenter.setView(viewFactory.getAlertDialogWithConfirmCancelView());
        progressDialogPresenter.setView(viewFactory.getProgressDialogView());
        welcomePresenter.setView(viewFactory.getWelcomeView());
        //last!
        mainPresenter.setView(viewFactory.getMainView());
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
    @JsMethod
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    /**
     * @return the loginPresenter
     */
    @JsMethod
    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    /**
     * @return the resultsPresenter
     */
    @JsMethod    
    public ResultsPresenter getResultsPresenter() {
        return resultsPresenter;
    }

    /**
     * @return the switchSchoolPresenter
     */
    @JsMethod    
    public SwitchSchoolPresenter getSwitchSchoolPresenter() {
        return switchSchoolPresenter;
    }

    /**
     * @return the scoResultsPresenter
     */
//    @JsMethod    
    public ScoResultsPresenter getScoResultsPresenter() {
        return scoResultsPresenter;
    }

    @JsMethod    
    public SchoolclassesPresenter getSchoolclassesPresenter() {
        return schoolclassesPresenter;
    }

//    @JsMethod    
    public CoursesOfSchoolclassPresenter getCoursesOfSchoolclassPresenter() {
        return coursesOfSchoolclassPresenter;
    }

//    @JsMethod    
    public StudentsInSchoolclassPresenter getStudentsInSchoolclassPresenter() {
        return studentsInSchoolclassPresenter;
    }

    /**
     * @return the accountPresenter
     */
    @JsMethod
    public AccountPresenter getAccountPresenter() {
        return accountPresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod    
    public WelcomePresenter getWelcomePresenter() {
        return welcomePresenter;
    }
    
    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod    
    public AddSchoolclassPresenter getAddSchoolclassPresenter() {
        return addSchoolclassPresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod    
    public EditSchoolclassPresenter getEditSchoolclassPresenter() {
        return editSchoolclassPresenter;
    }

    /**
     * @return the editStudentPresenter
     */
    @JsMethod    
    public EditStudentPresenter getEditStudentPresenter() {
        return editStudentPresenter;
    }

    /**
     * @return the msgDialogPresenter
     */
    @JsMethod    
    public MessageDialogWithConfirmPresenter getMessageDialogWithConfirmPresenter() {
        return messageDialogWithConfirmPresenter;
    }

    @JsMethod
    public MsgClickedDialogPresenter getMsgClickedDialogPresenter() {
        return alertDialogWithConfirmCancelPresenter;
    }

   @JsMethod
    public ProgressDialogPresenter getProgressDialogPresenter() {
        return progressDialogPresenter;
    }
        
    /**
     * @return the teachersInSchoolclassPresenter
     */
    @JsMethod    
    public TeachersInSchoolclassPresenter getTeachersInSchoolclassPresenter() {
        return teachersInSchoolclassPresenter;
    }

    /**
     * @return the addStudentsPresenter
     */
    @JsMethod    
    public AddStudentsPresenter getAddStudentsPresenter() {
        return addStudentsPresenter;
    }

    /**
     * @return the fileUploadStudentsPresenter
     */
//    @JsMethod    
    public FileUploadStudentsPresenter getFileUploadStudentsPresenter() {
        return fileUploadStudentsPresenter;
    }

    @Override
    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlertDialogWithConfirmPresenter getAlertDialogWithConfirmPresenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MessageDialogWithConfirmPresenter getMessageDialogWithConfirmPresenterr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
