package nl.uu.fi.dwo.account.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated. The following states exist and occur in the
 * following order Unintialized, Initializing, NotLoggedIn, LoggedIn, Closing.
 * Additionally each state can transition to the Uninitialized.
 *
 *
 *
 *
 * @author Gert van der Plas
 */
public class DwoGlobalVars {

    private static final Logger LOG = Logger.getLogger(DwoGlobalVars.class.getName());

    private SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
    private SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();

    /**
     * @return the state
     */
    public DwoGlobalVarsState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    private void setState(DwoGlobalVarsState state) {
        this.state = state;
    }

    /**
     * DwoGlobalStates that define which functions can be called without
     * problems.
     *
     */
    public enum DwoGlobalVarsState {

        /**
         * Can only transition to next state.
         */
        Unintialized,
        /**
         * Can only transition to next state.
         */
        Initializing,
        /**
         * Can only transition to next state.
         */
        Initialized,
        /**
         * Can only transition to next state.
         */
        NotLoggedIn,
        /**
         * Can only transition to next state.
         */
        LoggingIn,
        /**
         * Can only transition to next state.
         */
        LoggedIn,
        /**
         * Can transition to next state or NotLoggedIn state.
         */
        LoggingOut,
        /**
         * Can transition to Uninitialized state.
         */
        Closing
    }

    private DwoGlobalVarsState state = DwoGlobalVarsState.Unintialized;
    private DwoGlobalVarPromise statePromise = new DwoGlobalVarPromise(this, state);
    private static volatile DwoGlobalVars instance;
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private DomSchoolsRolesAndClassesV2 schoolLogins;

    /**
     * @return the instance
     */
    public static DwoGlobalVars instance() {
        if (instance == null) {
            try {
                instance = new DwoGlobalVars();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        }
        return instance;
    }

    /**
     *
     * @return
     */
    public DomSchoolClass getCurrentSchoolClass() {
        return schoolLogins.getActiveSchoolRoleAndClass().getSchoolClass();
    }

    /**
     *
     * @param currentSchoolClass
     */
    public void setCurrentSchoolClass(DomSchoolClass currentSchoolClass) {
        schoolLogins.getActiveSchoolRoleAndClass().setSchoolClass(currentSchoolClass);
    }

    static {
        try {
            instance = new DwoGlobalVars();

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            Window.alert("System error: app improperly configured.");
        }
    }

    /**
     * @return the dwoLocale
     */
    public static DwoLocale getDwoLocale() {
        return dwoLocale;
    }

    /**
     * @param aDwoLocale the dwoLocale to set
     */
    public static void setDwoLocale(DwoLocale aDwoLocale) {
        dwoLocale = aDwoLocale;
    }

    //properties
    private static String server;
    private static DwoLocale dwoLocale = new DwoLocale("nl-NL");

    /**
     *
     * @throws Dwo2Exception
     */
    public DwoGlobalVars() throws Dwo2Exception {
        //TODO define initialization stages: Unintialized, Initializing, NotLoggedIn, LoggedIn. Closing.
        setState(DwoGlobalVarsState.Initializing);
        initProperties();
        initObjects();
        initVars();
        setState(DwoGlobalVarsState.Initialized);
    }

    /**
     * boot phase one
     */
    private void initProperties() throws Dwo2Exception {
        LOG.log(Level.INFO, "Starting initProperties():");
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver=" + server + ".");
        LOG.log(Level.INFO, "Done initProperties():");
    }

    /**
     * boot phase two
     */
    private void initObjects() {
        LOG.log(Level.INFO, "Starting initObjects():");
        Defaults.setServiceRoot(this.getServer());
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        GwtRestVars.instance().setAuthenticator(RestAuthenticator.instance);
//            restService = GWT.create(DWO2RestCaller.class);
        LOG.log(Level.INFO, "Done initObjects():");
    }

    private void initVars() throws Dwo2Exception {
    }

    public Promise<DwoGlobalVarsState> initUser(String usercode, String password) throws Dwo2Exception {
        if (state != DwoGlobalVarsState.Initialized) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to initialize a user while in the wrong state");
            //better yet logout.
        };
        //correct state.
        state = DwoGlobalVarsState.LoggingIn;
        Promise<DomUserFullwLoginContext> userwLoginContext = accountManager.login(usercode, password);
//        userwLoginContext.then(initUser(userwLoginContext.getValue().getDomUserFull()));
        userwLoginContext.then(new Success<DomUserFullwLoginContext, DwoGlobalVarsState>() {
            @Override
            public Promise<DwoGlobalVarsState> call(Promise<DomUserFullwLoginContext> resolved) throws Exception {
                setCurrentUser(resolved.getValue().getDomUserFull());
                initUser(resolved.getValue().getDomUserFull());
                return statePromise.getPromise();
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                clearCurrentUser();
                state = DwoGlobalVarsState.Initialized;
            }
        }
        );
        return statePromise.getPromise();
    }

    private Promise<DwoGlobalVarsState> initUser(DomUserFull user) throws Dwo2Exception {
        if (state != DwoGlobalVarsState.Initializing) {
            //throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to initialize a user while in the wrong state");
            //better yet logout.
        };

        SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
        Promise<DomSchoolsRolesAndClassesV2> logins = loginManager.getSchoolLogins();
        logins.then(new Success<DomSchoolsRolesAndClassesV2, DwoGlobalVarsState>() {
            @Override
            public Promise<DwoGlobalVarsState> call(Promise<DomSchoolsRolesAndClassesV2> resolved) throws Exception {
                schoolLogins = (resolved.getValue());
                state = DwoGlobalVarsState.LoggedIn;
                if (statePromise.getValue().equals(state)) {
                    statePromise.resolve(state);
                } else {
                    statePromise.fail(new Dwo2Exception());
                }
                return statePromise.getPromise();
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                clearCurrentUser();
                state = DwoGlobalVarsState.Initialized;
                statePromise.fail(new Dwo2Exception());
            }
        }
        );
        return statePromise.getPromise();
    }

    public void initUser() throws Dwo2Exception {
        SecuredUserAccountManager userManager = new SecuredUserAccountManager();
        Promise<DomUserFull> user = userManager.getAccountData();
        initUser(user.getValue());
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    /**
     * @param aCurUser the currentUser to set
     */
    public void setCurrentUser(DomUserFull aCurUser) {
        currentUser = aCurUser;
        this.currentUser = aCurUser;
        //notify the gwt-rest interface configuration
        GwtRestVars.getInstance().setCurrentUser(aCurUser);

    }

    /**
     */
    public void clearCurrentUser() {
        currentUser = null;
        //notify the gwt-rest interface configuration
        GwtRestVars.getInstance().setCurrentUser(null);

    }

    /**
     * @return the currentLoginContext
     */
    public DomLoginContext getCurrentLoginContext() {
        return currentLoginContext;
    }

    /**
     * @param currentLoginContext the currentLoginContext to set
     */
    public void setCurrentLoginContext(DomLoginContext currentLoginContext) {
        this.currentLoginContext = currentLoginContext;
    }

    /**
     * @return the schoolLogins
     */
    public DomSchoolsRolesAndClassesV2 getSchoolLogins() {
        return schoolLogins;
    }

    /**
     * @param schoolLogins the schoolLogins to set
     */
    public void setSchoolLogins(DomSchoolsRolesAndClassesV2 schoolLogins) {
        this.schoolLogins = schoolLogins;
    }

}
