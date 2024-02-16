package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.i18n.client.LocaleInfo;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import fi.dwo.gwt.lib.rest.CallManagers.LoginPresenter;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.util.Base64;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.JsMainDisplay;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
    private final OAuthManager oauthManager;
    private SecuredUserSchoolLoginManagerV2 loginManager = new SecuredUserSchoolLoginManagerV2();

    private DwoGlobalVarsState state = DwoGlobalVarsState.Unintialized;
    private DwoGlobalVarDeferred<DwoGlobalVarsState> stateDeferred = new DwoGlobalVarDeferred<>(this, DwoGlobalVarsState.LoggedIn);
    private DomUserFull currentUser;
    private DomLoginContext currentLoginContext;
    private DomSchoolsRolesAndClassesV2 schoolLogins;
    private DomSchoolRoleAndClassV2 activeSchoolRoleAndClass;
    private Promise<DomDwoProfileFull> profile;

    private boolean test,saml, modulesOnly;
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
        this.role = null; // uncache
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
    public DwoGlobalVars(SecuredUserAccountManager aman, OAuthManager oman) {
        accountManager = aman;
        oauthManager = oman;
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
      RestAuthenticator.instance.setCredentials(null, null, null);
    }

    private Promise<DomSchoolsRolesAndClassesV2> login_step1a(Promise<DomUserFullwLoginContext> resolved) throws Exception {

    	currentLoginContext = resolved.getValue().getDomLoginContext();
        currentUser = resolved.getValue().getDomUserFull();
        role = null;
        DomContext context = createContext(currentLoginContext);
    	return loginManager.getSchoolLogins(context);
    }
    
    
    /**
     * perform first step: setCurrent user, fetch schoollogins
     * @param resolved
     * @return
     * @throws Exception
     */
    private Promise<DomSchoolsRolesAndClassesV2> login_step1(Promise<DomUserFullwLoginContext> resolved) throws Exception {
      LOG.log(Level.INFO, "Login completed setting current user.");
      currentLoginContext = resolved.getValue().getDomLoginContext();
      DomUserFull domUserFull = resolved.getValue().getDomUserFull();
      GwtRestVars.getInstance().setCurrentUser(domUserFull,currentLoginContext.getRealm());
      setCurrentUser(domUserFull, currentLoginContext.getRealm());
      LOG.log(Level.INFO, "Getting current and available school logins.");
      SecuredUserSchoolLoginManagerV2 loginManager = this.loginManager;
      DomContext context = createContext(currentLoginContext);
      Promise<DomSchoolsRolesAndClassesV2> logins;
      if (isTest() && (isSaml()||isSingleSchool())
    		  && GwtRestVars.getInstance().getRefreshToken() == null
    	 ) {     // alleen als nog GEEN OAUTHMANAGER ACTIEF IS
          OAuthManager oauth = new OAuthManager();
          logins = accountManager.getBearerToken(context).then(
    		  p -> { 
    			  String token = Base64.btoa("2\f" + p.getValue()); // Format 2 
    			  return oauth.authorization_token(token);
    		  }   		  
        ).then(p -> { 
      	  GwtRestVars.getInstance().setBearerToken(p.getValue().getAccess_token());
      	  GwtRestVars.getInstance().setRefreshToken(p.getValue().getRefresh_token());
      	  return loginManager.getSchoolLogins(context);
      	  
        } );
      } else {
        logins = loginManager.getSchoolLogins(context);
      }
      
      return logins;
  }

	public DomContext createContext(DomLoginContext loginContext) {
    DomContext context = new DomContext();
    context.setRealm(loginContext.getRealm());
    DomHasRole domHasRole = new DomHasRole();
    domHasRole.setUserId(loginContext.getUserId());
    domHasRole.setSchoolGroupId(loginContext.getSchoolGroupId());
    domHasRole.setId(loginContext.getHasRoleId());
    if (domHasRole.getId()== null)
    	setHasRoleId(domHasRole);
    domHasRole.setRights(""); // no rights....
    context.setDomHasRole(domHasRole);
    return context;
  }

	/* From PersistentHasRole
     public static PersistenceId buildPersistenceId(PersistentHasRolePK hasRoleKey) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d;%020d",
                PersistenceClassType.PersistentHasRole.name(), hasRoleKey.getUserID(),hasRoleKey.getSchoolGroupID()));
        return id;
    }

	 */
	
  // This is a big hack, we need getHasRoleId() in login context, deprecated getuserid of getsgid.	
  private void setHasRoleId(DomHasRole dom) {
    String uid = dom.getUserId().getIdString().substring(20);
    String sgid = dom.getSchoolGroupId().getIdString().substring(27);
    String id = "MYSQL;" + PersistenceClassType.PersistentHasRole.name() + uid + sgid;
    dom.setId(new PersistenceId(id));    
  }

  private boolean isSingleSchool() {
		Boolean singleSchool = currentUser.getSingleSchool();
		return Boolean.TRUE.equals(singleSchool);
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
     * @param presenter 
     * @return
     * @throws Dwo2Exception
     */
    public Promise<DwoGlobalVarsState> initUser(String usercode, String password, Supplier<Promise<Boolean>> supplier) throws Dwo2Exception {
        login_step0();
        LOG.log(Level.INFO, "state=LoggingIn. Calling accountManager.login.");
        LoginPresenter presenter = (callback) -> {
			Promise<Boolean> promise = supplier.get();
			promise.onResolve(() -> {
				if (promise.getFailure()!= null) callback.onFailure(promise.getFailure());
				else callback.onSuccess(promise.getValue());
			});
		};
		//logging in
        return accountManager.login(usercode, password, presenter).then(this::login_step1).then(this::login_step2, this::login_fail);
        }

    public Promise<DwoGlobalVarsState> initUserWithToken(String token) throws Dwo2Exception {
      login_step0();
      LOG.log(Level.INFO, "state=LoggingIn. Calling accountManager.getUserFromAuthToken.");
//      return accountManager.getUserFromAuthToken(token).then(this::login_step1).then(this::login_step2, this::login_fail);
      return initUserWithToken0(token);
    }

    public Promise<DwoGlobalVarsState> initUserWithSaml(String user_id, String org_id,
                                                        String token) throws Dwo2Exception {
      login_step0();
      //Promise<DomUserFullwLoginContext> p1;
      //p1 = accountManager.updateAccountData(user_id, org_id, token);
      
	  token = Base64.btoa("3\f" + user_id + "\f" + org_id + "\f" + token); // Format 3 

	  return initUserWithToken0(token);
    }

    private Promise<DwoGlobalVarsState> initUserWithToken0(String token) {
      Promise<DomUserFullwLoginContext> p1;
      p1 = oauthManager.authorization_token(token)
      	.then( 
      			p -> {
      	    	  GwtRestVars.getInstance().setBearerToken(p.getValue().getAccess_token());
      	    	  GwtRestVars.getInstance().setRefreshToken(p.getValue().getRefresh_token());
      			  return accountManager.getLoginContext();
      			}
      	).then(
      			q -> { 
      				DomContext context = createContext(q.getValue());
      			return accountManager.getAccountData(context).map( 
      					data -> { 
      				DomUserFullwLoginContext all = new DomUserFullwLoginContext();
      				all.setDomLoginContext(q.getValue());
      				all.setDomUserFull(data);
      				return all;
      			});
      		}
      	);
        Promise<DomSchoolsRolesAndClassesV2> p2 = p1.then(this::login_step1a);
        Promise<DwoGlobalVarsState> p3 = p2.then(this::login_step2, this::login_fail);
        return p3;
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
        DwoGlobalVars.server = server;
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
    public void setCurrentUser(DomUserFull aCurUser, String realm) {
        this.currentUser = aCurUser;
        this.role = null;
        if (!RestAuthenticator.instance.getAuthorization().startsWith("Bearer")) 
        	GwtRestVars.getInstance().setCurrentUser(aCurUser, realm);

    }

    /**
     */
    public void clearCurrentUser() {
        currentUser = null;
        role = null;
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

    public String getRealm() {
      return currentLoginContext.getRealm();
    }

    public void setCurrentUser(DomUserFull user) {
      currentUser = user;
    }
    
    public Promise<Dwo2Exception> logout() {
      DomContext context = new DomContext();
      context.setDomHasRole(getActiveSchoolRoleAndClass().getHasRole());
      context.setRealm(getRealm());
      return accountManager.logout(context, getCurrentLoginContext());
    }

    public void setTest(boolean b) {
      this.test = b;  
    }
    public boolean isTest() {
      return test;
    }

    /**
     * @return the saml
     */
    public boolean isSaml() {
      return saml;
    }

    /**
     * @param saml the saml to set
     */
    public void setSaml(boolean saml) {
      this.saml = saml;
    }

    private RoleType role = null;
	public RoleType getRole() {
		if (role == null) 
			role = RoleType.valueOf(getActiveSchoolRoleAndClass().getRole().getRoleName());
		return role;
	}

	private boolean inf, remedial; // profiel inf = 111
	public boolean isInf() {
		return inf;
	}
	public void setInf(boolean i) {
		inf = i;
	}

	public boolean isRemedial() {
		return remedial;
	}
	public void setRemedial(boolean r) {
		remedial = r;
	}

	public boolean isModulesOnly() {
		return modulesOnly;
	}

	public void setModulesOnly(boolean modulesOnly) {
		this.modulesOnly = modulesOnly;
	}
}
