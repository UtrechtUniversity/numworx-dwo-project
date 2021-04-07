package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;

import dagger.BindsInstance;
import dagger.Component;
import nl.uu.fi.dwo.mobile.WiskOpdrPlayer;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;

@Component(modules = { PlayerModule.class, DummyFactory.class, ModuleViewModule.class })
@Singleton
public interface WiskOpdrComponent {
  void inject(WiskOpdrPlayer dwo);
  EventBus bus();
  @Component.Builder 
  interface Builder {
    WiskOpdrComponent build();
    Builder moduleView(ModuleViewModule module);
    @BindsInstance Builder api(@Named("parentAPI")Scorm2004IF api);
    @BindsInstance Builder premium(@Named("premium") boolean b);
  }
}
