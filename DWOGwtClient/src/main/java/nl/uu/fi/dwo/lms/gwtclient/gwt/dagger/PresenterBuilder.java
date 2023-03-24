package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;

public interface PresenterBuilder {

  PresenterFactory presenterFactory();
  SwitchViewEventHandler viewHandler();

}
