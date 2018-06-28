package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.gwt.place.shared.PlaceHistoryHandler;

import dagger.Component;
import nl.uu.fi.dwo.mobile.DWOplayer;

@Component(modules = { PlayerModule.class, DummyFactory.class, PlaceModule.class })
@Singleton
public interface PlayerComponent {
  void inject(DWOplayer dwo);

  PlaceHistoryHandler historyHandler();
}
