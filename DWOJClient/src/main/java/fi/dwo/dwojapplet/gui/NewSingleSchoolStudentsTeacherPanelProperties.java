/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class NewSingleSchoolStudentsTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(NewSingleSchoolStudentsTeacherPanelProperties.class.getName());
    
//    public NewSingleSchoolStudentsTeacherPanelProperties() {
//
//    }

    public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
    }

    // password is MD5
    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
    	submit.getDomSingleSchoolStudent().setPassword(MD5.getHashString(submit.getDomSingleSchoolStudent().getPassword()));
        return SecureTeacherSchoolClassManager.submitSingleSchoolStudent(submit);
    }

    // password is crypt
    public static Boolean submitSingleSchoolStudentv2(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
    	submit.getDomSingleSchoolStudent().setPassword(DomLoginCheck.crypt(submit.getDomSingleSchoolStudent().getPassword()));
    	DomDwoProfileId profile = DWO.getDwoProfile();
        return SecureTeacherSchoolClassManager.submitSingleSchoolStudentv2(submit, profile);
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
