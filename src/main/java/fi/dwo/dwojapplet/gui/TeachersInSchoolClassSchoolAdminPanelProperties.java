/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class TeachersInSchoolClassSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassSchoolAdminPanelProperties.class.getName());

    public TeachersInSchoolClassSchoolAdminPanelProperties() {

    }

    public List<DomTeacher> getTeachersInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomTeacher> classTeachers = SecureSchoolAdminSchoolClassManager.getTeachersInSchoolClass(sc);
        List<DomTeacher> schoolTeachers = SecureSchoolAdminSchoolClassManager.getTeachersInSchool();
        List<DomTeacher> result = new ArrayList<>(Math.max(0, schoolTeachers.size() - classTeachers.size()));
        for (DomTeacher t : schoolTeachers) {
            Boolean flag = true; //add teacher to result list
            for (DomTeacher c : classTeachers) {
                if (t.getId().equals(c.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                result.add(t);
            }
        }
        return result;
    }

    public List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getTeachersInSchoolClass(sc);
    }

    public List<DomStudent> getStudentsInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getStudentsInSchoolClass(sc);
    }

    public void removeTeacherFromSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomRemoveTeacherFromSchoolClass submit = new DomRemoveTeacherFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureSchoolAdminSchoolClassManager.removeTeacherFromSchoolClass(submit);
    }

    public void submitTeacherToSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureSchoolAdminSchoolClassManager.submitTeacherToSchoolClass(submit);
    }

    public void submitStudentToSchoolClass(DomSchoolClass sc, DomStudent t) throws Dwo2Exception {
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassTo(sc);
        submit.setStudent(t);
        SecureSchoolAdminSchoolClassManager.submitStudentToSchoolClass(submit);
    }

}
