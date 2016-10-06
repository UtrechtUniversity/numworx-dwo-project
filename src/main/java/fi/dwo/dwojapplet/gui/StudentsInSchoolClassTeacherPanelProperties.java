/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
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

    /** 
     * Returns only single school students that are in the school but not in the school class.
     * 
     * @param sc
     * @return
     * @throws Dwo2Exception 
     */
    public List<DomStudent> getStudentsInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
        List<DomStudent> classStudents = SecureTeacherSchoolClassManager.getStudentsInSchoolClass(sc);
        List<DomStudent> schoolStudents = SecureTeacherSchoolClassManager.getSingleSchoolStudentsInSchool();
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
        return SecureTeacherSchoolClassManager.getStudentsInSchoolClass(sc);
    }

    public void removeStudentFromSchoolClass(DomSchoolClass sc, DomStudent t) throws Dwo2Exception {
        DomRemoveStudentFromSchoolClass submit = new DomRemoveStudentFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setStudent(t);
        SecureTeacherSchoolClassManager.removeStudentFromSchoolClass(submit);
    }
    

    public void submitStudentToSchoolClass(DomSchoolClass from, DomSchoolClass to, DomStudent t) throws Dwo2Exception {
        DomSubmitStudentToSchoolClass submit = new DomSubmitStudentToSchoolClass();
        submit.setSchoolClassTo(to);
        submit.setSchoolClassFrom(from);
        submit.setStudent(t);
        SecureTeacherSchoolClassManager.submitStudentToSchoolClass(submit);
    }    

    public List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception{
        return SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
    }
            
    public List<DomSchoolClass> getTeachersOtherSchoolClasses(DomSchoolClass sc) throws Dwo2Exception{
        //get new list instance
        List<DomSchoolClass> scList = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
        for(DomSchoolClass c : scList){
            if(sc.getId().equals(c.getId())){
                scList.remove(c);
                break;
            }
        }
        return scList;
    }

    DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception{
        return SecureTeacherSchoolClassManager.getSingleSchoolStudent(submit);
    }

    void updateSingleSchoolStudent(DomSingleSchoolStudent student) throws Dwo2Exception{
        SecureTeacherSchoolClassManager.updateSingleSchoolStudent(student);
    }
            
}
