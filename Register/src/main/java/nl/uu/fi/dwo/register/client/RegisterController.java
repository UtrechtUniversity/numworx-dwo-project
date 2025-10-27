package nl.uu.fi.dwo.register.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.PublicUserManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Base64;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.function.*;
import org.osgi.util.promise.*;

public class RegisterController {

	private Command next, cancel;
	
	private PublicUserManager pum = new PublicUserManager();

	private DomSamlUser samlUser;
	private String schoolClass;

	/**
	 * @param schoolClass the schoolClass to set
	 */
	public void setSchoolClass(String schoolClass) {
		this.schoolClass = schoolClass;
	}

	private final Success<Boolean,Void> succes = (promise) ->
		{   Boolean result = promise.getValue();
			DwoLocalesForGWT rb = DwoLocalesForGWT.instance;

			if (result) {
				Window.alert(rb.GUI_UserRegistrationSucceeded());
				if(next != null)
					next.execute();
			} else {
				Window.alert(rb.GUI_UserRegistrationFailed());	
			}
			return null;
		};

	private final Failure failure = (promise) -> 
		 {  Throwable caught = promise.getFailure();
			String message;
			if(caught instanceof Dwo2Exception)
			{	 
				 message = caught.getLocalizedMessage();
			} else
				message = caught.toString();
			Window.alert(message);
		};
	
	
	public Command getNext() {
		return next;
	}


	public void setNext(Command next) {
		this.next = next;
	}

	public void register(DomNewUser domNewUser) {
		SecuredUserAccountManager manager = new SecuredUserAccountManager();
		GwtRestVars.instance().setCurrentUser(null,null);
		Promise<Boolean> p;
		if (schoolClass != null && domNewUser.getRole() == RoleType.STUDENT) {
			DomNewStudent student = new DomNewStudent(domNewUser, schoolClass);
			student.setSamlUser(samlUser);
			//samlUser = null;
			p = pum.RegisterNewStudent(student);
		} else {
			DomNewStudent student = new DomNewStudent(domNewUser, null); // not really a student.
			student.setSamlUser(samlUser);
			//samlUser = null;
			p = pum.RegisterNewStudent(student);
			///p = pum.RegisterNewUser(domNewUser);
		}
		p = p.then( x -> { clearSAML(); return x; });

//		if (samlUser != null) {
//			Success<Boolean, Boolean> link = (promise) -> {
//				GwtRestVars.instance().setCredentials(domNewUser.getUsername(), domNewUser.getPassword(), null);			
//				DomContext context = new DomContext();
//				return manager.linkSaml(context, samlUser);
//				
//			};
//			Function<Promise<?>, Promise<? extends Boolean>> recovery = (promise) -> {
//				GwtRestVars.instance().setCredentials(domNewUser.getUsername(), domNewUser.getPassword(), null);
//				DomContext context = new DomContext();
//				return manager.getAccountData(context).map(v -> Boolean.TRUE);
//			};
//			p = p.recoverWith(recovery).then(link);
//		}

		if (putRequest != null)
		  p = p.map(x -> domNewUser).then(this::sendPutRequest);
		p.then(succes,failure);
	}


	protected void clearSAML() {
		samlUser = null;
		parent.account.setVisible(false);
		parent.account.setValue(false);
	}


	void link(DomNewUser u) {
		SecuredUserAccountManager manager = new SecuredUserAccountManager();
		GwtRestVars.instance().setCredentials(u.getUsername(), u.getPassword(), null);
		
		final Promise<Boolean> p;
		if (samlUser == null) {
			p = Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Rest_Registration_School_authentication_failed, "null user"));
		} else 
		{
			p = manager.linkSaml(new DomContext(), samlUser).then(x -> { clearSAML(); return x;});
		}
		p.then(succes,failure);
	}
	
	
	void setSamlUser(DomSamlUser samlUser) {
		this.samlUser = samlUser;
	}


	public void setCancel(Command object) {
		this.cancel = object;
	}

	public Command getCancel() {
		if (cancel == null) return next;
		return cancel;
	}
	
	private String putRequest;

  /**
   * @param putRequest the putRequest to set
   */
  public void setPutRequest(String putRequest) {
    this.putRequest = putRequest;
  }

    private static String serverURL = "registerform";
	
	Promise<Boolean> sendPutRequest(Promise<DomNewUser> user) {
	  if (putRequest == null) return Promises.resolved(Boolean.TRUE);
	  Deferred<Boolean> defer = new PromiseCallback<>();
      RequestBuilder requestBuilder = new RequestBuilder(
        RequestBuilder.PUT, serverURL);
	  requestBuilder.setUser(user.getValue().getUsername());
	  requestBuilder.setPassword(user.getValue().getPassword());
	  requestBuilder.setIncludeCredentials(true);
	  requestBuilder.setHeader("Authorization", "Basic " + Base64.btoa(requestBuilder.getUser() + ":" + requestBuilder.getPassword()));
	  RequestCallback callback = new RequestCallback() {
        
        @Override
        public void onResponseReceived(Request request, Response response) {
          if (response.getStatusCode() == 204) 
            defer.resolve(Boolean.TRUE);
          else {
            defer.fail(new RequestException(response.getStatusCode() + " " + response.getStatusText()));
          }
        }
        
        @Override
        public void onError(Request request, Throwable exception) {
          defer.fail(exception);
        }
      };
    try {
      requestBuilder.sendRequest(putRequest, callback);
    } catch (RequestException e) {
        defer.fail(e);
    }
	  
	  return defer.getPromise();
	}
	
	RegisterPanel parent;

	RegisterController(RegisterPanel parent) {
		this.parent = parent;
	}
	
}
