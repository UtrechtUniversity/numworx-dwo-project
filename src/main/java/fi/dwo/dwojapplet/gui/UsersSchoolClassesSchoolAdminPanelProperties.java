/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.commons.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClassFull;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.dom.entities.DomUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolClassManager;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolManager;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
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

    UsersSchoolClassesSchoolAdminPanelProperties() {

    }

    List<DomSchoolClass> getTeachersSchoolClasses(DomTeacher teacher) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetTeachersSchoolClasses(teacher);
    }

    List<DomSchoolClass> getStudentsSchoolClasses(DomStudent student) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetStudentsSchoolClasses(student);
    }

    List<DomSchoolClass> getOtherSchoolClasses(DomUser domUser, UsersSchoolClassesSchoolAdminPanel.UserType userType) {
        return new ArrayList<>();
    }

    Boolean removeUserFromSchoolClass(UsersSchoolClassesSchoolAdminPanel.UserType userType, DomUser domUser, DomSchoolClass schoolClass) throws Dwo2Exception {
        if (!((domUser instanceof DomTeacher && userType==UsersSchoolClassesSchoolAdminPanel.UserType.TEACHER) 
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

}
