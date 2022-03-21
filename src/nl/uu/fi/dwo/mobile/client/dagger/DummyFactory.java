package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.DWOplayerDefaults;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.DummyRPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module(subcomponents= {ActivityComponent.class})
public abstract class DummyFactory {
  ///@Binds abstract ClientFactory clientFactory(DummyClientFactory dummy);
  @Binds abstract RPCHandler  rpcHandler(DummyRPCHandler dummy);
  @Singleton
  @Binds abstract ViewModuleView viewModuleView(ViewModuleViewImpl impl);
  @Singleton
  @Provides static DWOplayerParameters parameters() {
    DWOplayerParameters create = GWT.create(DWOplayerDefaults.class);
    return create;
  }

  @Provides @Singleton static NeedLogin needLogin() {
	  return NeedLogin.instance();
  }
}
