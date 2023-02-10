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
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated. The following states exist: Unintialized, 
 * Initializing, NotLoggedIn, LoggedIn, LoggingOut, Closing.
 * Additionally each state can transition to the Uninitialized. Please see the source
 * for an accurate transition description.<p/>
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
        if(activeSchoolRoleAndClass != null) {
        	context.setDomHasRole(activeSchoolRoleAndClass.getHasRole());
        }
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

    private DwoGlobalVarsState state = DwoGlobalVarsState.Unintialized;
    private DwoGlobalVarPromise statePromise = new DwoGlobalVarPromise(this, DwoGlobalVarsState.LoggedIn);
    private static volatile DwoGlobalVars instance;
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private DomSchoolsRolesAndClassesV2 schoolLogins;
    private DomSchoolRoleAndClassV2  activeSchoolRoleAndClass;

	private DomContext context = new DomContext();

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
        setState(DwoGlobalVarsState.NotLoggedIn);
    }

    /**
     * boot phase one
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    private void initProperties() throws Dwo2Exception {
        LOG.log(Level.INFO, "Starting initProperties():");
        setServer(DwoConstants.constants.server());
        LOG.log(Level.INFO, "restserver="+ server+".");
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

//    /**
//     * Performs a login for the given credentials and initializes DwoGlobalVars
//     * to a DwoGlobalVarsState.LoggedIn state if the credentials are correct. Otherwise the
//     * DwoGlobalVarsState returned in the Promise is DwoGlobalVarsState.NotLoggedIn.
//     * 
//     * @param usercode
//     * @param password
//     * @return
//     * @throws Dwo2Exception 
//     */
//    public Promise<DwoGlobalVarsState> initUser(String usercode, String password) throws Dwo2Exception {
//        if (state != DwoGlobalVarsState.NotLoggedIn) {
//            //if not in proper state throw an exception.
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to initialize a user while in the wrong state");
//            //better yet logout.
//        };
//        //correct state.
//        state = DwoGlobalVarsState.LoggingIn;
//        LOG.log(Level.INFO, "state=LoggingIn. Calling accountManager.login.");
//        Promise<DomUserFullwLoginContext> userwLoginContext = accountManager.login(usercode, password);
////        userwLoginContext.then(initUser(userwLoginContext.getValue().getDomUserFull()));
//        userwLoginContext.then(new Success<DomUserFullwLoginContext, DwoGlobalVarsState>() {
//            @Override
//            public Promise<DwoGlobalVarsState> call(Promise<DomUserFullwLoginContext> resolved) throws Exception {
//                LOG.log(Level.INFO, "Login completed setting current user.");
//                
//                initUser(resolved.getValue().getDomUserFull());
//                state = DwoGlobalVarsState.LoggedIn;
//        LOG.log(Level.INFO, "state=LoggedIn. Calling statePromise.getPromise.");
//                return statePromise.getPromise();
//            }
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                clearCurrentUser();
//                state = DwoGlobalVarsState.NotLoggedIn;
//            }
//        }
//        );
//        return statePromise.getPromise();
//    }

//    @Deprecated
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
//                state = DwoGlobalVarsState.LoggedIn;
//                if (statePromise.getValue().equals(state)) {
//                    statePromise.resolve(state);
//                } else {
//                    statePromise.fail(new Dwo2Exception());
//                }
//                return statePromise.getPromise();
//            }
//        },
//                new Failure() {
//            @Override
//            public void fail(Promise<?> resolved) throws Exception {
//                clearCurrentUser();
//                state = DwoGlobalVarsState.NotLoggedIn;
//                statePromise.fail(new Dwo2Exception());
//            }
//        }
//        );
//        return statePromise.getPromise();
//    }

//    private void initUser() throws Dwo2Exception {
//        SecuredUserAccountManager userManager = new SecuredUserAccountManager();
//        Promise<DomUserFull> user = userManager.getAccountData();
//        initUser(user.getValue());
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
     * FIXME set password if Basic authentication
     */
    public void setCurrentUser(DomUserFull aCurUser) {
        currentUser = aCurUser;
        if (aCurUser == null || !RestAuthenticator.instance.getAuthorization().startsWith("Bearer"))
          GwtRestVars.getInstance().setCurrentUser(aCurUser, getRealm());

    }

    private String getRealm() {
      if (currentLoginContext == null)
        return null;
      else
        return currentLoginContext.getRealm();
    }

    /**
     */
    public void clearCurrentUser() {
        currentUser = null;
        //notify the gwt-rest interface configuration
        GwtRestVars.getInstance().setCurrentUser(null,null);
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
        if(schoolLogins != null) {
        	setActiveSchoolRoleAndClass(schoolLogins.getActiveSchoolRoleAndClass());
        }
    }

    /**
     *
     * @return
     */
    public DomSchoolClass getCurrentSchoolClass() {
        if(activeSchoolRoleAndClass != null)
        	return activeSchoolRoleAndClass.getSchoolClass();
		return null;
    }

    /**
     *
     * @param currentSchoolClass
     */
    public void setCurrentSchoolClass(DomSchoolClass currentSchoolClass) {
        try {
			schoolLogins.getActiveSchoolRoleAndClass().setSchoolClass(currentSchoolClass);
		} catch (NullPointerException e) {
			if(currentSchoolClass != null)
				throw e;
		}
    }

	public DomContext getContext() {
		return context;
	}

	public void setContext(DomContext context) {
		this.context = context;
	}

    public boolean withUser() {
        return getCurrentUser() != null;
    }

    public DomSchool getSchool() {
        try {
            return getActiveSchoolRoleAndClass().getSchool();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isIconizer() {
        try {
            return getCurrentSchoolClass().getIconizer().booleanValue();
        } catch (Exception e) {
            return true;
        }
    }

    public RoleType getRoleType() {
        try {
            String roleName = getActiveSchoolRoleAndClass().getRole().getRoleName();
            return RoleType.valueOf(roleName);
        } catch (Exception e) {
            return RoleType.ANONYMOUS;
        }
    }
   
    public Object getUserID() {
        PersistenceId id = getCurrentUser().getId();
        return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
    }

    public boolean isPremium() {
        //return withUser() && getSchool().getAboType() == AboType.premium;
        return !withUser() || getSchool().getAboType() == AboType.premium;
    }

    public String getUsername() {
      if (!withUser()) return null;
      String realm = getCurrentLoginContext().getRealm();
      String username = getCurrentUser().getUserName();
      if (realm != null) {
          if (username.endsWith("@")) {
              username = username.substring(0, username.length()-1);
          } else if (! username.contains("@")) {
              username += "@" + realm;
          }
      }
      return username;

    }

}
