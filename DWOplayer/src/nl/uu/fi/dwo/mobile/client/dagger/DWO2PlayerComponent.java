package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.place.shared.PlaceHistoryHandler;

import dagger.BindsInstance;
import dagger.Component;
import nl.uu.fi.dwo.mobile.DWO2player;
import nl.uu.fi.dwo.mobile.client.ui.NeedLoginModule;
import nl.uu.fi.dwo.mobile.client.ui.PageTracker;

@Singleton
@Component(modules = { PlayerModule.class, PlaceModule.class, HeaderLessModule.class, NeedLoginModule.class, ProductionModule.class, XapiModule.class })
public interface DWO2PlayerComponent {
  void inject(DWO2player dwo);
  PlaceHistoryHandler placeHistoryHandler();
  PageTracker tracker();
  @Component.Builder
  interface Builder {
    @BindsInstance Builder profile(@Named("profile") int profile);
    DWO2PlayerComponent build();
  }

}
