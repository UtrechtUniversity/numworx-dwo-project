package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.PagingView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.Stub;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.OrderType;
import nl.uu.fi.dwo.rest.dom.entities.util.SortType;

public class PersonsPresenterSchoolAdmin extends PersonsPresenter {
  private static final Logger LOG = Logger.getLogger(PersonsPresenterSchoolAdmin.class.getName());
  private final LoggingFailure FAILURE;
  private Stub<DomUser> stub;
  private PersonsServiceSchoolAdmin service;

  @Inject PersonsPresenterSchoolAdmin(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsServiceSchoolAdmin manager) {
    super(anEventBus, aDwoGlobalVars, manager);
    this.service = manager;
    FAILURE = new LoggingFailure(LOG, anEventBus);
  }

  List<DomUser> studentToUser(List<DomStudent>list) {
    return new ArrayList<>(list);
  }
  List<DomUser> teacherToUser(List<DomTeacher>list) {
    return new ArrayList<>(list);
  }
  
  
  @Override
public void setView(Display view) {
	super.setView(view);
	
	if (dwoGlobalVars.isTest()) {
		PagingView<DomUser> pagingview = new PagingView<DomUser>() {

			@Override
			public void showPersonen(Map<String, DomUser> collect, RoleType role) {
				view.showPersonen(collect);
			}
			
		};
		stub = new Stub<DomUser>(eventBus, pagingview);
		RootPanel root = RootPanel.get("personpager");
		root.clear();
		root.add(stub.getPager());
	}
}
  
  long restsize = 100;


@Override @JsMethod
  public void showStudentList() {
    view.setLoadingTableMessage();
    
    if (stub != null) {
        DomSchoolOrganisation org = new DomSchoolOrganisation();
        org.setRole(RoleType.STUDENT);
        stub.setRole(RoleType.STUDENT);
        org.setSort(sort);
        org.setOrder(order);
        org.setLimit(restsize);
        org.setSchoolClasses(Collections.emptyList());
        org.setUsersOfClasses(Collections.emptyList());
        personen = new LinkedHashMap<String, DomUser>();
        Promise<DomSchoolOrganisation> p1 = service.getStudentsInSchool(org);
        p1.then(this::extractStudents)
        	.then( null, failed -> view.setEmptyTableMessage() ). then( null, FAILURE);
    	return;
    }
    
    Promise<List<DomUser>> promise;
    promise = manager.getTeachersStudents().map(this::studentToUser);
    updateView(promise);
  }

	protected Promise<Object> extractStudents(Promise<DomSchoolOrganisation> p0) {
		DomSchoolOrganisation org = p0.getValue();
		if (org.getRole() != stub.role) return null;
		List<DomUser> s = org.getUsers();
		boolean empty = personen.isEmpty();
		s.forEach(item -> personen.put(item.getId().getIdString(), item));
		boolean last = s.size() < restsize;
		stub.limit(personen, empty, last);
		if (!last) {
			org.setUsers(null);
			org.setHasRoles(null);
			return service.getStudentsInSchool(org).then(this::extractStudents);
		}
		return null;
	}

  @Override  @JsMethod
  public void showTeacherList() {
    view.setLoadingTableMessage();
    if (stub != null) stub.setRole(RoleType.TEACHER);
    Promise<List<DomUser>> promise;
    promise = manager.getTeachersInSchool().map(this::teacherToUser);
    updateView(promise);
  }


  void updateView(Promise<List<DomUser>> promise) {
    // onSuccess update view
    promise.then( resolved -> {
            personen = new HashMap<>(resolved.getValue().size());
            resolved.getValue().forEach((k -> personen.put(k.getId().getIdString(), k)));
            if (stub != null)
            	stub.limit(personen, true, true);
            else
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

  @Override @JsMethod
  public void filterPersons(String username, String givenName, String insertion, String familyName) {
	  super.filterPersons(username, givenName, insertion, familyName);
	  if (stub != null) {
		  List <Predicate<Entry<String, DomUser>>> f = new ArrayList<>();
		  if (username != null && !username.isEmpty())
		  {		  
			  f.add( t -> Stub.containsIgnoreCase(t.getValue().getUserName(), username));
		  }
		  if (givenName != null && !givenName.isEmpty())
		  {
			  f.add(t -> Stub.containsIgnoreCase(t.getValue().getGivenName(), givenName));
		  }
		  if (familyName != null && !familyName.isEmpty())
		  {
			  f.add(t -> Stub.containsIgnoreCase(t.getValue().getFamilyName(), familyName));
		  }
		  if (insertion != null && !insertion.isEmpty())
		  {
			  f.add(t -> Stub.containsIgnoreCase(t.getValue().getInsertion(), insertion));
		  }
		  
		  stub.setFilter(f);
	  }
 }

@Override @JsMethod
public void clickSortButton(String value, String order, String type) {
	super.clickSortButton(value, order, type);
	if (stub != null && stub.personen.size() > stub.getPageSize()) {
		stub.personen = stub.personen.values().stream().sorted(PERSON_COMPARATOR)
				.collect(Collectors.toMap(t -> t.getId().getIdString(), Function.identity()
						, (a,b) -> a, LinkedHashMap::new));
		stub.setVisibleRange(stub.getVisibleRange());

	}
}
  
}
