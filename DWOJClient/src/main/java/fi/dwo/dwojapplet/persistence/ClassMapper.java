// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ClassMapper.java
package fi.dwo.dwojapplet.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

class ClassMapper {
    
    private final Map<Integer, SchoolClass> objects;

    /**
     *
     */
    ClassMapper() {
      objects = new HashMap<>();
    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     *
     */
    void put(int oid, SchoolClass obj) {
        objects.put(oid, obj);
    }

    SchoolClass[] getFromSchool() {
      if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.SCHOOLADMIN.name())) {
          try {
              List<DomSchoolClass> list = SecureSchoolAdminSchoolClassManager.getSchoolClasses();
              return toSchoolClasses(list);
          } catch (Dwo2Exception ex) {
              Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
          }
      } else if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName().equals(RoleType.TEACHER.name())) {
          try {
              List<DomSchoolClass> list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
              return toSchoolClasses(list);
          } catch (Dwo2Exception ex) {
              Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
          }
      }
      return null;
    }

    SchoolClass[] getFromTeacher() {
      try {
          // assert obj is to be DwoHelper.getCurrentFacadeUser();
          List<DomSchoolClass> list = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
          return toSchoolClasses(list);

      } catch (Dwo2Exception ex) {
          Logger.getLogger(ClassMapper.class.getName()).log(Level.SEVERE, "get", ex);
          return null;
      }
    }

    SchoolClass[] toSchoolClasses(List<DomSchoolClass> list) throws Dwo2Exception {
        SchoolClass[] array = createArray(list.size());
        for (int i = 0; i < array.length; i++) {
            DomSchoolClass item = list.get(i);
            int id = MySQLPersistenceId.getNativeId(item).intValue();
            SchoolClass cls = objects.get(id);
            if (cls == null) {
                cls = new SchoolClass();
            }
            cls.setDomSchoolClass(item);
            objects.put(id, cls);
            array[i] = cls;
        }
        return array;
    }


    SchoolClass[] createArray(int size) {
        return new SchoolClass[size];
    }

}
