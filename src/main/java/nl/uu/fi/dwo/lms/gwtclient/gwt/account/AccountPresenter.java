package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoViewer;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
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
public class AccountPresenter {

    private static final Logger LOG = Logger.getLogger(AccountPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private int selectedIndex = 0;
    private List<DomStudentScoContext> resultScoData;
    private AccountService accountService = new AccountService();

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(String username, String firstName, String insertion, String familyName, String email);
    }

    public AccountPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    public void init() {
        updateUserData();
    }

    public void updateUserData() {
        Promise<DomUserFull> userPromise;
        userPromise = accountService.getUserData();
        // onSuccess calculate results and show.
        userPromise.then(new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomFullUser data returned.");
                DomUserFull uf = resolved.getValue();
                view.updateView(uf.getUserName(), uf.getGivenName(), uf.getInsertion(), uf.getFamilyName(), uf.getEmail());
                dwoGlobalVars.setCurrentUser(uf);//updating data
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    //translate and display in gui
                } else {
                    //throw directly
                }
            }
        });
    }

    /**
     * @param row the course to set
     */
    public void selectRow(int row) {
        if (row != -1) {
            selectedIndex = row;
            return;
        }
    }

    public void updateUser(String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain) {
        if (!MD5.md5(curPassword).equals(dwoGlobalVars.getCurrentUser().getPassword())) {
            DwoViewer.showMessage(Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven);
            return;
        }

        DomUserFull user = new DomUserFull();
        user.setUserName(dwoGlobalVars.getCurrentUser().getUserName());
        //set freely allowed values
        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(curPassword, familyName, givenName)) {
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
            user.setPassword(dwoGlobalVars.getCurrentUser().getPassword());
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
        Promise<DomUserFull> promisedUser;
        promisedUser = accountService.UpdateUserData(user);
        // onSuccess calculate results and show.
        promisedUser.then(new Success<DomUserFull, Void>() {
            @Override
            public Promise<Void> call(Promise<DomUserFull> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomUser returned.");
                DomUserFull u = resolved.getValue();
                dwoGlobalVars.setCurrentUser(u);
                view.updateView(u.getUserName(), u.getGivenName(), u.getInsertion(), u.getFamilyName(), u.getEmail());
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });
        LOG.log(Level.INFO, "Data send to server.");
    }

}
