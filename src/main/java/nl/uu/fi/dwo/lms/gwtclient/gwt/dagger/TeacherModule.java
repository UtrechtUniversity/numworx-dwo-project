package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.TeacherViewHandler;

@Module
abstract class TeacherModule {
  @Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);
  @Binds abstract SwitchViewEventHandler viewHandler(TeacherViewHandler handler);

}
