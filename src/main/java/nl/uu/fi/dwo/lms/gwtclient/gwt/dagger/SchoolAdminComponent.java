package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Subcomponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;

@RoleScope
@Subcomponent(modules = { SchoolAdminModule.class })
public interface SchoolAdminComponent extends PresenterBuilder {

  PresenterFactory presenterFactory();

  @Subcomponent.Builder
  interface Builder {
    SchoolAdminComponent build();
  }
  
}
