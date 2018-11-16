package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.GuestPresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.GuestViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;

@Module
abstract class GuestModule {
  @Binds abstract PresenterFactory presenterFactory(GuestPresenterFactory factory);
  @Binds abstract SwitchViewEventHandler viewHandler(GuestViewHandler handler);
  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessages.class);
  }


}
