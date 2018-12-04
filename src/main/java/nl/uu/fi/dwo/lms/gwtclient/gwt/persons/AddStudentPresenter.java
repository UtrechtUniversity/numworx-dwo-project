package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 * 
 *
 * @author G.A.J. van der Plas
 */

public class AddStudentPresenter extends AddPersonPresenter {
  private static final Logger LOG = Logger.getLogger(AddStudentPresenter.class.getName());

    //    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#addStudent"));
        view.init(RoleType.TEACHER); //role of client user.
        view.setEmptyTableMessage();
        updateSchoolClasses();
    }

    @Inject public AddStudentPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        FAILURE = new LoggingFailure(LOG, anEventBus);
        manager = new PersonsServiceTeacher(aDwoGlobalVars);
        role = RoleType.TEACHER;
    }

}
