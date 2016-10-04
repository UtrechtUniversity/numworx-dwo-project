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
import fi.dwo.rest.dom.entities.DomLoginContext;
import fi.dwo.rest.dom.entities.DomSamlUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import fi.dwo.rest.entities.RestAuthToken;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestLoginContext;
import fi.dwo.rest.entities.RestSamlUser;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;

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

    public void loginUser(final String name, String password, final AsyncCallback<DomUserFullwLoginContext> callback, 
    		LoginPresenter presenter) {
        final String pwmd5 = MD5.md5(password);
        GWT.log(pwmd5);
        loginUserMD5(name, pwmd5, callback, presenter);

    }

	public void loginUserMD5(final String name, final String pwmd5,
			final AsyncCallback<DomUserFullwLoginContext> callback, final LoginPresenter presenter) {
		loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                	if(presenter != null)
                	{	getLoginContext(new AsyncCallback<DomLoginContext>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}
	
							@Override
							public void onSuccess(DomLoginContext loginContext) {
								if (loginContext.getLastLoginTimeStamp() != null 
									&& presenter != null)
								{	
			                		presenter.otherlogin(new AsyncCallback<Boolean>() {
	
										@Override
										public void onFailure(Throwable caught) {
											callback.onFailure(caught);
										}
	
										@Override
										public void onSuccess(Boolean result) {
											if(result.booleanValue())
												getDomUserFullwLoginContext(name, callback);
											else
												callback.onFailure(new RuntimeException("Cancelled"));
										}
									});
			                	} else {
			                		getDomUserFullwLoginContext(name, callback);
			                	}
	
							}
						});
                	} else 
                		getDomUserFullwLoginContext(name, callback);
                	
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
     * @param username
     * @param callBack 
     */
    public void loginUser(String username, AsyncCallback<DomUserFullwLoginContext> callBack) {
        service.loginUser(username, new Callback<DomUserFullwLoginContext>(callBack));
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

	public void samlLogin(String userid, String org, String token, final AsyncCallback<DomUserFullwLoginContext> userCallback)
	{
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
	public void getUserFromAuthToken(String authToken,final AsyncCallback<DomUserFullwLoginContext> userCallback) {
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
	
	
	public void logout(DomLoginContext loginContext, AsyncCallback<Dwo2Exception> callback) {
		RestLoginContext restcontext = new RestLoginContext();
		restcontext.setDomLoginContext(loginContext);
		restcontext.setRestContext(new DomContext());
		service.logout(restcontext, new Callback<Dwo2Exception>(callback));
	}
	
	public void getLoginContext(AsyncCallback<DomLoginContext> callback) {
		service.getLoginContext(new Callback<DomLoginContext>(callback));
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
		    	if(result.getDomUserFull() != null && name.equalsIgnoreCase(result.getDomUserFull().getUserName()))
		    		callback.onSuccess(result);
		    	else
		    		callback.onFailure(new RuntimeException("Please restart browser")); // FIXME showstopper?
		    }
		});
	}
}
