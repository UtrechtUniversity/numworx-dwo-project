/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class TeachersInSchoolClassAddTeacherTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassAddTeacherTeacherPanelProperties.class.getName());

    public TeachersInSchoolClassAddTeacherTeacherPanelProperties() {

    }
    
    public List<DomTeacher> getTeachersInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomTeacher> classTeachers = SecureTeacherSchoolClassManager.getTeachersInSchoolClass(sc);
        List<DomTeacher> schoolTeachers = SecureTeacherSchoolClassManager.getTeachersInSchool();
        List<DomTeacher> result = new ArrayList<DomTeacher>(schoolTeachers.size() - classTeachers.size());
        for (DomTeacher t : schoolTeachers) {
            Boolean flag = true; //add teacher to result list
            for (DomTeacher c : classTeachers) {
                if (t.equals(c)) {
                    flag = false;
                }
            }
            result.add(t);
        }
        return result;
    }

    public void addTeacherToSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureTeacherSchoolClassManager.submitTeacherToSchoolClass(submit);
    }
}
