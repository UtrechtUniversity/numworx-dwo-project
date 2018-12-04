package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars.DwoGlobalVarsState;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.PromisedDialogWithConfirmDeferred;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
@RoleScope
public class LoginPresenter {

    private static final Logger LOG = Logger.getLogger(LoginPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private String defaultUsername = "";
    private String defaultPassword = "";

    private DwoLocalesForGWT resourceBindings = DwoLocalesForGWT.instance;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

//    /**
//     * @return the resourceBindings
//     */
//    @JsMethod
//    public DwoLocalesForGWT getResourceBindings() {
//        return resourceBindings;
//    }
    /**
     * @param resourceBindings the resourceBindings to set
     */
    public void setResourceBindings(DwoLocalesForGWT resourceBindings) {
        this.resourceBindings = resourceBindings;
    }

    public interface Display extends BasicDisplay {

        /**
         * Sets the username in the ui box.
         */
        public void setUsername(String username);

        /**
         * Sets the password in the ui box.
         */
        public void setPassword(String password);

        /**
         * Sets a response message in the ui box. For example when a user fails
         * to login
         */
        public void showMessage(String message);

        /**
         * Sets a warning text in the ui box. For example when the client can
         * not reach the remote server.
         */
        public void showWarning(String warning);

        /**
         * Hides the message or warning box.
         */
        public void hideMsgBox();
    }

    @Inject LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        // broken calling view.clear();
        init();
    }

    final public void init() {
//    view.init();
//    view.clear();
//        getView().setUsername(defaultUsername);
//        getView().setPassword(defaultPassword);
    }

//public String loginClickedJS(String user, String password) {
//        this.loginClicked(user, password, false);
//        return "done";
//    }
    /**
     * User login call. A login function is called.
     *
     * @param user usernme
     * @param password cleartext password.
     * @param switchRole In case a roleSwitch is desired, generally the value is
     * false.
     */
    @JsMethod
    public void loginClicked(String user, String password, final Boolean switchRole) {
        PromisedDialogWithConfirmDeferred d;
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = dwoGlobalVars.initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Boolean>() {
                @Override
                public Promise<Boolean> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn && !dwoGlobalVars.getCurrentUser().getSingleSchool()) {
                        if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
                            eventBus.fireEvent(new MessageDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
                        };
                        boolean switchR = true;
                        LOG.log(Level.INFO, "login succeeded for user:" + dwoGlobalVars.getCurrentUser().getUniqueDisplayName());
                        try {
                            RoleType loginRole = RoleType.valueOf(dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName());    
                            if (RoleType.TEACHER == loginRole || RoleType.SCHOOLADMIN == loginRole) {
                                switchR = false;
                            }
                        } catch (Exception e) {
                            switchR = true;
                        }
                        if (switchR || switchRole) {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_ROLE));
                        } else {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_WELCOME));
                        }
                        LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
                        //true means we are done
                        return Promises.resolved(false);
                    } else if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn && dwoGlobalVars.getCurrentUser().getSingleSchool()) {
                        LOG.log(Level.INFO, "login failed, you are a single schoolstudent: " + resolved.getValue().name());
                        dwoGlobalVars.clearCurrentUser();
                        view.showWarning(DwoLocalesForGWT.instance.NUM_DLG_User_NoAccessForYourAccount());
                        return Promises.resolved(true);
                    } else {
                        LOG.log(Level.INFO, "login failed, wrong login state: " + resolved.getValue().name());
                        dwoGlobalVars.clearCurrentUser();
                        view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
                        return Promises.resolved(true);
                    }
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if (fail instanceof Dwo2Exception) {
                        //Login failed
                        LOG.log(Level.INFO, "dwo2exception thrown: " + fail.getMessage());
                        dwoGlobalVars.clearCurrentUser();
                        view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
                    } else {
                        dwoGlobalVars.clearCurrentUser();
                        LOG.log(Level.INFO, "none dwo2exception thrown: " + fail.getMessage());
                        view.showWarning(fail.getMessage());
                    }
                    eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                }
            }
            );
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
        } catch (Exception ex) {
            //Somehow not all exceptions are caught here.
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_InternalError());
        }
    }

    public void tokenLogin(String token, String user_id, String org_id) {
        try {
            Promise<DwoGlobalVarsState> statePromise;
            if(user_id != null && org_id != null) {
              statePromise = dwoGlobalVars.initUserWithSaml(user_id, org_id, token);
            } else {
              statePromise = dwoGlobalVars.initUserWithToken(token);
            }
            statePromise.then(resolved -> {
                if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                    if (!dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().licenseIsValid()) {
                        eventBus.fireEvent(new MessageDialogWithOKEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
                    };
                    boolean switchR = true;
                    LOG.log(Level.INFO, "login succeeded for user:" + dwoGlobalVars.getCurrentUser().getUniqueDisplayName());
                    try {
                        if (dwoGlobalVars.getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
                            switchR = false;
                        }
                    } catch (Exception e) {
                        switchR = true;
                    }
                    if (switchR) {
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_ROLE));
                    } else {
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_WELCOME));
                    }
                    LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
                    //true means we are done
                    return Promises.resolved(Boolean.FALSE);
                } else {
                    LOG.log(Level.INFO, "login failed, wrong login state: " + resolved.getValue().name());
                    dwoGlobalVars.clearCurrentUser();
                    view.showWarning("login failed");
                    return Promises.resolved(Boolean.TRUE);
                }
            },
                    resolved -> {
                        Throwable fail = resolved.getFailure();
                        dwoGlobalVars.clearCurrentUser();
                        if (fail instanceof Dwo2Exception) {
                            //Login failed
                            LOG.log(Level.INFO, "dwo2exception thrown: " + fail.getMessage());
                            Dwo2ExceptionCode code = ((Dwo2Exception) fail).getDwo2Code();
                            DwoLocale locale = DwoGlobalVars.getDwoLocale();
                            //view.hideMsgBox();
                        } else {
                            dwoGlobalVars.clearCurrentUser();
                            LOG.log(Level.INFO, "none dwo2exception thrown: " + fail.getMessage());
                            view.showWarning(fail.getMessage());
                        }
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    }
            );
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_User_AuthenticationError());
        } catch (Exception ex) {
            //Somehow not all exceptions are caught here.
            LOG.log(Level.SEVERE, null, ex);
            dwoGlobalVars.clearCurrentUser();
            view.showWarning(Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_Rest_InternalError());
        }

    }

    /**
     * Sets the default username and password in the login ui.
     *
     * @param u
     * @param pw
     */
    public void setDefaultLogin(String u, String pw) {
        defaultUsername = u;
        defaultPassword = pw;
    }

    public static boolean licenseIsValid(DomSchool s) {
        if (s.getExpire() == null) {
            return true;
        } else {
            Date now = new Date();
            return now.before(s.getExpire());
        }
    }
}
