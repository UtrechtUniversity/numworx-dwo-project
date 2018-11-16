package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class AddPersonPresenterSchoolAdmin extends AddPersonPresenter {

  private static final Logger LOG = Logger.getLogger(AddPersonPresenterSchoolAdmin.class.getName());

  @Inject AddPersonPresenterSchoolAdmin(DwoGlobalVars dwoGlobalVars, EventBus eventBus, PersonsServiceSchoolAdmin manager) {
    this.eventBus = eventBus;
    this.dwoGlobalVars = dwoGlobalVars;
    this.manager = manager;
    FAILURE = new LoggingFailure(LOG, eventBus);
  }

  @Override
  public void init() {
    view.clear();
    view.setHelp(dwoGlobalVars.buildHelpUrl("#addPerson"));
    view.init(RoleType.SCHOOLADMIN); //role of client user.
    view.setEmptyTableMessage();
    updateSchoolClasses();
  }

}
