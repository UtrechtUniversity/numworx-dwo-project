/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class NewSingleSchoolStudentsTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(NewSingleSchoolStudentsTeacherPanelProperties.class.getName());

    public NewSingleSchoolStudentsTeacherPanelProperties() {

    }

    public List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
    }

    public Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.submitSingleSchoolStudent(submit);
    }

    public Boolean CheckValidInput(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        if (submit.getDomSingleSchoolStudent() != null
                && submit.getDomSingleSchoolStudent().getUserName() != null
                && submit.getDomSingleSchoolStudent().getPassword() != null) {
            return true;
        } else {
            return false;
        }
    }
}
