package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module
public class ModuleViewModule {

  @Provides @Singleton protected ViewModuleViewImpl getViewModuleView() {
    return null;
  }
}
