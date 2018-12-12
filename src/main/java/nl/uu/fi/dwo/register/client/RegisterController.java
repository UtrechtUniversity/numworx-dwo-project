package nl.uu.fi.dwo.register.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.PublicUserManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import org.osgi.util.function.*;
import org.osgi.util.promise.*;

public class RegisterController {

	private Command next;
	
	private PublicUserManager pum = new PublicUserManager();

	private DomSamlUser samlUser;
	
	
	public Command getNext() {
		return next;
	}


	public void setNext(Command next) {
		this.next = next;
	}

	public void register(DomNewUser domNewUser) {
		GwtRestVars.instance().setCurrentUser(null);
		Promise<Boolean> p = pum.RegisterNewUser(domNewUser);

		Failure failure = (promise) -> 
			 {  Throwable caught = promise.getFailure();
				String message;
				if(caught instanceof Dwo2Exception)
				{	 
					 message = caught.getLocalizedMessage();
				} else
					message = caught.toString();
				Window.alert(message);
			};
		Success<Boolean,Void> succes = (promise) ->
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
		if (samlUser != null) {
			SecuredUserAccountManager manager = new SecuredUserAccountManager();
			Success<Boolean, Boolean> link = (promise) -> {
				GwtRestVars.instance().setCredentials(domNewUser.getUsername(), domNewUser.getPassword());			
				return manager.linkSaml(samlUser);
				
			};
			Function<Promise<?>, Promise<? extends Boolean>> recovery = (promise) -> {
				GwtRestVars.instance().setCredentials(domNewUser.getUsername(), domNewUser.getPassword());			
				return manager.getAccountData().map(v -> Boolean.TRUE);
			};
			p = p.recoverWith(recovery).then(link);
		}
		p.then(succes,failure);
	}


	void setSamlUser(DomSamlUser samlUser) {
		this.samlUser = samlUser;
	}

}
