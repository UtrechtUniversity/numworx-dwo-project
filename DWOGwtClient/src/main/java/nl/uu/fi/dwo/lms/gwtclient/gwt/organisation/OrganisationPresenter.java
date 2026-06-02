package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.RootPanel;
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
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.OrderType;
import nl.uu.fi.dwo.rest.dom.entities.util.SortType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

public class OrganisationPresenter {

// same as in Helpers.sort
		   public static native int localCompare( String source, String target ) /*-{
		     	return source.localeCompare( target );
		   }-*/;

  public interface Display extends BasicDisplay, PagingView<TaggedDomUser<DomUser>> {
    
  void setEmptyTableMessage();

  void setLoadingTableMessage();


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
  
  long restsize = 100;
  SimplePager pager;
  Stub<TaggedDomUser<DomUser>> stub;
  OrderType order = OrderType.asc;
  SortType  sort  = SortType.familyName;
  
  
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
    sort = SortType.familyName;
    order = OrderType.asc;
    if (stub != null) stub.init();
    
    initSchool();
    
    	DomSchoolOrganisation org = new DomSchoolOrganisation();
    	org.setLimit(restsize);
    	org.setRole(RoleType.STUDENT);
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

private void initSchool() {
	DomSchool school = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool();
    boolean premium = dwoGlobalVars.isPremium();
    boolean visible = true /* was dwoGlobalVars.isTest() || dwoGlobalVars.isSaml() */;
	view.initEditModules(school.teachersCanWrite(), school.accessControl() && premium, visible && premium);
    view.initChooseClass(school.studentsCanRegisterForSchoolClasses());
}

  
private final static DateTimeFormat DATE = DateTimeFormat.getFormat("yyMM");
protected Promise<Object> extractStudents(Promise<DomSchoolOrganisation> p0) {
	DomSchoolOrganisation org = p0.getValue();
	List<DomUser> s = org.getUsers();
	List<DomHasRole> roles = org.getHasRoles();
	if (roles == null) roles = Collections.emptyList();
	Map<String,Date> dates = new HashMap<>();
	roles.forEach(item -> {
		String key = item.getUserId().getIdString();
		Date value = item.getLastLogin();
		if (value != null) 	dates.put(key, value);
	});
	Map<String, TaggedDomUser<DomUser>> ss = s.stream().collect(Collectors.toMap(student -> student.getId().toString(), 
			student -> {
				String extra = "";
				String key = student.getId().getIdString();
				Date date = dates.get(key);
				if (date != null) extra = DATE.format(date);
				return new TaggedDomUser<DomUser>(student, new ArrayList<String>(), extra);
			}, 
			(e1 , e2) -> e1, // drop
			LinkedHashMap::new));
		org.getUsersOfClasses().forEach(t -> {
		String sid = t.getUserId().getIdString();
		List<String> array = ss.get(sid).getMemberOf();
		array.add(t.getClassId().getIdString());
	});
	boolean first = students.isEmpty();
	students.putAll(ss);
	showPersonen(students, RoleType.STUDENT, first, ss.isEmpty());
	if (!ss.isEmpty()) {
		org.setUsers(null);
		org.setUsersOfClasses(null);
		return service.getStudentsInSchool(org).then(this::extractStudents);
	} 
	return null;
}
  
