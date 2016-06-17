/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class TeachersInSchoolClassTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassTeacherPanelProperties.class.getName());

    public TeachersInSchoolClassTeacherPanelProperties() {

    }

    public List<DomTeacher> getTeachersInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomTeacher> classTeachers = SecureTeacherSchoolClassManager.getTeachersInSchoolClass(sc);
        List<DomTeacher> schoolTeachers = SecureTeacherSchoolClassManager.getTeachersInSchool();
        List<DomTeacher> result = new ArrayList<>(schoolTeachers.size() - classTeachers.size());
        for (DomTeacher t : schoolTeachers) {
            Boolean flag = true; //add teacher to result list
            for (DomTeacher c : classTeachers) {
                if (t.getId().equals(c.getId())) {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                result.add(t);
            }
        }
        return result;
    }
    
    public List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.getTeachersInSchoolClass(sc);
    }

    public void removeTeacherFromSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomRemoveTeacherFromSchoolClass submit = new DomRemoveTeacherFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureTeacherSchoolClassManager.removeTeacherFromSchoolClass(submit);
    }
    

    public void submitTeacherToSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureTeacherSchoolClassManager.submitTeacherToSchoolClass(submit);
    }    
        
}
