package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.RowCountChangeEvent;
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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
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
  private Map<String,TaggedDomUser<DomUser>> students;
  private Map<String,TaggedDomUser<DomUser>> teachers;
  private Map<String,TaggedDomUser<DomUser>> schooladmins;

  //private Map<String, Promise<List<DomStudent>>> studentMap;
  private Map<String, Promise<List<DomTeacher>>> teacherMap;
  
  int pagesize = 50;
  long restsize = 100;
  SimplePager pager;
  Stub stub;
  
  static final Predicate<Entry<String, TaggedDomUser<DomUser>>> NULL =  t -> true;
  
  class Stub implements HasRows {
	  
	boolean rowCountExact = false;
	int rowCount = 0;
	Range visibleRange = new Range(0,pagesize);
	private RoleType role;
	private Map<String, TaggedDomUser<DomUser>> personen = Collections.emptyMap(); 
	private Predicate<Entry<String, TaggedDomUser<DomUser>>> filter = NULL;

	@Override
	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration r = eventBus.addHandlerToSource(RangeChangeEvent.getType(), this, handler);
		return r::removeHandler;
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(
			com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		com.google.web.bindery.event.shared.HandlerRegistration r = eventBus.addHandlerToSource(RowCountChangeEvent.getType(), this, handler);
		return r::removeHandler;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Range getVisibleRange() {
		return visibleRange;
	}

	@Override
	public boolean isRowCountExact() {
		return rowCountExact;
	}

	@Override
	public void setRowCount(int count) {
		setRowCount(count, true);		
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		rowCount = count;
		rowCountExact = isExact;
		RowCountChangeEvent.fire(this, count, isExact);
	}

	@Override
	public void setVisibleRange(int start, int length) {
		setVisibleRange(new Range(start, length));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setVisibleRange(Range range) {
		visibleRange = range;
		Set<Entry<String,TaggedDomUser<DomUser>>> entrySet = personen.entrySet();
		Stream<Entry<String, TaggedDomUser<DomUser>>> stream = entrySet.stream();
		view.showPersonen(
				stream
				.filter(filter)
				.skip(range.getStart())
				.limit(range.getLength())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)), 
				role);
		RangeChangeEvent.fire(this, range);
	}

	protected void countPersons(boolean last) {
		int count;
		if (filter == NULL) count = personen.size();
		else count = (int) personen.entrySet().stream().filter(filter).count();
		if (count != rowCount || last != isRowCountExact()) setRowCount(count, last);
	}

	public void setRole(RoleType role) {
		this.role = role;		
	}

	public void limit(Map<String, TaggedDomUser<DomUser>> students, boolean first, boolean last) {
		first |= rowCount < pager.getPageSize();
		add(students, last);
		if(first) setVisibleRange(0, pager.getPageSize()); // to front
	}
	
	public void add(Map<String, TaggedDomUser<DomUser>> students, boolean last) {
		this.personen = students;
		countPersons(last);
		//pager.setPageSize(Math.min(pagesize, rowCount));
	}

	public void setFilter(Predicate filter) {
		if (filter == null) filter = NULL;
		this.filter = filter;	
		countPersons(isRowCountExact());
	}
	  
  }
  
  
  @Inject OrganisationPresenter(DwoGlobalVars vars, EventBus bus, PersonsServiceSchoolAdmin service) {
    this.dwoGlobalVars = vars;
    this.eventBus = bus;
    this.service = service;
    this.FAILURE = new LoggingFailure(LOG, bus);

    if (vars.isTest()) {
    
	    RootPanel root = RootPanel.get("organisationpager");
		root.clear();
	    pager = new SimplePager();
	    pager.setPageSize(pagesize);
	    root.add(pager);
	    pager.setDisplay(stub = new Stub());
	    
	    stub.addRangeChangeHandler(ev -> { 
	    	LOG.info("range update:" + ev.getNewRange() );    	
	    } );
	    stub.addRowCountChangeHandler(ev -> {
	    	LOG.info("count is = " + ev.getNewRowCount());
	    });
    
    }
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
    
//    Promise<List<DomSchoolClass>> pp = service.getTeachersSchoolClasses().then(
//      p -> {
//        List<DomSchoolClass> list = p.getValue();
//        LinkedHashMap<String,TaggedDomSchoolClass> map = new LinkedHashMap<>();
//        Collections.sort(list, (a,b) -> {
//          return String.CASE_INSENSITIVE_ORDER.compare(a.getSchoolClassName(), b.getSchoolClassName());
//        });
//        list.forEach(item -> map.put(item.getId().toString(), new TaggedDomSchoolClass(item)));
//        view.showSchoolClasses(map);
//        schoolClasses = map;
//        return p;
//      });
//      Promise<?> p1 = pp.flatMap(this::getStudents);
//      Promise<List<DomStudent>> p2 = service.getTeachersStudents();
//      Promises.all(p1,p2).then(
//        x -> {
//          List<DomStudent> s = p2.getValue();
//          students = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
//                                        student -> {
//                                          return new TaggedDomUser<DomStudent>(student, new ArrayList<String>());
//                                          
//                                     }));
//          for(Entry<String, Promise<List<DomStudent>>> entry : studentMap.entrySet()) {
//            String key = entry.getKey();
//            entry.getValue().getValue().forEach(item -> students.get(item.getId().getIdString()).getMemberOf().add(key));
//          }
//          showPersonen(students, RoleType.STUDENT);
//          return null;
//        }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
//      
    	DomSchoolOrganisation org = new DomSchoolOrganisation();
    	org.setLimit(restsize);
		Promise<DomSchoolOrganisation> p0 = service.getStudentsInSchool(org);
    	Promise<List<DomSchoolClass>> pp = p0.map(DomSchoolOrganisation::getSchoolClasses);
    	pp = pp.then( p -> {
    	        List<DomSchoolClass> list = p.getValue();
    	        LinkedHashMap<String,TaggedDomSchoolClass> map = new LinkedHashMap<>();
    	        Collections.sort(list, (a,b) -> {
    	          return String.CASE_INSENSITIVE_ORDER.compare(a.getSchoolClassName(), b.getSchoolClassName());
    	        });
    	        list.forEach(item -> map.put(item.getId().toString(), new TaggedDomSchoolClass(item)));
    	        view.showSchoolClasses(map);
    	        schoolClasses = map;
    	        students = new LinkedHashMap<>();
    	        return p;
  		});
    	pp.then( x -> {
    		return extractStudents(p0);
    	}).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
    
    
  }

