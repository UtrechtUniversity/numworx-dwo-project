package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
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
  private Map<String, Promise<List<DomTeacher>>> teacherMap;
  
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
      Promise<?> p1 = pp.flatMap(this::getStudents);
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
        }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
      
      
  }
  
  private void getTeachers() {
    view.setLoadingTableMessage();
    Promise<List<DomTeacher>> p2 = service.getTeachersInSchool();
    Promise<?> p1 = getTeachers(schoolClasses.values());
    Promises.all(p1,p2).then(
      x -> {
        List<DomTeacher> s = p2.getValue();
        teachers = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
                                      student -> {
                                        return new TaggedDomUser(student, new ArrayList<String>());
                                        
                                   }));
        for(Entry<String, Promise<List<DomTeacher>>> entry : teacherMap.entrySet()) {
          String key = entry.getKey();
          entry.getValue().getValue().forEach(item -> teachers.get(item.getId().getIdString()).getMemberOf().add(key));
        }
        view.showPersonen(teachers, RoleType.TEACHER);
        return null;
      }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
   
  }
  
  private void getStudents() {
    view.setLoadingTableMessage();
    Promise<?> p1 = getStudents(schoolClasses.values().stream().map(TaggedDomSchoolClass::getSchoolClass).collect(Collectors.toList()));
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
      }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);

  }
  
  
  private void getSchoolAdmins() {
    view.setLoadingTableMessage();
    Promise<List<DomSchoolAdmin>> p2 = service.getSchoolAdminsInSchool();
    p2.then( p -> {
      List<DomSchoolAdmin> s = p.getValue();
      schooladmins = s.stream()
          .filter(admin -> !dwoGlobalVars.getCurrentUser().getId().equals(admin.getId()))
          .collect(Collectors.toMap( admin -> admin.getId().toString(), admin -> new TaggedDomUser(admin)));
      view.showPersonen(schooladmins, RoleType.SCHOOLADMIN);
      return null;
    }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
    
  }

  private Promise<?> getStudents(Collection <DomSchoolClass> list) {
    
    studentMap = list.stream().
      collect(Collectors.toMap( (DomSchoolClass item) -> item.getId().toString(), item -> service.getStudentsInSchoolClass(item)));   
    return Promises.all(studentMap.values());
  }
  
  private Promise<?> getTeachers(Collection<TaggedDomSchoolClass> list) {
    teacherMap = list.stream().map(TaggedDomSchoolClass::getSchoolClass).
        collect(Collectors.toMap( 
          (DomSchoolClass item) -> item.getId().toString(), 
          (DomSchoolClass item) -> service.getTeachersInSchoolClass(item)
          ));
    return Promises.all(teacherMap.values());
  }
  
  
  @JsMethod void setChooseClass(boolean choice) {
    DomSchoolFull school = new DomSchoolFull();
    DomSchool s = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    school.setId(s.getId());
    String rights = s.getSchoolRights();
    rights = rights.replace("c", "");
    if (choice) {
      rights = rights + "c";
    }
    school.setSchoolRights(rights);
    String r = rights;
    service.updateSchool(school).then(p->{s.setSchoolRights(r);return null;}, FAILURE);
    
  }
  
  @JsMethod void setEditModules(boolean choice) {
    DomSchoolFull school = new DomSchoolFull();
    DomSchool s = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    school.setId(s.getId());
    String rights = s.getSchoolRights();
    rights = rights.replace("m", "");
    if (choice) {
      rights = rights + "m";
    }
    final String r = rights;
    school.setSchoolRights(rights);   
    service.updateSchool(school).then(p-> { s.setSchoolRights(r); return null;}, FAILURE);
  }
  
  @JsMethod void deletePersons(JavaScriptObject obj, String role) {
    
  }
  
  @JsMethod void selectRole(String str) {
    RoleType role = RoleType.valueOf(str);
    switch(role) {
      case STUDENT: getStudents(); break;
      case TEACHER: getTeachers(); break;
      case SCHOOLADMIN: getSchoolAdmins(); break;
      default:
        view.setEmptyTableMessage();
    }
  }
  
  @Inject void setView (Display view) {
    this.view = view;
  }
}
