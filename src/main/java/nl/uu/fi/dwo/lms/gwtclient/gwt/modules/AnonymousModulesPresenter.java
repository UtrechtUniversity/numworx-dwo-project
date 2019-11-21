package nl.uu.fi.dwo.lms.gwtclient.gwt.modules;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.SimpleEventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;


@RoleScope
public class AnonymousModulesPresenter extends ModulesPresenter {

  @Inject AnonymousModulesPresenter(SimpleEventBus anEventBus, DwoGlobalVars aDwoGlobalVars, ViewFactory v) {
    super(anEventBus, aDwoGlobalVars, v);
  }

}
