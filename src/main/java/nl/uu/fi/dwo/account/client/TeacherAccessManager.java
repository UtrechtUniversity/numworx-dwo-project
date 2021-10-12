package nl.uu.fi.dwo.account.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;
import org.osgi.util.function.Function;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserCourseManager;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class TeacherAccessManager extends AccessManager {

  Promise<Set<PersistenceId>> ids;
  private PersistenceId teacher;
  private PersistenceId school;
  private Promise<? extends DomDwoProfile> profile;
  
  interface ParentAccess extends Function<DomCourseStudent, Promise<DomCourseStudent>> {
    void cache(List<DomCourseStudent> list );
  }
   
  final ParentAccess getParent;
  private Promise<Boolean> root;
  private Promise<DomCourseStudent> NULL = Promises.resolved(null);
  class DefaultGetParent implements ParentAccess {

    SecuredUserCourseManager securedUserCourseManager = new SecuredUserCourseManager();

    Map<PersistenceId, Promise<DomCourseStudent>> map = new HashMap<>();
    
    
    DefaultGetParent() {
      map.put(null, NULL);
    }

    @Override
    public Promise<DomCourseStudent> apply(DomCourseStudent t) {
      Promise<DomCourseStudent> r = map.get(t.getParentID());
      if (r != null) return r;
      DomCourse course = new DomCourse(t.getParentID());
      r = securedUserCourseManager.getCourse(course, profile.getValue(), null, context);
      map.putIfAbsent(course.getId(), r);
      return r;
    }

    @Override
    public void cache(List<DomCourseStudent> list) {
      for(DomCourseStudent e: list) {
        map.put(e.getId(), Promises.resolved(e));
      }      
    }
    
  }
  
  public TeacherAccessManager(DomSchoolRoleAndClassV2 dom, ParentAccess getParent, Promise<? extends DomDwoProfile> profile) {
    this.profile = profile;
    context = new DomContext();
    context.setDomHasRole(dom.getHasRole());
    
    this.teacher = context.getDomHasRole().getUserId();
    this.school  = dom.getSchool().getId();
    boolean canWrite = dom.getSchool().teachersCanWrite();
    this.getParent = getParent == null ? new DefaultGetParent() : getParent;
    this.root = canWrite ? TRUE : FALSE;
    ids = new SecuredTeacherSchoolClassManager().getTeachersSchoolClasses(context).fallbackTo(Promises.resolved(Collections.emptyList())).map(this::mapper);
  }

  private Set<PersistenceId> mapper(List<? extends DomSchoolClass> list) {
    Set<PersistenceId> set = list.stream().map(DomSchoolClass::getId).collect(Collectors.toSet());
    set.add(teacher);
    set.add(school);
    return set;    
  }

  private static final Promise<Boolean> FALSE = Promises.resolved(Boolean.FALSE);
  private DomContext context;
  class Checker implements Success<Set<PersistenceId>, Boolean> {
    DomCourseStudent course;
    final boolean parent;
    
    Checker(DomCourseStudent course, boolean parent) {
      this.course = course;
      this.parent = parent;
    }

    @Override
    public Promise<Boolean> call(Promise<Set<PersistenceId>> resolved) throws Exception {
      if (course == null) return root;
      List<DomACL> acls = course.getAcls();
      if( (acls == null||acls.isEmpty())) {
        return getParent.apply(course).then(p -> new Checker(p.getValue(), true).call(ids));
      }      
      Comparator<ACL> comparator = (a,b) -> -a.compareTo(b);
      ACL max = acls.stream().filter(t -> ids.getValue().contains(t.getEntity())).map(DomACL::getAccess).
          sorted(comparator).findFirst().orElse(ACL.NONE);
      if (max == ACL.NONE || (max==ACL.ACCESS && parent))
        return FALSE;
      return TRUE;
    }
  }

  @Override
  Promise<Boolean> access(DomCourseStudent course) {
    return ids.then(new Checker(course, false)).recoverWith(oops -> super.access(course));
  }

  @Override
  public List<DomCourseStudent> apply(List<DomCourseStudent> t) {
    getParent.cache(t);
    return super.apply(t);
  }

  @Override
  public Promise<List<DomCourseStudent>> call(Promise<List<DomCourseStudent>> resolved)
      throws Exception {
    List<DomCourseStudent> t = apply(resolved.getValue());
    if (t.isEmpty()) return resolved;
    if (t.get(0).getSchoolId() == null) return resolved; // public courses
    List<Promise<Boolean>> access;
    access = t.stream().map(this::access).collect(Collectors.toList());
    return Promises.all(access).then(p -> {
      List<DomCourseStudent> list = new ArrayList<>(t);
      Iterator<DomCourseStudent> iters = list.iterator(); 
      Iterator<Promise<Boolean>> itersb = access.iterator();
      while (iters.hasNext()) {
        iters.next();
        Promise<java.lang.Boolean> promise = itersb.next();
        if (! promise.getValue().booleanValue()) iters.remove();       
      }     
      return Promises.resolved(list);
    });
    
    //return resolved;
  }
  
  
}
