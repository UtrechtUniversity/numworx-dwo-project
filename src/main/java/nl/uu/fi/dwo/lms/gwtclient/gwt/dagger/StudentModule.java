package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.StudentPresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.StudentViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessagesStudent;

@Module
abstract class StudentModule {
  @Binds abstract PresenterFactory presenterFactory(StudentPresenterFactory factory);
  @Binds abstract SwitchViewEventHandler viewHandler(StudentViewHandler handler);

  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessagesStudent.class);
  }
}
