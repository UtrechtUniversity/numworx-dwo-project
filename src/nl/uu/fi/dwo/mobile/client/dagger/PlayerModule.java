package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.ActivityMapperModule;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.activities.ClassCourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ClassesActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.GuestActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LogoutActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.MaybeLogoutActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.ClassesPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.Exam;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LogoutPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.MaybeLogout;
import nl.uu.fi.dwo.mobile.client.ui.places.cc;
import nl.uu.fi.dwo.mobile.client.ui.places.guest;

@Module(includes={ActivityMapperModule.class })
public abstract class PlayerModule {

  @Provides
  @Singleton
  static EventBus eventBus() {
    return new SimpleEventBus();
  }

  @BindsOptionalOf abstract DwoGlobalVars novars();

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

  @Provides @Reusable
  static PlaceHistoryMapper getHistoryMapper() {
    return GWT.create(AppPlaceHistoryMapper.class);
  }

  @Provides
  @Singleton
  static ActivityManager getActivityManager(TabletActivityMapper appActivityMapper, EventBus bus) {
    return new ActivityManager(appActivityMapper, bus);
  }

  @Binds @IntoMap @ClassKey(ClassesPlace.class) abstract Activity classesActivity(ClassesActivity classes);
  @Binds @IntoMap @ClassKey(LogoutPlace.class)  abstract Activity logoutActivity(LogoutActivity logout);
  @Binds @IntoMap @ClassKey(guest.class) abstract Activity guestActivity(GuestActivity guest);
  @Binds @IntoMap @ClassKey(Exam.class) abstract Activity examActivity(ExamActivity exam);
  @Binds @IntoMap @ClassKey(cc.class) abstract Activity ccActivity(ClassCourseActivity cc);
  @Binds @IntoMap @ClassKey(MaybeLogout.class) abstract Activity maybeLogoutActivity(MaybeLogoutActivity maybe);
  @Binds @IntoMap @ClassKey(LoginPlace.class) abstract Activity loginActivity(LoginActivity login);
}
