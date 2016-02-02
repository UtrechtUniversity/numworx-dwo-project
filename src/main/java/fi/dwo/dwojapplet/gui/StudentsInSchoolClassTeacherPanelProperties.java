/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomSubmitStudentToSchoolClass;
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
public class StudentsInSchoolClassTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolClassTeacherPanelProperties.class.getName());

    public StudentsInSchoolClassTeacherPanelProperties() {

    }

    public List<DomStudent> getStudentsInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomStudent> classStudents = SecureTeacherSchoolClassManager.GetStudentsInSchoolClass(sc);
        List<DomStudent> schoolStudents = SecureTeacherSchoolClassManager.GetStudentsInSchool();
        List<DomStudent> result = new ArrayList<DomStudent>(schoolStudents.size() - classStudents.size());
        for (DomStudent t : schoolStudents) {
            Boolean flag = true; //add teacher to result list
            for (DomStudent c : classStudents) {
                if (t.equals(c)) {
                    flag = false;
                }
            }
            result.add(t);
        }
        return result;
    }
    
    public List<DomStudent> getStudentsInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.GetStudentsInSchoolClass(sc);
    }

    public void removeStudentFromSchoolClass(DomSchoolClass sc, DomStudent t) throws Dwo2Exception {
        DomRemoveStudentFromSchoolClass submit = new DomRemoveStudentFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setStudent(t);
        SecureTeacherSchoolClassManager.removeStudentFromSchoolClass(submit);
    }
    

    public void submitStudentToSchoolClass(DomSchoolClass sc, DomStudent t) throws Dwo2Exception {
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolToClass(sc);
        submit.setStudent(t);
        SecureTeacherSchoolClassManager.SubmitStudentToSchoolClass(submit);
    }    
        
}