  private Promise<Void> getTeachers0() {
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
  
  private Promise<Void> getTeachers() {
	    view.setLoadingTableMessage();
	    Promise<List<DomTeacherAndHasRole>> p2 = service.getTeachersAndHasRoleInSchool();
	    Promise<?> p1 = getTeachers(schoolClasses.values());
	    
	    return Promises.all(p1,p2).then(
	      x -> {
	        List<DomTeacherAndHasRole> s = p2.getValue();
	        teachers = s.stream().collect(Collectors.toMap(student -> student.getTeacher().
	        				getId().toString(), 
	                                      student -> {
	                          				String extra = "";	                        				
	                        				Date date = student.getHasRole().getLastLogin();
	                        				if (date != null) extra = DATE.format(date);
	                                        return new TaggedDomUser<DomUser>(student.getTeacher(), new ArrayList<String>(), extra);
	                                        
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
    org.setRole(RoleType.STUDENT);
    org.setSort(sort);
    org.setOrder(order);
    org.setLimit(restsize);
    org.setSchoolClasses(schoolClasses.values().stream().map(TaggedDomSchoolClass::getSchoolClass).collect(Collectors.toList()));
    students.clear();
    Promise<DomSchoolOrganisation> p1 = service.getStudentsInSchool(org);
    return p1.then(this::extractStudents)
    	.then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);

  }
  
  
  private Promise<Void> getSchoolAdmins() {
    view.setLoadingTableMessage();
    Promise<List<DomSchoolAdminAndHasRole>> p2 = service.getSchoolAdminsAndHasRoleInSchool();
    return p2.then( p -> {
      List<DomSchoolAdminAndHasRole> s = p.getValue();
      schooladmins = s.stream()
          .filter(admin -> !dwoGlobalVars.getCurrentUser().getId().equals(admin.getSchoolAdmin().getId()))
          .collect(Collectors.toMap( admin -> admin.getSchoolAdmin().getId().toString(), admin -> {
        	    String extra = "";	                        				
				Date date = admin.getHasRole().getLastLogin();
				if (date != null) extra = DATE.format(date);
        	    return new TaggedDomUser<DomUser>(admin.getSchoolAdmin(), Collections.emptyList(), extra);
          }));
      showPersonen(schooladmins, RoleType.SCHOOLADMIN, true, true);
      return null;
    }).then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
    
  }
  private Promise<Void> getSchoolAdmins0() {
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

  void showPersonen(Map<String, TaggedDomUser<DomUser>> students, RoleType role, boolean first, boolean last) {
	  if (role != selectedRole) return;
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

  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  Promise<Boolean> recover(Promise p) {
	  initSchool();
	  return p;
  }
  
  void setSchoolRights(DomSchool s, String rights) {
    DomSchoolFull school = new DomSchoolFull();
    school.setId(s.getId());
    school.setSchoolRights(rights);
    String r = rights;
    service.updateSchool(school)
    .recoverWith(this::recover)
    .then(p->{
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

  private RoleType selectedRole = RoleType.STUDENT;
  @JsMethod void selectRole(String str) {
    selectedRole = RoleType.valueOf(str);
    Promise<Void> p;
    switch(selectedRole) {
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

    if (dwoGlobalVars.isTest()||dwoGlobalVars.isSaml()) { // isSaml voor gebruik in numworx.uu.nl
        
	    RootPanel root = RootPanel.get("organisationpager");
		root.clear();    
		stub = new Stub(eventBus, view);
		pager = stub.getPager();
		root.add(pager);
    
	    stub.addRangeChangeHandler(ev -> { 
	    	LOG.info("range update:" + ev.getNewRange() );    	
	    } );
	    stub.addRowCountChangeHandler(ev -> {
	    	LOG.info("count is = " + ev.getNewRowCount());
	    });
    }
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
		  
		  stub.setFilter(f);
		  stub.setVisibleRange(stub.getVisibleRange());
	  }
  }
  
  
  

static boolean containsIgnoreCase(String source, String regex) {
	  return Stub.containsIgnoreCase(source, regex);	  
  }

@JsMethod
void clickSortButton(String value, String order, String type) {
	if ("extra".equals(value)) value = "lastLogin";
	LOG.info( "value is " + value + ", order = " + order);
	if (this.order.name().equals(order) && this.sort.name().equals(value)) return;
	
	this.order = OrderType.valueOf(order);
	this.sort = SortType.valueOf(value);
	if (stub != null && stub.role == RoleType.STUDENT && students.size() > stub.getPageSize()) {
		students = students.values().stream().sorted(new Comparator<TaggedDomUser<DomUser>>() {

			private String toString(List<String> memberof) {
				StringBuffer sb = new StringBuffer();
				Consumer<? super String> fun = t -> sb.append(t).append(", ");
				try {
					memberof.stream()
						.map ( m -> schoolClasses.get(m).getSchoolClass().getSchoolClassName())
					    .forEach(fun);
				} catch (Exception oops) { // NPE not fatal.
				}
				int l = sb.length();
				if (l >= 2) sb.setLength(l-2); 
				return sb.toString();
			}
			
			
			@Override
			public int compare(TaggedDomUser<DomUser> o1, TaggedDomUser<DomUser> o2) {
				String a, b;
				switch(OrganisationPresenter.this.sort) {
				case lastLogin:
						a = o1.getExtra();
						b = o2.getExtra();
						break;			
				case givenName:
						a = o1.getUser().getGivenName();
						b = o2.getUser().getGivenName();
						break;
				case userName:
						a = o1.getUser().getUserName();
						b = o2.getUser().getUserName();
						break;
				case schoolClassName:
						a = toString(o1.getMemberOf());
						b = toString(o2.getMemberOf());
						break;
				case familyName:
					default:
						a = o1.getUser().getFamilyName();
						b = o2.getUser().getFamilyName();						
				}
				int result = localCompare(a, b);
						// a.compareToIgnoreCase(b);
				if (OrganisationPresenter.this.order == OrderType.desc) result = -result;
				return result;
			}}).collect(Collectors.toMap(t -> t.getUser().getId().getIdString(), Function.identity()
					, (a,b) -> a, LinkedHashMap::new
					));
			stub.personen = students;
			stub.setVisibleRange(stub.getVisibleRange());
	}
}
}
