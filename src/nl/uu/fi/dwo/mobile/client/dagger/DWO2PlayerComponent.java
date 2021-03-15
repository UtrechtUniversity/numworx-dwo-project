package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.gwt.place.shared.PlaceHistoryHandler;

import dagger.BindsInstance;
import dagger.Component;
import nl.uu.fi.dwo.mobile.DWO2player;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
@Singleton
@Component(modules = { PlayerModule.class, PlaceModule.class, HeaderLessModule.class })
public interface DWO2PlayerComponent {
  void inject(DWO2player dwo);
  ClientFactory clientFactory();
  PlaceHistoryHandler placeHistoryHandler();
  @Component.Builder
  interface Builder {
    @BindsInstance Builder rpcHandler(RPCHandler rpcHandler);
    DWO2PlayerComponent build();
  }

}
