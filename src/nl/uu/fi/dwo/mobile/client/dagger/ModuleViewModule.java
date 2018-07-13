package nl.uu.fi.dwo.mobile.client.dagger;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module
public class ModuleViewModule {

  @Provides protected ViewModuleViewImpl getViewModuleView() {
    return null;
  }
}
