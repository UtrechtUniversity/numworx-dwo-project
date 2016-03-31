/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolClassManager;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class StudentsInSchoolClassSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolClassSchoolAdminPanelProperties.class.getName());

    public StudentsInSchoolClassSchoolAdminPanelProperties() {

    }
    
    public List<DomStudent> getStudentsInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getStudentsInSchoolClass(sc);
    }

    public void removeStudentFromSchoolClass(DomSchoolClass sc, DomStudent t) throws Dwo2Exception {
        DomRemoveStudentFromSchoolClass submit = new DomRemoveStudentFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setStudent(t);
        SecureSchoolAdminSchoolClassManager.removeStudentFromSchoolClass(submit);
    }
    

    public void submitStudentToSchoolClass(DomSchoolClass to, DomStudent t) throws Dwo2Exception {
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassTo(to);
        submit.setSchoolClassFrom(null);
        submit.setStudent(t);
        SecureSchoolAdminSchoolClassManager.submitStudentToSchoolClass(submit);
    }    

    DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception{
        return SecureSchoolAdminSchoolManager.getSingleSchoolStudent(submit);
    }

    void updateSingleSchoolStudent(DomSingleSchoolStudent student) throws Dwo2Exception{
        SecureSchoolAdminSchoolManager.updateSingleSchoolStudent(student);
    }

public List<DomStudent> getStudentsInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomStudent> classStudents = SecureSchoolAdminSchoolClassManager.getStudentsInSchoolClass(sc);
        List<DomStudent> schoolStudents = SecureSchoolAdminSchoolClassManager.getStudentsInSchool();
        List<DomStudent> result = new ArrayList<>(Math.max(0,schoolStudents.size() - classStudents.size()));
        for (DomStudent t : schoolStudents) {
            Boolean flag = true; //add student to result list
            for (DomStudent c : classStudents) {
                if (t.getId().equals(c.getId())) {
                    flag = false;
                    break;
                }
            }
            if(flag == true) result.add(t);
        }
        return result;
    }    
}
