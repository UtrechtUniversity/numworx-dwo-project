package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Reusable;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.last;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Reusable
public class LastActivity extends AbstractActivity {

	final private static String BASE_KEY = LastActivity.class.getName()+ ".";
	
	final DwoGlobalVars vars;
	final PlaceController controller;
	final PlaceHistoryMapper mapper;
	final Storage storage;
	
	@Inject LastActivity(DwoGlobalVars vars, PlaceController controller, PlaceHistoryMapper mapper) {
		this.vars = vars;
		this.controller = controller;
		this.mapper = mapper;
		this.storage = Storage.getLocalStorageIfSupported();
	}

	
	public void putPlace(Place p) {
		if (!vars.withUser()) return;
		String subkey = getSubkey();
		String value = mapper.getToken(p);
		storage.setItem(BASE_KEY + subkey, value);
	}

	String getSubkey() {
		return vars.getActiveSchoolRoleAndClass().getHasRole().getId().getIdString();
	}
	
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		if (!vars.withUser()) {
			controller.goTo(new LoginPlace(last.LAST_PLACE));
			return;
		}
		Place place = getCourseId();
		controller.goTo(place);
	}

	
	private Place getCourseId() {
		String subkey = getSubkey();
		String value = storage.getItem(BASE_KEY  + subkey);
		if (value != null) {
			Place place = mapper.getPlace(value);
			return place;
		}		
		return new TreeModulePlace();
	}

}
