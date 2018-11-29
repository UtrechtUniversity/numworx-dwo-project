package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUserFull;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class OrganisationPresenter {

  public interface Display extends BasicDisplay {
    
  void setEmptyTableMessage();

  void setLoadingTableMessage();

  void showPersonen(Map<String, TaggedDomUserFull> personen, RoleType role);
}

  private static final Logger LOG = Logger.getLogger(OrganisationPresenter.class.getName());

  private Display view;
  private final DwoGlobalVars dwoGlobalVars;
  private final EventBus eventBus;
  private final Failure FAILURE;

  
  
  @Inject OrganisationPresenter(DwoGlobalVars vars, EventBus bus) {
    this.dwoGlobalVars = vars;
    this.eventBus = bus;
    this.FAILURE = new LoggingFailure(LOG, bus);
  }
  
  public void init() {
    view.init();
    view.clear();
    view.setHelp(dwoGlobalVars.buildHelpUrl("#organisation"));
    view.setEmptyTableMessage();
  }
  
  
  @JsMethod void setChooseClass(boolean choice) {
    
  }
  
  @JsMethod void setEditModules(boolean choice) {
    
  }
  
  @JsMethod void deletePersons(JavaScriptObject obj, String role) {
    
  }
  
  @JsMethod void selectRole(String role) {
    view.setEmptyTableMessage();
  }
  
  @Inject void setView (Display view) {
    this.view = view;
  }
}
