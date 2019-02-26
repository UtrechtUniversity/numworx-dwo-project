package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.gwt.place.shared.PlaceHistoryMapper;

import dagger.Component;
import nl.uu.fi.dwo.mobile.DWO2player;
@Singleton
@Component(modules = { PlayerModule.class, PlaceModule.class })
public interface DWO2PlayerComponent {
  void inject(DWO2player dwo);

}
