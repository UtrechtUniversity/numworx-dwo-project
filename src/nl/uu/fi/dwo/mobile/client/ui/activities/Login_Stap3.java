package nl.uu.fi.dwo.mobile.client.ui.activities;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.place.shared.Place;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

final class Login_Stap3 implements Success<Void, Void> {

	ClientFactory clientFactory;
	Place next;
	
	Login_Stap3(ClientFactory clientFactory, Place next) {
		super();
		this.clientFactory = clientFactory;
		this.next = next;
	}

	@Override
	public Promise<Void> call(Promise<Void> resolved) throws Exception {

		DomUserFull currentUser = DwoGlobalVars.instance().getCurrentUser();
		RoleType roleType = clientFactory.getRoleType();
		clientFactory.getHeaderView().setUserAndRole(currentUser, roleType);
		if(next == null)
		{
			DWOplayer.gotoCourses();
		}
		else
			clientFactory.getPlaceController().goTo(next);
		return null;

	}
}