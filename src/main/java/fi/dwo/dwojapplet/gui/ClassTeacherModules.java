package fi.dwo.dwojapplet.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.DomCoursesOfSchoolclassTree;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

final public class ClassTeacherModules {

  Map<String,MyCourse> cache = new TreeMap<>();
  
  class MyCourse extends Course {

    class MyClassCourse extends ClassCourse {

      private DomClassCourse4Teacher cc;
      public MyClassCourse(DomClassCourse4Teacher cc) {
        this.cc = cc;
        try {
          setClassCourseID(MySQLPersistenceId.getNativeId(cc).intValue());
        } catch (Exception e) {
        }
      }


      @Override
      public int getClassID() {
        return sc.getID();
      }

      @Override
      public int getCourseID() {
        return MyCourse.this.getID();
      }

      @Override
      public int getType() {
        return cc.getType();
      }

      @Override
      public Date getNotBefore() {
        return cc.getNotBefore();
      }

      @Override
      public Date getNotAfter() {
        return cc.getNotAfter();
      }

      //@Override
//      public SchoolClass getSchoolClass() {
//        return sc;
//      }

      //@Override
//      public Course getCourse() {
//        return MyCourse.this;
//      }

      @Override
      public int getViewState() {
        return cc.getViewState().ordinal();
      }

      @Override
      public String getAccessKey() {
        return cc.getAccessKey();
      }

    }

    private DomTree<DomCourseOfClass> node;

    public MyCourse(DomTree<DomCourseOfClass> domTree) {
      this.node = domTree;     
      setDomCourse(domTree.getObject().getCourse());
      resetParent();
      DomClassCourse4Teacher cc = domTree.getObject().getClassCourse();
      if(cc != null) {
        this.link = new MyClassCourse(cc);
      }
    }

    @Override
    public CourseMap getParentMap() {
      CourseMap parent = super.getParentMap();
      if(parent == null && node.getObject().getCourse().getParentID() != null) {
        DomTree domParent = node.getParent();
// BUG, kan be null
        if(domParent == null) {
          domParent = csct.getNode(node.getObject().getCourse().getId().getIdString());
          node.setParent(domParent);
        }
        parent = toMyCourse(domParent);
        setParentMap(parent);
      }
      return parent;
    }

    public String getKey() {
      return node.getObject().getCourse().getId().getIdString();
    }

    @Override
    public CourseMap[] getChildren() {
      if(super.getChildren() == NO_CHILDREN)
      {
        MyCourse[] children = node.getChildren().values().stream().map(ClassTeacherModules.this::toMyCourse).sorted().collect(Collectors.toList()).toArray(NULL);
        setChildren(children);
      }
      return super.getChildren();
    }

  }
  private MyCourse NULL[] = new MyCourse[0];
  
  private static Logger LOG = Logger.getLogger(ClassTeacherModules.class.getName());

  private SchoolClass sc = null;

  private DomCoursesOfSchoolclassTree csct;
  
  ClassTeacherModules() {
  }

