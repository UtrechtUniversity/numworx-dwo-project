package fi.dwo.gwt.lib.rest.CallManagers;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.PublicUserRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.entities.RestNewUser;

public class PublicUserManager {
	
	PublicUserRestCaller caller = GWT.create(PublicUserRestCaller.class);

	public void RegisterNewUser(DomNewUser domNewUser,
			AsyncCallback<Boolean> asyncCallback) {
		RestNewUser user = new RestNewUser();
		user.setDomNewUser(domNewUser);
		user.setRestContext(new DomContext());
		instance.setCurrentUser(null);
		caller.submitNewUser(user, new Callback<Boolean>(asyncCallback));
		
	}
	
	GwtRestVars instance;
	
	public PublicUserManager() {	
		instance = GwtRestVars.instance();
	}
}
