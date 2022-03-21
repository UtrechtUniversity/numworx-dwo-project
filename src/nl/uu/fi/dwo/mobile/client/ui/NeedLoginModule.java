package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class NeedLoginModule {
	@Provides
	@Singleton static NeedLogin needLogin(@Named("defaultPlace") Place place, PlaceController controller, EventBus bus) {
		if (Actions.isAvailable()) {
			return new NeedLogin.ActionNeedLogin(controller, bus);
		}
		return new NeedLogin.PlaceNeedLogin(place, controller, bus);
	}
}
