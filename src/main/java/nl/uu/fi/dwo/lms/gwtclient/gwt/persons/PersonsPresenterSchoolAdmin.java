package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

public class PersonsPresenterSchoolAdmin extends PersonsPresenter {
  private static final Logger LOG = Logger.getLogger(PersonsPresenterSchoolAdmin.class.getName());
  private final LoggingFailure FAILURE;

  @Inject PersonsPresenterSchoolAdmin(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsServiceSchoolAdmin manager) {
    super(anEventBus, aDwoGlobalVars, manager);
    FAILURE = new LoggingFailure(LOG, anEventBus);
  }

  List<DomUser> studentToUser(List<DomStudent>list) {
    return new ArrayList<>(list);
  }
  List<DomUser> teacherToUser(List<DomTeacher>list) {
    return new ArrayList<>(list);
  }
  
  
  @Override @JsMethod
  public void showStudentList() {
    view.setLoadingTableMessage();
    Promise<List<DomUser>> promise;
    promise = manager.getTeachersStudents().map(this::studentToUser);
    updateView(promise);
  }

  @Override  @JsMethod
  public void showTeacherList() {
    view.setLoadingTableMessage();
    Promise<List<DomUser>> promise;
    promise = manager.getTeachersInSchool().map(this::teacherToUser);
    updateView(promise);
  }


  void updateView(Promise<List<DomUser>> promise) {
    // onSuccess update view
    promise.then( resolved -> {
            personen = new HashMap<>(resolved.getValue().size());
            resolved.getValue().forEach((k -> personen.put(k.getId().getIdString(), k)));
            view.showPersonen(personen);
            if(personen.isEmpty())
              view.setEmptyTableMessage();
            return null;
        }, FAILURE)
    .recover((p) -> { view.setEmptyTableMessage(); return null; });
  }

  @Override  @JsMethod
  public void editStudent(String id) {
    // TODO Auto-generated method stub
    super.editStudent(id);
  }

  @Override  @JsMethod
  public void editTeacher(String id) {
    // TODO Auto-generated method stub
    super.editTeacher(id);
  }

  @Override  @JsMethod
  public void addPerson() {
    super.addPerson();
  }

  @Override  @JsMethod
  public boolean hasImportPersons() {
    return true;
  }

  @Override @JsMethod
  public void importPersons(JavaScriptObject file) {
    super.importPersons(file);
  }

}
