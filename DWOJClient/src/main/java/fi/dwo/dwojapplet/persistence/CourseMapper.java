// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\CourseMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserCourseManager;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class CourseMapper  implements Comparator<Course> {
    private static final Logger LOG = Logger.getLogger(CourseMapper.class.getName());
    private static final Course[] NO_ACCESS = new Course[0];

    private Map<Integer, Course> objects = new HashMap<>();

    private Object key;

    /**
     *
     */
    CourseMapper() {

    }

    /**
     * Put a new object into the cache... NOT into the DATABASE
     *
     * @param oid
     * @param obj
     * @throws java.io.IOException
     *
     */
    void put(int oid, Course obj)  {
        objects.put(new Integer(oid), obj);
    }

    class LazyCourse extends Course {

        private boolean loaded;

        public void setChildren(CourseMap[] children) {
            loaded = children != NO_CHILDREN;
            super.setChildren(children);
        }

        /* (non-Javadoc)
         * @see fi.dwo.client.domain.Course#getChildren()
         */
        public CourseMap[] getChildren() {
            if (!loaded) {
                try {
                    setChildren(getFromCourse(this));
                } catch (Exception e) {
    
                    LOG.log(Level.SEVERE,null,e);
                }
            }
            return super.getChildren();
        }

    }

    

    private ACL effectiveAccess(Course course) {
      if (course.getSchoolID() == 0) {
        if (hasAdminRight()) return ACL.WRITE;
        return ACL.READ; // FIXED write if profileadmin/dwoadmin
      }
      RoleType role = getRoleType();
      switch(role) {
        case STUDENT: return ACL.READ; // oe ACL.NONE if not in classcourse
        case SCHOOLADMIN: return ACL.FULL; // or ACL.WRITE if no Access
        case ADMIN: return ACL.WRITE;
        case TEACHER: {
          boolean parent = false;
          List<DomACL> acls = course.getAcls();
          Course c = course;
          while ( (acls == null||acls.isEmpty()) && c != null) {
            parent = true;
            CourseMap m = c.getParentMap();
            if (m==null) { m = objects.get(c.getParentID()); }
            if (m instanceof Course) {
              c = (Course)m; acls = c.getAcls();
            } else {
              c = null; acls = null;
            }
          }
          if (acls == null) { 
            if (DwoHelper.getCurrentFacadeUser().hasRight(User.ACCESS_RIGHT) && !DwoHelper.getCurrentFacadeUser().hasRight(User.MODIFY_MODULES_RIGHT)) return ACL.NONE;
            return ACL.FULL; // Of read // of Write
          } else {
            Set<PersistenceId> ids = getIds();
            Comparator<ACL> sorter = this::compare;
            Optional<ACL> opt = acls.stream().filter(a -> ids.contains(a.getEntity())).map(DomACL::getAccess).sorted(sorter).findFirst();
            ACL acl = opt.orElse(ACL.NONE);
            if (acl == ACL.ACCESS && parent) return ACL.NONE; 
            return acl;
          }
        }
      }    
      return ACL.NONE;
    }

    @SuppressWarnings("deprecation")
    private boolean hasAdminRight() {
      return DwoHelper.isAdminLoggedIn() || DwoHelper.getCurrentFacadeUser().hasRight(User.PROFILE_ADMIN_RIGHT);
    }

    int compare(ACL a, ACL b) {
      return -a.compareTo(b);
    }
    
    private Set<PersistenceId> getIds() {
      Set<PersistenceId> set = new HashSet<>();
      set.add(DwoHelper.getCurrentUser().getId());
      set.add(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getId());
      Teacher t = (Teacher) DwoHelper.getCurrentFacadeUser();
      SchoolClass[] classes = t.getClasses();
      for(SchoolClass c: classes) {
        long id = c.getID();
        set.add(PersistentSchoolClass.buildPersistenceId(id));
      }
      return set;
    }

    private RoleType getRoleType() {
      try {
        return RoleType.valueOf(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName());
      } catch(Exception t) {
      }
      return RoleType.NONE;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#get(int)
     */
    Course get(int oid) throws PersistenceException {
        Integer Oid = new Integer(oid);
        Course result = objects.get(Oid);
        if (result != null) {
            return result;
        }
        DomCourseStudent s = new DomCourseStudent();
        s.setId(PersistentCourse.buildPersistenceId(Long.valueOf(oid)));
        try {
          if (DwoHelper.getCurrentUser() != null)
            s = SecureUserCourseManager.getCourse(s, DWO.getDwoProfile());
          else
            s = PublicCourseManager.getCourse(s, DWO.getDwoProfile());
        } catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        Course object = getObjectFromReturn(s);
        if (object != null) 
        	put(oid, object);
		return object;
    }

    

    ////peter


    
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    Course[] createArray(int size) {
        return new Course[size];
    }
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */


    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#removeAllObjects()
     */
    void removeAllObjects() {
        objects.clear();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#removeObject(int)
     */
    void removeObject(int key) {
        objects.remove(key);
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getObjectFromReturn(java.util.Vector)
     */
    Course[] getObjectFromReturn(List<? extends DomCourse> org) throws PersistenceException {
      if(!org.isEmpty() && org.get(0) instanceof DomCourseStudent)
      {
        return getObjectFromDCS((List<DomCourseStudent>) org);
      }
      if(!org.isEmpty() && org.get(0) instanceof DomCourse)
      {
        Course[] result = createArray(org.size());
        for (int i = 0; i < result.length; i++) {
          result[i] = getObjectFromReturn( (DomCourse) org.get(i));
        }
        Arrays.sort(result,this);
        return result;
      }
      return NO_ACCESS;
    }

    private Course[] getObjectFromDCS(List<DomCourseStudent> data) {
      Course[] result = createArray(data.size());
      int i = 0;
      for(DomCourseStudent item: data) {
        Course cource = getObjectFromReturn(item);
        if( effectiveAccess(cource) != ACL.NONE)
          result[i++] = cource;
      }
      if (i != data.size()) {
        Course[] r = createArray(i);
        System.arraycopy(result, 0, r, 0, i);
        result = r;
      }
      //Arrays.sort(result,this);
      return result;
    }
    
    private Course getObjectFromReturn(DomCourseStudent data) {
      int id = PersistenceFacade.idOf(data.getId());
      Course c = objects.get(Integer.valueOf(id));
      if (c == null) {
          c = new LazyCourse();
      }
      c.setDomCourseStudent(data);
      if(c.isWithChildren() && ! (c instanceof LazyCourse)) {
        // prefetch children
        try {
          c.setChildren(getFromCourse(c)); // Not Lazy, .... jammer dan.
      } catch (Exception e) {
          c.setChildren(Course.NO_CHILDREN);
      }
      }
      objects.putIfAbsent(Integer.valueOf(id), c);
      return c;
    }
 
    Course getObjectFromReturn(DomCourse data) {
      int id = PersistenceFacade.idOf(data.getId());
      Course c = objects.get(Integer.valueOf(id));
      if (c == null) {
          c = new LazyCourse();
      }
      c.setDomCourse(data);
      if(c.isWithChildren() && ! (c instanceof LazyCourse)) {
        // prefetch children
        try {
          c.setChildren(getFromCourse(c)); // Not Lazy, .... jammer dan.
      } catch (Exception e) {
          c.setChildren(Course.NO_CHILDREN);
      }
      }
      objects.putIfAbsent(Integer.valueOf(id), c);
      return c;
    }

    @Override
    public int compare(Course o1, Course o2) {
      if(o1.sequencenr != null && o2.sequencenr != null) {
        int sort = o1.sequencenr.compareTo(o2.sequencenr);
        if(sort == 0)
          return o1.getName().compareTo(o2.getName());
        return sort;
      }
      if(o1.sequencenr == null) return +1;
      if(o2.sequencenr == null) return -1;
      return o1.getName().compareTo(o2.getName());
    }
    
    Map<PersistenceId, DomCourse> insertCache(List<DomMapEntry<PersistenceId, DomCourse>> all) {
      Map<PersistenceId, DomCourse> allcourses = new HashMap<>();
      all.forEach(e -> allcourses.put(e.getKey(), e.getValue()));
//      Map<PersistenceId, Set<PersistenceId>> children = new HashMap<>();
//      allcourses.values().forEach(course -> {
//        PersistenceId parent = course.getParentID();
//        Set<PersistenceId> set = children.get(parent);
//        if(set == null) {
//          set = new HashSet<>();
//          children.put(parent,  set);
//        }
//        set.add(course.getId());
//      });
      return allcourses;

      
      
    }

    Course[] getFromSchool(School parent) throws PersistenceException {
      // must be your own school....
      try {
        return getObjectFromDCS((SecureUserCourseManager.getCoursesSchool(DWO.getDwoProfile())));
      } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "getCourses of course", e);
      }
      return NO_ACCESS;
    }

    Course[] getFromCourse(Course course) throws PersistenceException {
      if(course.getSchoolID() == 0 && DwoHelper.getCurrentUser()==null) {
        DomCourse domcourse = new DomCourse();
        domcourse.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
        try {
          return getObjectFromDCS((PublicCourseManager.getCourses(domcourse, DWO.getDwoProfile())));
        } catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, "getCourses of course", e);
        }
      } else if (DwoHelper.getCurrentUser() != null) {
        DomCourse domcourse = new DomCourse();
        domcourse.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
        try {
            if (effectiveAccess(course) != ACL.NONE)
                return getObjectFromDCS((SecureUserCourseManager.getCourses(domcourse, DWO.getDwoProfile())));
            return NO_ACCESS;
     } catch (Dwo2Exception e) {
          LOG.log(Level.SEVERE, "getCourses of course", e);
        }
      }
      return NO_ACCESS;
    }

    public CourseMap[] getTrash(Course course) {
      DomCourse dom = new DomCourse();
      dom.setId(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
      try {
        if (effectiveAccess(course).ordinal() >= ACL.WRITE.ordinal()) 
          return getObjectFromDCS(SecureUserCourseManager.getTrash(dom, DWO.getDwoProfile()));
      } catch(Dwo2Exception e) {
        LOG.log(Level.SEVERE, "getTrash of course", e);
      }
      return NO_ACCESS;
    }

    public CourseMap[] getTrashSchool() {
      try {
        return getObjectFromDCS(SecureUserCourseManager.getTrash(DWO.getDwoProfile()));
      } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "getTrash of school", e); 
      }
      return NO_ACCESS;
    }

    public CourseMap[] getTrashRoot() {
      try {
        return getObjectFromDCS(SecureUserCourseManager.getTrashRoot(DWO.getDwoProfile()));
      } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "getTrash of root", e); 
      }
      return NO_ACCESS;
    }
}
