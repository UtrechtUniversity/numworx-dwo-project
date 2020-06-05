package nl.uu.fi.dwo.account.client;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author plas0006
 */
@Deprecated
public abstract class RPCHandlerV2 extends RPCHandlerV1 {

    /**
     *
     */
    protected final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();

    /**
     *
     */
    protected final SecuredUserSchoolLoginManagerV2 schoolManager = new SecuredUserSchoolLoginManagerV2();

	protected DomContext context = new DomContext();
	protected static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";

    /**
     *
     * @param server
     * @param profile
     */
    RPCHandlerV2(int profile) {
		super(profile);
	}
	
    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> login(String name, String password) {
		return accountManager.login(name,  password, null);
	}

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
		return accountManager.loginMD5(name,  password);
	}
	
    /**
     *
     * @return
     */
    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		return schoolManager.getSchoolLogins();
	}
		
 
   /**
     *
     * @param name
     * @param org
     * @param callback
     */
    private void samlLoginHelper(String name, String org,
                                   PromiseCallback<DomUserFullwLoginContext> callback) {
		String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
		callback.resolveWith(accountManager.updateAccountData(name, org, authToken));
	}

	
    /**
     *
     * @param authToken
     * @return
     */
    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
		return accountManager.getUserFromAuthToken(authToken);
	}
		
    /**
     *
     */
    @Override
    public Promise<Void> logout() {
      Promise<Void> resolved = Promises.<Void>resolved(null);
      if (DwoGlobalVars.instance().getCurrentUser() != null) {
        resolved = accountManager.logout(context, DwoGlobalVars.instance().getCurrentLoginContext())
            .then(new Success<Dwo2Exception, Void>() {
  
              @Override
              public Promise<Void> call(Promise<Dwo2Exception> resolved) throws Exception {
                Window.alert(String.valueOf(resolved.getValue()));
                return null;
              }
            }).recoverWith(new Function<Promise<?>, Promise<? extends Void>>() {
  
              @Override
              public Promise<? extends Void> apply(Promise<?> t) {
                DwoGlobalVars.instance().clearCurrentUser();
                return Promises.<Void>resolved(null);
              }
            });
    }
    return resolved;

  }
	
    /**
     *
     * @param name
     * @param org
     * @return
     */
    public Promise<DomUserFullwLoginContext> samlLogin(String name, String org) {
		PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<>();
		samlLoginHelper(name, org, defer);
		return defer.getPromise();
	}

	
}
