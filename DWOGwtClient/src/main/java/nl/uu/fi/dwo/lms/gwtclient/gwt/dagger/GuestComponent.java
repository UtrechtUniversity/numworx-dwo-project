package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Subcomponent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;

@RoleScope @Subcomponent(modules= { GuestModule.class } )
public interface GuestComponent extends PresenterBuilder {
  PresenterFactory presenterFactory();
  SwitchViewEventHandler viewHandler();
  
  @Subcomponent.Builder
  interface Builder {
    GuestComponent build();
  }


}
