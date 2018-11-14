package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Subcomponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;

@RoleScope
@Subcomponent(modules= { TeacherModule.class })
public interface TeacherComponent extends PresenterBuilder {

  PresenterFactory presenterFactory();
  
  @Subcomponent.Builder
  interface Builder {
    TeacherComponent build();
  }
  
}