  void edit(DomSchoolClass schoolClass, ClassTeacherPanel parent) {
    MyCourse[] allCourses = null;
    MyCourse[] selectedSchoolCourses = null;
    Promise<List> rc = Promises.failed(new IllegalArgumentException());
    try {
      GuiCreator.instance().getDWO().setWait();
      DomCoursesOfSchoolClass4Teacher csct0 = SecureTeacherSchoolClassManager.getModules(schoolClass, DWO.getDwoProfile());
      rc = Promises.resolved(PersistenceFacade.instance().getResultCount(csct0));
      DomSchool school = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool();
      csct = new DomCoursesOfSchoolclassTree(school, csct0);
      DomTree<DomCourseOfClass> root = csct.getCourseTree();
      Collection<DomTree<DomCourseOfClass>> children0 = root.getChildren().values(); //public, school
      List<DomTree<DomCourseOfClass>> children1 = new ArrayList<>();
      children0.forEach(child -> children1.addAll(child.getChildren().values()));
      allCourses = new MyCourse[children1.size()];
      for (int i = 0; i < allCourses.length; i++) {
        DomTree<DomCourseOfClass> child = children1.get(i);
        child.getObject().getCourse().setParentID(null); // toplevel 
        allCourses[i] = new MyCourse(child);
        String key = allCourses[i].getKey();
        cache.putIfAbsent(key, allCourses[i]);
      }
      selectedSchoolCourses = csct0.getClassCourses().stream()
      .map(DomMapEntry::getValue)
      .filter(v -> v.getViewState() == ViewState.studentsAndTeachers)
      .map(v->csct.getNode(v.getCourseId().getIdString()))
      .filter(v->!v.getObject().getCourse().getWithChildren().booleanValue())
      .map(this::toMyCourse)
      .collect(Collectors.toList())
      .toArray(NULL);
      sc = PersistenceFacade.instance().toSchoolClass(Collections.singleton(schoolClass))[0];
    } catch (Dwo2Exception ex) {
        LOG.log(Level.SEVERE, null, ex);
    } catch (PersistenceException ex) {
        LOG.log(Level.SEVERE, null, ex);
    } finally {
        GuiCreator.instance().getDWO().setReady();
    }
    Course[] selectedCourses = SelectCoursesDialog.selectCourses(parent, allCourses, selectedSchoolCourses, sc, rc);
    if (selectedCourses != null) {
        GuiCreator.instance().getDWO().setWait();
        try {
            //sc.saveSelectedCourses(allCourses, selectedCourses);
            saveSelectedCourses(schoolClass, selectedSchoolCourses, selectedCourses);
        } finally {
            GuiCreator.instance().getDWO().setReady();
        }
    }
  }

  private MyCourse toMyCourse(DomTree<DomCourseOfClass> v) {
    String key = v.getObject().getCourse().getId().getIdString();
    MyCourse c = cache.get(key);
    if(c == null) { c = new MyCourse(v); cache.put(key, c); }
    return c;
  }

