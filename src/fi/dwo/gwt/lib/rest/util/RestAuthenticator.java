package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Singleton pattern.
 * @author wim
 *
 */
public class RestAuthenticator implements DispatcherFilter, HasValueChangeHandlers<String> {
		
	
	private String username;
	private String password;
	private String realm = "";
	private String authorization;
	private final EventBus bus = new SimpleEventBus();
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
	  	String old = getAuthorization();
        username = aUsername;
        password = aPassword;
    	haspassword = username != null && password != null;
        setRealm(realm);
        ValueChangeEvent.fireIfNotEqual(this, old, getAuthorization());
    }
    
  public void setBearer(String bearer) {
	  if (bearer != null)
	  {	  String old = getAuthorization();
		  authorization = "Bearer " + bearer;
		  haspassword = true;
	      ValueChangeEvent.fireIfNotEqual(this, old, getAuthorization());
	  }
	  else // reset to basic/none
		  setCredentials(username, password, realm);
  }
  
    private RestAuthenticator() {
    	// install in restygwt
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
    }
    
    public final static RestAuthenticator instance = new RestAuthenticator();
    

    public String getAuthorization() {
    	if (haspassword) return authorization;
    	else return "None";
    }

	@Override
	public void fireEvent(GwtEvent<?> event) {
		bus.fireEventFromSource(event,this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
	}
    
    
}