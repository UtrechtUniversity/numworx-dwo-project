package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JsArrayString;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.StringFormatter;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomUser;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class OrganisationPresenter {

  public interface Display extends BasicDisplay {
    
  void setEmptyTableMessage();

  void setLoadingTableMessage();

  void showPersonen(Map<String, ?> personen, RoleType role);

  void initEditModules(boolean flag);

  void initChooseClass(boolean flag);

  void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

  void initEditModules(boolean flag, boolean xs, boolean premium);
}

  private static final Logger LOG = Logger.getLogger(OrganisationPresenter.class.getName());

  private Display view;
  private final DwoGlobalVars dwoGlobalVars;
  private final EventBus eventBus;
  private final Failure FAILURE;
  private final PersonsServiceSchoolAdmin service;

  private Map<String,TaggedDomSchoolClass> schoolClasses;
  private Map<String,TaggedDomUser<DomStudent>> students;
  private Map<String,TaggedDomUser<DomTeacher>> teachers;
  private Map<String,TaggedDomUser<DomSchoolAdmin>> schooladmins;

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
    view.setLoadingTableMessage();
    
    DomSchool school = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    boolean premium = dwoGlobalVars.isPremium();
    boolean visible = true /* was dwoGlobalVars.isTest() || dwoGlobalVars.isSaml() */;
	view.initEditModules(school.teachersCanWrite(), school.accessControl() && premium, visible && premium);
    view.initChooseClass(school.studentsCanRegisterForSchoolClasses());
    
    Promise<List<DomSchoolClass>> pp = service.getTeachersSchoolClasses().then(
      p -> {
        List<DomSchoolClass> list = p.getValue();
        LinkedHashMap<String,TaggedDomSchoolClass> map = new LinkedHashMap<>();
        Collections.sort(list, (a,b) -> {
          return String.CASE_INSENSITIVE_ORDER.compare(a.getSchoolClassName(), b.getSchoolClassName());
        });
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
                                          return new TaggedDomUser<DomStudent>(student, new ArrayList<String>());
                                          
                                     }));
          for(Entry<String, Promise<List<DomStudent>>> entry : studentMap.entrySet()) {
            String key = entry.getKey();
            entry.getValue().getValue().forEach(item -> students.get(item.getId().getIdString()).getMemberOf().add(key));
          }
          view.showPersonen(students, RoleType.STUDENT);
          return null;
        }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
      
      
  }
  
  private Promise<Void> getTeachers() {
    view.setLoadingTableMessage();
    Promise<List<DomTeacher>> p2 = service.getTeachersInSchool();
    Promise<?> p1 = getTeachers(schoolClasses.values());
    
    return Promises.all(p1,p2).then(
      x -> {
        List<DomTeacher> s = p2.getValue();
        teachers = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
                                      student -> {
                                        return new TaggedDomUser<DomTeacher>(student, new ArrayList<String>());
                                        
                                   }));
        for(Entry<String, Promise<List<DomTeacher>>> entry : teacherMap.entrySet()) {
          String key = entry.getKey();
          // NPE if garbage: non existent user in ClassOf table
          entry.getValue().getValue().forEach(item -> {
			TaggedDomUser<DomTeacher> u = teachers.get(item.getId().getIdString());
			if (u != null) 
				u.getMemberOf().add(key);
		});
        }
        view.showPersonen(teachers, RoleType.TEACHER);
        return null;
      }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
   
  }
  
  private Promise<Void> getStudents() {
    view.setLoadingTableMessage();
    Promise<?> p1 = getStudents(schoolClasses.values().stream().map(TaggedDomSchoolClass::getSchoolClass).collect(Collectors.toList()));
    Promise<List<DomStudent>> p2 = service.getTeachersStudents();
    return Promises.all(p1,p2).then(
      x -> {
        List<DomStudent> s = p2.getValue();
        students = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
                                      student -> {
                                        return new TaggedDomUser<DomStudent>(student, new ArrayList<String>());
                                        
                                   }));
        for(Entry<String, Promise<List<DomStudent>>> entry : studentMap.entrySet()) {
          String key = entry.getKey();
          entry.getValue().getValue().forEach(item -> students.get(item.getId().getIdString()).getMemberOf().add(key));
        }
        view.showPersonen(students, RoleType.STUDENT);
        return null;
      }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);

  }
  
  
  private Promise<Void> getSchoolAdmins() {
    view.setLoadingTableMessage();
    Promise<List<DomSchoolAdmin>> p2 = service.getSchoolAdminsInSchool();
    return p2.then( p -> {
      List<DomSchoolAdmin> s = p.getValue();
      schooladmins = s.stream()
          .filter(admin -> !dwoGlobalVars.getCurrentUser().getId().equals(admin.getId()))
          .collect(Collectors.toMap( admin -> admin.getId().toString(), admin -> new TaggedDomUser<DomSchoolAdmin>(admin)));
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
  
  
  @JsMethod void setChooseClass(boolean choice) { // Verkeerd om!
    DomSchool s = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    String rights = s.getSchoolRights();
    if ("_".equals(rights)) rights = DomSchool.defaultRights();
    rights = rights.replace("c", "");
    if (choice) {
      rights = rights + "c";
    }
    setSchoolRights(s, rights);
    
  }

  void setSchoolRights(DomSchool s, String rights) {
    DomSchoolFull school = new DomSchoolFull();
    school.setId(s.getId());
    school.setSchoolRights(rights);
    String r = rights;
    service.updateSchool(school).then(p->{
      s.setSchoolRights(r);
      dwoGlobalVars.getSchoolLogins().getSchoolsRolesAndClassesList().forEach(item -> 
      {  DomSchool ss = item.getSchool();
         if (ss.getId() .equals( s.getId()))
          ss.setSchoolRights(r);
      });
      return null;}, FAILURE);
  }
  
  @JsMethod void setEditModules(boolean choice, boolean xs) { // Verkeerd om
    DomSchool s = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    String rights = s.getSchoolRights();
    if ("_".equals(rights)) rights = DomSchool.defaultRights();
    rights = rights.replace("m", "").replace("X", "");
    if (choice) {
      rights = rights + "m";
    }
    if (xs) rights += "X";

    setSchoolRights(s, rights);
  }
  
  @JsMethod void deletePersons(JsArrayString obj, String str) {
    RoleType role = RoleType.valueOf(str);
    int size = obj.length();
    AlertDialogWithConfirmCancelDeferred defer;
    ProgressDialogWithAbortDeferred progress =
    new ProgressDialogWithAbortDeferred(DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_CONFIRM_TITLE());
    String aMsg = 
        size == 1 ? DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_CONFIRM_REMOVE1() :
        StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_CONFIRM_REMOVE(), size); //    
    defer = new AlertDialogWithConfirmCancelDeferred(aMsg);
   
    
    eventBus.fireEvent(new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, defer));
    defer.getPromise()
    .then(p -> {
      if (p.getValue()) {
        ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Init, 0, DwoLocalesForGWT.instance.NUM_DLG_Class_StartingCopyStudents(), progress);
        eventBus.fireEvent(e);
        return deleteUser(obj, 0, role, progress.getPromise());
      }
      return p;
    }) 
    .then(p -> {
        switch(role) {
          case STUDENT: return getStudents();
          case TEACHER: return getTeachers();
          default:
          case SCHOOLADMIN: return getSchoolAdmins();
        }
      })
      .then(null, FAILURE);
  }
  
  private Promise<Boolean> deleteUser(JsArrayString obj, int i, RoleType role, Promise<Boolean> cancel) {
    if(i >= obj.length() || cancel.isDone()) {
      ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Complete, 100, DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_REMOVING_COMPLETE(), null);
      eventBus.fireEvent(e);
      return null;
    }
    double r = (100.0 * (i+1) / obj.length());
    ProgressDialogWithAbortEvent e = new ProgressDialogWithAbortEvent(ProgressDialogWithAbortEvent.EventType.Update, (int) r, DwoLocalesForGWT.instance.NUM_DLG_ORGANISATION_REMOVING(), null);
    eventBus.fireEvent(e);
    String id = obj.get(i);
    Promise<Boolean> next = null;
    TaggedDomUser<DomStudent> s;
    switch(role) {
      case STUDENT: s = students.get(id);
          if (s.getUser().getSingleSchool().booleanValue())
            next = service.removeSingleSchoolStudentFromSchool(s.getUser());
          else
            next = service.removeStudentFromSchool(s.getUser());
      break;
      case TEACHER: TaggedDomUser<DomTeacher> t = teachers.get(id);
          next = service.removeTeacherFromSchool(t.getUser());
      break;
      case SCHOOLADMIN: TaggedDomUser<DomSchoolAdmin> a = schooladmins.get(id);
          next = service.removeSchoolAdminFromSchool(a.getUser());
      break;
      default: next = Promises.failed(new IllegalArgumentException());
    }
    return next.then(p -> deleteUser(obj, i+1, role, cancel));
  }

  @JsMethod void selectRole(String str) {
    RoleType role = RoleType.valueOf(str);
    Promise<Void> p;
    switch(role) {
      case STUDENT: p = getStudents(); break;
      case TEACHER: p = getTeachers(); break;
      case SCHOOLADMIN: p = getSchoolAdmins(); break;
      default:
        p = Promises.failed(new IllegalArgumentException());
        view.setEmptyTableMessage();
    }
    p.then(null, FAILURE);
  }
  
  @Inject void setView (Display view) {
    this.view = view;
    view.setHelp(dwoGlobalVars.buildHelpUrl("#organisation"));
  }
}
