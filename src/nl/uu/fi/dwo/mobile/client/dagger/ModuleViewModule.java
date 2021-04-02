package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module
public class ModuleViewModule {

  @Provides @Singleton protected ViewModuleViewImpl getViewModuleView(RPCHandler rpc, @Named("parentAPI") Scorm2004IF api, ActivityComponent.Builder builder) {
    return null;
  }
  
  protected ModuleViewModule() {}
}
