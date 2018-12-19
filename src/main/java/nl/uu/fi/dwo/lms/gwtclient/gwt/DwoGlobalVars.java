package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.i18n.client.LocaleInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

import org.osgi.util.promise.Promise;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated. The following states exist: Unintialized,
 * Initializing, NotLoggedIn, LoggedIn, LoggingOut, Closing. Additionally each
 * state can transition to the Uninitialized. Please see the source for an
 * accurate transition description.<p/>
 *
 * @author Gert van der Plas
 */
@Singleton
public class DwoGlobalVars {

    private static final Logger LOG = Logger.getLogger(DwoGlobalVars.class.getName());

    private final SecuredUserAccountManager accountManager;
    private SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();

    private DwoGlobalVarsState state = DwoGlobalVarsState.Unintialized;
    private DwoGlobalVarDeferred<DwoGlobalVarsState> stateDeferred = new DwoGlobalVarDeferred<>(this, DwoGlobalVarsState.LoggedIn);
//    private static volatile DwoGlobalVars instance;
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private DomSchoolsRolesAndClassesV2 schoolLogins;
    private DomSchoolRoleAndClassV2 activeSchoolRoleAndClass;
    private Promise<DomDwoProfileFull> profile;
    private static String helpUrlPrefix ;

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
        NotLoggedIn,
        /**
         * Can only transition to next state.
         */
        LoggingIn,
        /**
         * Can only transition to next state or NotLoggedIn state.
         */
        LoggedIn,
        /**
         * Can transition to next state.
         */
        LoggingOut,
        /**
         * Can transition to Uninitialized state.
         */
        Closing
    }

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
     * Returns the activeSchoolRoleAndClass selected in the Application.
     *
     * @return the activeSchoolRoleAndClass
     */
    public DomSchoolRoleAndClassV2 getActiveSchoolRoleAndClass() {
        return activeSchoolRoleAndClass;
    }

    /**
     * @param activeSchoolRoleAndClass the activeSchoolRoleAndClass to set
     */
    public void setActiveSchoolRoleAndClass(DomSchoolRoleAndClassV2 activeSchoolRoleAndClass) {
        this.activeSchoolRoleAndClass = activeSchoolRoleAndClass;
    }

//    /**
//     * @return the instance
//     */
//    private static DwoGlobalVars instance() {
//        if (instance == null) {
//            try {
//                instance = new DwoGlobalVars();
//            } catch (Dwo2Exception ex) {
//                LOG.log(Level.SEVERE, "", ex);
//            }
//        }
//        return instance;
//    }
//    static {
//        try {
//            instance = new DwoGlobalVars();
//
//        } catch (Dwo2Exception ex) {
//            LOG.log(Level.SEVERE, "", ex);
//            Window.alert("System error: app improperly configured.");
//        }
//    }
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
    @Inject
    public DwoGlobalVars(SecuredUserAccountManager aman) {
        accountManager = aman;
        //TODO define initialization stages: Unintialized, Initializing, NotLoggedIn, LoggedIn. Closing.
        setState(DwoGlobalVarsState.Initializing);
        initProperties();
        initObjects();
        initVars();
        setState(DwoGlobalVarsState.NotLoggedIn);
    }

    /**
     * boot phase one
     *
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    private void initProperties() {
        LOG.log(Level.INFO, "Starting initProperties():");
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver=" + server + ".");
        LOG.log(Level.INFO, "Done initProperties():");
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
        if ("default".equals(locale)) locale = "en"; // fallback to "en"
        String url = "helppage/helpindex_"+locale+".html";
        helpUrlPrefix = url;
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

    private void initVars() {
    }

    /**
     * initialisation of login steps
     * @throws Dwo2Exception
     */
    void login_step0() throws Dwo2Exception {
      stateDeferred = new DwoGlobalVarDeferred<DwoGlobalVarsState>(this, DwoGlobalVarsState.LoggedIn);
      GwtRestVars.getInstance().setAuthenticator(RestAuthenticator.instance);
      if (state != DwoGlobalVarsState.NotLoggedIn) {
          //if not in proper state throw an exception.
          throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to initialize a user while in the wrong state");
          //better yet logout.
      };
      //correct state.
      state = DwoGlobalVarsState.LoggingIn;
      RestAuthenticator.instance.setCredentials(null, null);
    }

    /**
     * perform first step: setCurrent user, fetch schoollogins
     * @param resolved
     * @return
     * @throws Exception
     */
    private Promise<DomSchoolsRolesAndClassesV2> login_step1(Promise<DomUserFullwLoginContext> resolved) throws Exception {
      LOG.log(Level.INFO, "Login completed setting current user.");
      GwtRestVars.getInstance().setCurrentUser(resolved.getValue().getDomUserFull());
      setCurrentUser(resolved.getValue().getDomUserFull());
      currentLoginContext = resolved.getValue().getDomLoginContext();
      LOG.log(Level.INFO, "Getting current and available school logins.");
      SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
      Promise<DomSchoolsRolesAndClassesV2> logins = loginManager.getSchoolLogins();
      return logins;
  }
    /**
     * perform second step: setActiveSchoolAndClass, resolve(LoggedIn)
     * @param resolved
     * @return
     * @throws Exception
     */
    
    private Promise<DwoGlobalVarsState> login_step2(Promise<DomSchoolsRolesAndClassesV2> resolved) throws Exception {
      schoolLogins = (resolved.getValue());
      setActiveSchoolRoleAndClass(schoolLogins.getActiveSchoolRoleAndClass());
      state = DwoGlobalVarsState.LoggedIn;
      stateDeferred.resolve(state);
      return stateDeferred.getPromise();
  }

    private void login_fail(Promise<?> fail) throws Exception {
        clearCurrentUser();
        state = DwoGlobalVarsState.NotLoggedIn;
        stateDeferred.fail(fail.getFailure());
    }
    
    /**
     * Performs a login for the given credentials and initializes DwoGlobalVars
     * to a DwoGlobalVarsState.LoggedIn state if the credentials are correct.
     * Otherwise the DwoGlobalVarsState returned in the Promise is
     * DwoGlobalVarsState.NotLoggedIn.
     *
     * @param usercode
     * @param password
     * @return
     * @throws Dwo2Exception
     */
    public Promise<DwoGlobalVarsState> initUser(String usercode, String password) throws Dwo2Exception {
        login_step0();
        LOG.log(Level.INFO, "state=LoggingIn. Calling accountManager.login.");
        //logging in
        return accountManager.login(usercode, password).then(this::login_step1).then(this::login_step2, this::login_fail);
        }

    public Promise<DwoGlobalVarsState> initUserWithToken(String token) throws Dwo2Exception {
      login_step0();
      LOG.log(Level.INFO, "state=LoggingIn. Calling accountManager.getUserFromAuthToken.");
      return accountManager.getUserFromAuthToken(token).then(this::login_step1).then(this::login_step2, this::login_fail);
    }

    public Promise<DwoGlobalVarsState> initUserWithSaml(String user_id, String org_id,
                                                        String token) throws Dwo2Exception {
      login_step0();
      Promise<DomUserFullwLoginContext> p1 = accountManager.updateAccountData(user_id, org_id, token);
      Promise<DomSchoolsRolesAndClassesV2> p2 = p1.then(this::login_step1);
      Promise<DwoGlobalVarsState> p3 = p2.then(this::login_step2, this::login_fail);
      return p3;
    }

    //
