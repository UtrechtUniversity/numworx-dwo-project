package nl.uu.fi.dwo.register.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.PublicUserManager;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

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
					message = caught.toString();
				Window.alert(message);
			}

			@Override
			public void onSuccess(Boolean result) {
				DwoLocalesForGWT rb = DwoLocalesForGWT.instance;

				if (result) {
					Window.alert(rb.GUI_UserRegistrationSucceeded());

					if(next != null)
						next.execute();
				} else {
					Window.alert(rb.GUI_UserRegistrationFailed());
					
				}
				
			}
		});
	}

}
