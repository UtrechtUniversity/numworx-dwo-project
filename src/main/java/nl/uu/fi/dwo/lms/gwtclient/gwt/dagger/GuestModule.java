package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.lms.gwtclient.gwt.GuestPresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.GuestViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;

@Module
abstract class GuestModule {
  @Binds abstract PresenterFactory presenterFactory(GuestPresenterFactory factory);
  @Binds abstract SwitchViewEventHandler viewHandler(GuestViewHandler handler);


}
