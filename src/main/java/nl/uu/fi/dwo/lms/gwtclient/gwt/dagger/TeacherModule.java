package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.TeacherViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;

@Module
abstract class TeacherModule {
  @Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);
  @Binds abstract SwitchViewEventHandler viewHandler(TeacherViewHandler handler);

  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessages.class);
  }
}
