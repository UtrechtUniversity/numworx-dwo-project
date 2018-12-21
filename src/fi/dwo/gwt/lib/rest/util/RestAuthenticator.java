package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.http.client.RequestBuilder;

/**
 * Singleton pattern.
 * @author wim
 *
 */
public class RestAuthenticator implements DispatcherFilter {
	private String username;
	private String password;
	private String realm = "";
	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		boolean haspassword = username != null && password != null;
//		if(haspassword)builder.setPassword(password);
//		if(haspassword)builder.setUser(username);
		if(haspassword)
		{
			builder.setHeader("Authorization", "Basic " + Base64.btoa(username + realm + ":" + password));
		}
//		builder.setIncludeCredentials(haspassword);
		return true;
	}
    /**
   * @return the realm
   */
  public String getRealm() {
    return realm;
  }
  /**
   * @param realm the realm to set
   */
  public void setRealm(String realm) {
    if(realm == null) realm = "";
    this.realm = realm;
  }
    public void setCredentials(String aUsername, String aPassword, String realm){
        username = aUsername;
        password = aPassword;
        setRealm(realm);
    }
    
    private RestAuthenticator() {
    	// install in restygwt
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
    }
    
    public final static RestAuthenticator instance = new RestAuthenticator();
}