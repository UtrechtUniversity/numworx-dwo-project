/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolManager;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class UsersInSchoolSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(UsersInSchoolSchoolAdminPanelProperties.class.getName());

    public UsersInSchoolSchoolAdminPanelProperties() {

    }

//    public List<DomTeacher> getTeachersInSchoolNotInClass(DomSchoolClass sc) throws Dwo2Exception {
//        List<DomTeacher> classTeachers = SecureTeacherSchoolClassManager.getTeachersInSchoolClass(sc);
//        List<DomTeacher> schoolTeachers = SecureTeacherSchoolClassManager.getTeachersInSchool();
//        List<DomTeacher> result = new ArrayList<DomTeacher>(schoolTeachers.size() - classTeachers.size());
//        for (DomTeacher t : schoolTeachers) {
//            Boolean flag = true; //add teacher to result list
//            for (DomTeacher c : classTeachers) {
//                if (t.equals(c)) {
//                    flag = false;
//                    break;
//                }
//            }
//            result.add(t);
//        }
//        return result;
//    }
    public List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.getStudentsInSchool();
    }

    public List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.getTeachersInSchool();
    }

    public List<DomSchoolAdmin> getSchoolAdminsInSchool() throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.getSchoolAdminsInSchool();
    }

    public DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.getSingleSchoolStudent(submit);
    }

    public void updateSingleSchoolStudent(DomSingleSchoolStudent student) throws Dwo2Exception {
        SecureSchoolAdminSchoolManager.updateSingleSchoolStudent(student);
    }


    public Boolean removeSingleSchoolStudentFromSchool(DomStudent submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.removeSingleSchoolStudentFromSchool(submit);
    }
    
    
    public Boolean removeStudentFromSchool(DomStudent submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.removeStudentFromSchool(submit);
    }

    public Boolean removeTeacherFromSchool(DomTeacher submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.removeTeacherFromSchool(submit);
    }

    public Boolean removeSchoolAdminFromSchool(DomSchoolAdmin submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.removeSchoolAdminFromSchool(submit);
    }
    
}