protected Promise<Object> extractStudents(Promise<DomSchoolOrganisation> p0) {
	DomSchoolOrganisation org = p0.getValue();
	List<DomStudent> s = org.getStudents();
	Map<String, TaggedDomUser<DomUser>> ss = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
			student -> {
				return new TaggedDomUser<DomUser>(student, new ArrayList<String>());
			}));
		org.getStudentsOfClasses().forEach(t -> {
		String sid = t.getStudentId().getIdString();
		List<String> array = ss.get(sid).getMemberOf();
		array.add(t.getClassId().getIdString());
	});
	boolean first = students.isEmpty();
	students.putAll(ss);
	showPersonen(students, RoleType.STUDENT, first, ss.isEmpty());
	if (!ss.isEmpty()) {
		org.setStudents(null);
		org.setStudentsOfClasses(null);
		return service.getStudentsInSchool(org).then(this::extractStudents);
	} 
	return null;
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
                                        return new TaggedDomUser<DomUser>(student, new ArrayList<String>());
                                        
                                   }));
        for(Entry<String, Promise<List<DomTeacher>>> entry : teacherMap.entrySet()) {
          String key = entry.getKey();
          // NPE if garbage: non existent user in ClassOf table
          entry.getValue().getValue().forEach(item -> {
			TaggedDomUser<DomUser> u = teachers.get(item.getId().getIdString());
			if (u != null) 
				u.getMemberOf().add(key);
		});
        }
        showPersonen(teachers, RoleType.TEACHER, true, true);
        return null;
      }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
   
  }
  
  private Promise<Void> getStudents() {
    view.setLoadingTableMessage();
    DomSchoolOrganisation org = new DomSchoolOrganisation();
    org.setLimit(restsize);
    org.setSchoolClasses(schoolClasses.values().stream().map(TaggedDomSchoolClass::getSchoolClass).collect(Collectors.toList()));
    students.clear();
    Promise<DomSchoolOrganisation> p1 = service.getStudentsInSchool(org);
    return p1.then(this::extractStudents)
    	.then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);

  }
  
  
  private Promise<Void> getSchoolAdmins() {
    view.setLoadingTableMessage();
    Promise<List<DomSchoolAdmin>> p2 = service.getSchoolAdminsInSchool();
    return p2.then( p -> {
      List<DomSchoolAdmin> s = p.getValue();
      schooladmins = s.stream()
          .filter(admin -> !dwoGlobalVars.getCurrentUser().getId().equals(admin.getId()))
          .collect(Collectors.toMap( admin -> admin.getId().toString(), admin -> new TaggedDomUser<DomUser>(admin)));
      showPersonen(schooladmins, RoleType.SCHOOLADMIN, true, true);
      return null;
    }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
    
  }

  void showPersonen(@SuppressWarnings("rawtypes") Map<String, TaggedDomUser<DomUser>> students, RoleType role, boolean first, boolean last) {
	  if(stub != null) {
		  stub.setRole(role);
		  stub.limit(students, first, last);
		  return;
	  }
	  view.showPersonen(students, role);
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
    TaggedDomUser<DomUser> s;
    switch(role) {
      case STUDENT: s = students.get(id);
          if (s.getUser().getSingleSchool().booleanValue())
            next = service.removeSingleSchoolStudentFromSchool(new DomStudent(s.getUser()));
          else
            next = service.removeStudentFromSchool(new DomStudent(s.getUser()));
      break;
      case TEACHER: TaggedDomUser<DomUser> t = teachers.get(id);
          next = service.removeTeacherFromSchool(new DomTeacher(t.getUser()));
      break;
      case SCHOOLADMIN: TaggedDomUser<DomUser> a = schooladmins.get(id);
          next = service.removeSchoolAdminFromSchool(new DomSchoolAdmin(a.getUser()));
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

  @JsMethod
  void filterPersonsList(String username, String givenName, String insertion, String familyName, String schoolClass) {
	  if (stub != null) {
		  List <Predicate<Entry<String, TaggedDomUser<DomUser>>>> f = new ArrayList<>();
		  if (username != null && !username.isEmpty())
		  {		  
			  f.add( t -> containsIgnoreCase(t.getValue().getUser().getUserName(), username));
		  }
		  if (givenName != null && !givenName.isEmpty())
		  {
			  f.add(t -> containsIgnoreCase(t.getValue().getUser().getGivenName(), givenName));
		  }
		  if (familyName != null && !familyName.isEmpty())
		  {
			  f.add(t -> containsIgnoreCase(t.getValue().getUser().getFamilyName(), familyName));
		  }
		  if (insertion != null && !insertion.isEmpty())
		  {
			  f.add(t -> containsIgnoreCase(t.getValue().getUser().getInsertion(), insertion));
		  }

		  if (schoolClass != null && !schoolClass.isEmpty()) {
			  if ("NONE".equals(schoolClass)) {
				  f.add(t -> t.getValue().getMemberOf().isEmpty() );
			  } else {
				  f.add(t -> t.getValue().getMemberOf().contains(schoolClass));
			  }
		  }
		  
		  stub.setFilter(and(f));
		  stub.setVisibleRange(stub.getVisibleRange());
	  }
  }
  
  
  
  private Predicate<Entry<String, TaggedDomUser<DomUser>>> and(List<Predicate<Entry<String, TaggedDomUser<DomUser>>>> f) {
	if (f.isEmpty()) return NULL;
	if (f.size() == 1) return f.get(0);
	return (t) -> {
		for( Predicate<Entry<String, TaggedDomUser<DomUser>>> x : f) {
			if (!x.test(t)) return false;
		}
		return true;
		
	};
}

static boolean containsIgnoreCase(String source, String regex) {
	  RegExp r = RegExp.compile(regex, "i");
	  return null !=  r.exec(source);	  
  }
}
