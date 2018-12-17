package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredSchoolAdminSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredSchoolAdminSchoolManager;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class OrganisationPresenter {

  public interface Display extends BasicDisplay {
    
  void setEmptyTableMessage();

  void setLoadingTableMessage();

  void showPersonen(Map<String, TaggedDomUser> personen, RoleType role);

  void initEditModules(boolean flag);

  void initChooseClass(boolean flag);

  void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);
}

  private static final Logger LOG = Logger.getLogger(OrganisationPresenter.class.getName());

  private Display view;
  private final DwoGlobalVars dwoGlobalVars;
  private final EventBus eventBus;
  private final Failure FAILURE;
  private final PersonsServiceSchoolAdmin service;

  private Map<String,TaggedDomSchoolClass> schoolClasses;
  private Map<String,TaggedDomUser> students;
  private Map<String,TaggedDomUser> teachers;
  private Map<String,TaggedDomUser> schooladmins;

  private Map<String, Promise<List<DomStudent>>> studentMap;
  
  @Inject OrganisationPresenter(DwoGlobalVars vars, EventBus bus, PersonsServiceSchoolAdmin service) {
    this.dwoGlobalVars = vars;
    this.eventBus = bus;
    this.service = service;
    this.FAILURE = new LoggingFailure(LOG, bus);
  }
  
  public void init() {
    view.init();
    view.clear();
    view.setHelp(dwoGlobalVars.buildHelpUrl("#organisation"));
    view.setLoadingTableMessage();;
    
    DomSchool school = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    view.initEditModules(school.getSchoolRights().contains("m"));
    view.initChooseClass(school.getSchoolRights().contains("c"));
    
    Promise<List<DomSchoolClass>> pp = service.getTeachersSchoolClasses().then(
      p -> {
        List<DomSchoolClass> list = p.getValue();
        HashMap<String,TaggedDomSchoolClass> map = new HashMap<>();
        list.forEach(item -> map.put(item.getId().toString(), new TaggedDomSchoolClass(item)));
        view.showSchoolClasses(map);
        schoolClasses = map;
        return p;
      });
      Promise<?> p1 = pp.then(this::getStudents);
      Promise<List<DomStudent>> p2 = service.getTeachersStudents();
      Promises.all(p1,p2).then(
        x -> {
          List<DomStudent> s = p2.getValue();
          students = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
                                        student -> {
                                          return new TaggedDomUser(student, new ArrayList<String>());
                                          
                                     }));
          for(Entry<String, Promise<List<DomStudent>>> entry : studentMap.entrySet()) {
            String key = entry.getKey();
            entry.getValue().getValue().forEach(item -> students.get(item.getId().getIdString()).getMemberOf().add(key));
          }
          view.showPersonen(students, RoleType.STUDENT);
          return null;
        },
        
        FAILURE).then( null, failed -> view.setEmptyTableMessage() );
      
      
  }
  
  private Promise<?> getStudents(Promise<List<DomSchoolClass>> p) {
    
    List<DomSchoolClass> list = p.getValue();
    studentMap = list.stream().
      collect(Collectors.toMap( (DomSchoolClass item) -> item.getId().toString(), item -> service.getStudentsInSchoolClass(item)));   
    return Promises.all(studentMap.values());
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
