package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class AccessControlAction extends GuiAction {

  Object course;
  private DomSchool school = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool();
  
  AccessControlAction() {
    super(TextMapper.getText(TextMapper.GUIAC_ACCESS));
  }

  public AccessControlAction(CourseManagementPanel courseManagementPanel) {
    this();
    setMap(courseManagementPanel.getUserObject());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      if (course instanceof Course) {
        List<DomSchoolClass> classes;
        List<DomTeacher> teachers;
        Course c = (Course) course;
        
        if (DwoHelper.isContact()) {
          classes = SecureSchoolAdminSchoolClassManager.getSchoolClasses();
          teachers = SecureSchoolAdminSchoolClassManager.getTeachersInSchool();       
        } else if (!DwoHelper.isAdminLoggedIn()) {
          classes = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
          teachers = SecureTeacherSchoolClassManager.getTeachersInSchool();        
        } else {
          classes = Collections.emptyList();
          teachers = Collections.emptyList();
        }
        List<DomACL> acls = c.getAcls(); 
        if (acls == null) acls = Collections.emptyList();
        
        AccessControlPanel panel = new AccessControlPanel(acls, teachers, classes, school);
        int ok = JOptionPane.showConfirmDialog(getCenter(), panel, e.getActionCommand(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
          acls = panel.getAcls();
          DomCourseFull edit = new DomCourseFull();
          edit.setId(PersistentCourse.buildPersistenceId((long) c.getID()));
          edit.setDwoProfileId(DWO.getDwoProfile().getId());
          edit.setSchoolId(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getId());
          edit.setAcls(acls);
          edit.setNotVisible(c.isNotVisible()); // Moet
          instance().getCourseManager().update(edit);
          c.setAcls(acls);
        }
      }
    } catch (Dwo2Exception e1) {
        instance().ShowErrorDialog(getCenter(), e1);
    }

  }

  public void setMap(Object map) {
    course = map;
    setEnabled(course instanceof Course && 
      instance().getUser().hasRight(User.ACCESS_RIGHT) &&
      ((Course)course).getSchoolID() == DwoHelper.getActiveSchoolId() &&
      hasFullAccess( (Course)course)
      );
  }
  
  private boolean hasFullAccess(Course c) {
    if (isSchoolAdmin()) return true;
    Set<PersistenceId> set = new HashSet<>();
    set.add(DwoHelper.getCurrentUser().getId());
    set.add(school.getId());
    try {
      List<DomSchoolClass> classes;
      classes = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
      classes.forEach(cl -> set.add(cl.getId()));
    } catch (Dwo2Exception e) {
    }
    List<DomACL> acls = c.getAcls();
    while ( (acls == null||acls.isEmpty()) && c != null) {
      CourseMap m = c.getParentMap();
      if (m instanceof Course) {
        c = (Course)m;
        acls = c.getAcls();
      } else {
        c = null;
        acls = null;
      }
    }
    if (acls == null||acls.isEmpty()) return instance().getUser().hasRight(User.MODIFY_MODULES_RIGHT);
    return acls.stream().filter(a -> a.getAccess() == ACL.FULL).anyMatch(a -> set.contains(a.getEntity()));
  }

  private boolean isSchoolAdmin() {
    return RoleType.SCHOOLADMIN.name() .equals 
    (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName());
  }

  @Override
  public void setMap(CourseMap map) {
    setMap( (Object) map);
  }

}
