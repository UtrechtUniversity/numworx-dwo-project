package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWO2ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.DWO2RPCHandler;
import nl.uu.fi.dwo.mobile.client.DWO2playerDefaults;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderLessView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.Login3ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewNumworx;

@Module(subcomponents= {ActivityComponent.class})
public abstract class HeaderLessModule {

  static boolean headerless() {
    return "less".equals(Window.Location.getParameter("header"));
  }
  
  @Provides @Singleton @Named("header")
  static HeaderView header(Provider<HeaderLessView> less, Provider<HeaderViewNumworx> more) {
    if (headerless()) 
        return less.get();
    else
        return more.get();  
  }
  
  @Provides
  static ViewModuleViewBuilder builder(Provider<ViewModuleViewNumworx> numworx, RPCHandler rpc, ActivityComponent.Builder builder, Provider<SMLogger.LoggingModule>loggingProvider ) {
    if (headerless()) {
      ActivityComponent a = builder.loggingModule(loggingProvider.get()).build();
      ViewModuleViewImpl view = new ViewModuleViewImpl(a, rpc);
      view.initialize(a.api());
	return view;
    } else {
      return numworx.get();
    }
  }
  
  @Binds abstract ClientFactory factory(DWO2ClientFactoryImpl impl);
  @Binds abstract LoginView loginview(Login3ViewImpl view);
  @Binds abstract SMLogger.LoggingModule loggingModule(SMLogger.DWO2playerProvider impl);

  @BindsOptionalOf abstract DwoGlobalVars optionalvars();
  @Provides static DwoGlobalVars vars() { return DwoGlobalVars.instance(); }
  @Provides @Named("premium") static boolean premium(DwoGlobalVars vars) {
	  return vars.isPremium();
  }
  
  @Provides static HeaderView headerview(DWO2ClientFactoryImpl impl) {
    return impl.getHeaderView();
  }
  
  @Provides static TreeModuleView treemoduleview(DWO2ClientFactoryImpl impl) {
    return impl.getTreeModuleView();
  }

  @Provides static ViewModuleView viewmoduleview(DWO2ClientFactoryImpl impl) {
    return impl.getEntryView();
  }
 
  @Provides @Named("parentAPI") static Scorm2004IF api(DWO2ClientFactoryImpl impl) {
	  return impl.setupAPI();
  }
  
  
  @Provides static NavigationView navigationview(NavigationViewNumworx impl) {
    return impl;
  }
  
  @Binds abstract DWOplayerParameters parameters(DWO2playerDefaults create);

  @Binds abstract RPCHandler rpc(DWO2RPCHandler rpc);
}
