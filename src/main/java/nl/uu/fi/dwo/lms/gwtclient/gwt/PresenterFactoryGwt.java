package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.ui.MsgClickedDialogPresenter;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
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
public class PresenterFactoryGwt implements PresenterFactory {

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final MainPresenter mainPresenter;
    private final LoginPresenter loginPresenter;
    private final WelcomePresenter welcomePresenter;
    private final AccountPresenter accountPresenter;
    private final SchoolclassesPresenter schoolclassesPresenter;
    private final EditSchoolclassPresenter editSchoolclassPresenter;
//    private final ResultsPresenter resultsPresenter;
    private final AddStudentToSchoolclassPresenter addStudentToSchoolclassPresenter;
    private final AddTeacherToSchoolclassPresenter addTeacherToSchoolclassPresenter;
//    private final CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
//    private final ScoResultsPresenter scoResultsPresenter;
//    private final StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
//    private final TeachersInSchoolclassPresenter teachersInSchoolclassPresenter;
//    private final EditStudentPresenter editStudentPresenter;
//    private final AddStudentsPresenter addStudentsPresenter;
    private final MessageDialogWithConfirmPresenter messageDialogWithConfirmPresenter;
    private final AlertDialogWithConfirmPresenter alertDialogWithConfirmPresenter;
    private final AlertDialogWithConfirmCancelPresenter alertDialogWithConfirmCancelPresenter;
    private final FileUploadStudentsPresenter fileUploadStudentsPresenter;
    private final ProgressDialogWithAbortPresenter progressDialogPresenter;

    public PresenterFactoryGwt(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        dwoGlobalVars = aDwoGlobalVars;
        eventBus = anEventBus;
        mainPresenter = new MainPresenter(eventBus, dwoGlobalVars);
        loginPresenter = new LoginPresenter(eventBus, dwoGlobalVars);
        welcomePresenter = new WelcomePresenter(eventBus, dwoGlobalVars);
        accountPresenter = new AccountPresenter(eventBus, dwoGlobalVars);
        schoolclassesPresenter = new SchoolclassesPresenter(eventBus, dwoGlobalVars);
        addStudentToSchoolclassPresenter = new AddStudentToSchoolclassPresenter(anEventBus, aDwoGlobalVars);
        addTeacherToSchoolclassPresenter = new AddTeacherToSchoolclassPresenter(anEventBus, aDwoGlobalVars);
//        coursesOfSchoolclassPresenter = new CoursesOfSchoolclassPresenter(eventBus, dwoGlobalVars);
//        studentsInSchoolclassPresenter = new StudentsInSchoolclassPresenter(eventBus, dwoGlobalVars);
//        teachersInSchoolclassPresenter = new TeachersInSchoolclassPresenter(eventBus, dwoGlobalVars);
//        resultsPresenter = new ResultsPresenter(eventBus, dwoGlobalVars);
        editSchoolclassPresenter = new EditSchoolclassPresenter(eventBus, dwoGlobalVars);
//        editStudentPresenter = new EditStudentPresenter(eventBus, dwoGlobalVars);
//        addStudentsPresenter = new AddStudentsPresenter(eventBus, dwoGlobalVars);
        messageDialogWithConfirmPresenter = new MessageDialogWithConfirmPresenter(eventBus);
        fileUploadStudentsPresenter = new FileUploadStudentsPresenter(eventBus, dwoGlobalVars);
        alertDialogWithConfirmPresenter = new AlertDialogWithConfirmPresenter(eventBus);
        alertDialogWithConfirmCancelPresenter = new AlertDialogWithConfirmCancelPresenter(eventBus);
        progressDialogPresenter = new ProgressDialogWithAbortPresenter(eventBus);

    }

    @Override
    public void bindViewFactory(ViewFactory viewFactory) {
        loginPresenter.setView(viewFactory.getLoginView());
        welcomePresenter.setView(viewFactory.getWelcomeView());
        accountPresenter.setView(viewFactory.getAccountView());
        schoolclassesPresenter.setView(viewFactory.getSchoolclassesView());
        addStudentToSchoolclassPresenter.setView(viewFactory.getAddStudentToSchoolclassView());
        addTeacherToSchoolclassPresenter.setView(viewFactory.getAddTeacherToSchoolclassView());
        
//        resultsPresenter.setView(viewFactory.getResultsView());
//        coursesOfSchoolclassPresenter.setView(viewFactory.getCoursesOfSchoolclassView());
//        studentsInSchoolclassPresenter.setView(viewFactory.getStudentsInSchoolclassView());
//        teachersInSchoolclassPresenter.setView(viewFactory.getTeachersInSchoolclassView());
        editSchoolclassPresenter.setView(viewFactory.getEditSchoolclassView());
//        editStudentPresenter.setView(viewFactory.getEditStudentView());
//        addStudentsPresenter.setView(viewFactory.getAddStudentsView());
        fileUploadStudentsPresenter.setView(viewFactory.getFileUploadStudentsView());
        messageDialogWithConfirmPresenter.setView(viewFactory.getMessageDialogWithConfirmView());
        alertDialogWithConfirmPresenter.setView(viewFactory.getAlertDialogWithConfirmView());
        alertDialogWithConfirmCancelPresenter.setView(viewFactory.getAlertDialogWithConfirmCancelView());
        progressDialogPresenter.setView(viewFactory.getProgressDialogView());
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
//
//    /**
//     * @return the resultsPresenter
//     */
//    @JsMethod    
//    public ResultsPresenter getResultsPresenter() {
//        return resultsPresenter;
//    }
//
//    /**
//     * @return the switchSchoolPresenter
//     */
//    @JsMethod    
//    public SwitchSchoolPresenter getSwitchSchoolPresenter() {
//        return switchSchoolPresenter;
//    }
//
//    /**
//     * @return the scoResultsPresenter
//     */
////    @JsMethod    
//    public ScoResultsPresenter getScoResultsPresenter() {
//        return scoResultsPresenter;
//    }

    @JsMethod    
    public SchoolclassesPresenter getSchoolclassesPresenter() {
        return schoolclassesPresenter;
    }
//
////    @JsMethod    
//    public CoursesOfSchoolclassPresenter getCoursesOfSchoolclassPresenter() {
//        return coursesOfSchoolclassPresenter;
//    }
//
////    @JsMethod    
//    public StudentsInSchoolclassPresenter getStudentsInSchoolclassPresenter() {
//        return studentsInSchoolclassPresenter;
//    }

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
    public EditSchoolclassPresenter getEditSchoolclassPresenter() {
        return editSchoolclassPresenter;
    }
//
//    /**
//     * @return the editStudentPresenter
//     */
//    @JsMethod    
//    public EditStudentPresenter getEditStudentPresenter() {
//        return editStudentPresenter;
//    }

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
//        
//    /**
//     * @return the teachersInSchoolclassPresenter
//     */
//    @JsMethod    
//    public TeachersInSchoolclassPresenter getTeachersInSchoolclassPresenter() {
//        return teachersInSchoolclassPresenter;
//    }
//
//    /**
//     * @return the addStudentsPresenter
//     */
//    @JsMethod    
//    public AddStudentsPresenter getAddStudentsPresenter() {
//        return addStudentsPresenter;
//    }

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

    @Override
    public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
