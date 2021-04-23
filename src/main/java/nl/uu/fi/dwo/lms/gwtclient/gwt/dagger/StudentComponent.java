package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Subcomponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsModule;

@RoleScope
@Subcomponent(modules= { StudentModule.class, StudentResultsModule.class })
public interface StudentComponent extends PresenterBuilder {

  PresenterFactory presenterFactory();
  SwitchViewEventHandler viewHandler();
  
  @Subcomponent.Builder
  interface Builder {
    StudentComponent build();
  }

  
}
