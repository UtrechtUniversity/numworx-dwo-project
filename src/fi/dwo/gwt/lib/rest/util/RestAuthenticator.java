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
	private String authorization;
	private boolean haspassword;
	
	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		if(haspassword)
		{
			builder.setHeader("Authorization", authorization);
		}
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
    if (haspassword)
    	authorization = "Basic " + Base64.btoa(username + realm + ":" + password);
  }

  public void setCredentials(String aUsername, String aPassword, String realm){
        username = aUsername;
        password = aPassword;
    	haspassword = username != null && password != null;
        setRealm(realm);
    }
    
  public void setBearer(String bearer) {
	  if (bearer != null)
	  {
		  authorization = "Bearer " + bearer;
		  haspassword = true;
	  }
	  else // reset to basic/none
		  setCredentials(username, password, realm);
  }
  
    private RestAuthenticator() {
    	// install in restygwt
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
    }
    
    public final static RestAuthenticator instance = new RestAuthenticator();
}