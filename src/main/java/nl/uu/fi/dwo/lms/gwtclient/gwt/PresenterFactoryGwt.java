package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.FileUploadStudentsPresenter;
import fi.dwo.gwt.lib.rest.ui.ProgressDialogPresenter;
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
    private final CopyOrMoveStudentToSchoolclassPresenter copyOrMoveStudentToSchoolclassPresenter;
    private final AddTeacherToSchoolclassPresenter addTeacherToSchoolclassPresenter;
    private final ModulesOfSchoolclassPresenter modulesOfSchoolclassPresenter;
//    private final CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
//    private final ScoResultsPresenter scoResultsPresenter;
//    private final StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
//    private final TeachersInSchoolclassPresenter teachersInSchoolclassPresenter;
//    private final EditStudentPresenter editStudentPresenter;
//    private final AddStudentsPresenter addStudentsPresenter;
    private final MessageDialogWithOKPresenter messageDialogWithOKPresenter;
    private final AlertDialogWithOKPresenter alertDialogWithOKPresenter;
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
        editSchoolclassPresenter = new EditSchoolclassPresenter(eventBus, dwoGlobalVars);
        addStudentToSchoolclassPresenter = new AddStudentToSchoolclassPresenter(eventBus, dwoGlobalVars);
        copyOrMoveStudentToSchoolclassPresenter = new CopyOrMoveStudentToSchoolclassPresenter(eventBus, dwoGlobalVars);
        addTeacherToSchoolclassPresenter = new AddTeacherToSchoolclassPresenter(eventBus, dwoGlobalVars);
        modulesOfSchoolclassPresenter = new ModulesOfSchoolclassPresenter(eventBus, dwoGlobalVars);
//        coursesOfSchoolclassPresenter = new CoursesOfSchoolclassPresenter(eventBus, dwoGlobalVars);
//        studentsInSchoolclassPresenter = new StudentsInSchoolclassPresenter(eventBus, dwoGlobalVars);
//        teachersInSchoolclassPresenter = new TeachersInSchoolclassPresenter(eventBus, dwoGlobalVars);
//        resultsPresenter = new ResultsPresenter(eventBus, dwoGlobalVars);
//        editStudentPresenter = new EditStudentPresenter(eventBus, dwoGlobalVars);
//        addStudentsPresenter = new AddStudentsPresenter(eventBus, dwoGlobalVars);
        messageDialogWithOKPresenter = new MessageDialogWithOKPresenter(eventBus);
        fileUploadStudentsPresenter = new FileUploadStudentsPresenter(eventBus, dwoGlobalVars);
        alertDialogWithOKPresenter = new AlertDialogWithOKPresenter(eventBus);
        alertDialogWithConfirmCancelPresenter = new AlertDialogWithConfirmCancelPresenter(eventBus);
        progressDialogPresenter = new ProgressDialogWithAbortPresenter(eventBus);

    }

    @Override
    public void bindViewFactory(ViewFactory viewFactory) {
        loginPresenter.setView(viewFactory.getLoginView());
        welcomePresenter.setView(viewFactory.getWelcomeView());
        accountPresenter.setView(viewFactory.getAccountView());
        schoolclassesPresenter.setView(viewFactory.getSchoolclassesView());
        editSchoolclassPresenter.setView(viewFactory.getEditSchoolclassView());
        addStudentToSchoolclassPresenter.setView(viewFactory.getAddStudentToSchoolclassView());
        copyOrMoveStudentToSchoolclassPresenter.setView(viewFactory.getCopyOrMoveStudentToSchoolclassView());
        addTeacherToSchoolclassPresenter.setView(viewFactory.getAddTeacherToSchoolclassView());
        modulesOfSchoolclassPresenter.setView(viewFactory.getModulesOfSchoolclassView());
//        resultsPresenter.setView(viewFactory.getResultsView());
//        coursesOfSchoolclassPresenter.setView(viewFactory.getCoursesOfSchoolclassView());
//        studentsInSchoolclassPresenter.setView(viewFactory.getStudentsInSchoolclassView());
//        teachersInSchoolclassPresenter.setView(viewFactory.getTeachersInSchoolclassView());
//        editStudentPresenter.setView(viewFactory.getEditStudentView());
//        addStudentsPresenter.setView(viewFactory.getAddStudentsView());
        fileUploadStudentsPresenter.setView(viewFactory.getFileUploadStudentsView());
        messageDialogWithOKPresenter.setView(viewFactory.getMessageDialogWithOKView());
        alertDialogWithOKPresenter.setView(viewFactory.getAlertDialogWithOKView());
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
    @Override
    public DwoGlobalVars getDwoGlobalVars() {
        return dwoGlobalVars;
    }

    /**
     * @return the mainPresenter
     */
    @JsMethod
    @Override
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    /**
     * @return the loginPresenter
     */
    @JsMethod
    @Override
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
    @Override
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
    @Override
    public AccountPresenter getAccountPresenter() {
        return accountPresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod    
    @Override
    public WelcomePresenter getWelcomePresenter() {
        return welcomePresenter;
    }
    
    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod    
    @Override
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
     * @return the messageDialogWithOKPresenter
     */
    @JsMethod    
    @Override
    public MessageDialogWithOKPresenter getMessageDialogWithOKPresenter() {
        return messageDialogWithOKPresenter;
    }

    @JsMethod
    @Override
    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter() {
        return alertDialogWithConfirmCancelPresenter;
    }

   @JsMethod
    @Override
    public ProgressDialogPresenter getProgressDialogPresenter() {
        return progressDialogPresenter;
    }
    
    @JsMethod
    @Override
    public AlertDialogWithOKPresenter getAlertDialogWithOKPresenter() {
        return alertDialogWithOKPresenter;
    }

//    @JsMethod    
    @Override
    public FileUploadStudentsPresenter getFileUploadStudentsPresenter() {
        return fileUploadStudentsPresenter;
    }

     @JsMethod
    public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter() {
        return addStudentToSchoolclassPresenter;
    }

     @JsMethod
    @Override
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter() {
        return addTeacherToSchoolclassPresenter;
    }

    /**
     * @return the copyOrMoveStudentToSchoolclassPresenter
     */
     @JsMethod
    @Override
    public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter() {
        return copyOrMoveStudentToSchoolclassPresenter;
    }

    /**
     * @return the modulesOfSchoolclassPresenter
     */
      @JsMethod
    @Override
    public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter() {
        return modulesOfSchoolclassPresenter;
    }

}
