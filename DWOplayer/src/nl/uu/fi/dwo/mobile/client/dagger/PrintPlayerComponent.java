package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;

import dagger.BindsInstance;
import dagger.Component;
import nl.uu.fi.dwo.mobile.PrintPlayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Component(modules = { PlayerModule.class, DummyFactory.class, PrintViewModule.class })
@Singleton
public interface PrintPlayerComponent {
  void inject(PrintPlayer dwo);
  EventBus bus();
  ViewModuleViewImpl view();
  ActivityInterface activity();
  @Component.Builder 
  interface Builder {
    PrintPlayerComponent build();
    @BindsInstance Builder api(@Named("parentAPI")Scorm2004IF api);
    @BindsInstance Builder premium(@Named("premium") boolean b);
  }
}
