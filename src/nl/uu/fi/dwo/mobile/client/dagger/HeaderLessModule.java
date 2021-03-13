package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWO2ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderLessView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.Login3ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewNumworx;

@Module
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
  static ViewModuleViewBuilder builder() {
    if (headerless()) {
      return new ViewModuleViewImpl();
    } else {
      return new ViewModuleViewNumworx();
    }
  }
  @Binds abstract ClientFactory factory(DWO2ClientFactoryImpl impl);
  @Binds abstract LoginView loginview(Login3ViewImpl view);
  
  @Provides static DwoGlobalVars vars() { return DwoGlobalVars.instance(); }
  
  @Provides static HeaderView headerview(DWO2ClientFactoryImpl impl) {
    return impl.getHeaderView();
  }
  @Provides static NavigationView navigationview(DWO2ClientFactoryImpl impl) {
    return impl.getNavigationView();
  }
}
