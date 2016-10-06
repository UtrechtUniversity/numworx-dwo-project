/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
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
