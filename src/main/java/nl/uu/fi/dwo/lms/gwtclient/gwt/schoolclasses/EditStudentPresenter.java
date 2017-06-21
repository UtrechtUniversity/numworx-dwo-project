package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class EditStudentPresenter implements SchoolClassDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(EditStudentPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private DomStudent student;
    private String password;
    private DomSchoolClass schoolClass;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
//    private AccountService accountService = new AccountService();

    private Display view;

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(String username, String firstName, String insertion, String familyName, String email);

        void showDialog(String username, String firstName, String insertion, String familyName, String email);
    }

    public EditStudentPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(SchoolClassDialogEvent.TYPE, this);
    }

    public void init() {
        updateUserData();
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    @Override
    public void onDialogEvent(SchoolClassDialogEvent dialogEvent) {
        if (dialogEvent.getEventValue() == SchoolClassDialogEvent.Dialogs.EditStudent) {
            student = dialogEvent.getStudent();
            schoolClass = dialogEvent.getSchoolClass();
            DomGetSingleSchoolStudent singleStudent = new DomGetSingleSchoolStudent(student, schoolClass);
            Promise<DomSingleSchoolStudent> promise;
            promise = manager.getSingleSchoolStudent(singleStudent);
            // onSuccess calculate results and show.
            promise.then(new Success<DomSingleSchoolStudent, Void>() {
                @Override
                public Promise<Void> call(Promise<DomSingleSchoolStudent> resolved) throws Exception {
                    //flip back to schoolclasses screen 
                    DomSingleSchoolStudent value = resolved.getValue();
                    password = value.getPassword();
                    view.showDialog(value.getUserName(), value.getGivenName(), value.getInsertion(), value.getFamilyName(), value.getEmail());
                    return null;
                }
            }, new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if (fail instanceof Dwo2Exception) {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                    } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                        //throw directly
                    }
                }
            }
            );
        }
    }

    public void updateUserData() {
        DomGetSingleSchoolStudent singleStudent = new DomGetSingleSchoolStudent(student, schoolClass);
        Promise<DomSingleSchoolStudent> promise;
        promise = manager.getSingleSchoolStudent(singleStudent);
        promise.then(new Success<DomSingleSchoolStudent, Void>() {
            @Override
            public Promise<Void> call(Promise<DomSingleSchoolStudent> resolved) throws Exception {
                //flip back to schoolclasses screen 
                DomSingleSchoolStudent value = resolved.getValue();
                view.showDialog(value.getUserName(), value.getGivenName(), value.getInsertion(), value.getFamilyName(), value.getEmail());
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        }
        );
    }

    public void updateUser(String givenName, String insertion, String familyName, String email, String newPassword, String newPasswordAgain) {
        DomSingleSchoolStudent user = new DomSingleSchoolStudent();
        user.setId(student.getId());
        user.setUserName(student.getUserName());
        //set freely allowed values
        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(familyName, givenName)) {
            LOG.log(Level.INFO, "valid required fields.");
            user.setFamilyName(familyName.trim());
            user.setGivenName(givenName.trim());
            if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(insertion)) {
                user.setInsertion(insertion.trim());
            } else {
                user.setInsertion(null);
            }
        } else {
            DwoViewer.showMessage(Dwo2ExceptionCode.Rest_Registration_Required_Fields);
            return;
        }

        //check values
        if (!SimpleValidUserFieldsChecker.isValidEmail(email)) {
            DwoViewer.showMessage(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid);
            return;
        } else {
            user.setEmail(email.trim());
        }

        if (!SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && !SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)) {
            user.setPassword(password);
        } else if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPassword)
                && SimpleValidUserFieldsChecker.isNonEmptyNorNull(newPasswordAgain)
                && newPassword.compareTo(newPasswordAgain) == 0) {
            if (!SimpleValidUserFieldsChecker.isValidPassword(newPassword)) {
                //invalid password format
                DwoViewer.showMessage(Dwo2ExceptionCode.User_NewPasswordsDoNotMatch);
            } else {
                user.setPassword(MD5.md5(newPassword));
            }
        } else {
            DwoViewer.showMessage(Dwo2ExceptionCode.User_NewPasswordsDoNotMatch);
            return;
        }

        //All is well, proceed with REST-request
        Promise<Boolean> promisedUser;
        promisedUser = manager.updateSingleSchoolStudent(user);
        // onSuccess calculate results and show.
        promisedUser.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                Boolean result = resolved.getValue();
                eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.STUDENTSINSCHOOLCLASS, schoolClass));
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
        LOG.log(Level.INFO, "Data send to server.");
    }

}
