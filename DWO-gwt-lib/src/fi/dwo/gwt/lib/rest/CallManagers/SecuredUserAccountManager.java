package fi.dwo.gwt.lib.rest.CallManagers;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserAccountRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestLoginCheck;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.PathId;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.entities.RestAuthToken;
import nl.uu.fi.dwo.rest.entities.RestContext;

import org.osgi.util.promise.Promise;

public class SecuredUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountManager.class.getName());

    private SecuredUserAccountRestCaller service;

    public SecuredUserAccountManager() {
        String url = GwtRestVars.instance().getServer();
        init(url);

    }

    public SecuredUserAccountManager(String url) {
//        try {
//            dgv= new GwtRestVars();
//        } catch (Dwo2Exception ex) {
//            LOG.log(Level.SEVERE, null, ex);
//        }
        init(url);
    }

    private void init(String url) {
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        service = (SecuredUserAccountRestCaller) GWT.create(SecuredUserAccountRestCaller.class);
        LOG.log(Level.INFO, "" + service);
    }

    /**
     * ******************************************************************************
     * Extra login functions, For Resty
     *
     *******************************************************************************
     */
//    /**
//     * Checks if a username/password combination is valid. This function is a
//     * security risk.
//     *
//     * @param username
//     * @param password
//     * @return
//     */
//    private Promise<Boolean> loginCheck(String username, String password) {
//        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
//        this.loginCheck(username, password, defer);
//        return defer.getPromise();
//    }

    /**
     * Checks if a username/password combination is valid. This function is a
     * security risk.
     *
     * @param username
     * @param password
     * @param callback
     */
    @Deprecated
    private void loginCheck(final String username, final String password, final MethodCallback<Boolean> callback) {
        DomLoginCheck domLoginCheck = new DomLoginCheck();
        domLoginCheck.setUsername(username);
        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setDomLoginCheck(domLoginCheck);
        GwtRestVars.instance().setCurrentUser(null,null);
        service.loginCheck(restLoginCheck, callback);

    }

    public Promise<DomUserFullwLoginContext> login(String name, String password) {
    	return login(name, password, null);
    }

    public Promise<DomUserFullwLoginContext> login(String name, String password, LoginPresenter presenter) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.loginUser(name, password, defer, presenter);
        return defer.getPromise();
    }

    private void loginUser(final String name, String password, final PromiseCallback<DomUserFullwLoginContext> callback,
            LoginPresenter presenter) {
        final String pwmd5 = MD5.md5(password);
        loginUserMD5(name, pwmd5, callback, presenter);

    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.loginUserMD5(name, password, defer, null);
        return defer.getPromise();
    }

    private void loginUserMD5(final String name, final String pwmd5,
            final PromiseCallback<DomUserFullwLoginContext> callback, final LoginPresenter presenter) {
        loginCheck(name, pwmd5, new MethodCallback<Boolean>() {

            @Override
            public void onSuccess(Method m, Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                   GwtRestVars.instance().setCredentials(name, pwmd5,null);
                   if (presenter != null) {
                        getLoginContext(new MethodCallback<DomLoginContext>() {

                            @Override
                            public void onFailure(Method m, Throwable caught) {
                                callback.onFailure(caught);
                            }

                            @Override
                            public void onSuccess(Method m,DomLoginContext loginContext) {
                                if (loginContext.getSecretKey() != null) {
                                    presenter.otherlogin(new AsyncCallback<Boolean>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            callback.onFailure(caught);
                                        }

                                        @Override
                                        public void onSuccess(Boolean result) {
                                            if (result.booleanValue()) {
                                                getDomUserFullwLoginContext(name, callback);
                                            } else {
                                                callback.onFailure(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationCancelled,"Cancelled"));
                                            }
                                        }
                                    });
                                } else {
                                    getDomUserFullwLoginContext(name, callback);
                                }

                            }
                        });
                    } else {
                        getDomUserFullwLoginContext(name, callback);
                    }

                } else {
                    callback.onFailure(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, "LoginException"));
                }
            }

            @Override
            public void onFailure(Method m, Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * ******************************************************************************
     * Interface login stuff
     *
     *******************************************************************************
     */
    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> loginUser(String name) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.loginUser(name, defer);
        return defer.getPromise();
    }

    /**
     *
     * @param username
     * @param callBack
     */
    private void loginUser(String username, MethodCallback<DomUserFullwLoginContext> callBack) {
        service.loginUserWithPOST(username, callBack);
    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
//    @Deprecated
//    public Promise<DomUserFull> updateAccountData(DomUserFull updateUser) {
//        PromiseCallback<DomUserFull> defer = new PromiseCallback<DomUserFull>();
//        this.updateAccountData(updateUser, defer);
//        return defer.getPromise();
//    }
    public Promise<DomUserFull> updateAccountData(DomContext context, DomUserFull updateUser) {
        PromiseCallback<DomUserFull> defer = new PromiseCallback<DomUserFull>();
        this.updateAccountData(context, updateUser, defer);
        return defer.getPromise();
    }

//    /**
//     *
//     * @param updateUser
//     * @param callBack
//     * @deprecated use with context
//     */
//    public void updateAccountData(DomUserFull updateUser, AsyncCallback<DomUserFull> callBack) {
//    	updateAccountData(new DomContext(), updateUser, new Callback<DomUserFull>(callBack));
//    }
    private void updateAccountData(DomContext context, DomUserFull updateUser, MethodCallback<DomUserFull> callBack) {   
    	RestUserFull user = new RestUserFull();
        user.setRestContext(context);
        user.setDomUserFull(updateUser);
        F( service::updateAccountData, PathId.getId(context), user, (callBack));
    }


    public Promise<DomUserFull> getAccountData(DomContext context) {
    	RestContext rest = new RestContext(); 
    	rest.setRestContext(context);
    	return F( service::getAccount, rest);
    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> updateAccountData(String userid, String org, String token) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.samlLogin(userid, org, token, defer);
        return defer.getPromise();
    }

    private void samlLogin(String userid, String org, String token, final AsyncCallback<DomUserFullwLoginContext> userCallback) {
        DomSamlUser domSamlUser = new DomSamlUser();
        domSamlUser.setSamlUserId(userid);
        domSamlUser.setSamlOrgId(org);
        domSamlUser.setAuthToken(token);
        GwtRestVars.instance().setCurrentUser(null,null);
        RestSamlUser samlRestUser = new RestSamlUser();
        samlRestUser.setDomSamlUser(domSamlUser);
        MethodCallback<DomUserFullwLoginContext> restcallback = new MethodCallback<DomUserFullwLoginContext>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                userCallback.onFailure(exception);
            }

            @Override
            public void onSuccess(Method method, DomUserFullwLoginContext response) {
                GwtRestVars.instance().setCurrentUser(response.getDomUserFull(),response.getDomLoginContext().getRealm());
                userCallback.onSuccess(response);
            }
        };
        service.getSamlUser(samlRestUser, restcallback);
    }

    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.getUserFromAuthToken(authToken, defer);
        return defer.getPromise();
    }

    private void getUserFromAuthToken(String authToken, final PromiseCallback<DomUserFullwLoginContext> userCallback) {
        RestAuthToken restToken = new RestAuthToken();
        restToken.setAuthToken(authToken);
        restToken.setRestContext(new DomContext());
        MethodCallback<DomUserFullwLoginContext> restcallback = new MethodCallback<DomUserFullwLoginContext>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                userCallback.onFailure(exception);
            }

            @Override
            public void onSuccess(Method method, DomUserFullwLoginContext response) {
                GwtRestVars.instance().setCurrentUser(response.getDomUserFull(), response.getDomLoginContext().getRealm());
                userCallback.onSuccess(response);
            }
        };
        service.getUserFromAuthToken(restToken, restcallback);

    }
 
    public Promise<Dwo2Exception> logout(DomContext context, DomLoginContext loginContext) {
        PromiseCallback<Dwo2Exception> defer = new PromiseCallback<Dwo2Exception>();
        this.logout(context, loginContext, defer);
        return defer.getPromise();
    }
 
    private void logout(DomContext context, DomLoginContext loginContext, MethodCallback<Dwo2Exception> callback) {
        RestLoginContext restcontext = new RestLoginContext();
        restcontext.setDomLoginContext(loginContext);
        restcontext.setRestContext(context);
        F(service::logout,PathId.getId(context), restcontext, callback);
    }

    public Promise<DomLoginContext> getLoginContext() {
        PromiseCallback<DomLoginContext> defer = new PromiseCallback<DomLoginContext>();
        this.getLoginContext(defer);
        return defer.getPromise();
    }

    private void getLoginContext(MethodCallback<DomLoginContext> callback) {
        F( (id, arg, c ) -> service.getLoginContext(c), null, null, callback);
    }

   public Promise<DomUserFullwLoginContext> getDomUserFullwLoginContext(String name) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.getDomUserFullwLoginContext(name,defer);
        return defer.getPromise();
    }
    
    private void getDomUserFullwLoginContext(final String name,
            final PromiseCallback<DomUserFullwLoginContext> callback) {
        loginUser(name, new MethodCallback<DomUserFullwLoginContext>() {

            @Override
            public void onFailure(Method m, Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Method m, DomUserFullwLoginContext result) {
            	String realm = result.getDomLoginContext().getRealm();
            	if (realm == null) realm = "";
                if (result.getDomUserFull() != null && name.equalsIgnoreCase(result.getDomUserFull().getUserName()+realm)) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(new RuntimeException("Please restart browser")); // FIXME showstopper?
                }
            }
        });
    }
    
    public Promise<JSONValue> verifyTOTP(DomContext context) {
    	return F( (id, arg, c) -> service.verifyTOTP(id, c), PathId.getId(context), null);
    }
  
    public Promise<String> getBearerToken(DomContext context) {
        return F( (id, arg, c) -> service.getBearerToken(id, c), PathId.getId(context), null);
    }

    public Promise<String> getBearerToken(DomContext context, DomSchoolClass schoolclass) {
    	RestSchoolClass rest = new RestSchoolClass();
    	rest.setDomSchoolClass(schoolclass);
    	rest.setRestContext(context);
        return F( (id, arg, c) -> service.getBearerToken(id, arg, c), PathId.getId(context), rest);
    }
    
    public Promise<Boolean> linkSaml(DomContext context, DomSamlUser samluser) {
    	RestSamlUser rest = new RestSamlUser();
    	rest.setDomSamlUser(samluser);
    	rest.setRestContext(context);
		return F( (id, arg, c) -> service.linkSaml(id, arg, c), PathId.getId(context), rest);
    }
            
    public Promise<Boolean> removeCurrentUser(DomContext context) {
      return F( (id, arg, c ) -> service.removeCurrentUser(id, c), PathId.getId(context), null);
    }
}
