package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Reusable;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.last;
import nl.uu.fi.dwo.mobile.client.ui.places.m;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Reusable
public class LastActivity extends AbstractActivity {

	final private static String BASE_KEY = LastActivity.class.getName()+ "$";
	
	final DwoGlobalVars vars;
	final PlaceController controller;
	final PlaceHistoryMapper mapper;
	final Storage storage;

	private Promise<DomDwoProfileFull> profile;
	
	@Inject LastActivity(DwoGlobalVars vars, PlaceController controller, PlaceHistoryMapper mapper, RPCHandler rpc) {
		this.vars = vars;
		this.controller = controller;
		this.mapper = mapper;
		this.storage = Storage.getLocalStorageIfSupported();
		this.profile = rpc.getDwoProfile();
	}

	
	public void putPlace(Place p) {
		if (!vars.withUser()) return;
		if (p instanceof TreeModulePlace) {
			String t = ((TreeModulePlace) p).getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(t);
			m m = new m(t); /// mmmh... obscure
			if (item.getParent() != SelectModuleItem.ROOT)
				m.setBack(item.getParent());
			p = m;
		}
		String subkey = getSubkey();
		String value = mapper.getToken(p);
		storage.setItem(BASE_KEY + subkey, value);
	}

	String getSubkey() {
		return 
			getProfileName() + "/" +
			vars.getActiveSchoolRoleAndClass().getHasRole().getId().getIdString();
	}


	public String getProfileName() {
		if (profile.isDone())
			return profile.getValue().getDwoProfileName();
		else
			return "unknown"; // should not happen!
	}
	
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (!vars.withUser()) {
			controller.goTo(new LoginPlace(last.LAST_PLACE));
			return;
		}
		Place place = getCourseId();
		if (place instanceof TreeModulePlace) {
			TreeModulePlace tmp = (TreeModulePlace) place;
			String token = tmp.getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(token);
			if (item == null) {
				place = new m(token);
				((m) place).setBack(SelectModuleItem.ROOT);
			}
		}
		controller.goTo(place);
	}

	
	private Place getCourseId() {
		String subkey = getSubkey();
		String value = storage.getItem(BASE_KEY  + subkey);
		if (value != null) {
			Place place = mapper.getPlace(value);
// FIXME verify if this is a legal place, if not, unexpected login under GWTClient
			return place;
		}		
		return new TreeModulePlace();
	}

}
