/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class NewSingleSchoolStudentsSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(NewSingleSchoolStudentsSchoolAdminPanelProperties.class.getName());
    

    public NewSingleSchoolStudentsSchoolAdminPanelProperties() {

    }

    public static List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getSchoolClasses();
    }

    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        Boolean result =  SecureSchoolAdminSchoolClassManager.submitSingleSchoolStudent(submit);
        return result;
    }

    public static Boolean IsValidUserDataInput(DomSingleSchoolStudent submit) throws Dwo2Exception {
        if (submit.getUserName() != null
                && !submit.getUserName().equals("")
                && submit.getPassword() != null) {
            return true;
        } else {
            return false;
        }
    }
}
