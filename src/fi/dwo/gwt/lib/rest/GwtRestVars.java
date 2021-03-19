package fi.dwo.gwt.lib.rest;

import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionMapper;
import fi.dwo.gwt.lib.rest.util.HeadersFilter;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.PathId;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Stores global variables The class is state is initialized by calls in
 * different boot phases. Whenever a global state is changed it should be called
 * and have the state updated.
 *
 * @author Gert van der Plas
 */
public class GwtRestVars {

    private static final Logger LOG = Logger.getLogger(GwtRestVars.class.getName());
    //Runtime Variabes
    DomUserFull currentUser;
    private RestAuthenticator authenticator = RestAuthenticator.instance;
    private Map<String,String> customHeaders = Collections.emptyMap();


    private static volatile GwtRestVars instance;

    public static GwtRestVars getInstance() {
        return instance;
    }

    public static void setInstance(GwtRestVars instance) {
        GwtRestVars.instance = instance;
    }

    static {
        try {
            instance = new GwtRestVars();

        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            Window.alert("System error: app improperly configured.");
        }
    }

//    /**
//     * @return the dwoLocale
//     */
//    public static DwoLocale getDwoLocale() {
//        return dwoLocale;
//    }
//
//    /**
//     * @param aDwoLocale the dwoLocale to set
//     */
//    public static void setDwoLocale(DwoLocale aDwoLocale) {
//        dwoLocale = aDwoLocale;
//    }


    public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

	//properties
    private static String server;

    /**
     * @return the instance
     */
    public static GwtRestVars instance() {
        if (instance == null) {
            try {
                instance = new GwtRestVars();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        }
        return instance;
    }

    public GwtRestVars() throws Dwo2Exception {
        initProperties();
        initObjects();
        initVars();
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
        Defaults.setExceptionMapper(new Dwo2ExceptionMapper());
        setAuthenticator(RestAuthenticator.instance);
        customHeaders = HeadersFilter.instance.getHeaders();
        customHeaders.clear();
        LOG.log(Level.INFO, "Done initObjects():");
    }

    private void initVars() throws Dwo2Exception {
        //TODO fill DwoSystemParameters and more into the instance.
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
        Defaults.setServiceRoot(this.getServer());
    }

    /**
     * @return the currentUser
     */
    public DomUserFull getCurrentUser() {
        return currentUser;
    }

    @Deprecated
    public void setCurrentUser(DomUserFull u) {
      setCurrentUser(u, null);
    }
    
    /**
     * @param aCurUser
     */
    public void setCurrentUser(DomUserFull aCurUser, String realm) {
        currentUser = aCurUser;
        if(currentUser != null)
        	setCredentials(currentUser.getUserName(), currentUser.getPassword(), realm);        
        else
        	setCredentials(null, null,null);
    }

    public void setCredentials(String username, String password, String realm) {
    	getAuthenticator().setCredentials(username, password, realm);
    }
    
    /**
     * @return the authenticator
     */
    RestAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(RestAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

	public void setBearerToken(String bearer) {
		authenticator.setBearer(bearer);
		
	}

	@FunctionalInterface
	public interface TriConsumer<P, Q> {
		void accept(String id, P arg, Q callback);
	}
	

	Promise<DomToken> tokenRequest;
	OAuthManager oauth = new OAuthManager();
	String refresh_token;
	/**
	 * intercept resty calls.
	 * @param f
	 * @param arg
	 * @param callback
	 */

	public class Retry<T> implements Function<Promise<T>, Promise<T>> {

		@Override
		public Promise<T> apply(Promise<T> t) {
			if (t instanceof Dwo2Exception) {
				if (Dwo2ExceptionCode.User_AuthenticationError == ((Dwo2Exception) t).getDwo2Code()) {
					if (tokenRequest == null) {
						tokenRequest = oauth.refresh_token(refresh_token);
					} else return t;
					return tokenRequest.then(
							p -> {
								DomToken dt = p.getValue();							
								setBearerToken(dt.getAccess_token());
								setRefreshToken(dt.getRefresh_token());
								return supplier.get();
							}).fallbackTo(t);
				}
			}
			return t;
		}
		
		private final Supplier<Promise<T>> supplier;

		public Retry(Supplier<Promise<T>> supplier) {
			this.supplier = supplier;
		}
	}
	
	class RetryCallback<T> implements MethodCallback<T> {

		MethodCallback<T> delegate;
		Consumer<MethodCallback<T>> f;
		
		@Override
		public void onFailure(Method method, Throwable t) {
			if (t instanceof Dwo2Exception) {
				Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
				if (code == Dwo2ExceptionCode.User_AuthenticationError) {
					if (tokenRequest == null) {
						if(refresh_token != null)
							tokenRequest = oauth.refresh_token(refresh_token);
						else
							tokenRequest = Promises.failed(t);
					}
					tokenRequest.then(
						(Promise<DomToken>	p) -> {
							DomToken dt = p.getValue();							
							setBearerToken(dt.getAccess_token());
							setRefreshToken(dt.getRefresh_token());
							f.accept(delegate);
						 return null;
						},
						p -> delegate.onFailure(method, p.getFailure())
					);
					return;
				}
				delegate.onFailure(method, t);
			}			
		}

		@Override
		public void onSuccess(Method method, T response) {
			delegate.onSuccess(method, response);		
		}

		RetryCallback(MethodCallback<T> delegate, Consumer<MethodCallback<T>> f) {
			this.delegate = delegate;
			this.f = f;
		}
		
	}

	public static <P,R> void  F(TriConsumer<P, MethodCallback<R>> f, String id, P arg, MethodCallback<R> callback) {
		GwtRestVars v = instance();		
		f.accept(id, arg, v.new RetryCallback<>(callback, c -> f.accept(id, arg, c)));
	}
	
//	public static <R> void F(BiConsumer<String, MethodCallback<R>> f, String id, MethodCallback<R> callback) {
//		f.accept(id, callback);
//	}
	
	public static <P, R> Promise<R> F(TriConsumer<P, MethodCallback<R>> f, String id, P arg) {
		PromiseCallback<R> defer = new PromiseCallback<R>();
		F(f, id, arg, defer);
		return defer.getPromise();
	}
	
	public static <P extends RestContext, R> Promise<R> F(TriConsumer<P, MethodCallback<R>> f, P arg) {
		return F(f, PathId.getId(arg.getRestContext()), arg);
	}

	public void setRefreshToken(String refresh_token) {
		this.refresh_token = refresh_token;
		tokenRequest = null;
	}
	
	public String getLogoutQuery() {
	  if (refresh_token != null && !"None".equals(authenticator.getAuthorization())) {
	    String access_token = authenticator.getAuthorization().substring(7);
	    return "refresh_token=" + refresh_token + "&access_token=" + access_token;
	  }
	  return null;
	}

  public String getLogoutURL() {
    return server + "oauth2/nekot";
  }
}
