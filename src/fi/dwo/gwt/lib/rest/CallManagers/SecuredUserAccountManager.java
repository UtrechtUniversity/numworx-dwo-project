package fi.dwo.gwt.lib.rest.CallManagers;


import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.SecuredUserAccountRestCaller;
import fi.dwo.rest.dom.entities.DomContext;

import fi.dwo.rest.dom.entities.DomLoginCheck;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestUserFull;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private void init(String url){
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        DefaultFilterawareDispatcher.singleton().addFilter(GwtRestVars.instance().getAuthenticator());
        service = (SecuredUserAccountRestCaller) GWT.create(SecuredUserAccountRestCaller.class);
        LOG.log(Level.INFO,""+service);
    }
    
/********************************************************************************
*   Extra login functions, For Resty
* 
********************************************************************************/
    /**
     * 
     * 
     * @param username
     * @param password
     * @param callback 
     */
    
    public void loginCheck(final String username, final String password, final AsyncCallback<Boolean> callback) {
        DomLoginCheck domLoginCheck = new DomLoginCheck();
        domLoginCheck.setUsername(username);
        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setDomLoginCheck(domLoginCheck);
        GwtRestVars.instance().getAuthenticator().setCredentials(null, null);
        service.loginCheck(restLoginCheck, new MethodCallback<Boolean>() {

            @Override
            public void onSuccess(Method method, Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    GwtRestVars.instance().getAuthenticator().setCredentials(username, password);
                }
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                callback.onFailure(exception);
            }
        });

    }

    public void loginUser(String name, String password, final AsyncCallback<DomUserFull> callback) {
        final String pwmd5 = MD5.md5(password);
        GWT.log(pwmd5);

        loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    login(new AsyncCallback<DomUserFull>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(DomUserFull result) {
                            callback.onSuccess(result);
                        }
                    });
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

/********************************************************************************
*   Interface login stuff
* 
********************************************************************************/

    /**
     * 
     * @param callBack 
     */
    public void login(AsyncCallback<DomUserFull> callBack) {
        service.login(new Callback<DomUserFull>(callBack));
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
     * @param callBack 
     */
    public void getAccountData(AsyncCallback<DomUserFull> callBack) {
        service.getAccountData(new Callback<DomUserFull>(callBack));
    }

}
