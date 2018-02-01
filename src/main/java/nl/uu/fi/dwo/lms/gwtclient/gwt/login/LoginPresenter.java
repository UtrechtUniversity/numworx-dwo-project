package nl.uu.fi.dwo.lms.gwtclient.gwt.login;

import com.google.gwt.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.ui.DialogEvent;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPromise;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
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

    public interface Display {

        /**
         * Clears the username and password in the ui.
         */
        public void clear();

        /**
         * Sets the username in the ui box.
         */
        public void setUsername(String username);

        /**
         * Sets the password in the ui box.
         */
        public void setPassword(String password);
    }

    public LoginPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        init();
    }

    final public void init() {
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
        Promise<DwoGlobalVars.DwoGlobalVarsState> loginUser;
        try {
            loginUser = dwoGlobalVars.initUser(user, password);
            loginUser.then(new Success<DwoGlobalVars.DwoGlobalVarsState, Void>() {
                @Override
                public Promise<Void> call(Promise<DwoGlobalVars.DwoGlobalVarsState> resolved) throws Exception {
                    if (resolved.getValue() == DwoGlobalVars.DwoGlobalVarsState.LoggedIn) {
                        if (!licenseIsValid(dwoGlobalVars.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool())) {
                            eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "License expired.")));
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
                        if (switchR || switchRole) {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_ROLE));
                        } else {
                            eventBus.fireEvent(new LoginEvent(LoginEvent.State.SUCCESS_WELCOME));
                        }
                        LOG.log(Level.INFO, "login succeeded. Firing Login success event.");
                    } else {                        
                        dwoGlobalVars.clearCurrentUser();
                        LOG.log(Level.INFO, "login failed. Firing Login fail event.");
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));                        
//                        eventBus.fireEvent(new DialogEvent(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, "Wrong login state.")));
                        // TODO fix login stuff
//                        Window.Location.assign("");

                    }
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if (fail instanceof Dwo2Exception) {
                        //Login failed
                        LOG.log(Level.SEVERE, fail.getMessage());
                        dwoGlobalVars.clearCurrentUser();                        
                        //note the order of the events in case of an exception
                        //that might break the running thread.
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    } else {
                        LOG.log(Level.SEVERE, fail.getMessage());
                        dwoGlobalVars.clearCurrentUser();                        
                        eventBus.fireEvent(new LoginEvent(LoginEvent.State.FAIL));
                    }
                }
            }
            );
        } catch (Dwo2Exception ex) {
            Logger.getLogger(LoginPresenter.class.getName()).log(Level.SEVERE, null, ex);
            eventBus.fireEvent(new DialogEvent(ex));
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
