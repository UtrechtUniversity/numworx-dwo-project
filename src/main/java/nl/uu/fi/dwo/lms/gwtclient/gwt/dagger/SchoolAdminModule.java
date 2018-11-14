package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;

@Module
public abstract class SchoolAdminModule {
  @Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);

}
