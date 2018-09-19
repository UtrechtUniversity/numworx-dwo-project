package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;

@Module
public abstract class PlayerModule {

  @Provides
  @Singleton
  static EventBus eventBus() {
    return new SimpleEventBus();
  }

  
  @Provides
  @Singleton
  static PlaceController getController(EventBus bus) {
    return new PlaceController(bus);
  }

  @Provides
  @Singleton
  static PlaceHistoryHandler getHandler(PlaceHistoryMapper mapper, PlaceController placeController,
      EventBus bus, @Named("defaultPlace") Place defaultPlace) {
    PlaceHistoryHandler handler = new PlaceHistoryHandler(mapper);
    handler.register(placeController, bus, defaultPlace);
    return handler;
  }

  @Provides
  static PlaceHistoryMapper getHistoryMapper() {
    return GWT.create(AppPlaceHistoryMapper.class);
  }

  @Provides
  @Singleton
  static ActivityManager getActivityManager(TabletActivityMapper appActivityMapper, EventBus bus) {
    return new ActivityManager(appActivityMapper, bus);
  }

}
