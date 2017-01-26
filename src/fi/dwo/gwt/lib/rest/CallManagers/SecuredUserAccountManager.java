package fi.dwo.gwt.lib.rest.CallManagers;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserAccountRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestLoginCheck;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.entities.RestAuthToken;
import org.osgi.util.promise.Promise;

public class SecuredUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountManager.class.getName());

    private SecuredUserAccountRestCaller service;
    private GwtRestVars dgv;

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
    /**
     * Checks if a username/password combination is valid. This function is a
     * security risk.
     *
     * @param username
     * @param password
     * @return
     */
    public Promise<Boolean> loginCheck(String username, String password) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.loginCheck(username, password, defer);
        return defer.getPromise();
    }

    /**
     * Checks if a username/password combination is valid. This function is a
     * security risk.
     *
     * @param username
     * @param password
     * @param callback
     */
    @Deprecated
    public void loginCheck(final String username, final String password, final AsyncCallback<Boolean> callback) {
        DomLoginCheck domLoginCheck = new DomLoginCheck();
        domLoginCheck.setUsername(username);
        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setDomLoginCheck(domLoginCheck);
        GwtRestVars.instance().setCurrentUser(null);
        service.loginCheck(restLoginCheck, new MethodCallback<Boolean>() {

            @Override
            public void onSuccess(Method method, Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    GwtRestVars.instance().setCredentials(username, password);
                }
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                callback.onFailure(exception);
            }
        });

    }

    public Promise<DomUserFullwLoginContext> login(String name, String password) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.loginUser(name, password, defer, null);
        return defer.getPromise();
    }

    public void loginUser(final String name, String password, final AsyncCallback<DomUserFullwLoginContext> callback,
            LoginPresenter presenter) {
        final String pwmd5 = MD5.md5(password);
        GWT.log(pwmd5);
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

    public void loginUserMD5(final String name, final String pwmd5,
            final AsyncCallback<DomUserFullwLoginContext> callback, final LoginPresenter presenter) {
        loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    if (presenter != null) {
                        getLoginContext(new AsyncCallback<DomLoginContext>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            @Override
                            public void onSuccess(DomLoginContext loginContext) {
                                if (loginContext.getLastLoginTimeStamp() != null
                                        && presenter != null) {
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
                                                callback.onFailure(new RuntimeException("Cancelled"));
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
                    callback.onFailure(new RuntimeException("LoginException"));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
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
    public void loginUser(String username, AsyncCallback<DomUserFullwLoginContext> callBack) {
        service.loginUserWithPOST(username, new Callback<DomUserFullwLoginContext>(callBack));
    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFull> updateAccountData(DomUserFull updateUser) {
        PromiseCallback<DomUserFull> defer = new PromiseCallback<DomUserFull>();
        this.updateAccountData(updateUser, defer);
        return defer.getPromise();
    }

    /**
     *
     * @param updateUser
     * @param callBack
     */
    public void updateAccountData(DomUserFull updateUser, AsyncCallback<DomUserFull> callBack) {
        RestUserFull user = new RestUserFull();
        user.setRestContext(new DomContext());
        user.setDomUserFull(updateUser);
        service.updateAccountData(user, new Callback<DomUserFull>(callBack));
    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFull> updateAccountData() {
        PromiseCallback<DomUserFull> defer = new PromiseCallback<DomUserFull>();
        this.getAccountData(defer);
        return defer.getPromise();
    }

    /**
     *
     * @param callBack
     */
    public void getAccountData(AsyncCallback<DomUserFull> callBack) {
        service.getAccountData(new Callback<DomUserFull>(callBack));
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

    public void samlLogin(String userid, String org, String token, final AsyncCallback<DomUserFullwLoginContext> userCallback) {
        DomSamlUser domSamlUser = new DomSamlUser();
        domSamlUser.setSamlUserId(userid);
        domSamlUser.setSamlOrgId(org);
        domSamlUser.setAuthToken(token);
        GwtRestVars.instance().setCurrentUser(null);
        RestSamlUser samlRestUser = new RestSamlUser();
        samlRestUser.setDomSamlUser(domSamlUser);
        MethodCallback<DomUserFullwLoginContext> restcallback = new MethodCallback<DomUserFullwLoginContext>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                userCallback.onFailure(exception);
            }

            @Override
            public void onSuccess(Method method, DomUserFullwLoginContext response) {
                GwtRestVars.instance().setCurrentUser(response.getDomUserFull());
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

    public void getUserFromAuthToken(String authToken, final AsyncCallback<DomUserFullwLoginContext> userCallback) {
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
                GwtRestVars.instance().setCurrentUser(response.getDomUserFull());
                userCallback.onSuccess(response);
            }
        };
        service.getUserFromAuthToken(restToken, restcallback);

    }

    public Promise<Dwo2Exception> logout(DomLoginContext loginContext) {
        PromiseCallback<Dwo2Exception> defer = new PromiseCallback<Dwo2Exception>();
        this.logout(loginContext, defer);
        return defer.getPromise();
    }

    public void logout(DomLoginContext loginContext, AsyncCallback<Dwo2Exception> callback) {
        RestLoginContext restcontext = new RestLoginContext();
        restcontext.setDomLoginContext(loginContext);
        restcontext.setRestContext(new DomContext());
        service.logout(restcontext, new Callback<Dwo2Exception>(callback));
    }

   public Promise<DomLoginContext> logout() {
        PromiseCallback<DomLoginContext> defer = new PromiseCallback<DomLoginContext>();
        this.getLoginContext(defer);
        return defer.getPromise();
    }

       public Promise<DomLoginContext> getLoginContext() {
        PromiseCallback<DomLoginContext> defer = new PromiseCallback<DomLoginContext>();
        this.getLoginContext(defer);
        return defer.getPromise();
    }

    public void getLoginContext(AsyncCallback<DomLoginContext> callback) {
        service.getLoginContext(new Callback<DomLoginContext>(callback));
    }

   public Promise<DomUserFullwLoginContext> getDomUserFullwLoginContext(String name) {
        PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<DomUserFullwLoginContext>();
        this.getDomUserFullwLoginContext(name,defer);
        return defer.getPromise();
    }
    
    private void getDomUserFullwLoginContext(final String name,
            final AsyncCallback<DomUserFullwLoginContext> callback) {
        loginUser(name, new AsyncCallback<DomUserFullwLoginContext>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DomUserFullwLoginContext result) {
                if (result.getDomUserFull() != null && name.equalsIgnoreCase(result.getDomUserFull().getUserName())) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(new RuntimeException("Please restart browser")); // FIXME showstopper?
                }
            }
        });
    }
}