  // XXX uitzoeken hoe we 4 setXXX calls kunnen vervangen door 1 update
  private void saveSelectedCourses(DomSchoolClass schoolClass, MyCourse[] oldselected,
                                   Course[] selected) {
                                 // TODO attach selected & detach oldselected
                                 DomSchoolClassCourseProfilewAccessKey key = new DomSchoolClassCourseProfilewAccessKey();
                                 DomSchoolClassCourseAndProfile dom;
                                 dom = key;
                                 dom.setDomSchoolClass(schoolClass);
                                 dom.setDomDwoProfile(DWO.getDwoProfile());
                                 List<Course> selectedList = Arrays.asList(selected);
                                 for(MyCourse c : oldselected) {
                                   while(c != null && ! selectedList.contains(c) && c.getDwoProfile() == DWO.getDwoProfileID() && !c.isWithChildren()) {
                                       DomCourse domcourse = toDomCourse(c);
                                       dom.setCourse(domcourse);
                                       try {
                                         SecureTeacherSchoolClassManager.detachCourseFromClass(dom);
                                         int parent = c.getParentID();
                                         if (parent == 0) break;
                                         c = //(Course) PersistenceFacade.instance().get(parent, Course.class);
                                             cache.get(domcourse.getParentID().getIdString());
                                       } catch (Dwo2Exception e) {
                                         LOG.log(Level.SEVERE, "detach", e);
                                         return;
                                       }
                                   }
                                 }
                                 DomSchoolClassCourseProfilewFrom from = new DomSchoolClassCourseProfilewFrom();
                                 from.setDomDwoProfile(DWO.getDwoProfile());
                                 from.setDomSchoolClass(schoolClass);
                                 DomSchoolClassCourseProfilewTo to = new DomSchoolClassCourseProfilewTo();
                                 to.setDomDwoProfile(DWO.getDwoProfile());
                                 to.setDomSchoolClass(schoolClass);

                                 DomSchoolClassCourseProfilewType type = new DomSchoolClassCourseProfilewType();
                                 type.setDomDwoProfile(DWO.getDwoProfile());
                                 type.setDomSchoolClass(schoolClass);
                                 
                                 DomClassCourseFull ccfull = new DomClassCourseFull();
                                 ccfull.setClassId(schoolClass.getId());
                                 
                                 selectedList = Arrays.asList(oldselected);
                                 for(Course c: selected) {
                                   if(c.link == null) 
                                     c.link = new ClassCourse();
                                   DomCourse domcourse = toDomCourse(c);
                                   ccfull.setCourseId(domcourse.getId());
                                   if (c.link.getClassCourseID() != 0)
                                     ccfull.setId(PersistentClassCourse.buildPersistenceId(Long.valueOf(c.link.getClassCourseID())));
                                   else 
                                     ccfull.setId(null);
                                   
                                   if (!selectedList.contains(c)) {
                                     try {
                                       dom.setCourse(domcourse);
                                       SecureTeacherSchoolClassManager.attachCourseToClass(dom); // Prepare attach
                                     } catch (Dwo2Exception e) {
                                       LOG.log(Level.SEVERE, "attach", e);
                                       return;
                                     }
                                   }

                                   from.setCourse(domcourse);
                                   from.setFrom(c.link.getNotBefore());
                                   ccfull.setNotBefore(c.link.getNotBefore());
                                   try {
                                     SecureTeacherSchoolClassManager.setFromDataClassCourse(from);
                                   } catch (Dwo2Exception e) {
                                     LOG.log(Level.SEVERE, "from", e);
                                     return;
                                   }

                                   to.setCourse(domcourse);
                                   to.setTo(c.link.getNotAfter());
                                   ccfull.setNotAfter(c.link.getNotAfter());
                                   try {
                                     SecureTeacherSchoolClassManager.setToDataClassCourse(to);
                                   } catch (Dwo2Exception e) {
                                     LOG.log(Level.SEVERE, "to", e);
                                     return;
                                   }
                                   type.setCourse(domcourse);
                                   type.setType(CourseType.values()[c.link.getType()]);
                                   ccfull.setCourseType(type.getType());
                                   try {
                                     SecureTeacherSchoolClassManager.setClassCourseType(type);
                                   } catch (Dwo2Exception e) {
                                     LOG.log(Level.SEVERE, "type", e);
                                     return;
                                   }
                                   key.setCourse(domcourse);
                                   key.setAccessKey(c.link.getAccessKey());
                                   ccfull.setAccessKey(key.getAccessKey());
                                   try {
                                     SecureTeacherSchoolClassManager.setAccessKeyClassCourse(key);
                                   } catch (Dwo2Exception e) {
                                     LOG.log(Level.SEVERE, "key", e);
                                     return;
                                   }
                                  
                                   if (ccfull.getId() != null && false) {
                                     try {
                                       SecureTeacherSchoolClassManager.updateClassCourse(ccfull);
                                     } catch (Dwo2Exception e) {
                                       LOG.log(Level.SEVERE, "update", e);

                                     }
                                   }
                                   
                                   
                                   if (!selectedList.contains(c)) {
                                     try {
                                       SecureTeacherSchoolClassManager.attachCourseToClass(dom); // commit attach
                                     } catch (Dwo2Exception e) {
                                       LOG.log(Level.SEVERE, "attach", e);
                                       return;
                                     }
                                   }
                                  
                                   
                                 }
                                 
                                 
                               }

  private static DomCourse toDomCourse(Course c) {
    if(c instanceof MyCourse) 
      return toDomCourse( (MyCourse) c);
    DomCourse result = new DomCourse();
    result.setId(PersistentCourse.buildPersistenceId(Long.valueOf(c.getID())));
    result.setName(c.getName());
    result.setWithChildren(c.isWithChildren());
    result.setParentID(PersistentCourse.buildPersistenceId(Long.valueOf(c.getParentID())));
    
    return result;
  }

  private static DomCourse toDomCourse(MyCourse c) {
    return c.node.getObject().getCourse();
  }
}