//    private Promise<DwoGlobalVarsState> initUser(DomUserFull user) throws Dwo2Exception {
//        if (state != DwoGlobalVarsState.Initializing) {
//            //throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to initialize a user while in the wrong state");
//            //better yet logout.
//        };
//        setCurrentUser(user);
//        SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();
//        Promise<DomSchoolsRolesAndClassesV2> logins = loginManager.getSchoolLogins();
//        logins.then(new Success<DomSchoolsRolesAndClassesV2, DwoGlobalVarsState>() {
//            @Override
//            public Promise<DwoGlobalVarsState> call(Promise<DomSchoolsRolesAndClassesV2> resolved) throws Exception {
//                schoolLogins = (resolved.getValue());
//                setActiveSchoolRoleAndClass(schoolLogins.getActiveSchoolRoleAndClass());
//                //state = DwoGlobalVarsState.LoggedIn;
//                if (stateDeferred.getValue().equals(DwoGlobalVarsState.LoggedIn)) {
//                    stateDeferred.resolve(state);
//                } else {
//                    clearCurrentUser();
//                    state = DwoGlobalVarsState.NotLoggedIn;
//                    stateDeferred.fail(new Dwo2Exception());
//                }
//                return stateDeferred.getPromise();
//            }
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> fail) throws Exception {
//                clearCurrentUser();
//                state = DwoGlobalVarsState.NotLoggedIn;
//                stateDeferred.fail((Dwo2Exception) fail);
//            }
//        }
//        );
//        return stateDeferred.getPromise();
//    }
//
//    public void initUser() throws Dwo2Exception {
//        SecuredUserAccountManager userManager = new SecuredUserAccountManager();
//        Promise<DomUserFull> user = userManager.getAccountData();
//        initUser(user);
//    }

 
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

        state = DwoGlobalVarsState.NotLoggedIn;

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
        this.setActiveSchoolRoleAndClass(schoolLogins.getActiveSchoolRoleAndClass());

    }

    /**
     *
     * @return
     */
    public DomSchoolClass getCurrentSchoolClass() {
        return activeSchoolRoleAndClass.getSchoolClass();
    }

    /**
     *
     * @param currentSchoolClass
     */
    public void setCurrentSchoolClass(DomSchoolClass currentSchoolClass) {
      activeSchoolRoleAndClass.setSchoolClass(currentSchoolClass);
    }

    /**
     * @return the profile
     */
    public Promise<DomDwoProfileFull> getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(Promise<DomDwoProfileFull> profile) {
        this.profile = profile;
    }

    public String cloneMsg(String msg) {
        return msg;
    }

    public String buildHelpUrl(String tag) {
        return helpUrlPrefix + tag;
    }

    public boolean isPremium() {
      return getActiveSchoolRoleAndClass().getSchool().getAboType() == AboType.premium;
    }
}
