/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
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
public class UsersSchoolClassesSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(UsersSchoolClassesSchoolAdminPanelProperties.class.getName());
//    List<DomSchoolClass> schoolClassList;

    UsersSchoolClassesSchoolAdminPanelProperties() {

    }

    List<DomSchoolClass> getTeachersSchoolClasses(DomTeacher teacher) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetTeachersSchoolClasses(teacher);
    }

    List<DomSchoolClass> getStudentsSchoolClasses(DomStudent student) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetStudentsSchoolClasses(student);
    }

    List<DomSchoolClass> getOtherSchoolClasses(DomUser domUser, UsersSchoolClassesSchoolAdminPanel.UserType userType) throws Dwo2Exception {
        List<DomSchoolClass> scList = SecureSchoolAdminSchoolClassManager.getSchoolClasses();
        if (!((domUser instanceof DomTeacher && userType == UsersSchoolClassesSchoolAdminPanel.UserType.TEACHER)
                || (domUser instanceof DomStudent && userType == UsersSchoolClassesSchoolAdminPanel.UserType.STUDENT))) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "DomUser is of an illegal DomUser type.");
        }
        List<DomSchoolClass> classesList = null;
        switch (userType) {
            case STUDENT:
                classesList = SecureSchoolAdminSchoolManager.GetStudentsSchoolClasses((DomStudent) domUser);
                break;
            case TEACHER:
                classesList = SecureSchoolAdminSchoolManager.GetTeachersSchoolClasses((DomTeacher) domUser);
                break;
            default:
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Switch statement failed to cover UserTypes.");
        }
        List<DomSchoolClass> result = new ArrayList<DomSchoolClass>();
        for (DomSchoolClass t : scList) {
            boolean flag = true;
            for (DomSchoolClass c : classesList) {
                if (t.getId().equals(c.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result.add(t);
            }
        }
        return result;
    }

    Boolean removeUserFromSchoolClass(DomUser domUser, UsersSchoolClassesSchoolAdminPanel.UserType userType, DomSchoolClass schoolClass) throws Dwo2Exception {
        if (!((domUser instanceof DomTeacher && userType == UsersSchoolClassesSchoolAdminPanel.UserType.TEACHER)
                || (domUser instanceof DomStudent && userType == UsersSchoolClassesSchoolAdminPanel.UserType.STUDENT))) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "DomUser is of an illegal DomUser type.");
        }
        switch (userType) {
            case STUDENT:
                DomRemoveStudentFromSchoolClass submitStudent = new DomRemoveStudentFromSchoolClass();
                DomStudent student = (DomStudent) domUser;
                submitStudent.setStudent(student);
                submitStudent.setSchoolClass(schoolClass);
                return SecureSchoolAdminSchoolClassManager.removeStudentFromSchoolClass(submitStudent);
            case TEACHER:
                DomRemoveTeacherFromSchoolClass submitTeacher = new DomRemoveTeacherFromSchoolClass();
                DomTeacher teacher = (DomTeacher) domUser;
                submitTeacher.setTeacher(teacher);
                submitTeacher.setSchoolClass(schoolClass);
                return SecureSchoolAdminSchoolClassManager.removeTeacherFromSchoolClass(submitTeacher);
            default:
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Switch statement failed to cover UserTypes.");
        }
    }

    DomSchoolClassFull getFullSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getFullSchoolClass(sc);
    }

    Boolean updateSchoolClass(DomSchoolClassFull fullSchoolClass) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.updateSchoolClass(fullSchoolClass);
    }

    Boolean submitUserToSchoolClass(DomUser domUser, UsersSchoolClassesSchoolAdminPanel.UserType userType, DomSchoolClass schoolClass) throws Dwo2Exception {
        if (!((domUser instanceof DomTeacher && userType == UsersSchoolClassesSchoolAdminPanel.UserType.TEACHER)
                || (domUser instanceof DomStudent && userType == UsersSchoolClassesSchoolAdminPanel.UserType.STUDENT))) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "DomUser is of an illegal DomUser type.");
        }
        switch (userType) {
            case STUDENT:
                DomSubmitStudentToSchoolClass submitStudent = new DomSubmitStudentToSchoolClass();
                DomStudent student = (DomStudent) domUser;
                submitStudent.setStudent(student);
                submitStudent.setSchoolClassTo(schoolClass);
                return SecureSchoolAdminSchoolClassManager.submitStudentToSchoolClass(submitStudent);
            case TEACHER:
                DomSubmitTeacherToSchoolClass submitTeacher = new DomSubmitTeacherToSchoolClass();
                DomTeacher teacher = (DomTeacher) domUser;
                submitTeacher.setTeacher(teacher);
                submitTeacher.setSchoolClass(schoolClass);
                return SecureSchoolAdminSchoolClassManager.submitTeacherToSchoolClass(submitTeacher);
            default:
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Switch statement failed to cover UserTypes.");
        }
    }
}
