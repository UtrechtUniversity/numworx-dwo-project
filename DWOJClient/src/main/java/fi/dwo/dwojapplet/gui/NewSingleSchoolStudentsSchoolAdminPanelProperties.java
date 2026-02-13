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
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.DWO;

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

    // password is MD5    
    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
    	submit.getDomSingleSchoolStudent().setPassword(MD5.getHashString(submit.getDomSingleSchoolStudent().getPassword()));
    	Boolean result =  SecureSchoolAdminSchoolClassManager.submitSingleSchoolStudent(submit);
        return result;
    }

    // password is crypt 
    public static Boolean submitSingleSchoolStudentv2(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
    	submit.getDomSingleSchoolStudent().setPassword(DomLoginCheck.crypt(submit.getDomSingleSchoolStudent().getPassword()));
    	DomDwoProfileId profile = DWO.getDwoProfile();
    	Boolean result =  SecureSchoolAdminSchoolClassManager.submitSingleSchoolStudentv2(submit, profile);
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
