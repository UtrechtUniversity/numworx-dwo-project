package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.gwt.place.shared.PlaceHistoryHandler;

import dagger.Component;
import nl.uu.fi.dwo.mobile.WiskOpdrPlayer;

@Component(modules = { PlayerModule.class, DummyFactory.class, ModuleViewModule.class })
@Singleton
public interface WiskOpdrComponent {
  void inject(WiskOpdrPlayer dwo);
  @Component.Builder 
  interface Builder {
    WiskOpdrComponent build();
    Builder moduleView(ModuleViewModule module);
  }
}
