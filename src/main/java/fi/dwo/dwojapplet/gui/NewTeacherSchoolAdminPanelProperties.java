/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomFullTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class NewTeacherSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(NewTeacherSchoolAdminPanelProperties.class.getName());
    
//    public NewSingleSchoolStudentsTeacherPanelProperties() {
//
//    }
//
//    public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
//        return SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
//    }
//
//    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
//        return SecureTeacherSchoolClassManager.submitSingleSchoolStudent(submit);
//    }

    public static Boolean IsValidUserDataInput(DomFullTeacher submit) throws Dwo2Exception {
        if (submit.getUserName() != null
                && !submit.getUserName().equals("")
                && submit.getPassword() != null) {
            return true;
        } else {
            return false;
        }
    }
}
