package nl.uu.fi.dwo.register.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.PublicUserManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionMapper;
import fi.dwo.rest.dom.entities.DomNewUser;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.locale.Dwo2ExceptionsForGWT;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class RegisterController {

	private Command next;
	
	private PublicUserManager pum = new PublicUserManager();
	
	
	public Command getNext() {
		return next;
	}


	public void setNext(Command next) {
		this.next = next;
	}

	public void register(DomNewUser domNewUser) {
		
		pum.RegisterNewUser(domNewUser, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				String message;
				if(caught instanceof Dwo2Exception)
				{	 Dwo2ExceptionCode code = ((Dwo2Exception) caught).getDwo2Code();
					 message = Dwo2ExceptionTranslator.getLocalizedCodeExplanation(null, code);
				} else
					message = caught.getMessage();
				Window.alert(message);
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("De gebruiker is succesvol aangemeld.");

					if(next != null)
						next.execute();
				}
				
			}
		});
	}

}
