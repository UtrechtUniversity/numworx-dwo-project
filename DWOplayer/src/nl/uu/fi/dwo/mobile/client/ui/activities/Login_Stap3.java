package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.place.shared.Place;
import com.google.gwt.regexp.shared.RegExp;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.PageTracker;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

final class Login_Stap3 implements Success<Place, Void> {

	ClientFactory clientFactory;

	private HeaderView headerView;
	private DwoGlobalVars instance;
	private PageTracker tracker;
  
	  boolean legal(String base) {
	    RegExp r = RegExp.compile("^/[a-z]+(/[a-z]+)*/$");
	    return r.test(base);
	  }

	
	@Inject Login_Stap3(ClientFactory clientFactory, HeaderView headerView, DwoGlobalVars vars, PageTracker t) {
		super();
		this.clientFactory = clientFactory;
		this.headerView = headerView;
		this.instance = vars;
		tracker = t;
	}

	@Override
	public Promise<Void> call(Promise<Place> resolved) throws Exception {

		Place next = resolved.getValue();
		DomUserFull currentUser = instance.getCurrentUser();
		RoleType roleType = instance.getRoleType();
		headerView.setUserAndRole(currentUser, roleType);
		tracker.logon();
		if(next == null)
		{ 
		  clientFactory.gotoCourses();
		}
		else
			clientFactory.goTo(next);
		return null;

	}
}